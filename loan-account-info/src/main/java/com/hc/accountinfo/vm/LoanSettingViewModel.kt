package com.hc.accountinfo.vm

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.hc.accountinfo.R
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.ABOUT_US
import com.hc.data.COSTACT_US_URL
import com.hc.data.common.CommonDataModel
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
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

    var kycId = ContextProvider.mNavIdProvider?.getKycNavId() ?: 0
    var webId =  ContextProvider.mNavIdProvider?.getWebViewNavId() ?: 0
    var aboutUs = String.format(ABOUT_US, "en")
    var contactUs =  String.format(COSTACT_US_URL, "en")

    var isChangedHead = false

    fun toSettingPage(view: View) {
        Navigation.findNavController(view)
            .navigate(ContextProvider.mNavIdProvider?.getSettingNavId() ?: 0)
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