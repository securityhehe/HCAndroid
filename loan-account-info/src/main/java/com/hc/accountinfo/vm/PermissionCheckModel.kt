package com.hc.accountinfo.vm

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.hc.accountinfo.R
import com.hc.permission.AndroidPermissions
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.utils.DialogUtils
import com.hc.uicomponent.utils.LocationUtils

class PermissionCheckModel {

    companion object {
        const val PERMISSION_ALL_VIEW_CLICK_TYPE = 10
    }
    private val mMustPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS
    )
    val isGoogleServiceAvail = LocationUtils.isGoogleServiceAvailable(ContextProvider.app)
    var mAlertDialog: AlertDialog? = null

    fun checkMustPermissions(fragment: Fragment): Boolean {
        val context = ContextProvider.app
        if (!(AndroidPermissions.hasPermissions(fragment.requireContext(), *mMustPermission))) {
            AndroidPermissions.requestPermissions(
                fragment,
                "",
                false,
                PERMISSION_ALL_VIEW_CLICK_TYPE,
                *mMustPermission
            )
            return false
        }
        if (!isGoogleServiceAvail) {
            mAlertDialog?.apply {
                if (this.isShowing) this.dismiss()
            }

            if (!LocationUtils.isLocServiceEnable(context)) {
                showTipsDialog(fragment)
                return false
            }
            if (isReqPermissionHasGet(context)) {
                LocationUtils.getInstance().startGps(context)
                return true
            }
        }
        return true
    }

    private fun showTipsDialog(fragment: Fragment) {
        val deniedPassionTip = ContextProvider.getString(R.string.request_perssion_open_location_service_tip)
        mAlertDialog = DialogUtils.showPermissionDialog(
            fragment.requireActivity(),
            deniedPassionTip = deniedPassionTip,
            isCancelDialog = false,
            isOpenLocationService = true
        )
    }

    private fun isReqPermissionHasGet(context: Context): Boolean {
        val isOpenLocationService = LocationUtils.isLocServiceEnable(context)
        val hasPermissions =
            AndroidPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val isResult = isOpenLocationService && hasPermissions
        return isResult && (isResult && isOpenLocationService)
    }
}