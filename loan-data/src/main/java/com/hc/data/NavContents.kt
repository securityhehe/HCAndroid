package com.hc.data

object NavContents {
    const val state = "state"
    const val loanResult = "loan://action/auth/result?${state}=%d"
    const val loanKyc = "loan://loan/infoModel/Kyc?formKey=1"

    const val loanUserInfo = "loan://action/auth/userInfo?$state=%b"
    const val loanSupplement = "loan://action/auth/supplement?$state=%b"
    const val loanBank = "loan://action/auth/bank?is_change_bank=%b"
}