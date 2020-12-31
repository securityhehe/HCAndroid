package com.hc.login.vm

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.appsflyer.AppsFlyerLib
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.MenuData
import com.hc.data.formKey
import com.hc.data.formPermissionPage
import com.hc.login.BuildConfig
import com.hc.login.R
import com.hc.login.api.LoginServer
import com.hc.data.common.TokenData
import com.hc.data.user.UserType
import com.hc.login.data.UserDeviceInfo
import com.hc.login.provider.LoginProvider
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.config.STATE_OPEN_CHANNEL
import com.hc.uicomponent.config.isPrivatePackage
import com.hc.uicomponent.provider.isTEST
import com.hc.uicomponent.provider.navigationStackPrintln
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.timer.DefaultTimer
import com.hc.uicomponent.timer.Timer
import com.hc.uicomponent.utils.*
import com.tools.network.entity.HttpResult
import frame.utils.DeviceUtil
import frame.utils.StringUtil
import kotlinx.coroutines.launch
import java.util.*

sealed class RegisterState(var stateValue: String) {
    object Registered : RegisterState("20")
    object NotRegistered : RegisterState("10")
}

class LoginVM : BaseViewModel() {
    companion object {
        const val PERMISSION_REQ = 100
        const val PHONE_NUMBER_FORMAT = 10
        const val CODE_FORMAT = 4
        private val mValidPhoneNumberCache by lazy {
            mutableMapOf<String, Boolean>()
        }
    }

    val test = false
    val timeTask: Timer = DefaultTimer
    val isEnableLoginLoan = ObservableBoolean(true)
    val isLanguageMenuShowHide = ObservableInt(View.GONE)
    val enablePhoneLongNextBtn = ObservableBoolean(false)
    fun start(view: View) {
        Navigation.findNavController(view).navigate(R.id.action_noLogin_to_phoneLogin)
    }

    fun clickLanguage() {
        if (isLanguageMenuShowHide.get() == View.GONE) {
            isLanguageMenuShowHide.set(View.VISIBLE)
        } else {
            isLanguageMenuShowHide.set(View.GONE)
        }
    }

    fun touchOutMenuRectHintMenu(): Boolean {
        isLanguageMenuShowHide.set(View.GONE)
        return false
    }

    var language = ObservableField<MenuData>()
    var isUpdateLanguageUI = MutableLiveData<Unit>()
    var listData = ObservableArrayList<MenuData>()
    var title: String? = ""

    init {
        listData.add(
            MenuData(
                UserType(info = ContextProvider.app.getString(R.string.lan_login_language_en), state = ""),
                0,
                true
            )
        )
        listData.add(
            MenuData(
                UserType(info = ContextProvider.app.getString(R.string.loan_login_language_hindi), state = ""),
                1,
                false
            )
        )
    }

    val callbackSelectLanguageData: (RecyclerView, MenuData) -> Unit = { rv, it ->
        listData?.forEach {
            it.isSelect = false
        }
        listData[(it.index)].isSelect = true
        isUpdateLanguageUI.value = null
        language.set(it)
        rv.adapter?.notifyDataSetChanged()
    }

    var phoneCode: String = "+91"
        set(phoneCode) {
            field = phoneCode
            inputCheck()
        }

    var phoneNumber: String = ""
        set(phoneNumber) {
            field = phoneNumber
            inputCheck()
        }

    private fun inputCheck() {
        enablePhoneLongNextBtn.set((!phoneNumber.isBlank()) && removeBlank(phoneNumber).length == PHONE_NUMBER_FORMAT)
    }


    /**
     *一:第一步本地缓存中是否有
     *****1.判断是否在倒计时内。
     *****2.在倒计时，跳转输入验证码页面,不发送验证码。
     *****3.不在倒计时,跳转到验证码页面，发送验证码。
     *二：如果手机号码不在缓存中，查询api,结果返回，
     *****1.服务端缓存是否注册。
     *****2.跳转到验证码页面
     *****3.发送验证码。
     *三.没有结果返回，不做任何操作。
     */
    fun doLoginStep1IsToSendSms(view: View) {
        if (isTEST) {
            gotoInputCheckCodePage(view)
            return
        }
        FirseBaseEventUtils.trackEvent(StatEventTypeName.LOGIN_NEXT)
        if (mValidPhoneNumberCache.containsKey(phoneNumber)) {
            gotoInputCheckCodePage(view)
            if (!timeTask.isStart) {
                sendSmsCode(phoneNumber)
            }
        } else {
            viewModelScope.launch {
                val result = reqApi(LoginServer::class.java, {
                    isPhoneExists(phoneNumber)
                })
                result.data?.run {
                    val isRegister = (isExists == RegisterState.Registered.stateValue)
                    if (!mValidPhoneNumberCache.containsKey(phoneNumber)) {
                        mValidPhoneNumberCache[phoneNumber] = isRegister
                    }
                    gotoInputCheckCodePage(view)
                    sendSmsCode(phoneNumber)
                }
            }
        }
    }

    //关闭键盘，调整到输入验证码也念。
    private fun gotoInputCheckCodePage(view: View) {
        ActivityStack.currentActivity()?.let {
            KeyboardUtils.hideSoftInput(it)
        }
        Navigation.findNavController(view).navigate(R.id.action_noLogin_to_phoneLogin)
    }


    // sms check code request
    private fun sendSmsCode(phone: String) {
        viewModelScope.launch {
            val reqResult = reqApi(LoginServer::class.java,
                block = { getSmsCode(DeviceUtil.getDeviceId(ContextProvider.app), phone, "login") },
                callDone = {},
                apiFailure = { apiEx: HttpResult<*> ->
                    ToastUtils.showShort(apiEx.msg)
                    false
                }

            )
            reqResult?.let {
                startTimeCount()
            }

        }
    }

