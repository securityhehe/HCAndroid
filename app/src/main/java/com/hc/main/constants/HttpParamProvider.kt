package com.hc.main.constants

import android.text.TextUtils
import com.hc.data.common.CommonDataModel
import com.hc.uicomponent.config.*
import com.hc.uicomponent.provider.ContextProvider
import com.tools.network.callback.IHttpParamProvider
import frame.utils.VersionUtil
import java.util.*

class HttpParamProvider : IHttpParamProvider {
    override val curRuntimeEvn: Boolean = CUR_RUNTIME_EVN
    override var loanUrl: String = LOAN_URI

    override fun addCommonHttpParam(map: TreeMap<String, String>) {
        map[MOBILE_TYPE] = ANDROID_MOBILE_TYPE
        map[VERSION_NUMBER] = VersionUtil.getVersion(ContextProvider.app)
    }

    override fun addSigParam(map: TreeMap<String, String>): String {
        map[USER_AGENT] = "Android"
        map[USER_LANG] = "en"

        val token: String =  CommonDataModel.mTokenData?.token?:""
        if (!TextUtils.isEmpty(token)) {
            map[TOKEN] = token
        }
        return SIGNA
    }

    override fun getKey(): String {
        return REQ_SERVICE_KEY
    }

    override fun getToken(): String {
        return  CommonDataModel.mTokenData?.token?:""
    }

    override fun getUserId(): String {
        return CommonDataModel.mTokenData?.userId?:"0"
    }

    override fun getUserIdKey(): String {
        return USER_ID
    }

}