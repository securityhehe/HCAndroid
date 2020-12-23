package com.hc.login.data

data class PhoneHistory(
    /** 是否存在 10 不存在  */
    val isExists: String? = null,

    /** 编号  */
    val code: String? = null,

    /** 名称  */
    val name: String? = null,

    /** 值  */
    val value: String? = null,
    val authSignData: String? = null,

    /** 认证数量  */
    val init: String? = null,

    /** 针对新用户（即：未注册的用户）使用此URL跳转到第三方H5页面（此页面由WebView加载处理）  */
    var url: String? = null
)
