package com.hc.permission.rationle
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import com.hc.permission.AndroidPermissions.PermissionCallbacks
import com.hc.permission.AndroidPermissions.RationaleCallbacks
import com.hc.permission.param.PermissionRequest

@RestrictTo(RestrictTo.Scope.LIBRARY)
open class RationaleDialogFragment : DialogFragment() {
    private var mPermissionCallbacks: PermissionCallbacks? = null
    private var mRationaleCallbacks: RationaleCallbacks? = null
    private var mStateSaved = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && parentFragment != null) {
            if (parentFragment is PermissionCallbacks) {
                mPermissionCallbacks = parentFragment as PermissionCallbacks
            }
            if (parentFragment is RationaleCallbacks) {
                mRationaleCallbacks = parentFragment as RationaleCallbacks
            }
        }
        if (context is PermissionCallbacks) {
            mPermissionCallbacks = context
        }
        if (context is RationaleCallbacks) {
            mRationaleCallbacks = context
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mStateSaved = true
        super.onSaveInstanceState(outState)
    }


    fun showAllowingStateLoss(manager: FragmentManager?, tag: String?) {
        // API 26 added this convenient method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager?.isStateSaved == false) {
                return
            }
        }
        if (mStateSaved) {
            return
        }
        manager?.apply {
            show(this, tag)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mPermissionCallbacks = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val config = RationaleDialogConfig(arguments)
        val clickListener = RationaleDialogClickListener(this, config, mPermissionCallbacks, mRationaleCallbacks)
        val ctx = context
        return config.createFrameworkDialog(ctx, clickListener)
    }

    companion object {
        val TAG = "RationaleDialogFragment"
        fun newInstance(
            req: PermissionRequest
        ): RationaleDialogFragment {
            val config = RationaleDialogConfig(
                req.theme?:0,
                req.rationale,
                req.positiveButtonText,
                req.negativeButtonText,
                req.requestCode,
                req.perms
            )
            val dialogFragment = RationaleDialogFragment()
            dialogFragment.arguments = config.toBundle()
            return dialogFragment
        }
    }
}