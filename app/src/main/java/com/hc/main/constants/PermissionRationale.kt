package com.hc.main.constants

import android.Manifest
import android.content.Context
import android.content.res.Resources
import com.test.app.R

object PermissionRationale {

    fun groupPermissionTip(context: Context, mRationalePermissions: Array<out String>? ): String? {
        try {
            if (mRationalePermissions == null || mRationalePermissions.isEmpty()) {
                return ""
            }
            val buffer = StringBuffer()
            for (permission in mRationalePermissions) {
                when {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_sd_write)).append("、")
                    }
                    Manifest.permission.ACCESS_FINE_LOCATION == permission || Manifest.permission.ACCESS_COARSE_LOCATION == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_location)).append("、")
                    }
                    Manifest.permission.READ_CONTACTS == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_contact_person)).append("、")
                    }
                    Manifest.permission.READ_PHONE_STATE == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_device_info)).append("、")
                    }
                    Manifest.permission.CAMERA == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_take_photo)).append("、")
                    }
                    Manifest.permission.CALL_PHONE == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_call_phone)).append("、")
                    }
                    Manifest.permission.READ_SMS == permission -> {
                        buffer.append(context.resources.getString(R.string.permission_sms_info)).append("、")
                    }
                }
            }
            return if (buffer.toString().trim { it <= ' ' }.isEmpty()) {
                ""
            } else{
                buffer.toString().substring(0, buffer.toString().length - 1)
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        return ""
    }


}