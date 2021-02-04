package com.hc.main.exec

import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.common.CommonDataModel
import com.hc.main.constants.UserFilePath
import com.hc.main.vm.MainViewModel
import com.hc.uicomponent.call.IExceptionHandling
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.mmkv
import com.test.app.R
import com.tools.network.callback.AppResultCode
import com.tools.network.entity.HttpResult
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.atomic.AtomicBoolean

object ExceptionHandling : IExceptionHandling {
    var isProcessLogout =  AtomicBoolean(false)
    override fun onSystemInterceptApiError(result: HttpResult<*>): Boolean {
        return when (result.code) {
            AppResultCode.OFFSIDE_LOGIN_EXIT,
            AppResultCode.TOKEN_REFRESH_TIMEOUT,
            AppResultCode.TOKEN_NOT_UNIQUE,
            AppResultCode.SIGN_NOT_EXIST -> {
                if(isProcessLogout.compareAndSet(false,true)) {
                    ToastUtils.showShort("Login has expired, please log in again!")
                    ContextProvider.mNavIdProvider?.getToLogInActionId()?.let { target ->
                        ActivityStack.currentActivity()?.let {
                            CommonDataModel.clearUser(ContextProvider.app)
                            Navigation.findNavController(it, R.id.nav_host_fragment).navigate(target)
                            MainViewModel.isVisibleNavigationBottom.set(View.GONE)
                        }
                    }
                }
                true
            }
            else -> false
        }
    }

}