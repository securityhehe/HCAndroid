package com.hc.uicomponent

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View

import androidx.recyclerview.widget.RecyclerView



/**
 * Desc：适用于GridView，当然也适用于普通的RecyclerView
 * 支持在item之间设置任何类型的间距，支持控制是否显示上下左右间隔及是否绘制上下左右背景
 */
class GridItemDecoration private constructor(builder: Builder) : RecyclerView.ItemDecoration() {
    private val verticalDivider: Drawable?
    private val horizontalDivider: Drawable?
    private val spanCount: Int
    private val verticalSpaceSize: Int
    private val horizontalSpaceSize: Int
    private val includeVerticalEdge //是否绘制左右边界，注意不绘制并不表示没有预留边界空间
            : Boolean
    private val includeHorizontalEdge //是否绘制上下边界，注意不绘制并不表示没有预留边界空间
            : Boolean

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        if (verticalSpaceSize > 0) {
            outRect.left = verticalSpaceSize - column * verticalSpaceSize / spanCount
            outRect.right = (column + 1) * verticalSpaceSize / spanCount
        } else {
            outRect.left = column * verticalSpaceSize / spanCount
            outRect.right = verticalSpaceSize - (column + 1) * verticalSpaceSize / spanCount
        }
        if (horizontalSpaceSize > 0) {
            if (position < spanCount) outRect.top = horizontalSpaceSize // top edge
            outRect.bottom = horizontalSpaceSize // item bottom
        } else {
            if (position >= spanCount) outRect.top = horizontalSpaceSize // item top
        }
        log("确定边界" + outRect.left.toString() + "  " + outRect.right.toString() + "  " + outRect.top.toString() + "  " + outRect.bottom)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        log(toString())
        //在边界上绘制图形
        if (horizontalDivider != null && horizontalSpaceSize > 0) {
            drawHorizontalLineAtItemTop(c, parent) //在每一个item的上面绘制水平分割线
            drawLineAtTopAndBottom(c, parent) //绘制上下边界，包括倒数第二行中的部分item
        }
        if (verticalDivider != null && verticalSpaceSize > 0) {
            drawVerticalLineAtItemLeft(c, parent) //绘制竖直分割线
            if (includeVerticalEdge) drawLR(c, parent) //绘制左右边界
        }
    }


    //在每一个item的上面绘制水平分割线
    private fun drawHorizontalLineAtItemTop(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (position in 0 until childCount) {
            if (position >= spanCount) { //第一行不绘制
                val child: View = parent.getChildAt(position)
                val left: Int = child.getLeft()
                val right: Int =
                    if (position % spanCount == spanCount - 1) child.getRight() else child.getRight() + verticalSpaceSize
                val top: Int = child.getTop() - horizontalSpaceSize
                val bottom = top + horizontalSpaceSize
                log("绘制水平分割线$left  $right  $top  $bottom")
                horizontalDivider!!.setBounds(left, top, right, bottom)
                horizontalDivider.draw(c)
            }
        }
    }

    //绘制竖直分割线
    private fun drawVerticalLineAtItemLeft(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (position in 0 until childCount) { //第一列不绘制
            if (position % spanCount != 0) {
                val child: View = parent.getChildAt(position)
                val left: Int = child.getLeft() - verticalSpaceSize
                val right = left + verticalSpaceSize
                val top: Int = child.getTop() /*- horizontalSpaceSize*/
                val bottom: Int = child.getBottom()
                log("绘制竖直分割线$left  $right  $top  $bottom")
                verticalDivider!!.setBounds(left, top, right, bottom)
                verticalDivider.draw(c)
            }
        }
    }

    //绘制左右边界
    private fun drawLR(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (position in 0 until childCount) {
            val child: View = parent.getChildAt(position)
            val maxLines =
                if (childCount % spanCount == 0) childCount / spanCount else childCount / spanCount + 1 //一共有多少行
            val isLastLineItem = position / spanCount == maxLines - 1 //倒数第一行的元素
            //最左边那条线
            if (position % spanCount == 0) {
                val left: Int = child.getLeft() - verticalSpaceSize
                val right = left + verticalSpaceSize
                val top: Int = child.getTop() - horizontalSpaceSize
                var bottom: Int
                bottom = if (isLastLineItem) { //【左下角】
                    child.getBottom() + horizontalSpaceSize
                } else {
                    child.getBottom()
                }
                verticalDivider!!.setBounds(left, top, right, bottom)
                verticalDivider.draw(c)
                log("绘制最左边那条线$left  $right  $top  $bottom")
            }

            //最右边那条线
            if ((position + 1) % spanCount == 0) {
                val left: Int = child.getRight()
                val right = left + verticalSpaceSize
                val top: Int = child.getTop() - horizontalSpaceSize
                val isLastSecondLineItem =
                    position / spanCount == maxLines - 2 && position + spanCount >= childCount //倒数第二行的部分元素
                var bottom: Int
                bottom = if (isLastLineItem || isLastSecondLineItem) {
                    child.getBottom() + horizontalSpaceSize //【右下角】
                } else {
                    child.getBottom()
                }
                verticalDivider!!.setBounds(left, top, right, bottom)
                verticalDivider.draw(c)
                log("绘制最右边那条线$left  $right  $top  $bottom")
            }
        }
    }

    //绘制上下边界，包括倒数第二行中的部分item
    private fun drawLineAtTopAndBottom(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (position in 0 until childCount) {
            val child: View = parent.getChildAt(position)

            //最上边那条线
            if (includeHorizontalEdge && position < spanCount) {
                val top: Int = child.getTop() - horizontalSpaceSize
                val bottom = top + horizontalSpaceSize
                val left: Int = child.getLeft()
                var right: Int
                val isRightEdgeItem = (position + 1) % spanCount == 0 //最右侧的元素
                val isFirstLineItem =
                    childCount < spanCount && position == childCount - 1 //只有一行的元素
                right = if (isRightEdgeItem || isFirstLineItem) { //【右上角】
                    child.getRight()
                } else {
                    child.getRight() + verticalSpaceSize //如果同时有竖直分割线，则需要包含竖直分割线的宽度
                }
                horizontalDivider!!.setBounds(left, top, right, bottom)
                horizontalDivider.draw(c)
                log("绘制最上边那条线$left  $right  $top  $bottom")
            }

            //最下边那条线
            val maxLines =
                if (childCount % spanCount == 0) childCount / spanCount else childCount / spanCount + 1 //一共有多少行
            val isLastLineItem = position / spanCount == maxLines - 1 //倒数第一行的元素
            val isLastSecondLineItem =
                position / spanCount == maxLines - 2 && position + spanCount >= childCount //倒数第二行的部分元素
            if (includeHorizontalEdge && isLastLineItem || isLastSecondLineItem) {
                val top: Int = child.getBottom()
                val bottom = top + horizontalSpaceSize
                val left: Int = child.getLeft()
                var right: Int
                val isLastSecondLineEdgeItem =
                    isLastSecondLineItem && (position + 1) % spanCount == 0 //倒数第二行最后一个元素
                right = if (position == childCount - 1 || isLastSecondLineEdgeItem) { //【右下角】
                    child.getRight()
                } else {
                    child.getRight() + verticalSpaceSize //如果同时有竖直分割线，则需要包含竖直分割线的宽度
                }
                horizontalDivider!!.setBounds(left, top, right, bottom)
                horizontalDivider.draw(c)
                log("绘制最下边那条线$left  $right  $top  $bottom")
            }
        }
    }

    class Builder  constructor() {
         var horizontalDivider: Drawable? = null
         var verticalDivider: Drawable? = null
         var spanCount = 0
         var horizontalSpaceSize = 0
         var verticalSpaceSize = 0
         var includeHorizontalEdge = false
         var includeVerticalEdge = false
        fun horizontalDivider(
            horizontalDivider: Drawable?,
            horizontalSpaceSize: Int,
            includeHorizontalEdge: Boolean
        ): Builder {
            this.horizontalDivider = horizontalDivider
            this.horizontalSpaceSize = horizontalSpaceSize
            this.includeHorizontalEdge = includeHorizontalEdge
            return this
        }

        fun verticalDivider(
            verticalDivider: Drawable?,
            verticalSpaceSize: Int,
            includeVerticalEdge: Boolean
        ): Builder {
            this.verticalDivider = verticalDivider
            this.verticalSpaceSize = verticalSpaceSize
            this.includeVerticalEdge = includeVerticalEdge
            return this
        }

        fun spanCount(`val`: Int): Builder {
            spanCount = `val`
            return this
        }

        fun build(): GridItemDecoration {
            return GridItemDecoration(this)
        }
    }

    private fun log(msg: String) {
        Log.i("bqt", "【】$msg")
    }

    companion object {
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    init {
        verticalDivider = builder.verticalDivider
        horizontalDivider = builder.horizontalDivider
        spanCount = builder.spanCount
        verticalSpaceSize = builder.verticalSpaceSize
        horizontalSpaceSize = builder.horizontalSpaceSize
        includeVerticalEdge = builder.includeVerticalEdge
        includeHorizontalEdge = builder.includeHorizontalEdge
    }
}
