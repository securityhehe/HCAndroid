package com.hc.uicomponent.utils;

import com.google.firebase.analytics.FirebaseAnalytics
import com.hc.uicomponent.BuildConfig
import com.hc.uicomponent.provider.ContextProvider

/**
 *
 * firebase 统计
 */
object FirseBaseEventUtils {

    private var mFirebaseAnalytics: FirebaseAnalytics =
        FirebaseAnalytics.getInstance(ContextProvider.app)

    interface IAppFlyerEvent {
        fun putEvents(eventMap: MutableMap<String, Any>)
        fun putEventName(): String
    }

    fun trackEvent(eventName: String, eventValue: MutableMap<String, Any>? = null) {
        try {
            if (!BuildConfig.IS_APP_DEBUG) {
                mFirebaseAnalytics.logEvent(eventName, null)
            }
        } catch (e: Exception) {
        }
    }
}

///**
// * 自定义事件参数
// */
//object StatEventParams {
//    const val P_VERSION_NAME = "p_version_code"
//}

/**
 * 定义AppFlyer的事件名称
 */
object StatEventTypeName {
    const val LOGIN_VIA_OTP = "Login_Via_OTP"
    const val OTP = "OTP"
    const val LOGIN_NEXT = "Login_Next"
    const val APP_UPGRADE = "App_Upgrade"
    const val FEED_BACK = "Feedback"
    const val PERSON_INFO_COMMIT = "Person_Info_Commit"
    const val SUPPLEMENTARY_INFO_COMMIT = "Supplementary_Info_Commit"
    const val BANK_INFO_COMMIT = "Bank_Info_Commit"
    const val KYC_INFO_COMMIT = "Kyc_Info_Commit"
    const val ORDER_COMMIT = "Order_Commit"
    const val SINGLE_PERIOD_PAY_CLICK = "Single_Period_Pay_Click"
    const val SINGLE_PERIOD_DELAY_PAY_CLICK = "Single_Periods_Delay_Pay_Click"
    const val Multiple_PERIOD_DELAY_PAY_CLICK = "Multiple_Periods_Delay_Pay_Click"
    const val MULTIPLE_PERIOD_PAY_CLICK = "Multiple_Periods_Pay_Click"
    const val JUMP_TO_THE_PAYMENT_PAGE_CLICK = "Payment_Page_Click"

    const val PROFILE_PAGE = "Profile"//访问认证主页面
    const val PERSON_INFO_PAGE = "person_info"//访问基本信息页面
    const val KYC_INFO_PAGE = "kyc_info"//访问KYC页面
    const val SUPPLEMENTARY_INFO_PAGE = "supplementary_info"//访问工作信息页面
    const val BANK_INFO_PAGE = "bank_info"//访问银行卡页面
    const val KYC_AADHAAR_FRONT_CLICK = "kyc_aadhaar_front"//点击“Aadhaar-front”
    const val KYC_AADHAAR_BACK_CLICK = "kyc_aadhaar_back"//点击“Aadhaar-back”
    const val KYC_PAN_FRONT_CLICK = "kyc_pan_front"//点击“PAN-front”
    const val KYC_PHOTO_CLICK = "kyc_photo"//点击“Your Photo”
    const val WORK_OCCUPATION_CLICK = "work_occupation"//触发“Occupation”选择项
    const val WORK_EMAIL_OTP_CLICK = "work_email_otp"//点击“Work Email--OTP”
    const val LOAN_GET_NOW_CLICK = "loan_get_now"//点击“Get now”
    const val LOAN_ORDER_CONFIRMATION_PAGE = "loan_order_confirmation"//访问订单提交页面

    const val KYC_ANTI_HACK_SUCCESS_NUMS = "face_anti_hack_succ_nums"//检验人脸真实度通过次数
    const val KYC_ANTI_HACK_FAILURE_NUMS = "face_anti_hack_fail_nums"//检验人脸真实度失败次数
    const val KYC_ANTI_HACK_API_CALL_FAILURE_NUMS = "face_anti_hack_api_call_fail_nums"//检验人脸真实度失败次数
}


