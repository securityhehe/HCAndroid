package com.hc.data.common

data class TokenData(
    var token: String ,
    /** 刷新token值  */
    var refreshToken: String,
    /** 用户ID  */
    var userId: String,
    /** 用户注册时使用的手机号  */
    var mobile: String,
    /** 用户注册时使用的验证码  */
    var smsCode: String?
)