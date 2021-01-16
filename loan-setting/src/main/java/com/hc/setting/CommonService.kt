package com.hc.setting

import com.hc.data.param.RequestParams
import com.tools.network.entity.HttpResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CommonService {
    /** app更新  */
    @POST("app/checkVersion.htm")
    fun checkUpdate(): Call<HttpResult<Map<String, Any>>>

    @FormUrlEncoded
    @POST("act/mine/opinion/submit.htm")
    fun adviceFeedBack(@Field(RequestParams.ADVICE_FEED_BACK) opinion: String?): Call<HttpResult<Any>>

}