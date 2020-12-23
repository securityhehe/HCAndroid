package com.hc.permission

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Permissions helper for [Fragment] from the support library.
 */
internal class SupportFragmentPermissionHelper(host: Fragment) : RationalePermissionsHelper<Fragment>(host) {

    override fun getSupportFragmentManager(): FragmentManager? {
        return host.childFragmentManager
    }

    override fun directRequestPermissions(requestCode: Int, vararg perms: String) {
        host.requestPermissions(perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return host.shouldShowRequestPermissionRationale(perm)
    }

    override fun getContext(): Context? {
        return host.activity
    }
}