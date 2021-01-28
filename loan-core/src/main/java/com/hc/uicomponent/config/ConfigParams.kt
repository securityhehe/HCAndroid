package com.hc.uicomponent.config

import android.Manifest
import com.hc.uicomponent.BuildConfig
import com.hc.uicomponent.utils.getSDPath
import java.io.File

/**
 */
const val CUR_RUNTIME_EVN = BuildConfig.IS_APP_DEBUG

//保存键值
const val CACHE_USER_INFO_KEY = "CACHE_USER_INFO"//用户信息保存的key值
const val CACHE_APP_FIRST_START = "CACHE_APP_FIRST_START"//app是否第一次启动

const val STATE_OPEN_CHANNEL = "state_open_channel"//缓存统计到的打开渠道（是否来源于GP）
const val IS_HAS_STAT_OPEN_AMOUNT = "isHasStatOpenAmount"//是否已经统计过打开量

const val CACHE_LOGIN_PHONE = "cache_login_phone" //缓存上次登录的手机号码

/** SP文件配置  */
const val SP_NAME = "basic_params"
const val SP_KEY_PREFIX = "ucash"
val SP_SEED_KEY = "ucash".toByteArray()

const val SIGNA = "signMsg"
const val TS = "ts"
const val MOBILE_TYPE = "mobileType"
const val VERSION_NUMBER = "versionNumber"

/** ios传“1”，安卓传“2”  */
const val ANDROID_MOBILE_TYPE = "2"

const val TOKEN = "token"

const val USER_ID = "userId"
const val USER_AGENT = "User-Agent"
const val USER_LANG = "lang"

/** LOAN API域名地址  */
private val LOAN_API_URL = if (CUR_RUNTIME_EVN) {
    if (BuildConfig.IS_TEST_ENV) BuildConfig.IS_TEST_URL else "http://test-api.trustrock.in/"
} else "https://kushcash-api.trustrock.in/"
private const val URI_PATH = "/api/"

/** 请求地址  */
val LOAN_URI: String = LOAN_API_URL + URI_PATH

/** H5域名地址  */
private val URI_AUTHORITY_WEB_RELEASE =
    if (CUR_RUNTIME_EVN) "http://test-web.trustrock.in/" else "https://kushcash-web.trustrock.in/"

/** 注册协议  */
val REGISTER_URL: String = "$URI_AUTHORITY_WEB_RELEASE/web/protocol_regit.html"

/** 贷款协议  */
val LOAN_AGREEMENT_URL: String = "$URI_AUTHORITY_WEB_RELEASE/web/protocol_borrow.html"

/** 贷款信  */
val LOAN_LETTER_URL: String = "$URI_AUTHORITY_WEB_RELEASE/web/LoanLetter ananshri.html"

/** 隐私协议  */
val PRIVACY_URL: String = "$URI_AUTHORITY_WEB_RELEASE/web/protocol_privacy.html"

/** 联系我们链接  */
val COSTACT_US_URL: String = "$URI_AUTHORITY_WEB_RELEASE/web/contact.html?lang=%1\$s"

/** 关于我们  */
val ABOUT_US: String = "$URI_AUTHORITY_WEB_RELEASE/web/about.html?lang=%1\$s"

/** 问卷调查  */
val QUESTIONNAIRE_SURVE: String = "$URI_AUTHORITY_WEB_RELEASE/web/questionnaire.html?orderId=%1\$s"

/** 是否打开更新开关  */
const val isOpenUpdateCall = true

/** 是否是私服包  */
const val isPrivatePackage = BuildConfig.isPrivatePackage

/** 是否统计渠道  */
const val isOpenFireBase = false

const val DIALOG_UPDATE_VERSION_TAG = "version update tag"


/** 根路径  */
val ROOT_PATH: String = getSDPath() + "${File.separator}eCommerce${File.separator}"
const val PHOTO = "photo"
/** 照片文件文件保存路径  */
val RELEASE_PHOTO_PATH_APP = "$ROOT_PATH$PHOTO${File.separator}release${File.separator}"
val TEMP_PHOTO_PATH_FOR_APP = "$ROOT_PATH$PHOTO${File.separator}temp${File.separator}"
//保存签名文件
val SAVE_PHOTO_PATH_FOR_SIGN: String = "$ROOT_PATH$PHOTO${File.separator}sign${File.separator}"
const val SIGN_FILE_NAME = "sign.png"


//Epoch
const val EPOCH_FAILED_TIMES = "EPOCH_FAILED_TIMES" //缓存 Epoch 失败次数
const val EPOCH_FAILED_TIMES_MAX = 5 //缓存 Epoch 失败的最大次数

object ConfigParams {
    val _PRIVACY_URL = PRIVACY_URL
}


val mustNeedPermission = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_SMS,
    Manifest.permission.READ_CONTACTS
)
