package com.hc.permission.rationle

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.hc.permission.BuildConfig
import com.hc.permission.R

/**
 * Configuration for either [RationaleDialogFragment] or [RationaleDialogFragmentCompat].
 */


class RationaleDialogConfig(
    private var theme: Int = 0,
    var rationaleMsg: String? = "",
    var positiveButton: String? = "",
    var negativeButton: String? = "",
    var requestCode: Int = 0,
    var permissions: Array<out String>? = null
) {
    constructor(bundle: Bundle?) : this(
        bundle?.getInt(KEY_THEME)?:0
        , bundle?.getString(KEY_RATIONALE_MESSAGE)
        , bundle?.getString(KEY_POSITIVE_BUTTON)
        , bundle?.getString(KEY_NEGATIVE_BUTTON)
        , bundle?.getInt(KEY_REQUEST_CODE)?:0
        , bundle?.getStringArray(KEY_PERMISSIONS)
    )

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(KEY_POSITIVE_BUTTON, positiveButton)
        bundle.putString(KEY_NEGATIVE_BUTTON, negativeButton)
        bundle.putString(KEY_RATIONALE_MESSAGE, rationaleMsg)
        bundle.putInt(KEY_THEME, theme)
        bundle.putInt(KEY_REQUEST_CODE, requestCode)
        bundle.putStringArray(KEY_PERMISSIONS, permissions)
        return bundle
    }

    fun createSupportDialog(
        context: Context,
        listener: DialogInterface.OnClickListener?
    ): android.app.AlertDialog {
        assert(context != null) { "permission createSupportDialog error" }
        val ctx = context
        rationaleMsg ?: ctx.getString(R.string.android_rationale_ask)
        positiveButton ?: ctx.getString(android.R.string.ok)
        negativeButton ?: ctx.getString(android.R.string.cancel)
        val builder: android.app.AlertDialog.Builder = if (theme > 0) {
            android.app.AlertDialog.Builder(ctx, theme)
        } else {
            android.app.AlertDialog.Builder(ctx)
        }
        return builder.run {
            val dialog = setCancelable(false)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, listener)
                .setMessage(rationaleMsg)
                .create()
            dialog
        }
    }

    fun createFrameworkDialog(
        context: Context,
        listener: DialogInterface.OnClickListener?
    ): AlertDialog {
        if (BuildConfig.DEBUG) {
            assert(context != null) { "permission createFrameworkDialog error" }
        }
        val ctx = context
        rationaleMsg ?: ctx.getString(R.string.android_rationale_ask)
        positiveButton ?: ctx.getString(android.R.string.ok)
        negativeButton ?: ctx.getString(android.R.string.cancel)
        val builder = context.let {
            if (theme > 0) {
                AlertDialog.Builder(ctx, theme)
            } else {
                AlertDialog.Builder(ctx)
            }
        }

        return builder.run {
            val dialog = setCancelable(false)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, listener)
                .setMessage(rationaleMsg)
                .create()
            dialog
        }
    }

    companion object {
        private const val KEY_POSITIVE_BUTTON = "positiveButton"
        private const val KEY_NEGATIVE_BUTTON = "negativeButton"
        private const val KEY_RATIONALE_MESSAGE = "rationaleMsg"
        private const val KEY_THEME = "theme"
        private const val KEY_REQUEST_CODE = "requestCode"
        private const val KEY_PERMISSIONS = "permissions"
    }
}