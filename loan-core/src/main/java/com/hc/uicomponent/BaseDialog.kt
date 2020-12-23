package com.hc.uicomponent

import android.app.Dialog
import android.content.Context
import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.common_dialog.*

class BaseDialog : Dialog {

    constructor(context: Context) : this(context, 0)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    init {
        initDialogAttrs()
        initView()
    }

    private fun initDialogAttrs() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        window?.let {
            val params: WindowManager.LayoutParams = it.attributes
            params.gravity = Gravity.CENTER
            it.attributes = params
            it.decorView.setPadding(0, 0, 0, 0)
            val w =
                context.resources.getDimensionPixelOffset(R.dimen.base_common_dialog_padding) * 2
            it.setLayout(
                context.resources.displayMetrics.widthPixels - w,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            it.setBackgroundDrawable(ColorDrawable(0x00000000))
        }
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        setContentView(layoutInflater.inflate(R.layout.common_dialog, null))
    }

    override fun onBackPressed() {}

    fun setData(
        contents: CharSequence?,
        content1Cancel: CharSequence?,
        contentOk: CharSequence?
    ): BaseDialog {
        content?.text = contents
        if (content1Cancel != null) {
            dialog_cancel?.text = content1Cancel
            dialog_cancel?.visibility = View.VISIBLE
        } else {
            dialog_cancel?.visibility = View.GONE
        }
        if(contentOk != null){
            dialog_accept?.text = contentOk
            dialog_accept?.visibility = View.VISIBLE
        }else{
            dialog_accept?.visibility = View.GONE
        }
        return this
    }

    fun setCallback(callback: Callback?): BaseDialog {
        dialog_accept?.setOnClickListener {
            callback?.confirm(this@BaseDialog)
        }

        dialog_cancel?.setOnClickListener {
            callback?.cancel(this@BaseDialog)
        }
        return this
    }

    interface Callback {
        fun cancel(d: Dialog?){}
        fun confirm(d: Dialog?){}
    }
}
