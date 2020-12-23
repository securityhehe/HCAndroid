package com.wildma.idcardcamera.global

/**
 * 配置全局运行参数
 */
object RuntimeConfig {
    //KYC拍照时所使用以下变量
    lateinit var TEMP_PHOTO_PATH    : String
    lateinit var RELEASE_PHOTO_PATH : String

    //几项认证状态 （即：true（未完成认证），false(已完成认证)）
    var RUNTIME_USER_SUPPLEMENT_INFO_STATE :Boolean = false
    var RUNTIME_USER_BANK_INFO_STATE :Boolean       = false

}