package com.hc.permission.rationle

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.hc.permission.AndroidPermissions.PermissionCallbacks
import com.hc.permission.AndroidPermissions.RationaleCallbacks
import com.hc.permission.R
import com.hc.permission.param.PermissionRequest

/**
 * [AppCompatDialogFragment] to display rationale for permission requests when the request
 * comes from a Fragment or Activity that can host a Fragment.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class RationaleDialogFragmentCompat : AppCompatDialogFragment() {

    private var mPermissionCallbacks: PermissionCallbacks? = null
    private var mRationaleCallbacks: RationaleCallbacks? = null

    /**
     * Version of [.show] that no-ops when an IllegalStateException
     * would otherwise occur.
     */
    fun showAllowingStateLoss(
        manager: FragmentManager,
        tag: String?
    ) {
        if (manager.isStateSaved) {
            return
        }
        show(manager, tag)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null) {
            if (parentFragment is PermissionCallbacks) {
                mPermissionCallbacks = parentFragment as PermissionCallbacks?
            }
            if (parentFragment is RationaleCallbacks) {
                mRationaleCallbacks = parentFragment as RationaleCallbacks?
            }
        }
        if (context is PermissionCallbacks) {
            mPermissionCallbacks = context
        }
        if (context is RationaleCallbacks) {
            mRationaleCallbacks = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mPermissionCallbacks = null
        mRationaleCallbacks = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val config = RationaleDialogConfig(arguments)
        val clickListener = RationaleDialogClickListener(this, config, mPermissionCallbacks, mRationaleCallbacks)
        return config.createSupportDialog(requireContext(), clickListener)
    }

    companion object {
        const val TAG = "RationaleDialogFragmentCompat"
        fun newInstance(
            rationaleMsg: String,
            positiveButton: String,
            negativeButton: String,
            @StyleRes theme: Int,
            requestCode: Int,
            vararg permissions:String
        ): RationaleDialogFragmentCompat {
            val dialogFragment = RationaleDialogFragmentCompat()
            val config = RationaleDialogConfig(theme,rationaleMsg,positiveButton, negativeButton,requestCode , permissions)
            dialogFragment.arguments = config.toBundle()
            return dialogFragment
        }

        fun newInstance(req: PermissionRequest): RationaleDialogFragmentCompat {
            // Create new Fragment
            val dialogFragment = RationaleDialogFragmentCompat()
            val ctx = req.helper.getContext();
            val config = RationaleDialogConfig(
                req.theme?:0,
                req.rationale?:ctx?.getString(R.string.android_rationale_ask),
                req.positiveButtonText?:ctx?.getString(android.R.string.ok),
                req.negativeButtonText?:ctx?.getString(android.R.string.cancel),
                req.requestCode,
                req.perms
            )
            dialogFragment.arguments = config.toBundle()
            return dialogFragment
        }
    }
}