package com.hc.uicomponent.tab

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hc.uicomponent.R

/**
 * 一个简单的Tabs选项卡视图
 *
 * @author noonecode
 */
class TabsView(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)  : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context?):this(context,null)
    constructor(context: Context?,attrs: AttributeSet?):this(context,attrs,0)

    private var mSelectedColor = -0x10000 // 选中的字体颜色
    private var mNotSelectedColor = mSelectedColor ushr 25 shl 24 or (mSelectedColor and 0x00ffffff) // 未选中的字体颜色
    private var mIndicatorColor = -0xffff01 // 指示器的颜色
    private var mTabsContainer: LinearLayout? = null
    private var mIndicator: View? = null
    private var mBottomLine: View?=null
    private var listener: OnTabsItemClickListener? = null

    init {
        context?.let {
            mSelectedColor = ContextCompat.getColor(context,R.color.colorPrimary)
            mNotSelectedColor = ContextCompat.getColor(context,R.color.colorTabNotSelect)
            mIndicatorColor = ContextCompat.getColor(context,R.color.colorPrimary)
        }
        orientation = VERTICAL
        // 初始化容器
        mTabsContainer = LinearLayout(getContext())
        mTabsContainer?.orientation = HORIZONTAL
        mTabsContainer?.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(mTabsContainer)
        // 初始化指示器
        mIndicator = View(getContext())
        mIndicator?.setBackgroundColor(mIndicatorColor)
        mIndicator?.layoutParams = LayoutParams(300, 8) // 先任意设置宽度
        addView(mIndicator)
        // 初始化底部横线
        mBottomLine = View(getContext())
        mBottomLine?.setBackgroundColor(mIndicatorColor)
        mBottomLine?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 2)
        //addView(mBottomLine)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetIndicator()
    }

    /**
     * 重新设置指示器
     */
    private fun resetIndicator() {
        val childCount = mTabsContainer?.childCount
        val layoutParams = mIndicator?.layoutParams
        if (childCount?:0 <= 0) {
            layoutParams?.width = 0
        } else {
            layoutParams?.width = width / (childCount?:1)
        }

        mIndicator?.layoutParams = layoutParams
        // mIndicator.setX(0f);
    }

    /**
     * 设置选项卡
     *
     * @param titles
     */
    fun setTabs(vararg titles: String?) {
        mTabsContainer?.removeAllViews()
        if (titles != null) {
            for (i in titles.indices) {
                val textView = TextView(context)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                // textView.setTextColor(mNotSelectedColor);
                textView.text = titles[i]
                textView.isClickable = true
                textView.setPadding(0, 10, 0, 10)
                textView.gravity = Gravity.CENTER
                textView.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelOffset(R.dimen.loan_tab_height),
                    1f
                )
                textView.tag = i
                textView.setOnClickListener { v ->
                    val position = v.tag as Int
                    setCurrentTab(position, true)
                    if (listener != null) {
                        listener!!.onClick(v, position)
                    }
                }
                mTabsContainer?.addView(textView)
            }
            // 初始化，默认选中第一个
            setCurrentTab(0, false)
            // 设置指示器
            post { // 设置指示器
                resetIndicator()
            }
        }
    }

    /**
     * 设置当前的tab
     *
     * @param position
     */
    fun setCurrentTab(position: Int, anim: Boolean) {
        val childCount = mTabsContainer?.childCount
        childCount?.let {
            if (position < 0 || position >= childCount) {
                return
            }
            // 设置每个tab的状态
            for (i in 0 until childCount) {
                val childView =
                    mTabsContainer?.getChildAt(i) as TextView
                if (i == position) {
                    childView.setTextColor(mSelectedColor)
                } else {
                    childView.setTextColor(mNotSelectedColor)
                }
            }
            // 指示器的移动
            val objectAnimator =
                ObjectAnimator.ofFloat(mIndicator, "x", position * (mIndicator?.width?.toFloat()?:1f))
            if (anim) {
                objectAnimator.setDuration(200).start()
            } else {
                objectAnimator.setDuration(0).start()
            }

        }

    }

    /**
     * Tabs点击的监听事件
     *
     * @param listener
     */
    fun setOnTabsItemClickListener(listener: OnTabsItemClickListener?) {
        this.listener = listener
    }

    interface OnTabsItemClickListener {
        fun onClick(view: View?, position: Int)
    }


}