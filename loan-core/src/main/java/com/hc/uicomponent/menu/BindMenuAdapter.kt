package com.hc.uicomponent.menu

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hc.data.MenuData
import com.hc.uicomponent.GridItemDecoration
import com.hc.uicomponent.R

object BindMenuAdapter {

    @BindingAdapter(
        value = ["recyclerAdapter", "spanCount", "addItemDecoration", "includeHorizontalEdge", "includeVerticalEdge"],
        requireAll = false
    )
    @JvmStatic
    fun recyclerViewAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        spanCount: Int = 1,
        addItemDecoration: Boolean? = true,
        includeHorizontalEdge: Boolean? = true,
        includeVerticalEdge: Boolean? = true
    ) {
        val context = recyclerView.context
        if (addItemDecoration == true) {
            val decoration = GridItemDecoration.newBuilder().spanCount(spanCount)
                .horizontalDivider(
                    ColorDrawable(ContextCompat.getColor(context, R.color.colorDiv)),
                    context.resources.getDimensionPixelSize(R.dimen.menu_item_divider),
                    includeHorizontalEdge ?: false
                )
                .verticalDivider(
                    ColorDrawable(ContextCompat.getColor(context, R.color.colorDiv)),
                    context.resources.getDimensionPixelSize(R.dimen.menu_item_divider),
                    includeVerticalEdge ?: false
                ).build()
            recyclerView.addItemDecoration(decoration)
        }
        recyclerView.isFocusable = false
        recyclerView.isFocusableInTouchMode = false
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        // set Adapter
        if (null == recyclerView.adapter) {
            recyclerView.adapter = adapter
        } else {
            recyclerView.swapAdapter(adapter, true)
        }
    }

    @BindingAdapter("isSelect", requireAll = true)
    @JvmStatic
    fun bindSelectState(view: View, isSelect: Boolean) {
        view.isSelected = isSelect
    }

    @BindingAdapter(value = ["menuData", "menuTitle", "callback"], requireAll = false)
    @JvmStatic
    fun bindMenuData(view: BaseMenuUI, menuData: ObservableArrayList<MenuData>, menuTitle:String?="",callback:((RecyclerView,MenuData)->Unit) ) {
        view.statData(menuTitle,menuData,callback)
    }


}