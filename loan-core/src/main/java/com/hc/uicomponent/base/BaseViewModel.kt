package com.hc.uicomponent.base

import android.net.Uri
import android.view.View
import androidx.core.os.bundleOf
import androidx.databinding.*
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.hc.uicomponent.R
import com.hc.uicomponent.provider.ContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job


open class BaseViewModel : ViewModel(), ITaskJob, Observable{

    //缓存请求时发起的job，用于后续中途终止任务时使用！
    lateinit var taskJob :Job

    //用于控制主界面占位图显示的标记值
    open var statusPlaceFlag = ObservableInt(4)

    //用于控制取消请求
    val cancelObservable = ObservableBoolean()//表示当前请求的默认标识状态（没有发起请求）false

    open lateinit var actContext : CoroutineScope

    override fun cancelJob() {
        if (this::taskJob.isInitialized){
            if (taskJob.isActive){
                println("------cancelJob ----")
                taskJob.cancel()
            }
        }
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    @Suppress("unused")
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    fun navigateUp(view: View){
       Navigation.findNavController(view).navigateUp()
    }

    //返回按钮处理
    open fun back(view:View){
        Navigation.findNavController(view).navigateUp()
    }

}


fun BaseViewModel.jumpDeepLikPage(view :View,popupToId:Int?,url:String){
    ContextProvider.mNavIdProvider?.let {
        val opt = NavOptions.Builder()
            .setEnterAnim(R.anim.anim_right_to_middle)
            .setLaunchSingleTop(true)
            .setPopExitAnim(R.anim.anim_middle_to_right)
        popupToId?.let {
            opt.setPopUpTo(it, false)
        }
        val url = Uri.parse(url)
        val a = NavDeepLinkRequest.Builder.fromUri(url).build()
        val navigation = Navigation.findNavController(view)
        navigation.navigate(a, opt.build())
    }

}

interface ITaskJob{
    /**点击请求&显示dialog情况：目的中途取消Job
     * 1.dialog -> key listener -> [多任务 activity -> vm(cancelTask)]
     * 2.dialog -> key listener -> [单任务 -> okHttp-cancel即可]
     */
    fun cancelJob()
}
