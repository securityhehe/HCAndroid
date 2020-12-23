package com.hc.main.exec

import com.blankj.utilcode.util.ToastUtils
import com.hc.main.constants.UserFilePath
import com.hc.uicomponent.call.IExceptionHandling
import com.hc.uicomponent.utils.mmkv
import com.tools.network.callback.AppResultCode
import com.tools.network.entity.HttpResult

object ExceptionHandling :IExceptionHandling {
   override fun onSystemInterceptApiError(result: HttpResult<*>) :Boolean {
       return  when (result.code) {
            AppResultCode.OFFSIDE_LOGIN_EXIT,
            AppResultCode.TOKEN_REFRESH_TIMEOUT,
            AppResultCode.TOKEN_NOT_UNIQUE,
            AppResultCode.SIGN_NOT_EXIST -> {
                ToastUtils.showShort("Login has expired, please log in again!")
                mmkv().remove(UserFilePath.userInfo)
                true
            }
            else -> false
        }
    }
}