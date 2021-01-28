package com.hc.accountinfo.vm

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.blankj.utilcode.util.ToastUtils
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.MenuData
import com.hc.data.user.BankInfo
import com.hc.data.user.ConfirmIfsc
import com.hc.data.user.UserType
import com.hc.uicomponent.LoanObservableField
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.menu.BaseMenuViewModel
import com.hc.uicomponent.menu.BasePopupWindow
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.*
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.listener.OnViewClickListener
import com.tools.network.entity.Params.RES_SUCCEED
import frame.utils.DeviceUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class BankMenuData(var title: Int, var data: List<BankInfo>)

class BankInfoViewModel : BaseViewModel(), LifecycleObserver {
    var data  = MutableLiveData<String>()
    private var countDownTimer: CountDownTimer? = null
    private val x217 = ScreenAdapterUtils.dp2px(ContextProvider.app, 217)
    private val x23 = ScreenAdapterUtils.dp2px(ContextProvider.app, 23)
    private val x187 = ScreenAdapterUtils.dp2px(ContextProvider.app, -70)
    private lateinit var mMenuEntryList: ArrayList<BankMenuData>
    var mCurrentIndex = 0
    var isEnable = LoanObservableField<Boolean>()

    var mSelectCardData = LoanObservableField<UserType>().setCallT {
        mCardName.set(it.info)
        inputCheck()
    }

    var mCardName = LoanObservableField<String>().setCallT{
        inputCheck()
    }

    var cardNum = LoanObservableField<String>().setCallT {
        inputCheck()
    }

    var cardNumConfirm = LoanObservableField<String>().setCallT {
        inputCheck()
    }

    var code = LoanObservableField<String>().setCallT {
        inputCheck()
    }

    var viewData = mutableListOf(cardNum, cardNumConfirm, code);

    private suspend fun checkMenuData() {
        return withContext(viewModelScope.coroutineContext) {
            if (!this@BankInfoViewModel::mMenuEntryList.isInitialized) {
                val result = reqApi(UserInfoService::class.java, { getBankListDictionary(Constants.BANKTYPE) }, isShowLoading = true)
                result.data?.run {
                    this.bankTypeList?.run {
                        mMenuEntryList = arrayListOf()
                        mMenuEntryList.add(BankMenuData(R.string.bank_info, this))
                    }
                }
            }
        }
    }

