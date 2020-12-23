package com.hc.uicomponent.utils;

import android.content.Context
import android.os.Bundle
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.hc.uicomponent.config.CUR_RUNTIME_EVN
import java.math.BigDecimal
import java.util.*

/**
 * facebook 统计
 */
object FacebookEventUtils{

    // 设置事件统计
    fun setIsFBDebugEnabled(isDebug: Boolean) {
        if (isDebug) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
        }
    }

    // Facebook
    fun setFacebookEvent(context: Context, event: String) {
        if (!CUR_RUNTIME_EVN) {
            val logger = AppEventsLogger.newLogger(context)
            logger.logEvent(event)
        }
    }

    fun logPurchase(context: Context, purchaseAmount: BigDecimal, currency: Currency, parameters: Bundle?) {
        if (!CUR_RUNTIME_EVN) {
            val logger = AppEventsLogger.newLogger(context)

            if (parameters == null) {
                logger.logPurchase(purchaseAmount, currency)
            } else {
                logger.logPurchase(purchaseAmount, currency, parameters)
            }
        }
    }

    val COMPLETE_REGISTRATION = "Complete Registration"  //产品注册成功
    val PURCHASE = "purchase" // 提交借贷申请成功
}
