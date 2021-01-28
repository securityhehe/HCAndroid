package com.hc.accountinfo.vm


import android.Manifest
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.accountinfo.vm.viewdata.ViewUserInfoData
import com.hc.accountinfo.vm.viewdata.getIntKey
import com.hc.accountinfo.vm.viewdata.getTextKey
import com.hc.data.MenuData
import com.hc.data.common.CommonDataModel
import com.hc.data.formPermissionPage
import com.hc.data.user.BankDictList
import com.hc.data.user.UserInfoExt
import com.hc.data.user.UserInfoRange
import com.hc.data.user.UserType
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance
import frame.utils.Base64
import frame.utils.RegularUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.thread


data class MenuEntry(var title: Int, var data: List<UserType>?)

class ProfileInfoViewModel : BaseViewModel() {

    private var mCurrentIndex = -1

    companion object {
        const val NO_WORK = 4
        const val SELF_EMPLOYED = 3
        const val BUSINESS = 5
        const val SALARIED_FULL_TIME = 1
        const val SALARIED_PART_TIME = 2
    }

    private var mPermissionModel: PermissionCheckModel = PermissionCheckModel()
    private val x217 = ScreenAdapterUtils.dp2px(ContextProvider.app, 217)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)
    private var cacheContactInfo: String? = null
    private var isFirstLoadContact: Boolean = true
    private lateinit var mMenuEntryList: ArrayList<MenuEntry>
    private var mJobNature: List<UserType>? = mutableListOf()
    private var mMapUserTypeData = mutableMapOf<String, UserType>()
    private var isExistUserInfo = false //是否存在。
    var mViewData = ViewUserInfoData()
    var baseVm: BaseAuthCenterInfo? = null


    fun initBaseInfoViewModel(fragment: Fragment) {
        val viewModelStoreOwner = NavHostFragment.findNavController(fragment).getViewModelStoreOwner(R.id.loan_info_model_container)
        baseVm = ViewModelProvider(viewModelStoreOwner).get((BaseAuthCenterInfo::class.java))
        println("center===> $baseVm")
    }

    private suspend fun checkMenuData(): Unit {
        return withContext(viewModelScope.coroutineContext) {
            if (!this@ProfileInfoViewModel::mMenuEntryList.isInitialized) {
                val result = reqApi(UserInfoService::class.java, { queryListInfo() }, isShowLoading = true)
                result.data?.let {
                    initData(it)
                }
            }
        }
    }

    override fun back(view: View) {
        if (baseVm?.formKey == formPermissionPage) {
            gotoAuthCenterPage(view)
        } else {
            super.back(view)
        }
    }

    fun gotoAuthCenterPage(view: View) {
        Navigation.findNavController(view).navigate(R.id.loan_info_model_kyc_auth_center)
    }

    private fun initData(menu: UserInfoRange) {
        val gender = MenuEntry(R.string.loan_info_select_gender_title, menu.gender)
        val language = MenuEntry(R.string.loan_info_select_language_title, menu.language)
        val edu = MenuEntry(R.string.loan_info_select_education_title, menu.education)
        val ms = MenuEntry(R.string.loan_info_select_marital_status_title, menu.maritalStatus)
        val purpose = MenuEntry(R.string.loan_info_select_purpose_title, menu.purpose)
        val occupation = MenuEntry(R.string.loan_info_select_occupation_title, menu.occupation)
        val companyIndustry = MenuEntry(R.string.loan_info_select_company_industry_title, menu.companyIndustry)
        val jobNature = MenuEntry(R.string.loan_info_select_job_nature_title, mJobNature)
        val staffSize = MenuEntry(R.string.loan_info_select_staff_size_title, menu.staffSize)
        val levelOfPosition = MenuEntry(R.string.loan_info_select_level_of_position_title, menu.levelOfPosition)
        val salaryRange = MenuEntry(R.string.loan_info_select_salary_range_title, menu.salaryRange)
        mMenuEntryList = arrayListOf(
            gender,
            language,
            edu,
            ms,
            purpose,
            occupation,
            companyIndustry,
            jobNature,
            staffSize,
            levelOfPosition,
            salaryRange
        )

        mMenuEntryList.forEach { key ->
            val map = key.data?.associateBy({ "${it.state}_${it.info}" }, { it })
            map?.let {
                mMapUserTypeData.putAll(it)
            }
        }
    }

    fun showMenu(index: Int, fragment: Fragment, menuVm: BaseMenuViewModel?, view: TextView) {
        viewModelScope.launch {
            try {
                mCurrentIndex = index
                checkMenuData()
                if (index in 0 until mMenuEntryList.size) {
                    val menuEntry = mMenuEntryList[index]
                    showMenu(fragment, menuVm, menuEntry.data, menuEntry.title, view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showMenu(fragment: Fragment, menuVm: BaseMenuViewModel?, data: List<UserType>?, titleRes: Int, view: TextView) {
        viewModelScope.launch {
            val act = ActivityStack.currentActivity()
            if (data != null && act != null && !act.isFinishing && !act.isDestroyed) {
                val isOkResult = checkMustPermission(fragment)
                var popupWindow: BasePopupWindow?
                menuVm?.apply {
                    if (isOkResult) {
                        val menuData = data.mapIndexed { i, d -> MenuData(d, i, false) }
                        title = view.context.getString(titleRes)
                        listData.clear()
                        listData.addAll(menuData)
                        popupWindow = BasePopupWindow(act, menuVm, view, x217)
                        popupWindow?.show(x187, x23)
                        callbackData = { it ->
                            popupWindow?.dismiss()
                            val selectNetData = mViewData.selectNetData
                            if (mCurrentIndex in 0 until selectNetData.size) {
                                val loanObservableField = selectNetData[mCurrentIndex]
                                loanObservableField.set(it.menuInfo)
                            }
                            Unit
                        }
                    }
                }
            }
        }
    }

    fun checkMustPermission(et: EditText, fragment: Fragment) {
        if (et.isFocusable) {
            viewModelScope.launch {
                checkMustPermission(fragment)
            }
        }
    }

    private suspend fun checkMustPermission(fragment: Fragment): Boolean {
        return withContext(viewModelScope.coroutineContext) {
            var isPermissionOk = false
            if (mPermissionModel.checkMustPermissions(fragment)) {
                if (mPermissionModel.isGoogleServiceAvail) {
                    LocationUtils.getInstance().resetLocationUpdates(ContextProvider.app) { isOk ->
                        if (isOk) {
                            isPermissionOk = true
                        }
                    }
                } else {
                    isPermissionOk = true
                }
            }
            isPermissionOk
        }
    }


    fun reqUserInfo(isCertifyFinish: Boolean) {
        viewModelScope.launch {
            checkMenuData()
            var userInfo = reqApi(UserInfoService::class.java, { queryUserExtraInfo() }, isCancelDialog = false, isShowLoading = true)
            if (userInfo.data == null) {
                if (!isCertifyFinish) {
                    //没有认证过。 默认控制可以编辑的有 firstName,middle,lastName,gender
                    mViewData.isEditable.set(true)
                    mViewData.isShowJobAndLevelOfPosition.set(View.GONE)
                    //sms code
                    mViewData.isShowSmsCodeInput.set(false)
                    mViewData.isCheckEmail.set(false)
                    //更新认证状态
                    mViewData.isCreditFinish.set(false)
                } else {
                    //如果认证实现，则四个值不可以编辑。firstName,middle,lastName,gender
                    mViewData.isEditable.set(false)
                    //  writeWork.set(View.GONE)
                    mViewData.isShowJobAndLevelOfPosition.set(View.GONE)
                    //sms code
                    mViewData.isShowSmsCodeInput.set(false)
                    mViewData.isCheckEmail.set(false)
                    //更新认证状态
                    mViewData.isCreditFinish.set(true)
                }
            } else {
                userInfo.data?.let {
                    it.run {
                        var isCanEdit = false //默认包含30状态的情况
                        if (!isCertifyFinish) {//handler editable
                            isCanEdit = TextUtil.isExistEmpty(purposeStr)
                        }

                        mViewData.isEditable.set(isCanEdit)
                        //是否显示邮箱验证码输入框。
                        mViewData.isShowSmsCodeInput.set(it.emailCheck == Constants.NUMBER_1)
                        mViewData.isCheckEmail.set(mViewData.isShowSmsCodeInput.get())

                        mViewData.selectNetData.forEachIndexed { i, data ->
                            val a = getIntKey()
                            val userType = mMapUserTypeData[a[i]]
                            if (userType != null) {
                                data.set(userType)
                            }
                        }

                        mViewData.dataTextList.forEachIndexed { i, data ->
                            val a = getTextKey()
                            val s = a[i]
                            if (!s.isNullOrEmpty()) {
                                data.set(s)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun collectContactInfo() {
        if (AndroidPermissions.hasPermissions(ContextProvider.app, Manifest.permission.READ_CONTACTS)) {
            if (isFirstLoadContact) {
                isFirstLoadContact = false
                thread(true) {
                    ActivityStack.currentActivity()?.let {
                        if (!it.isDestroyed || !it.isFinishing) {
                            val contacts = PhoneUtil.getContactsList(it)
                            cacheContactInfo = Base64.encode(GsonUtils.toJsonString(contacts).toByteArray())
                        }
                    }
                }
            }
        }
    }

    fun commitBeforeCheckPermission(fragment: Fragment) {
        mPermissionModel.requestPermission(fragment)
    }

    fun commitUserInfoData(fragment: Fragment, reqCode: Int) {
        if (reqCode == PermissionCheckModel.PERMISSION_DATA_COMMIT) {
            collectContactInfo()
            FirseBaseEventUtils.trackEvent(StatEventTypeName.PERSON_INFO_COMMIT)

            if (!TextUtil.isEmpty(mViewData.anotherPhone.get()) && (mViewData.anotherPhone.get()?.length ?: 0 < 10)) {
                ToastUtils.showShort(R.string.loan_info_write_user_info_input_ten_anther_code)
                return
            }

            if (mViewData.occ.get() == null) {
                ToastUtils.showShort(R.string.loan_info_write_work_not_first_choice_occupation_toast)
                return
            }

            if (!(mViewData.condition1() || mViewData.condition2() || mViewData.condition3())) {
                ToastUtils.showShort(R.string.loan_info_write_work_info_check_necessary_fill_in_tip)
                return
            }

            if (mViewData.writeWork.get() == View.VISIBLE) {

                if (!RegularUtil.isEmail(mViewData.workEmail.get())) {
                    ToastUtils.showShort(R.string.loan_info_write_user_info_email_error_tip)
                    return
                }

                if ((mViewData.companyTel.get()?.length ?: 0) < 10) {
                    ToastUtils.showShort(R.string.loan_info_write_work_company_tel_error_tip)
                    return
                }
            }

            if (TextUtil.isEmpty(cacheContactInfo)) {
                ToastUtils.showShort(R.string.loan_info_write_user_info_submit_try_again)
                return
            }
            mViewData.mUserInfo.registerCoordinate = LocationUtils.getLatLng()
            mViewData.mUserInfo.info = cacheContactInfo
            viewModelScope.launch {
                val reqResult = reqApi(UserInfoService::class.java, block = { saveOrUpdateUserInfo(mViewData.mUserInfo) }, isCancelDialog = false)
                reqResult.data?.run {
                    /** 10-黑名单 ,20-白名单  */
                    if (!isExistUserInfo) {
                        // 跳转到认证结果页面。
                        NavHostFragment.findNavController(fragment).navigate(R.id.loan_info_model_profile_info_supply_info)
                        // NavHostFragment.findNavController(fragment).navigate()//
                        // ARouter.getInstance().build(ARouterPath.CREDIT_RESULT).withInt(ARouterKeys.STATE, this.userState).withDouble(ARouterKeys.DATA, this.userCredit).navigation()
                        return@run
                    }
                    when {
                        CommonDataModel.RUNTIME_USER_SUPPLEMENT_INFO_STATE -> {
                            NavHostFragment.findNavController(fragment).navigate(R.id.loan_info_model_profile_info_supply_info)
                        }
                        CommonDataModel.RUNTIME_USER_BANK_INFO_STATE -> {
                            // ARouterPath.BANK //跳转到银行页面。
                        }
                        else -> {
                            back(fragment.requireView())
                        }
                    }

                }
            }
        }
    }

    fun choseWorkDateClick(fragment: Fragment) {
        var now = Calendar.getInstance()
        var dpd = newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                mViewData.workSince.set("$dayOfMonth-${(monthOfYear + 1)}-$year")
                mViewData.mWorkSinceText = "$year-${monthOfYear + 1}-$dayOfMonth"
            },
            now.get(Calendar.YEAR), // Initial year selection
            now.get(Calendar.MONTH), // Initial month selection
            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        )
        dpd.maxDate = now
        dpd.show(fragment.childFragmentManager, "WorkSinceDatePickerDialog")
    }


}



