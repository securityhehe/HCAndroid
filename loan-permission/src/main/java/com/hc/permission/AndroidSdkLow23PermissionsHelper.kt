package com.hc.permission

import android.app.Activity
import android.content.Context
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.hc.permission.param.PermissionRequest


internal class AndroidSdkLow23PermissionsHelper<T>(host: T) : BasePermissionHelper<T>(host) {

    override fun directRequestPermissions(requestCode: Int, vararg perms: String) {
        if(BuildConfig.DEBUG){
            throw IllegalStateException("Should never be requesting permissions on API < 23!")
        }else{
            println("Should never be requesting permissions on API < 23!")
        }
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return false
    }

    override fun getContext(): Context? {
        return when (host) {
            is Activity -> {
                host
            }
            is Fragment -> {
                (host as Fragment).context
            }
            else -> {
                throw IllegalStateException("Unknown host: $host")
            }
        }
    }

    override fun showRequestPermissionRationale(request: PermissionRequest) {
        if(BuildConfig.DEBUG){
            throw IllegalStateException("Should never be requesting permissions on API < 23!")
        }else{
            println("Should never be requesting permissions on API < 23!")
        }
    }
}