    fun showMenu(index: Int, menuVm: BaseMenuViewModel?, view: View) {
        viewModelScope.launch {
            try {
                mCurrentIndex = index
                checkMenuData()
                if (index in 0 until mMenuEntryList.size) {
                    val menuEntry = mMenuEntryList[index]
                    showMenu(menuVm, menuEntry.data, menuEntry.title, view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showMenu(menuVm: BaseMenuViewModel?, data: List<BankInfo>?, titleRes: Int, view: View) {
        val act = ActivityStack.currentActivity()
        if (data != null && act != null && !act.isFinishing && !act.isDestroyed) {
            var popupWindow: BasePopupWindow?
            menuVm?.apply {
                val menuData = data.mapIndexed { i, d ->
                    val a = UserType(null, d.code ?: "", d.value ?: "", null, "", "")
                    MenuData(a, i, false)
                }
                title = view.context.getString(titleRes)
                listData.clear()
                listData.addAll(menuData)
                popupWindow = BasePopupWindow(act, menuVm, view, x217)
                popupWindow?.show(x187, x23)
                callbackData = { it ->
                    popupWindow?.dismiss()
                    mSelectCardData.set(it.menuInfo)
                    Unit
                }
            }
        }
    }

    private fun inputCheck() {
        isEnable.set(!TextUtil.isExistEmpty(mCardName.get(), cardNum.get(), code.get()))
    }

    fun showBankDetailInfo() {
        viewModelScope.launch {
            var bankRes = reqApi(UserInfoService::class.java, { queryUserBankInfo() }, isCancelDialog = false)
            bankRes.data?.run {
                this.bankCard?.run {
                    mCardName.set(this.bankName)
                    cardNum.set(this.bankNo)
                    cardNumConfirm.set(this.bankNo)
                    code.set(ifscCode)
                }
            }
        }
    }


    //提交银行卡流程，先弹出确认弹出窗口。如果用户输入信息确认。在进行提交。
    fun showConfirmBankInfo(onceAgain: Boolean, view: View) {
        val context = view.context
        val rootView = LayoutInflater.from(context).inflate(R.layout.account_bank_info_confirm, null, false)
        rootView.run {
            findViewById<TextView>(R.id.dialog_bank_no).text = cardNum.get()
            findViewById<TextView>(R.id.dialog_ifsc).text = code.get()
        }
        val builder = TDialog.Builder((context as FragmentActivity).supportFragmentManager).setDialogView(rootView)
        val resources = context.resources
        val w = resources.displayMetrics.widthPixels - (resources.getDimensionPixelOffset(R.dimen.padding_30) * 2)
        builder.setWidth(w)
        builder.setGravity(Gravity.CENTER)
        builder.setCancelableOutside(false)

        builder.addOnClickListener(R.id.confirm_bank, R.id.cancel_confirm_bank)
        builder.setOnKeyListener { _, _, _ ->
            true
        }
        builder.setOnViewClickListener { _, v, tDialog ->
            tDialog.dismissAllowingStateLoss()
            when (v.id) {
                R.id.confirm_bank -> {
                    FirseBaseEventUtils.trackEvent(StatEventTypeName.BANK_INFO_COMMIT)
                    //调取接口确认后续流程
                    submitBankInfo(view, onceAgain)
                }
                R.id.cancel_confirm_bank -> {
                    if (!onceAgain) {
                        cancelCountDown()
                    }
                }
            }
        }
        val tDialog = builder.create()
        tDialog.show()
        countDown(rootView.findViewById(R.id.confirm_bank))
    }

    private fun submitBankInfo(view: View, isConfirmIfsc: Boolean) {
        if (cardNum.get() != cardNumConfirm.get()) {
            ToastUtils.showShort(R.string.write_bank_inconsistent_tip)
            return
        }
        val codeText = code.get()
        if (!TextUtil.isEmpty(codeText)) {
            if (codeText?.length != 11 || codeText[4] != '0') {
                ToastUtils.showShort(R.string.write_bank_ifsc_tip)
                return
            }
        }

        viewModelScope.launch {
            val bankName = mCardName.get()
            val bankNo = cardNum.get()
            var commitRes = reqApi(UserInfoService::class.java,
                block = {
                    saveBankInfo(
                        bankName
                        , bankNo
                        , codeText
                        , Constants.NUMBER_1
                        , DeviceUtil.getDeviceId(ContextProvider.app),
                        isConfirmIfsc
                    )
                }
                , isShowLoading = true
                , apiFailure = {
                    if (!isConfirmIfsc) {
                        if (it.data != null) {
                            try {
                                val confirmIfsc = GsonUtils.parserJsonToArrayBean(GsonUtils.toJsonString(it.data), ConfirmIfsc::class.java)
                                if (confirmIfsc != null) {
                                    val isNeed: Boolean = confirmIfsc.needConfirmSign
                                    if (isNeed) {
                                        showIfscConfirmDialog(view.context, bankNo ?: "", codeText ?: "")
                                    }
                                }
                            } catch (e: Exception) {
                                showConfirmBankInfo(true, view)
                            }
                        } else {
                            ToastUtils.showShort(it.msg)
                        }
                    }
                    false
                })
            if(commitRes.code == RES_SUCCEED){
                data.value = cardNum.get()
            }

        }
    }

    private fun showIfscConfirmDialog(context: Context, bankNo: String, ifscCode: String) {
        val rootView = LayoutInflater.from(context).inflate(R.layout.account_bank_info_confirm, null, false)
        rootView.run {
            findViewById<TextView>(R.id.dialog_bank_no).text = bankNo
            findViewById<TextView>(R.id.dialog_ifsc).text = ifscCode
            findViewById<TextView>(R.id.dialog_content).text = resources.getString(R.string.dialog_bank_check_ifsc_content)
            findViewById<TextView>(R.id.confirm_bank).apply {
                text = rootView.resources.getString(R.string.dialog_confirm)
                setTextColor(resources.getColor(R.color.C_APP_THEME))
            }
        }
        val builder = TDialog.Builder((context as FragmentActivity).supportFragmentManager).setDialogView(rootView)
        val resources = context.resources
        val w = resources.displayMetrics.widthPixels - (resources.getDimensionPixelOffset(R.dimen.padding_30) * 2)
        builder.setWidth(w)
        builder.setGravity(Gravity.CENTER)
        builder.setCancelableOutside(false)

        builder.addOnClickListener(R.id.confirm_bank, R.id.cancel_confirm_bank)
        builder.setOnKeyListener { _, _, _ ->
            true
        }
        builder.setOnViewClickListener { _, view, tDialog ->
            tDialog.dismissAllowingStateLoss()
            when (view.id) {
                R.id.confirm_bank -> {
                    submitBankInfo(view, true)
                }
                R.id.cancel_confirm_bank -> {
                }
            }
        }
        val tDialog = builder.create()
        tDialog.show()
    }


    private fun countDown(confirmBankTv: TextView) {
        startCountDown(
            countDownAction = {
                confirmBankTv.run {
                    this.text = ContextProvider.getString(R.string.dialog_confirm_downtime, "$it")
                    this.setTextColor(Color.parseColor("#666666"))
                    this.isEnabled = false
                }
            }, finishCountDown = {
                confirmBankTv.run {
                    this.text = ContextProvider.getString(R.string.dialog_confirm)
                    this.setTextColor(Color.parseColor("#047538"))
                    this.isEnabled = true
                }
            }
        )
    }

    private fun startCountDown(countDownAction: (Long) -> Unit, finishCountDown: () -> Unit) {
        countDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownAction(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                finishCountDown()
            }
        }
        countDownTimer?.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCountDown() {
        countDownTimer?.cancel()
    }


}