package com.hc.main.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.hc.data.common.CommonDataModel
import com.hc.data.common.TokenData
import com.hc.login.provider.LoginProvider
import com.hc.main.constants.HttpParamProvider
import com.hc.main.constants.PermissionRationale
import com.hc.main.exec.ExceptionHandling
import com.hc.main.vm.MainViewModel
import com.hc.uicomponent.call.IExceptionHandling
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.provider.CommonProvider
import com.hc.uicomponent.utils.AppsFlyerUtils
import com.test.app.R
import com.tools.network.callback.IHttpParamProvider
import java.util.*

object LoanBase {
    private const val TAG = "LoanBase"
    var context: Context? = null
    private var isMainProcessInitialized = false

    fun initVideoChat(context: Context) {
        if (!isMainProcessInitialized ) {
            initVideoChatAllConfiguration(context)
        }
    }

    private fun initVideoChatAllConfiguration(context: Context) {
        this.context = context
        val packageName = context.packageName
        val currentProcessName = getCurrentProcessName(context)

        if (packageName == currentProcessName && !isMainProcessInitialized) {
            initMainProcess(context)
            Log.d(TAG, currentProcessName)
        }
    }

    private fun initMainProcess(context: Context) {
        isMainProcessInitialized = true
        val app = context.applicationContext as Application
        ContextProvider.init(app)
        CommonDataModel.initData(context)
        initHttpParam()
        initProvider()
        AppsFlyerUtils.initAppsFlyerSDK(app)

    }

    private fun initProvider() {
        LoginProvider.instance = object :LoginProvider{
            override fun saveUserInfo(user: TokenData) {
                context?.let{
                    CommonDataModel.saveUserInfo(it,user)
                }
            }
        }
        CommonProvider.instance = object :CommonProvider{
            override fun getWebViewNavId(): Int {
                return R.id.loan_main_model_web_view
            }
        }
        ContextProvider.mNavIdProvider = object : ContextProvider.INavContextProvider {
            override fun getKycNavId(): Int {
                return R.id.loan_info_model_container
            }

            override fun getMainNavId(): Int {
                return R.id.loan_main_model_main
            }

            override fun getRootNavId(): Int {
                return R.id.loan_main_model_container
            }

            override fun getInfoContainerId(): Int {
                return R.id.loan_info_model_container
            }

            override fun getWebViewNavId(): Int {
                return R.id.loan_main_model_web_view
            }

            override fun getSettingNavId(): Int {
                return R.id.action_navigation_account_to_navigation_settings
            }

            override fun showHideMainMenu(isVisible: Int) {
                MainViewModel.isVisibleNavigationBottom.set(isVisible)
            }


        }
        ContextProvider.mPermissionsRationaleProvider = object :ContextProvider.PermissionsRationaleProvider{
            override fun getRationaleText(context: Context, vararg permissions: String): String? {
               return PermissionRationale.groupPermissionTip(context,permissions)
            }
        }
    }


    private fun getCurrentProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        return processName
    }

    private fun initHttpParam() {
        IHttpParamProvider.instance = HttpParamProvider()
        IExceptionHandling.instance = ExceptionHandling

    }


}