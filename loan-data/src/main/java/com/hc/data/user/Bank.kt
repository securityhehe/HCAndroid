package com.hc.data.user

data class UserBankRec(
    var bankCard: BankCard?,
    var bankSubCards: List<BankSubCard>
)

data class ConfirmIfSign(
    var needConfirmSign: Boolean
)

data class BankSubCard(
    var accountAddress: String,
    var accountUser: String,
    var agreementPath: String,
    var bankName: String,
    var bankNo: String,
    var bankPhone: String,
    var bindTime: String,
    var bindType: String,
    var createTime: String,
    var id: String,
    var idNo: String,
    var ifscCode: String,
    var isvalid: String,
    var merUserId: String,
    var platformId: String,
    var updateTime: String
)

data class BankCard(
    var agreeNo: String,
    var agreementPath: String,
    var bankName: String,
    var bankNo: String,
    var bankPhone: String,
    var bindTime: String,
    var createTime: String,
    var id: String,
    var ifscCode: String,
    var isvalid: String,
    var state: String,
    var updateTime: String,
    var userId: String
)

class BankDictList {
    var bankTypeList: List<BankInfo>? = null
}

class BankInfo {
    var code: String? = null
    var id: String? = null
    var value: String? = null
}

