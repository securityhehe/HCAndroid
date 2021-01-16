package com.hc.uicomponent.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.user.UserType
import com.hc.uicomponent.R
import com.hc.uicomponent.databinding.CommonDialogBinding
import com.hc.uicomponent.databinding.DialogMobileAuthPageBinding
import com.hc.uicomponent.databinding.ItemMobileAuthTypeBinding
import com.hc.uicomponent.provider.ContextProvider
import com.timmy.tdialog.TDialog

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

    //显示手机认证弹出窗口。
    fun showMobileAuthType(context: Context, mobileList: List<UserType>, callback: (Int) -> Unit) {
        val bindView = createAuthMobileBind(context, callback, mobileList)
        bindView?.let {
            val d = getBaseBuild(context).setDialogView(it.root).create().show()
            it.root.tag = d
        }
    }

    //显示提示
    fun showTips(context: Context, view: ViewDataBinding) {
        val d = getBaseBuild(context).setDialogView(view.root).create().show()
        view.root.tag = d
    }

    private fun createAuthMobileBind(context: Context, callback: (Int) -> Unit, mobileList: List<UserType>): DialogMobileAuthPageBinding? {
        val binding = DataBindingUtil.inflate<DialogMobileAuthPageBinding>(LayoutInflater.from(context), R.layout.dialog_mobile_auth_page, null, false)
        val onClickListener = View.OnClickListener { v ->
            val mobileType = (v.tag as String).toInt()
            v.findViewById<CheckBox>(R.id.in_mobile_checkbox).isChecked = true
            callback.invoke(mobileType)
            binding.root.tag?.apply {
                if (this is TDialog) {
                    Handler(context.mainLooper).postDelayed({
                        this.dismissAllowingStateLoss()
                    }, 100)
                }
            }
        }
        dynamicAddChildView<UserType, ItemMobileAuthTypeBinding>(binding.mobileAuthRoot, R.layout.item_mobile_auth_type, mobileList) { _binding, index, data ->
            _binding.item = data
            _binding.index = index
            _binding.maxIndex = mobileList.size
            _binding.root.setOnClickListener(onClickListener)
        }
        return binding
    }

    private fun getBaseBuild(activity: Context): TDialog.Builder {
        return TDialog.Builder((activity as FragmentActivity).supportFragmentManager)?.apply {
            setWidth(ScreenUtils.getScreenWidth())
            setGravity(Gravity.BOTTOM)
            setCancelableOutside(true)
            setDimAmount(0.6f)
            setDialogAnimationRes(R.style.BottomDialog_AnimationStyle)
            setOnKeyListener { _, _, _ ->
                false
            }
            setOnViewClickListener { _, _, tDialog ->
                tDialog.dismissAllowingStateLoss()
            }
        }
    }


    fun createBaseDialogBind(context: Context, contentStr: String, isShowCancelData: Boolean, callback: (() -> Unit)?): CommonDialogBinding? {
        val binding = DataBindingUtil.inflate<CommonDialogBinding>(LayoutInflater.from(context), R.layout.common_dialog, null, false)
        binding?.apply {
            desc = contentStr
            isShowCancel = isShowCancelData
            dialogCancel.setOnClickListener {
                binding.root.tag?.apply {
                    if (this is TDialog) {
                        Handler(context.mainLooper).postDelayed({
                            this.dismissAllowingStateLoss()
                        }, 100)
                    }
                }
            }

            dialogAccept.setOnClickListener {
                binding.root.tag?.apply {
                    if (this is TDialog) {
                        Handler(context.mainLooper).postDelayed({
                            this.dismissAllowingStateLoss()
                        }, 100)
                    }
                }
                callback?.invoke()
            }

        }
        return binding
    }

    fun showCenterTips(context: Context, view: ViewDataBinding) {
        val d = getBaseCenter(context).setDialogView(view.root).create().show()
        view.root.tag = d
    }


     private fun getBaseCenter(activity: Context): TDialog.Builder {
        return TDialog.Builder((activity as FragmentActivity).supportFragmentManager)?.apply {
            val padding = 2 * activity.resources.getDimensionPixelOffset(R.dimen.base_common_dialog_padding)
            setWidth(ScreenUtils.getScreenWidth() - padding)
            setGravity(Gravity.CENTER)
            setCancelableOutside(true)
            setDimAmount(0.6f)
            setDialogAnimationRes(R.style.BottomDialog_AnimationStyle)
            setOnKeyListener { _, _, _ ->
                false
            }
            setOnViewClickListener { _, _, tDialog ->
                tDialog.dismissAllowingStateLoss()
            }
        }
    }


}