    val mSmsBtnState = ObservableBoolean(false)
    val countDown = ObservableField("90s")
    var count = 90

    private fun startTimeCount() {
        count = 90
        timeTask.reset()
        timeTask.start(object : TimerTask() {
            override fun run() {
                if (count < 0) {
                    timeTask.reset()
                    mSmsBtnState.set(true)
                    countDown.set(ContextProvider.app.getString(R.string.login_opt_get))
                } else {
                    countDown.set("${count}S")
                    count -= 1
                }
            }
        })
        mSmsBtnState.set(false)
    }

    override fun onCleared() {
        super.onCleared()
        timeTask.reset()
    }

    fun clearNumber(editText: EditText) {
        editText.setText("")
        mSmsBtnState.set(true)
    }

    var code: String = ""
        set(code) {
            field = code
            isSmsCodeValid()
        }

    private fun isSmsCodeValid() {
        inputCodeNextBtnState.set(!StringUtil.isEmpty(this.code) && this.code.length == CODE_FORMAT)
    }

    val inputCodeNextBtnState = ObservableBoolean(false)

    var isShowCheckError = ObservableInt(View.GONE)

    fun doCommitCheckCode(view: View) {
        if (isTEST) {
            gotoPermissionPage(view)
            return
        }

        val channelName = getChannel()
        FirseBaseEventUtils.trackEvent(StatEventTypeName.LOGIN_VIA_OTP)
        val info = UserDeviceInfo(
            phoneNumber
            , ""
            , code
            , channelName,
            LocationUtils.getLatLng(),
            "",
            Locale.getDefault().language,
            "android",
            DeviceUtil.getDeviceId(ContextProvider.app) ?: "",
            AppsFlyerLib.getInstance().getAppsFlyerUID(ActivityStack.currentActivity())
        )

        viewModelScope.launch {
            val result = reqApi(LoginServer::class.java, { doLogin(info) })
            result?.let {
                ToastUtils.showShort(it.msg)
                it.data?.apply {
                    AppsFlyerUtils.setCustomerUserId(ContextProvider.getString(R.string.app_name) + userId)
                    saveUserInfo(this)
                    if (mValidPhoneNumberCache[phoneNumber] == false) {
                        gotoPermissionPage(view)
                        val event = FacebookEventUtils.COMPLETE_REGISTRATION
                        FacebookEventUtils.setFacebookEvent(ContextProvider.app, event)
                        val event1 = AppsFlyerUtils.APPSFLYER_EVENT_COMPLETE_REGISTRATION
                        AppsFlyerUtils.setAppsFlyerEvent(event1)
                    } else {
                        gotoMainPage(view)
                    }
                }
            }
        }
    }

    private fun getChannel(): String {
        return if (isPrivatePackage) {
            BuildConfig.CHANNEL_NAME
        } else {
            val gpChannelName = mmkv().decodeString(STATE_OPEN_CHANNEL, "")
            if (StringUtil.isEmpty(gpChannelName)) {
                BuildConfig.CHANNEL_NAME
            } else {
                gpChannelName
            }
        }
    }

    private fun gotoPermissionPage(view: View) {
        Navigation.findNavController(view).printStack()
        ContextProvider.mNavIdProvider?.apply {
            val opt = NavOptions.Builder()
                .setEnterAnim(R.anim.anim_right_to_middle)
                .setLaunchSingleTop(true)
                .setPopExitAnim(R.anim.anim_middle_to_right)
                .setPopUpTo(getRootNavId(), false).build()
            Navigation.findNavController(view).navigate(R.id.loginModelPermission, null, opt)
        }
        Navigation.findNavController(view).printStack()
    }

    fun gotoMainPage(view: View) {
        Navigation.findNavController(view).printStack()
        ContextProvider.mNavIdProvider?.apply {
            val opt = NavOptions.Builder()
                .setEnterAnim(R.anim.anim_right_to_middle)
                .setLaunchSingleTop(true)
                .setPopExitAnim(R.anim.anim_middle_to_right)
                .setPopUpTo(getRootNavId(), false).build()
            Navigation.findNavController(view).navigate(getMainNavId(), null, opt)
        }
        Navigation.findNavController(view).printStack()
    }

    private fun saveUserInfo(userInfo: TokenData) {
        userInfo.mobile = phoneNumber
        LoginProvider.instance?.saveUserInfo(userInfo)
    }

    //调整到kyc页面。
    fun toKycPage(nextBtn: Button) {
        val bundle = bundleOf(Pair(formKey, formPermissionPage))
        ContextProvider.mNavIdProvider?.let {
            val opt = NavOptions.Builder()
                .setEnterAnim(R.anim.anim_right_to_middle)
                .setLaunchSingleTop(true)
                .setPopExitAnim(R.anim.anim_middle_to_right)
                .setPopUpTo(it.getRootNavId(), false).build()
            val url = Uri.parse("loan://loan/infoModel/Kyc?formKey=${formPermissionPage}")
            val a = NavDeepLinkRequest.Builder.fromUri(url).build()
            val navigation = Navigation.findNavController(nextBtn)
            navigation.navigate(a, opt)
            navigation.navigationStackPrintln("toKeyPage()")
        }
    }

    var permissionArray: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_SMS
    )

}


@SuppressLint("RestrictedApi")
fun NavController.printStack() {
    println("====================stack==================")
    val a = backStack.iterator()
    while (a.hasNext()) {
        val a = a.next()
        println(a.destination.toString())
    }
    println("====================end==================")
}

