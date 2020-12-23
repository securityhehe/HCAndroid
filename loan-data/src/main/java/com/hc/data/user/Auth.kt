package com.hc.data.user

import java.io.File

/** 10：未认证，20：认证中，30：认证通过 40: 失败**/
data class AuthInfo(
    var bankCardState: String,// 30
    var bankCardTime: String,// 16-12-2019 15:43:53
    var id: Int,// 31
    var idState: String,// 30
    var idTime: String,// 30-12-2019 18:36:31
    var kycState: String,// 30
    var kycTime: String,// 23-02-2020 11:44:40
    var operatorState: String,// 10
    var operatorTime: String,
    var userId: Int,// 32
    var userState: String,// 20
//		var workInfoState: String,// 10
//		var workInfoTime: String,// 16-01-2020 09:18:48
    var supplementState:String,//补充信息状态
    var supplementTime:String,
    var operatorDeplay:Int//1显示(默认) 0不显示
)

class KycUserInfo {
    var reqCode: String? = null
    var file : File? = null
    var deviceId:String? = null

}