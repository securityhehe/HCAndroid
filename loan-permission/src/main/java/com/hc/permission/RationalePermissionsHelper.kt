package com.hc.permission

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.hc.permission.param.PermissionRequest
import com.hc.permission.rationle.RationaleDialogFragmentCompat

/**
 * Implementation of [BasePermissionHelper] for Support Library host classes.
 */
abstract class RationalePermissionsHelper<T>(host: T) : BasePermissionHelper<T>(host) {

    abstract fun getSupportFragmentManager(): FragmentManager?

    override fun showRequestPermissionRationale(request: PermissionRequest) {

        val fm = getSupportFragmentManager()
        fm?.let {
            val fragment = fm.findFragmentByTag(RationaleDialogFragmentCompat.TAG)
            if (fragment is RationaleDialogFragmentCompat) {
                Log.d(TAG, "Found existing fragment, not showing rationale.")
                return
            }
            RationaleDialogFragmentCompat
                .newInstance(request)
                .showAllowingStateLoss(fm, RationaleDialogFragmentCompat.TAG)
        }
    }

    companion object {
        private const val TAG = "BSPermissionsHelper"
    }
}