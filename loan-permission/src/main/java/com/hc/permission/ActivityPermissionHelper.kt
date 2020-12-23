package com.hc.permission

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat
import com.hc.permission.param.PermissionRequest
import com.hc.permission.rationle.RationaleDialogFragment

/**
 * Permissions helper for [Activity].
 */
internal class ActivityPermissionHelper(host: Activity) : BasePermissionHelper<Activity>(host) {

    override fun directRequestPermissions(requestCode: Int, vararg perms: String) {
        ActivityCompat.requestPermissions(host, perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(host, perm)
    }

    override fun getContext(): Context? {
        return host
    }

    companion object {
        private const val TAG = "ActPermissionHelper"
    }

    override fun showRequestPermissionRationale(request: PermissionRequest) {
        val fm = host.fragmentManager
        val fragment = fm.findFragmentByTag(RationaleDialogFragment.TAG)
        if (fragment is RationaleDialogFragment) {
            Log.d(TAG, "Found existing fragment, not showing rationale.")
            return
        }
        RationaleDialogFragment.newInstance(request).showAllowingStateLoss(fm, RationaleDialogFragment.TAG)
    }
}