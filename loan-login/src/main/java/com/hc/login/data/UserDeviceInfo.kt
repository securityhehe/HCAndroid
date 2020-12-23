package com.hc.login.data

import java.util.*

data class UserDeviceInfo(
    /** 手机号  */
    var loginName: String,

    /** 设备指纹  */
    var blackBox: String,

    /** 短信登录验证码  */
    var smsCode: String,

    /** 渠道编码非必填  */
    var channelCode: String,

    /** 注册经纬度  */
    var registerCoordinate: String,

    /** 注册地址  */
    var registerAddr: String,

    /** 注册客户端 android 非必填  */
    var regClient: String = "android",

    /** 当前系统设备语言  */
    var userLang: String? = Locale.getDefault().language,

    /** 设备ID  */
    var phoneMark: String,

    /** appsflyerId  */
    var appsflyerId: String

)