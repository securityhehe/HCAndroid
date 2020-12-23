package com.hc.accountinfo.vm

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.hc.accountinfo.R
import com.hc.data.formPermissionPage
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.provider.navigationStackPrintln

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
        baseVm = ViewModelProvider(NavHostFragment.findNavController(fragment).getViewModelStoreOwner(R.id.loan_info_model_container)).get((BaseAuthCenterInfo::class.java))
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

}