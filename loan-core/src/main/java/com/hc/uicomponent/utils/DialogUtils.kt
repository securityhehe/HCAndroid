package com.hc.uicomponent.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.user.UserType
import com.hc.uicomponent.R
import com.hc.uicomponent.databinding.DialogMobileAuthPageBinding
import com.hc.uicomponent.databinding.ItemMobileAuthTypeBinding
import com.hc.uicomponent.provider.ContextProvider
import com.timmy.tdialog.TDialog
import com.tools.network.callback.AppResultCode

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


    private var mobileAuthDialog : TDialog?= null
    fun showMobileAuthType(activity: Activity, mobileList:List<UserType>, calback: (Int)->Unit) {
        val binding = DataBindingUtil.inflate<DialogMobileAuthPageBinding>(LayoutInflater.from(activity), R.layout.dialog_mobile_auth_page, null, false)
        val onClickListener = View.OnClickListener { v ->
            val mobileType = (v.tag as String).toInt()
            v.findViewById<CheckBox>(R.id.in_mobile_checkbox).isChecked = true
            calback.invoke(mobileType)
            if (mobileAuthDialog != null) {
                Handler(activity.mainLooper).postDelayed({
                    mobileAuthDialog!!.dismissAllowingStateLoss()
                    mobileAuthDialog = null
                },100)
            }
        }

        dynamicAddChildView<UserType, ItemMobileAuthTypeBinding>(binding.mobileAuthRoot,R.layout.item_mobile_auth_type,mobileList){
                _binding,index,data ->
            _binding.item = data
            _binding.index = index
            _binding.maxIndex = mobileList.size
            _binding.root.setOnClickListener(onClickListener)
        }

        val builder = TDialog.Builder((activity as FragmentActivity).supportFragmentManager).setDialogView(binding.root)
        builder.setWidth(ScreenUtils.getScreenWidth())
        builder.setGravity(Gravity.BOTTOM)
        builder.setCancelableOutside(true)
        builder.setDimAmount(0.6f)
        builder.setDialogAnimationRes(R.style.BottomDialog_AnimationStyle)

        builder.setOnKeyListener { dialog, keyCode, event ->
            false
        }
        builder.setOnViewClickListener { viewHolder, view, tDialog ->
            tDialog.dismissAllowingStateLoss()
        }
        mobileAuthDialog = builder.create()
        mobileAuthDialog!!.show()
    }











}