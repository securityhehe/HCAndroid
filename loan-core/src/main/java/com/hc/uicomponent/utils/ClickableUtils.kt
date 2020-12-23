package com.hc.uicomponent.utils

import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.hc.uicomponent.base.PlaceholderLayout
import com.hc.uicomponent.titlebar.ToolBarContainer

fun disableChildViewClickEvent(viewGroup: ViewGroup?) {
    if (viewGroup == null) return
    for (i in 0 until viewGroup.childCount) {
        val v = viewGroup.getChildAt(i)
        if (v is ViewGroup) {
            if (v is PlaceholderLayout) continue
            if (v is ToolBarContainer)  continue
            v.setEnabled(false)
            v.setClickable(false)
            disableChildViewClickEvent(v)
        } else if (v is EditText || v is Button) {
            v.isEnabled = false
            v.isClickable = false
        }
    }
}