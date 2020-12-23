package com.hc.data.common

import android.os.Environment
import com.google.gson.Gson
import com.hc.data.utils.mmkv
import com.hc.uicomponent.config.CACHE_USER_INFO_KEY
import frame.utils.StringUtil
import java.io.File

object CommonDataModel {
    var mLoggedIn: Boolean = false
    var mTokenData: TokenData? = null

    //KYC拍照时所使用以下变量
    var TEMP_PHOTO_PATH: String? = null
    var RELEASE_PHOTO_PATH: String? = null

    //几项认证状态 （即：true（未完成认证），false(已完成认证)）
    var RUNTIME_USER_SUPPLEMENT_INFO_STATE: Boolean = false
    var RUNTIME_USER_BANK_INFO_STATE: Boolean = false

    fun initData() {
        //路径初始化。
        initUserInfo()
    }

    fun saveUserInfo(user: TokenData) {
        cacheUserInfo(user)

        mmkv().encode(CACHE_USER_INFO_KEY, Gson().toJson(user))
    }

    //获取用户信息
    private fun initUserInfo() {
        var userStr = mmkv().decodeString(CACHE_USER_INFO_KEY, "")
        if (!StringUtil.isBank(userStr)) {
            val user = Gson().fromJson<TokenData>(userStr, TokenData::class.java)
            cacheUserInfo(user)
            mLoggedIn = !StringUtil.isBank(user.token)
        }
    }

    private fun cacheUserInfo(user: TokenData) {
        user.run {
            mTokenData = user
            mLoggedIn = !StringUtil.isBank(user.token)
        }
    }

    //清除用户信息
    fun clearUser() {
        mTokenData = null
        mLoggedIn = false
        mmkv().remove(CACHE_USER_INFO_KEY)
    }

    //检查是否登录
    fun checkLogin(): Boolean {
        return mLoggedIn
    }
}
