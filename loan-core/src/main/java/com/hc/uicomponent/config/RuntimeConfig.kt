package com.hc.uicomponent.config

/**
 * 配置全局运行参数
 */
object RuntimeConfig {
    var USER_TOKEN = "" //全局的token
        set(value) {
            field = value
            isHasLogin = value.trim().isNotEmpty() // 注意：在给Token赋值的时候，就确定下来isHasLogin的状态！
        }
    var USER_ID = "" //全局的userId
    var USER_PHONE = "" //全局的phone
    var isHasLogin = false //表示是否已经登录

    //KYC拍照时所使用以下变量
    lateinit var TEMP_PHOTO_PATH    : String
    lateinit var RELEASE_PHOTO_PATH : String

    //几项认证状态 （即：true（未完成认证），false(已完成认证)）
    var RUNTIME_USER_SUPPLEMENT_INFO_STATE :Boolean = false
    var RUNTIME_USER_BANK_INFO_STATE :Boolean       = false

}