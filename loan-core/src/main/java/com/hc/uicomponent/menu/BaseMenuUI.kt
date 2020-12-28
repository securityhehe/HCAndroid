package com.hc.uicomponent.menu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.hc.data.MenuData
import com.hc.uicomponent.R
import com.hc.uicomponent.databinding.MenuItemBinding
import com.hc.uicomponent.databinding.MenuLayoutBinding
import frame.utils.StringUtil

class BaseMenuUI : LinearLayout {
    var mBindLayout: MenuLayoutBinding? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mBindLayout = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.menu_layout, this, true)
        mBindLayout?.apply {
            adapter = MenuContentAdapter(menuContentRv)
        }
    }

    fun  statData(
        menuTitle: String?,
        menuData: ObservableArrayList<MenuData>,
        callback: ((RecyclerView,MenuData) -> Unit)
    ) {

        mBindLayout?.apply {
            adapter?.apply {
                this.callback = callback
                menuContent.clear()
                menuContent.addAll(menuData)
                notifyDataSetChanged()
            }
            menuTitleTv.apply {
                visibility = if (StringUtil.isBank(menuTitle)) View.GONE else {
                    View.VISIBLE
                }
                text = menuTitle
            }
        }
    }

}

class MenuContentAdapter(var recyclerView: RecyclerView,var menuContent: ObservableArrayList<MenuData> = ObservableArrayList(), var callback: ((RecyclerView,MenuData) -> Unit)?= null) :
    RecyclerView.Adapter<MenuItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemHolder {
        val menuItem: MenuItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.menu_item,
            parent,
            false
        )

        return MenuItemHolder(menuItem)
    }

    override fun getItemCount(): Int {
        return menuContent.size
    }

    override fun onBindViewHolder(holder: MenuItemHolder, position: Int) {
        holder.menuItem.data = menuContent[position]
        holder.menuItem.btn.setOnClickListener{

            callback?.invoke(recyclerView,menuContent[position])
        }
    }
}


class MenuItemHolder(var menuItem: MenuItemBinding) : RecyclerView.ViewHolder(menuItem.root)


