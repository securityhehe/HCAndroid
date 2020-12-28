package com.hc.main.vm

import android.app.Activity
import android.view.View
import androidx.databinding.ObservableInt
import androidx.navigation.Navigation
import com.hc.data.common.CommonDataModel
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.provider.isTEST
import com.test.app.R

class MainViewModel : BaseViewModel() {

    companion object {
        val isVisibleNavigationBottom = ObservableInt(View.VISIBLE)
    }
    fun checkLogin(act: Activity) {
        if (isTEST){
            isVisibleNavigationBottom.set(View.GONE)
            Navigation.findNavController(act, R.id.nav_host_fragment).navigate(R.id.main_to_login)
        }else{
            if (CommonDataModel.mLoggedIn) {
                isVisibleNavigationBottom.set(View.VISIBLE)
            } else {
                isVisibleNavigationBottom.set(View.GONE)
                Navigation.findNavController(act, R.id.nav_host_fragment).navigate(R.id.main_to_login)
            }
        }
    }

}