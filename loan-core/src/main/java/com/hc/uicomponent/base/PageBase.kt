package com.hc.uicomponent.base

interface PageBase {
    fun alert(task: java.lang.Runnable)
    fun dismissLoadingDialog()

    fun isDestroyed(): Boolean

    fun isFinishing(): Boolean

    fun isCanShowDialog():Boolean

    fun showLoadingDialog()

    fun showLoadingDialog(cancelAble: kotlin.Boolean)

    fun showLoadingDialog(cancelAble: kotlin.Boolean, call: () -> Unit)

    fun showLoadingDialog(message: String="" , canCancel: Boolean =false, cancelCall:()-> Unit?)
}

