package com.hc.data.user

import com.hc.data.MenuData


class UserInfoRange {
    var salaryRange: List<UserType>? = null
    var education: List<UserType>? = null
    var occupation: List<UserType>? = null
    var gender: List<UserType>? = null
    var purpose: List<UserType>? = null
    var language: List<UserType>? = null
    var maritalStatus: List<UserType>? = null
    var companyIndustry: List<UserType>? = null
    var staffSize: List<UserType>? = null
    var levelOfPosition: List<UserType>? = null
     var operType: List<UserType>? = null //mobile certify

    var relatives: List<UserType>? = null
    var address: List<UserType>? = null
    var netBank: List<UserType>? = null

    var razorNetBank: List<UserType>? = null  // razorpay netbank
    var otherRelatives: List<UserType>? = null // 补充信息其他人联系方式
}

class UserInfo(
    var age: String,
    var backImg: String,
    var blackReason: String,
    var channelName: String,
    var continuouRepayNum: String,
    var createTime: String,
    var frontImg: String,
    var id: String,
    var idAddr: String,
    var idNo: String,
    var livingImg: String,
    var national: String,
    var panImg: String,
    var panNo: String,
    var phone: String,
    var platformId: String,
    var realName: String,
    var registerAddr: String,
    var registerCoordinate: String,
    var sex: String,
    var state: String,
    var updateTime: String,
    var userId: String,
    var aadhaarSign: Boolean = true // 是否认证 Aadhaar, true 显示
)


/**
 * 用户扩展信息(用户填写的个人信息)
 */
data class UserInfoExt(
    var colleagueMobile: String?,
    var colleagueName: String?,
    var createTime: String?,
    var currentPinCode: String?,
    //var email: String,
    var education: Int?,
    var educationStr: String?,
    var firstNamePan: String?,
    var id: String?,
    var isvalid: String?,
    var language: Int?,
    var languageStr: String?,
    var lastNamePan: String?,
    var marryState: Int?,
    var marryStateStr: String?,
    var middleNamePan: String?,

    var purpose: Int?,
    var purposeStr: String?,
    var relatives: Int?,
    var otherRelatives: Int?,
    var otherRelativesStr: String?,
    var relativesMobile: String?,
    var relativesName: String?,
    var relativesStr: String?,
    var sex: Int?,
    var sexStr: String?,
    var updateTime: String?,
    var userId: String?,
    var anotherPhone: String?,
    var facebook: String?,
    var whatsapp: String?,
    var state: Int?,
    var stateStr: String?,
    var city: Int?,
    var cityStr: String?,
    var village: String?,
    var currentAddress: String?,

    var emailCheck: Int = 0,
    var userWorkInfoModel: UserWorkInfo?
)

class UserInfoSub {
    var firstNamePan: String? = null
    var middleNamePan: String? = null
    var lastNamePan: String? = null
    var sex: Int? = null
    var language: Int? = null
    var education: Int? = null
    var marryState: Int? = null
    var purpose: Int? = null

    var facebook: String? = null
    var whatsapp: String? = null
    var anotherPhone: String? = null

    var info: String? = null

    var callRecords: String? = null

    var registerCoordinate: String? = null

    var companyName: String? = null
    var companyEmail: String? = null

    //    var education: Int? = null
    var companyTel: String? = null
    var workingSince: String? = null
    var occupation: Int? = null
    var salaryRange: Int? = null
    var officePinCode: String? = null
    var officeAdress: String? = null
    var emailCode: String? = null

    var userId: String? = null
    var companyIndustry: Int? = null
    var staffSize: Int? = null
    var jobNature: Int? = null
    var levelOfPosition: Int? = null

}

data class UserWorkInfo(
    var emailCheck: Int = 0,
    var userWorkInfo: UserWorkInfo?
)

class UserWorkInfoAll {
    var companyEmail: String? = null
    var companyName: String? = null
    var createTime: String? = null

    //var education: Int
    var companyTel: String? = null
    var educationStr: String? = null
    var id: String? = null
    var occupation: Int? = null
    var occupationStr: String? = null
    var salaryRange: Int? = null
    var salaryRangeStr: String? = null
    var updateTime: String? = null
    var userId: String? = null
    var workingSince: String? = null
    var companyIndustry: Int? = null
    var companyIndustryStr: String? = null
    var staffSize: Int? = null
    var staffSizeStr: String? = null
    var jobNature: Int? = null
    var jobNatureStr: String? = null
    var levelOfPosition: Int? = null
    var levelOfPositionStr: String? = null
}

data class UserAuthRes(var userCredit: Double, var userState: Int)

data class UserType(
    var jobs: List<UserType>? = null,
    var state: String,
    var info: String,
    var citys: List<UserType>? = null,
    //netbanking part
    var code: String = "",
    var name: String = ""
):MenuData(info,0,false)