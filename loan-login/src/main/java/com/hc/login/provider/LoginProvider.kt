package com.hc.login.provider

import com.hc.data.common.TokenData

interface LoginProvider {
    companion object {
        var instance: LoginProvider? = null
    }
    fun saveUserInfo(user: TokenData)
}