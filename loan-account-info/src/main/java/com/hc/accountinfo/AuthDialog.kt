package com.hc.accountinfo

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.hc.accountinfo.databinding.AccountDialogAuthFinishLayoutBinding
import com.hc.accountinfo.vm.AuthCenterViewModel
import com.hc.uicomponent.BaseDialog
import com.hc.uicomponent.base.BaseActivity
import com.timmy.tdialog.TDialog
import com.tools.network.callback.AppResultCode

object AuthDialog {
    fun showCreditFinishTipDialog(activity:Activity,call:()->Unit) {
        val rootLayoutBinding = DataBindingUtil.inflate<AccountDialogAuthFinishLayoutBinding>(
            LayoutInflater.from(activity), R.layout.account_dialog_auth_finish_layout, null, false)
        val builder = TDialog.Builder((activity as FragmentActivity).supportFragmentManager).setDialogView(rootLayoutBinding.root)
        builder.setScreenWidthAspect(activity,1f)
        builder.setScreenHeightAspect(activity,1f)
        builder.setGravity(Gravity.CENTER)
        builder.setCancelableOutside(false)
        builder.setDimAmount(0.6f)

        builder.addOnClickListener(R.id.loan_cancel, R.id.loan_now)
        builder.setOnKeyListener { _, _, _ ->
            return@setOnKeyListener  true
        }
        builder.setOnViewClickListener { _, view, tDialog ->
            tDialog.dismissAllowingStateLoss()
            when (view.id) {
                R.id.loan_now -> {
                    call.invoke()
                }
            }
        }
        builder.create().show()
    }

}