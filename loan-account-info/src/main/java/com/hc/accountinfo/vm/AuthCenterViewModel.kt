package com.hc.accountinfo.vm

import android.view.View
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.hc.accountinfo.AuthDialog
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.common.CommonDataModel
import com.hc.data.formPermissionPage
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.provider.navigationStackPrintln
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.TextUtil
import kotlinx.coroutines.launch

class AuthCenterViewModel : BaseViewModel() {

    fun gotoKcyPage(view: View) {
        Navigation.findNavController(view).navigate(R.id.loan_info_model_auth_center_kyc)
    }

    fun gotoProfileInfoPage(view: View) {

        Navigation.findNavController(view).navigate(R.id.loan_info_model_auth_center_profile_info)
    }

    fun gotoSuppleInfoPage(view: View) {
        Navigation.findNavController(view).navigate(R.id.loan_info_model_auth_center_supply_info)
    }

    fun gotoBankPage(view: View) {
        Navigation.findNavController(view).navigate(R.id.loan_info_model_auth_center_bank)
    }

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
    }

    private var isShow = false
    fun reqCertifyState(view:View, isShowDialog: Boolean = false, isShowTip: Boolean = false) {
        viewModelScope.launch {
            val reqResult = reqApi(UserInfoService::class.java, { queryCreditState() }, isShowLoading = isShowDialog)
            reqResult.data?.run {
                if (!TextUtil.isExistEmpty(bankCardState, kycState, idState, userState, supplementState)) {
                    val employInt = this.supplementState.toInt()
                    val bankCardStateInt = this.bankCardState.toInt()
                    val kycStateInt = this.kycState.toInt()
                    val idStateInt = this.idState.toInt()
                    cacheCertifyState(employInt, bankCardStateInt)
                    var mobileH5State: Int = if (TextUtil.isEmpty(this.operatorState)) {
                        10
                    } else {
                        this.operatorState.toInt()
                    }
                    creditPersonStatus.set(idStateInt)
                    creditKycStatus.set(kycStateInt)
                    creditEmployStatus.set(employInt)
                    creditBankStatus.set(bankCardStateInt)
                    creditMobileStatus.set(mobileH5State)
                    val visible = if (operatorDeplay == 1) View.VISIBLE else View.GONE
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
                                    AuthDialog.showCreditFinishTipDialog(act){
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

    private fun cacheCertifyState(supplementState: Int, bankCardState: Int): Unit {
        CommonDataModel.RUNTIME_USER_SUPPLEMENT_INFO_STATE =
            (supplementState != Constants.NUMBER_30)
        CommonDataModel.RUNTIME_USER_BANK_INFO_STATE = (bankCardState != Constants.NUMBER_30)
    }

    var creditPersonStatus = ObservableInt(10)
    var creditKycStatus = ObservableInt(10)
    var creditEmployStatus = ObservableInt(10)
    var creditBankStatus = ObservableInt(10)
    var creditMobileStatus = ObservableInt(10)
    var visibleModelOpt = ObservableInt(View.GONE)
}