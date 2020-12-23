package com.hc.permission

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.hc.permission.param.PermissionRequest

/**
 * Permissions helper for [AppCompatActivity].
 */
internal class AppCompatActivityPermissionsHelper(host: AppCompatActivity ) : RationalePermissionsHelper<AppCompatActivity>(host) {

    override fun getSupportFragmentManager(): FragmentManager? {
        return host.supportFragmentManager
    }
    override fun directRequestPermissions(requestCode: Int, vararg perms: String) {
        ActivityCompat.requestPermissions(host, perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(host, perm)
    }

    override fun getContext(): Context {
        return host
    }

    override fun showRequestPermissionRationale(request: PermissionRequest) {

    }
}