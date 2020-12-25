package com.hc.uicomponent.menu

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.KeyboardUtils
import com.hc.uicomponent.R
import com.hc.uicomponent.databinding.LoanBasePopwindowContentLayoutBinding
import com.hc.uicomponent.utils.ScreenAdapterUtils


class BasePopupWindow(context: Activity, vm: BaseMenuViewModel, private val anchorView: View, width: Int)
    : PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

    private val windowPos = IntArray(2)
    private val mContext: Context

    init {
        KeyboardUtils.hideSoftInput(context)
        this.mContext = context

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = DataBindingUtil.inflate<LoanBasePopwindowContentLayoutBinding>(
            inflater,
            R.layout.loan_base_popwindow_content_layout,
            null,
            false
        )
        layout.vm = vm
        this.animationStyle = if(!vm.title.isNullOrEmpty()){
            R.style.AlphaOutInAnimFromRightTop
        }else{
            R.style.LoanDataAlphaOutInAnimFormLeftTop
        }

        val globalListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (vm.listData.size > 5) {
                update(width, ScreenAdapterUtils.dp2px(mContext, 240))
            }
        }

        layout.root.viewTreeObserver.addOnGlobalLayoutListener(globalListener)
        this.contentView = layout.root
        this.width = width
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.isFocusable = true
        this.isOutsideTouchable = true
        val color = ContextCompat.getColor(mContext, android.R.color.transparent)
        this.setBackgroundDrawable(ColorDrawable(color))
        anchorView.getLocationOnScreen(windowPos)
    }

    fun show(x: Int, y: Int) {
        showAtLocation(anchorView, Gravity.NO_GRAVITY, windowPos[0] + x, windowPos[1] + y)
    }
}
