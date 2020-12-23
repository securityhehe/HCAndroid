package com.hc.uicomponent


/**
 * config all params
 */
object BaseParams {
    /** 是否是debug模式  */
    val IS_DEBUG: Boolean = true

    /** API域名地址  */
    val URI_AUTHORITY = if (IS_DEBUG) "http://test-api.trustrock.in/" else "https://api.mecash.in/"

    /** H5域名地址  */
    private val URI_AUTHORITY_WEB_RELEASE =
        if (IS_DEBUG) "http://test-web.trustrock.in" else "https://web.trustrock.in"

    /** 模块名称("接口地址"会拼接在 URL 中最后的"/"之后，故URL必须以"/"结尾)  */
    private const val URI_PATH = "/api/"

    /** 请求地址  */
    val URI = URI_AUTHORITY + URI_PATH

    /** ios传“1”，安卓传“2”  */
    const val MOBILE_TYPE = "2"

    /** 根路径  */
    //   val ROOT_PATH: String = //AndroidUtil.getSDPath().toString() + "/eCommerce/"
    // const val PHOTO = "/photo/"

    //保存签名文件
    // val SAVE_PHOTO_PATH_FOR_SIGN = "$ROOT_PATH$PHOTO/sign/"
    //const val SIGN_FILE_NAME = "sign.png"
    //val TEMP_PHOTO_PATH_FOR_APP = "$ROOT_PATH$PHOTO/temp/"

    /** 照片文件文件保存路径  */
    // val RELEASE_PHOTO_PATH_APP =
    //    "$ROOT_PATH$PHOTO/release/"

    /** SP文件配置  */
    //   const val SP_NAME = "basic_params"
    //const val SP_KEY_PREFIX = "ucash"
    val SP_SEED_KEY = "ucash".toByteArray()

    /** 贷款协议  */
    val LOAN_AGREEMENT_URL =
        "$URI_AUTHORITY_WEB_RELEASE/web/protocol_borrow.html"

    /** 贷款信   */
    val LOAN_LETTER_URL =
        "$URI_AUTHORITY_WEB_RELEASE/web/LoanLetter ananshri.html"

    /** 隐私协议  */
    val PRIVACY_URL =
        "$URI_AUTHORITY_WEB_RELEASE/web/protocol_privacy.html"

    /** 注册协议  */
    val REGISTER_URL =
        "$URI_AUTHORITY_WEB_RELEASE/web/protocol_regit.html"

    /** 联系我们链接  */
    val COSTACT_US_URL =
        "$URI_AUTHORITY_WEB_RELEASE/web/contact.html?lang=%1\$s"

    /** 关于我们  */
    val ABOUT_US =
        "$URI_AUTHORITY_WEB_RELEASE/web/about.html?lang=%1\$s"

    /** 问卷调查  */
    val QUESTIONNAIRE_SURVE =
        "$URI_AUTHORITY_WEB_RELEASE/web/questionnaire.html?orderId=%1\$s"

    /** 默认渠道名  */
    // val APP_DEFAUL_CHANNEL: String =
    //    if (!TextUtil.isEmpty(PackerNg.getChannel(ContextHolder.getContext()))) PackerNg.getChannel(
    //        ContextHolder.getContext()
    //   ).trim() else BuildConfig.CHANNEL_NAME

    /** 是否打开更新开关  */
    const val isOpenUpdateCall = true

    /** 是否是私服包  */
    // val isPrivatePackage: Boolean = BuildConfig.isPrivatePackage
}