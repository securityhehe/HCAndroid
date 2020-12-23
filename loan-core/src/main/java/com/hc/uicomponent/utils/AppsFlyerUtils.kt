package com.hc.uicomponent.utils;

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore
import com.hc.uicomponent.config.APPS_FLYER_KEY
import com.hc.uicomponent.config.CUR_RUNTIME_EVN
import com.hc.uicomponent.provider.ContextProvider


object AppsFlyerUtils {
    const val APPSFLYER_EVENT_COMPLETE_REGISTRATION = "af_complete_registration"
    const val APPSFLYER_EVENT_PURCHASE = "submit_loan_application"

    /**
     * 如果在调用 startTracking 之前调用 setCustomerUserId，则 Customer User ID将出现在安装和应用内事件的原始数据报告中。
     * 如果在此之后设置，则“Customer User ID”仅关联该设置之后记录的事件。
     * 可以设置延迟初始化
     */
    fun setCustomerUserId(customerId: String) {
        if (!CUR_RUNTIME_EVN) {
            AppsFlyerLib.getInstance().setCustomerUserId(customerId)
        }
        /* if (AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID).isNullOrBlank()) {
         }*/
    }

    fun setAppsFlyerEvent(event: String) {
        if (!CUR_RUNTIME_EVN) {
            AppsFlyerLib.getInstance().trackEvent(ContextProvider.app, event, null);
        }

        /* val eventValue: MutableMap<String, Any> = HashMap()
         eventValue["loan_id"] = "1735102"
         eventValue["loam_amount"] = 1000
         eventValue["loan_type"] = "3 months"
         AppsFlyerLib.getInstance().trackEvent(MmContextHolder.getContext(), "submit_loan_application", null)*/

    }

    public fun initAppsFlyerSDK(applicationContext:Application) {
        val conversionDataListener  = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        Log.i(AppsFlyerLibCore.LOG_TAG, "conversion_attribute:  ${it.key} = ${it.value}")
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                Log.e(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    Log.d(AppsFlyerLibCore.LOG_TAG, "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(APPS_FLYER_KEY, conversionDataListener, applicationContext)
        AppsFlyerLib.getInstance().startTracking(applicationContext)
    }

}