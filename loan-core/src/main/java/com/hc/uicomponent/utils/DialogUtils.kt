package com.hc.uicomponent.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ToastUtils
import com.hc.uicomponent.R
import com.hc.uicomponent.provider.ContextProvider

object DialogUtils {

    interface IRequestCancelCall {
        fun cancel()
    }


    fun showPermissionDialog(
        activity: Activity,
        positiveTx: String = ContextProvider.app.getString(android.R.string.ok),
        negativeTx: String = ContextProvider.app.getString(android.R.string.cancel),
        deniedPassionTip: String,
        isCancelDialog: Boolean = true,
        isOpenLocationService: Boolean = false
    ): AlertDialog? {
        if (TextUtil.isEmpty(deniedPassionTip)) return null
        val msg = if (isOpenLocationService) deniedPassionTip else ContextProvider.app.getString(
            R.string.request_perssion_tip,
            deniedPassionTip
        )
        var builder = AlertDialog.Builder(activity).setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(positiveTx) { dialog, i ->
                try {
                    if (isOpenLocationService) {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        if (ContextProvider.app.packageManager.resolveActivity(
                                intent,
                                PackageManager.MATCH_DEFAULT_ONLY
                            ) != null
                        ) {
                            activity.startActivity(intent)
                        } else {
                            ToastUtils.showShort(
                                ContextProvider.getString(
                                    R.string.request_perssion_handle_open_location_service_tip,
                                    deniedPassionTip
                                )
                            )
                        }
                    } else {
                        // 去设置中设置权限
                        val info = activity.packageManager.getPackageInfo(activity.packageName, 0)

                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:" + info.packageName)
                        if (ContextProvider.app.packageManager.resolveActivity(
                                intent,
                                PackageManager.MATCH_DEFAULT_ONLY
                            ) != null
                        ) {
                            activity.startActivity(intent)
                        } else {
                            ToastUtils.showShort(
                                ContextProvider.app.getString(
                                    R.string.request_perssion_handle_tip,
                                    deniedPassionTip
                                )
                            )
                        }
                    }
                    dialog?.dismiss()
                } catch (e: Exception) {
                    dialog?.dismiss()
                    e.printStackTrace()
                }
            }
        if (isCancelDialog) {
            builder.setNegativeButton(negativeTx) { dialog, _ -> dialog.dismiss() }
        }
        val alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }





}