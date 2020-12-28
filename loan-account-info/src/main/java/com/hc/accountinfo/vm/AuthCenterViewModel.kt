package com.hc.accountinfo.vm

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.core.os.bundleOf
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.accountinfo.AuthDialog
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.common.CommonDataModel
import com.hc.data.formPermissionPage
import com.hc.uicomponent.BaseDialog
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.CommonProvider
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.provider.navigationStackPrintln
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.DialogUtils
import com.hc.uicomponent.utils.TextUtil
import frame.utils.DeviceUtil
import kotlinx.coroutines.launch

class AuthCenterViewModel : BaseViewModel() {
    var isTEST = true
    var creditPersonStatus = ObservableInt(10)
    var creditKycStatus = ObservableInt(10)
    var creditEmployStatus = ObservableInt(10)
    var creditBankStatus = ObservableInt(10)
    var creditMobileStatus = ObservableInt(10)
    var visibleModelOpt = ObservableInt(View.GONE)
    var isCreditPersonStatus: Boolean = false
    var isCreditKycStatus: Boolean = false
    var isCreditSupplementStatus: Boolean = false
    var isCreditBankStatus: Boolean = false
    var isCreditMobileEnable: Boolean = false

    var baseVm: BaseAuthCenterInfo? = null

    fun initBaseInfoViewModel(fragment: Fragment) {
        baseVm = ViewModelProvider(
            NavHostFragment.findNavController(fragment)
                .getViewModelStoreOwner(R.id.loan_info_model_container)
        ).get((BaseAuthCenterInfo::class.java))
        println("center===> $baseVm")
    }

    //返回到主Navigation,并跳转到首页。
    override fun back(view: View) {
        val findNavController = Navigation.findNavController(view)
        if (baseVm?.formKey == formPermissionPage) {
            ContextProvider.mNavIdProvider?.apply {
                val opt = NavOptions.Builder()
                    .setEnterAnim(R.anim.anim_right_to_middle)
                    .setLaunchSingleTop(true)
                    .setPopExitAnim(R.anim.anim_middle_to_right)
                    .setPopUpTo(getRootNavId(), false).build()
                findNavController.navigationStackPrintln()
                findNavController.navigate(getMainNavId(), null, opt)
                findNavController.navigationStackPrintln()
                baseVm?.enableChangeFrom = true
            }
        } else {
            findNavController.navigateUp()
        }
        ContextProvider.mNavIdProvider?.showHideMainMenu(View.VISIBLE)
    }

