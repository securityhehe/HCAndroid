package com.hc.load

import android.Manifest
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.hc.load.databinding.FragmentLoanInputMoneyLayoutBinding
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.base.BaseFragment
import com.hc.uicomponent.config.mustNeedPermission
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.DialogUtils
import com.hc.uicomponent.utils.FirseBaseEventUtils
import com.hc.uicomponent.utils.LocationUtils
import com.hc.uicomponent.utils.StatEventTypeName

open class PermissionBaseFragment<B : ViewBinding>(id:Int) : BaseFragment<B>(id),AndroidPermissions.PermissionCallbacks{
    //申请权限
    fun reqSmsPermissionAndGetNowMoney() {
        AndroidPermissions.requestPermissions(this, getString(R.string.login_permission_sms_desc), true, LoanMainFragment.READ_SMS_PERMISSION, Manifest.permission.READ_SMS)
    }

    override fun onAllPermissionGranted(requestCode: Int) {
        super.onAllPermissionGranted(requestCode)
        if (requestCode == LoanMainFragment.READ_SMS_PERMISSION) {
            //申请看看必须权限是否已经申请。
            if (necessaryPermissions(LoanMainFragment.BASE_PERMISSION)) {
                if (LocationUtils.isGoogleServiceAvailable(ContextProvider.app)) {
                    LocationUtils.getInstance().resetLocationUpdates(ContextProvider.app, null)
                }
                FirseBaseEventUtils.trackEvent(StatEventTypeName.LOAN_GET_NOW_CLICK)
                callPermissionOk()
            }
        }
    }

    open fun callPermissionOk(){

    }

    //判断是否有必要权限。
    private fun isHasGrantPermissionGet(): Boolean {
        return AndroidPermissions.hasPermissions(activity, *mustNeedPermission)
    }

    private var mReqPermissionDialog: AlertDialog? = null
    private fun necessaryPermissions(requestPermissionType: Int): Boolean {
        if (!isHasGrantPermissionGet()) {
            AndroidPermissions.requestPermissions(this, "", false, requestPermissionType, *mustNeedPermission)
            return false
        }
        if (!LocationUtils.isGoogleServiceAvailable(ContextProvider.app)) {
            if (!LocationUtils.isLocServiceEnable(ContextProvider.app)) {
                mReqPermissionDialog?.run {
                    if (this.isShowing) this.dismiss()
                }
                mReqPermissionDialog = DialogUtils.showPerssionDialog(
                    requireActivity(),
                    deniedPassionTip = ContextProvider.getString(R.string.request_perssion_open_location_service_tip),
                    isCancelDialog = false,
                    isOpenLocationService = true
                )
                return false
            }
            if (isHasGrantPermissionGet()) {
                LocationUtils.getInstance().resetGpsLocationUpdates(ContextProvider.app)
                return true
            }
        }
        return true
    }

}