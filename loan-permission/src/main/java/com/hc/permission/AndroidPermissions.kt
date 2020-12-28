/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hc.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.Size
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hc.permission.annotate.AfterPermissionGranted
import com.hc.permission.param.PermissionRequest
import java.lang.reflect.InvocationTargetException

object AndroidPermissions {
    private const val TAG = "AndroidPermissions"

    fun hasPermissions(context: Context?, @Size(min = 1) vararg perms: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default")
            return true
        }
        if (BuildConfig.DEBUG) {
            requireNotNull(context) { "Can't check permissions for null context" }
        }
        context?.let {
            for (perm in perms) {
                if (ContextCompat.checkSelfPermission(it, perm) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun requestPermissions(
        host: Activity,
        rationale: String,
        isShowRationale: Boolean,
        requestCode: Int,
        @Size(min = 1) vararg perms: String
    ) {
        requestPermissions(PermissionRequest.Builder(host, requestCode, isShowRationale, *perms).setRationale(rationale).build()
        )
    }

    fun requestPermissions(
        host: Fragment,
        rationale: String,
        isShowRationale: Boolean,
        requestCode: Int,
        @Size(min = 1) vararg perms: String
    ) {
        requestPermissions(PermissionRequest.Builder(host, requestCode, isShowRationale, *perms).setRationale(rationale).build())
    }

    fun requestPermissions(request: PermissionRequest) {
        val help = request.helper
        val context = help.getContext()
        context ?: return
        val host = help.host ?: return
        if (hasPermissions(context, *request.perms)) {
            notifyAlreadyHasPermissions(host, request.requestCode, *request.perms)
            return
        }
        request.helper.requestPermissions(request)
    }


    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        vararg receivers: Any
    ) {
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        for (i in permissions.indices) {
            val perm = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm)
            } else {
                denied.add(perm)
            }
        }
        for (obj in receivers) {
            if (granted.isNotEmpty()) {
                if (obj is PermissionCallbacks) {
                    obj.onPermissionsGranted(requestCode, granted.toTypedArray())
                }
            }

            if (denied.isNotEmpty()) {
                if (obj is PermissionCallbacks) {
                    obj.onPermissionsDenied(requestCode, denied.toTypedArray())
                }
            }

            if (granted.isNotEmpty() && denied.isEmpty()) {
                if (obj is PermissionCallbacks) {
                    obj.onAllPermissionGranted(requestCode)
                }
            }

            if (obj is PermissionCallbacks) {
                obj.onDoneRequestPermission(requestCode)
            }
        }

    }

    private fun allPermissionGranted(obj: Any, requestCode: Int) {

    }

    fun somePermissionPermanentlyDenied(
        host: Activity,
        @Size(min = 1) vararg deniedPermissions: String
    ): Boolean {
        return BasePermissionHelper.newInstance(host).morePermissionPermanentlyDenied(deniedPermissions)
    }


    fun somePermissionPermanentlyDenied(
        host: Fragment,
        @Size(min = 1) vararg deniedPermissions: String
    ): Boolean {
        return BasePermissionHelper.newInstance(host).morePermissionPermanentlyDenied(deniedPermissions)
    }


    fun permissionPermanentlyDenied(
        host: Activity,
        deniedPermission: String
    ): Boolean {
        return BasePermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission)
    }


    fun permissionPermanentlyDenied(
        host: Fragment,
        deniedPermission: String
    ): Boolean {
        return BasePermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission)
    }


    fun somePermissionDenied(host: Activity, vararg perms: String): Boolean {
        return BasePermissionHelper.newInstance(host).morePermissionDenied(*perms)
    }

    fun somePermissionDenied(host: Fragment, vararg perms: String): Boolean {
        return BasePermissionHelper.newInstance(host).morePermissionDenied(*perms)
    }

    private fun notifyAlreadyHasPermissions(
        obj: Any,
        requestCode: Int,
        @Size(min = 1) vararg perms: String
    ) {
        val grantResults = IntArray(perms.size)
        for (i in perms.indices) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED
        }
        onRequestPermissionsResult(requestCode, perms, grantResults, obj)
    }


    interface PermissionCallbacks :
        ActivityCompat.OnRequestPermissionsResultCallback {
        fun onPermissionsGranted(
            requestCode: Int,
            perms: Array<out String>
        ) {

        }

        fun onPermissionsDenied(requestCode: Int, perms: Array<out String>) {

        }

        fun onDoneRequestPermission(requestCode: Int) {

        }

        fun onAllPermissionGranted(requestCode: Int) {

        }
    }

    interface RationaleCallbacks {
        fun onRationaleAccepted(requestCode: Int)
        fun onRationaleDenied(requestCode: Int)
    }
}