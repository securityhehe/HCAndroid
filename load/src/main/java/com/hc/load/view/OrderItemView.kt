package com.hc.load.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hc.load.R

class OrderItemView :FrameLayout{
    constructor(context:Context):this(context,null)
    constructor(context: Context,attr: AttributeSet?):this(context,attr,0)
    constructor(context: Context,attr: AttributeSet?,defStyleAttr:Int):super(context,attr,defStyleAttr)

    init {
       val param =  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
       addView(LayoutInflater.from(context).inflate(R.layout.payment_order_details,this,false),param)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }
}