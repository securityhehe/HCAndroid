package com.hc.permission.param

import android.app.Activity
import androidx.annotation.RestrictTo
import androidx.annotation.Size
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.hc.permission.BasePermissionHelper
import com.hc.permission.BasePermissionHelper.Companion.newInstance
import com.hc.permission.R
import java.util.*

/**
 *
 * @see PermissionRequest.Builder
 */
class PermissionRequest(
    var helper: BasePermissionHelper<*>,
    var perms: Array<out String>,
    var requestCode: Int,
    var rationale: String?,
    var positiveButtonText: String?,
    var negativeButtonText: String?,
    var theme: Int?,
    var isShowRationale: Boolean = true,
    var mRationalePermissions: MutableList<String> = mutableListOf()
) {


    class Builder {
        private val mHelper: BasePermissionHelper<*>
        private val mRequestCode: Int
        private val mPerms: Array<out String>
        private var mRationale: String? = null
        private var mPositiveButtonText: String? = null
        private var mNegativeButtonText: String? = null
        private var isShowRationale: Boolean = true
        private var mTheme = -1

        constructor(activity: Activity, requestCode: Int, isShowRationale:Boolean,@Size(min = 1) vararg perms: String) {
            mHelper = newInstance(activity)
            mRequestCode = requestCode
            mPerms = perms
            this.isShowRationale = isShowRationale
        }

        constructor(fragment: Fragment, requestCode: Int,isShowRationale:Boolean, @Size(min = 1) vararg perms: String) {
            mHelper = newInstance(fragment)
            mRequestCode = requestCode
            this.isShowRationale = isShowRationale
            mPerms = perms
        }

        fun setRationale(rationale: String?): Builder {
            mRationale = rationale
            return this
        }

        fun setRationale(@StringRes resId: Int): Builder {
            mHelper.getContext()?.let {
                mRationale = it.getString(resId)
            }
            return this
        }

        fun setPositiveButtonText(positiveButtonText: String?): Builder {
            mPositiveButtonText = positiveButtonText
            return this
        }

        fun setPositiveButtonText(@StringRes resId: Int): Builder {
            mHelper.getContext()?.let {
                mPositiveButtonText = it.getString(resId)
            }
            return this
        }

        fun setNegativeButtonText(negativeButtonText: String?): Builder {
            mNegativeButtonText = negativeButtonText
            return this
        }

        fun setNegativeButtonText(@StringRes resId: Int): Builder {
            mHelper.getContext()?.let {
                mNegativeButtonText = it.getString(resId)
            }
            return this
        }

        fun setTheme(@StyleRes theme: Int): Builder {
            mTheme = theme
            return this
        }

        fun build(): PermissionRequest {
            return PermissionRequest(
                mHelper,
                mPerms,
                mRequestCode,
                mRationale,
                mPositiveButtonText,
                mNegativeButtonText,
                mTheme
            )
        }
    }


}