    private var isShow = false
    fun reqCertifyState(
        view: View,
        isShowDialog: Boolean = false,
        isShowTip: Boolean = false,
        call: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val reqResult = reqApi(
                UserInfoService::class.java,
                { queryCreditState() },
                isShowLoading = isShowDialog
            )
            reqResult.data?.run {
                if (!TextUtil.isExistEmpty(
                        bankCardState,
                        kycState,
                        idState,
                        userState,
                        supplementState
                    )
                ) {
                    val employInt = this.supplementState.toInt()
                    val bankCardStateInt = this.bankCardState.toInt()
                    val kycStateInt = this.kycState.toInt()
                    val idStateInt = this.idState.toInt()
                    cacheCertifyState(employInt, bankCardStateInt)
                    val mobileH5State: Int = if (TextUtil.isEmpty(this.operatorState)) {
                        10
                    } else {
                        this.operatorState.toInt()
                    }

                    isCreditPersonStatus = (idStateInt == Constants.NUMBER_30)
                    isCreditKycStatus = (kycStateInt == Constants.NUMBER_30)
                    isCreditSupplementStatus = (employInt == Constants.NUMBER_30)
                    isCreditBankStatus = (bankCardStateInt == Constants.NUMBER_30)
                    isCreditMobileEnable =
                        mobileH5State == Constants.NUMBER_10 || mobileH5State == Constants.NUMBER_40

                    creditPersonStatus.set(idStateInt)
                    creditKycStatus.set(kycStateInt)
                    creditEmployStatus.set(employInt)
                    creditBankStatus.set(bankCardStateInt)
                    creditMobileStatus.set(mobileH5State)

                    val visible = if (operatorDeplay == 1) View.VISIBLE else View.GONE
                    call.invoke()
                    visibleModelOpt.set(visible)
                    if (isShowTip) {
                        val count =
                            getAuthFinishCount(employInt, bankCardStateInt, kycStateInt, idStateInt)
                        if (count == Constants.NUMBER_3) {
                            isShow = true
                        } else {
                            if (count == 4 && isShow) {
                                isShow = false
                                val act = ActivityStack.currentActivity()
                                if (act != null && !act.isDestroyed && !act.isFinishing)
                                    AuthDialog.showCreditFinishTipDialog(act) {
                                        back(view)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAuthFinishCount(
        creditPersonStatus: Int,
        creditKycStatus: Int,
        creditEmployStatus: Int,
        creditBankStatus: Int
    ): Int {
        var countTrue = 0
        if (creditPersonStatus == Constants.NUMBER_30) countTrue++
        if (creditKycStatus == Constants.NUMBER_30) countTrue++
        if (creditEmployStatus == Constants.NUMBER_30) countTrue++
        if (creditBankStatus == Constants.NUMBER_30) countTrue++
        return countTrue
    }

    private fun cacheCertifyState(supplementState: Int, bankCardState: Int) {
        CommonDataModel.RUNTIME_USER_SUPPLEMENT_INFO_STATE =
            (supplementState != Constants.NUMBER_30)
        CommonDataModel.RUNTIME_USER_BANK_INFO_STATE = (bankCardState != Constants.NUMBER_30)
    }

    private fun checkNetwork2Action(): Boolean {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.app_access_server_error)
            return false
        }
        return true
    }

    fun gotoKcyPage(view: View) {
        if (!checkNetwork2Action()) {
            return
        }
        reqCertifyState(view, isShowDialog = true, isShowTip = false) {
            val second =
                if (Constants.NUMBER_20 == creditKycStatus.get()) true else isCreditKycStatus
            val values = bundleOf(Pair(Constants.STATE, second))
            Navigation.findNavController(view).navigate(
                R.id.loan_info_model_auth_center_kyc,
                values
            ) //20 & 30 -> finish , 10 & 40 no finish
        }
    }

    fun gotoProfileInfoPage(view: View) {
        if (isTEST) {
            val param = bundleOf(Pair(Constants.STATE, isCreditPersonStatus))
            Navigation.findNavController(view).navigate(R.id.loan_info_model_auth_center_profile_info, param)
        } else {
            if (!checkNetwork2Action()) {
                return
            }
            reqCertifyState(view, isShowDialog = true, isShowTip = false, call = {
                val context = view.context
                if (isCreditPersonStatus || isCreditKycStatus) {
                    val param = bundleOf(Pair(Constants.STATE, isCreditPersonStatus))
                    Navigation.findNavController(view)
                        .navigate(R.id.loan_info_model_auth_center_profile_info, param)
                } else {
                    var str = ""
                    val creditValue = creditKycStatus.get()
                    if (creditValue == Constants.NUMBER_20) {
                        str = context.getString(R.string.loan_info_mall_kyc_loading)
                    } else if (creditValue == Constants.NUMBER_10 || creditValue == Constants.NUMBER_40) {
                        str = context.getString(R.string.loan_info_credit_kyc_info)
                    }
                    showTip(context, str)
                }
            })
        }
    }

    fun gotoSuppleInfoPage(view: View) {
        if (isTEST) {
            val param = bundleOf(Pair(Constants.STATE, isCreditSupplementStatus))
            Navigation.findNavController(view).navigate(R.id.loan_info_model_auth_center_supply_info, param)
        }else {
            if (!checkNetwork2Action()) {
                return
            }
            reqCertifyState(view, isShowDialog = true) {
                if (isCreditSupplementStatus || isCreditPersonStatus && isCreditKycStatus) {
                    val param = bundleOf(Pair(Constants.STATE, isCreditSupplementStatus))
                    Navigation.findNavController(view)
                        .navigate(R.id.loan_info_model_auth_center_supply_info, param)
                } else {
                    var tip = ""
                    if (!isCreditKycStatus) {
                        if (Constants.NUMBER_20 == creditKycStatus.get()) {
                            tip = ContextProvider.getString(R.string.loan_info_mall_kyc_loading)
                        } else if (Constants.NUMBER_10 == creditKycStatus.get() || Constants.NUMBER_40 == creditKycStatus.get()) {
                            tip = ContextProvider.getString(R.string.loan_info_credit_kyc_info)
                        }
                    } else if (!isCreditPersonStatus) {
                        tip = ContextProvider.getString(R.string.loan_info_credit_person_info)
                    }
                    showTip(view.context, tip)
                }
            }
        }

    }

    fun gotoBankPage(view: View) {
        if (!checkNetwork2Action()) {
            return
        }
        reqCertifyState(view, isShowDialog = true) {
            if (isCreditBankStatus || isCreditKycStatus && isCreditPersonStatus && isCreditSupplementStatus) {
                val param = bundleOf(Pair(Constants.STATE, isCreditBankStatus))
                Navigation.findNavController(view)
                    .navigate(R.id.loan_info_model_auth_center_bank, param)
            } else {
                var tip = ""
                if (!isCreditKycStatus) {
                    if (Constants.NUMBER_20 == creditKycStatus.get()) {
                        tip = ContextProvider.getString(R.string.loan_info_mall_kyc_loading)
                    } else if (Constants.NUMBER_10 == creditKycStatus.get() || Constants.NUMBER_40 == creditKycStatus.get()) {
                        tip = ContextProvider.getString(R.string.loan_info_credit_kyc_info)
                    }
                } else if (!isCreditPersonStatus) {
                    tip = ContextProvider.getString(R.string.loan_info_credit_person_info)
                } else if (!isCreditSupplementStatus) {
                    tip = ContextProvider.getString(R.string.loan_info_credit_supplement_info)
                }
                showTip(view.context, tip)
            }
        }
    }

    fun reqMobileAuthState(view: View) {
        if (!checkNetwork2Action()) {
            return
        }
        val act = ActivityStack.currentActivity()
        if (act == null || act.isFinishing || act.isDestroyed) {
            return
        }
        reqCertifyState(view, isShowDialog = true) {
            if (isCreditMobileEnable) {
                viewModelScope.launch {
                    val reqMobileEnumRes = reqApi(UserInfoService::class.java, { queryListInfo() })
                    reqMobileEnumRes.data?.operType?.run end@{
                        if (this.isEmpty()) return@end
                        DialogUtils.showMobileAuthType(act, this) { reqMobileUrl(view, it) }
                    }
                }
            }
        }
    }

    private fun reqMobileUrl(view: View, type: Int): Unit {
        viewModelScope.launch {
            val mobile = reqApi(UserInfoService::class.java, {
                mobileCreditAuth(
                    type,
                    CommonDataModel.mTokenData?.mobile,
                    DeviceUtil.getDeviceId(ContextProvider.app)
                )
            })
            mobile.data?.run {
                CommonProvider.instance?.getWebViewNavId()?.let {
                    val bundle = bundleOf(Pair("link", it))
                    Navigation.findNavController(view).navigate(it, bundle)
                }
            }
        }
    }


    private fun showTip(context: Context, contentTxt: CharSequence) {
        BaseDialog(context).setData(contentTxt, "", context.getString(R.string.loan_go))
            .setCallback(object : BaseDialog.Callback {
                override fun confirm(d: Dialog?) {
                    super.confirm(d)
                    d?.dismiss()
                }

                override fun cancel(d: Dialog?) {
                    super.cancel(d)
                    d?.dismiss()
                }
            }).show()
    }



}