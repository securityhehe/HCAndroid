package com.hc.uicomponent.menu

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import com.hc.data.MenuData
import com.hc.data.user.UserType
import com.hc.uicomponent.base.BaseViewModel

class BaseMenuViewModel : BaseViewModel() {
    var listData = ObservableArrayList<MenuData>()
    var title: String? = ""
    val isLanguageMenuShowHide = ObservableInt(View.GONE)

    val callback: (RecyclerView, MenuData) -> Unit = { rv, data ->
        var prevIndex = -1
        listData.forEach end@{
            if (it.isSelect) {
                prevIndex = it.index
                return@end
            }
        }
        if(prevIndex > -1 && prevIndex != data.index){
            listData[prevIndex].isSelect = false
            rv.adapter?.notifyItemChanged(prevIndex)
            listData[data.index].isSelect = true
            rv.adapter?.notifyItemChanged(data.index)
        }else{
            listData[data.index].isSelect = true
            rv.adapter?.notifyItemChanged(data.index)
        }
        callbackData?.invoke(data)
    }

    var callbackData:(data:MenuData)->Unit? = {

    }
}