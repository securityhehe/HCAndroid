package com.hc.accountinfo.vm

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.hc.accountinfo.api.UserInfoService
import com.hc.data.common.CommonDataModel
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

    var phone: ObservableField<String> = ObservableField(StringFormat.phone7HideFormat(CommonDataModel.mTokenData?.mobile ?: ""))
    var gender: ObservableInt = ObservableInt(male)
    var kycId = ContextProvider.mNavIdProvider?.getKycNavId()?:0
    var isChangedHead = false

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