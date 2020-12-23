package com.hc.permission.rationle

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import com.hc.permission.AndroidPermissions.PermissionCallbacks
import com.hc.permission.AndroidPermissions.RationaleCallbacks
import com.hc.permission.BasePermissionHelper.Companion.newInstance
import java.util.*

/**
 */
internal class RationaleDialogClickListener : DialogInterface.OnClickListener {
    private var mHost: Any?
    private var mConfig: RationaleDialogConfig
    private var mCallbacks: PermissionCallbacks?
    private var mRationaleCallbacks: RationaleCallbacks?

    constructor(
        compatDialogFragment: RationaleDialogFragmentCompat,
        config: RationaleDialogConfig,
        callbacks: PermissionCallbacks?,
        rationaleCallbacks: RationaleCallbacks?
    ) {
        mHost = if (compatDialogFragment.parentFragment != null) compatDialogFragment.parentFragment else compatDialogFragment.activity
        mConfig = config
        mCallbacks = callbacks
        mRationaleCallbacks = rationaleCallbacks
    }

    constructor(
        dialogFragment: RationaleDialogFragment,
        config: RationaleDialogConfig,
        callbacks: PermissionCallbacks?,
        dialogCallback: RationaleCallbacks?
    ) {
        mHost = dialogFragment.activity
        mConfig = config
        mCallbacks = callbacks
        mRationaleCallbacks = dialogCallback
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        val requestCode = mConfig.requestCode
        if (which == Dialog.BUTTON_POSITIVE) {
            val permissions = mConfig.permissions
            mRationaleCallbacks?.onRationaleAccepted(requestCode)
            when (mHost) {
                is Fragment -> {
                    permissions?.let {
                        newInstance((mHost as Fragment?)!!).directRequestPermissions(requestCode, *permissions)
                    }
                }
                is Activity -> {
                    permissions?.let {
                        newInstance((mHost as Activity?)!!).directRequestPermissions(requestCode, *permissions)
                    }
                }
                else -> {
                    throw RuntimeException("Host must be an Activity or Fragment!")
                }
            }
        } else {
            mRationaleCallbacks?.onRationaleDenied(requestCode)
            notifyPermissionDenied()
        }
    }

    private fun notifyPermissionDenied() {
        if (mCallbacks != null) {
            mConfig.permissions?.let {
                mCallbacks!!.onPermissionsDenied(mConfig.requestCode, it)
            }
        }
    }
}