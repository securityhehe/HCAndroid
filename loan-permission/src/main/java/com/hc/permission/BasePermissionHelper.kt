package com.hc.permission

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hc.permission.param.PermissionRequest


abstract class BasePermissionHelper<T>(val host: T) {

    abstract fun getContext(): Context?

    private fun shouldShowRationale(vararg perms: String): Boolean {
        for (perm in perms) {
            if (shouldShowRequestPermissionRationale(perm)) {
                return true
            }
        }
        return false
    }

    fun requestPermissions(request:PermissionRequest) {
        if (request.isShowRationale && shouldShowRationale(*request.perms)) {
            showRequestPermissionRationale(request )
        } else {
            directRequestPermissions(request.requestCode, *request.perms)
        }
    }

    fun morePermissionPermanentlyDenied(perms: Array<out String>): Boolean {
        for (deniedPermission in perms) {
            if (permissionPermanentlyDenied(deniedPermission)) {
                return true
            }
        }
        return false
    }

    fun permissionPermanentlyDenied(perms: String): Boolean {
        return !shouldShowRequestPermissionRationale(perms)
    }

    fun morePermissionDenied(vararg perms: String): Boolean {
        return shouldShowRationale(*perms)
    }

    abstract fun directRequestPermissions(requestCode: Int, vararg perms: String)

    /**
     *  sdk 版本>=23 可以直接查看权限请求是否要对用户进行权限说明。 true 代表需要， false，不需要。
     *  底版本则不行。
     */
    abstract fun shouldShowRequestPermissionRationale(perm: String): Boolean

    /**
     * 需要的画，app需要实现给方法，给用户解析为什么需要这个权限。
     */
    abstract fun showRequestPermissionRationale(request: PermissionRequest)


    companion object {
        @JvmStatic
        fun newInstance(host: Activity): BasePermissionHelper<out Activity>{
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return AndroidSdkLow23PermissionsHelper(host)
            }
            return if (host is AppCompatActivity) {
                AppCompatActivityPermissionsHelper(host)
            } else {
                ActivityPermissionHelper(host)
            }
        }

        @JvmStatic
        fun newInstance(host: Fragment): BasePermissionHelper<out Fragment> {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                AndroidSdkLow23PermissionsHelper(host)
            } else SupportFragmentPermissionHelper(host)
        }
    }

}