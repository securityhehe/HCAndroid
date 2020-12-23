package com.hc.uicomponent.titlebar

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import com.hc.uicomponent.R


/**
 *
 * action bar  容器。
 *
 * @author create by  zshh 2019/7/18
 *
 */

open class ToolBarContainer(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mStatusBarHeight = getStatusBarHeight()

    private var mToolBarHeight =  resources.getDimensionPixelOffset(R.dimen.common_toolbar_height)

    private var mHeight =  mStatusBarHeight + mToolBarHeight
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        setPadding(0, mStatusBarHeight, 0, 0)
        val barHeight = getBarHeight()
        mHeight = if (barHeight > 0) (barHeight + mStatusBarHeight) else mHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    open fun getBarHeight(): Int {
        return -1
    }

}