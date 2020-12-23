package com.hc.uicomponent.base

import android.view.View
import androidx.databinding.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @Author : ZhouWei
 * @TIME   : 2020/3/18 9:48
 * @DESC   : 此ViewModel会存储到 ViewModelStore 中,
 *           对于某一个activity|fragment的关系为：
 *           >>1-activity包含一个ViewModelStore包含多个ViewModel对象，会在destroy的时候清除ViewModelStore中所有的ViewModel对象，
 *             屏幕旋转时，会缓存ViewModelStore，故所持有的ViewModel对象同样被保留了下来，故:称之为数据包含方案，可替代onSaveInstance(Bundle)
 */
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

interface ITaskJob{
    /**点击请求&显示dialog情况：目的中途取消Job
     * 1.dialog -> key listener -> [多任务 activity -> vm(cancelTask)]
     * 2.dialog -> key listener -> [单任务 -> okHttp-cancel即可]
     */
    fun cancelJob()
}
