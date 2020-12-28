package com.hc.accountinfo.vm

import android.view.View
import androidx.core.os.bundleOf
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.ABOUT_US
import com.hc.data.COSTACT_US_URL
import com.hc.data.common.CommonDataModel
import com.hc.uicomponent.R
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.provider.ContextProvider
import frame.utils.StringFormat
import kotlinx.coroutines.launch

class LoanSettingViewModel : BaseViewModel() {
    companion object {
        const val male = 0
        const val female = 1
    }

    var phone: ObservableField<String> =
        ObservableField(StringFormat.phone7HideFormat(CommonDataModel.mTokenData?.mobile ?: ""))
    var gender: ObservableInt = ObservableInt(male)

    var profileID = ContextProvider.mNavIdProvider?.getKycNavId() ?: 0
    var webId = ContextProvider.mNavIdProvider?.getWebViewNavId() ?: 0
    var aboutUs = String.format(ABOUT_US, "en")
    var contactUs = String.format(COSTACT_US_URL, "en")

    var isChangedHead = false

    fun toSettingPage(view: View) {
        ContextProvider.mNavIdProvider?.showHideMainMenu(View.GONE)
        Navigation.findNavController(view)
            .navigate(ContextProvider.mNavIdProvider?.getSettingNavId() ?: 0)
    }

    fun toProfileHideMainMenu(view: View) {
        val opt = NavOptions.Builder()
            .setEnterAnim(R.anim.anim_right_to_middle)
            .setLaunchSingleTop(false)
            .setPopExitAnim(R.anim.anim_middle_to_right)
            .build()
        Navigation.findNavController(view).navigate(profileID, null, opt)
        ContextProvider.mNavIdProvider?.showHideMainMenu(View.GONE)
    }

    fun toUsHideMainMenu(view: View, title: String) {
        val opt = NavOptions.Builder()
            .setEnterAnim(R.anim.anim_right_to_middle)
            .setLaunchSingleTop(false)
            .setPopExitAnim(R.anim.anim_middle_to_right)
            .build()
        val bundle = bundleOf(Pair("title", title), Pair("link", aboutUs))
        Navigation.findNavController(view).navigate(webId, bundle, opt)
        ContextProvider.mNavIdProvider?.showHideMainMenu(View.GONE)
    }

    fun toContactUsHideMainMenu(view: View, title: String) {
        val opt = NavOptions.Builder()
            .setEnterAnim(R.anim.anim_right_to_middle)
            .setLaunchSingleTop(false)
            .setPopExitAnim(R.anim.anim_middle_to_right)
            .build()
        val bundle = bundleOf(Pair("title", title), Pair("link", contactUs))
        Navigation.findNavController(view).navigate(webId, bundle, opt)
        ContextProvider.mNavIdProvider?.showHideMainMenu(View.GONE)
    }


    fun changeHead() {
        if (isChangedHead) return
        viewModelScope.launch {
            var userInfo = reqApi(UserInfoService::class.java, { queryUserExtraInfo() })
            userInfo.data?.apply {
                gender.set(this.sex ?: 0)
                isChangedHead = true
            }
        }
    }
}