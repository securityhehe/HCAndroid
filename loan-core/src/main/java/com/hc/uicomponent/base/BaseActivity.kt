package com.hc.uicomponent.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hc.uicomponent.R
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.utils.HandlerLivedataUtils
import com.hc.uicomponent.utils.StatusBarUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

open  class BaseActivity<B : ViewBinding> constructor(
    @LayoutRes var actLayout: Int,
    var isFullScreen: Boolean = true,
    var isRetry: Boolean = false
) : AppCompatActivity(), PlaceholderLayout.OnReloadListener, PageBase {

    private var mLoadingDialog: Dialog? = null
    private var mPageStartTime: Long = 0
    private var mIsResumed = true
    private var mIsAlertThisFocus = true
    private var isRTLLayout = false
    var mBinding: B? = null
    var mActivity: FragmentActivity? = null
    private val mAlertPendingTask = LinkedList<Runnable>()
    val alertQueueSize: Int
        get() = mAlertPendingTask.size

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configWindow()
        if (isFullScreen) {
            StatusBarUtils.with(this).init()
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        mActivity = this
        val rootView = LayoutInflater.from(this).inflate(actLayout, null)
        getViewBindProject(rootView)
        setContentView(rootView)
        initConfig(savedInstanceState)
        createViewModelObject()
        injectBindingViewClick()
        //生命周期状态分发。
    }

    override fun onResume() {
        super.onResume()
        mIsResumed = true
        processAlertTask()
    }

    override fun onPause() {
        super.onPause()
        mIsResumed = false
    }

    private fun getViewBindProject(rootView: View?) {
        try {
            val superClass: Type? = javaClass.genericSuperclass
            superClass?.let {
                if (superClass is ParameterizedType) {
                    val type: Type = superClass.actualTypeArguments[0]
                    val bindClazz = type as Class<*>
                    val method: Method = bindClazz.getMethod("bind", View::class.java)
                    mBinding = method.invoke(null, rootView) as B
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun configWindow() {}

    open fun initConfig(savedInstanceState: Bundle?) {
        try {
            if ("ar" == Locale.getDefault().language) {
                isRTLLayout = true
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
            } else {
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun injectBindingViewClick() {}

    private fun createViewModelObject() {
        val thisFields = this.javaClass.declaredFields
        thisFields.forEach {
            it.isAccessible = true
            val annotation = it.getAnnotation(BindViewModel::class.java)
            if (annotation != null) {
                val clazz: Class<BaseViewModel> = it.type as Class<BaseViewModel>;
                val viewModel = this.createGetViewModel(clazz)
                /**if (viewModel is BaseRefreshViewModel) {
                    if (isRetry) {
                        viewModel.retryLoadListener = this
                    }
                }**/
                it.set(this, viewModel)
                HandlerLivedataUtils.registerActObserver(this, viewModel)
            }
        }
    }

    open fun <M : BaseViewModel> createGetViewModel(clazz: Class<M>) =
        ViewModelProvider(this).get(clazz)

    override fun onReload(v: View) {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        handleSaveInstanceState(outState)
    }

    open fun handleSaveInstanceState(outState: Bundle?) {}


    override fun showLoadingDialog() {
        showLoadingDialog(false)
    }
    fun showLoadingDialog(resId: Int) {
        showLoadingDialog(getString(resId), false,{})
    }

    override fun showLoadingDialog(cancelAble: Boolean, call: () -> Unit) {
        showLoadingDialog("",cancelAble,{})
    }
    override fun showLoadingDialog(canCancel: Boolean) {
        showLoadingDialog("", canCancel,{})
    }

    override fun showLoadingDialog(message: String, canCancel: Boolean, cancelCall:()-> Unit?) {
        if (mLoadingDialog == null) {
            mLoadingDialog = Dialog(this, R.style.ThemeDialogTransparent)
            mLoadingDialog?.let {
                it.setCancelable(canCancel)
                if (!it.isShowing) {
                    it.setContentView(R.layout.dialog_waiting)
                    it.show()
                    it.findViewById<TextView>(R.id.tv_message).text = message
                }
                it.setOnCancelListener {
                    cancelCall.invoke()
                }
            }
        } else {
            mLoadingDialog?.let {
                if (!it.isShowing) {
                    it.show()
                }
            }
        }
    }

    override fun dismissLoadingDialog() {
        mLoadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoadingDialog()
    }

    /***
     * 页面打开时长统计
     */
    override fun onStart() {
        super.onStart()
        mPageStartTime = System.currentTimeMillis()
    }

    override fun onStop() {
        super.onStop()
        val duration = System.currentTimeMillis() - mPageStartTime
        logDurationIfNeed(duration)
    }

    open fun logDurationIfNeed(duration: Long) {}

    /**
     * 弹出窗口队列处理。
     */

    fun alert(alertTask: Runnable, isNeedGuarantee: Boolean) {
        if (hasWindowFocus() && mIsResumed && !mIsAlertThisFocus) {
            Log.i(TAG, "  alert run ")
            runAlertTask(alertTask)
        } else if (isNeedGuarantee) {
            mAlertPendingTask.add(alertTask)
        }

    }

    override fun alert(alertTask: Runnable) {
        if (hasWindowFocus() && mIsResumed && !mIsAlertThisFocus) {
            Log.i(TAG, "  alert run ")
            runAlertTask(alertTask)
        } else {
            mAlertPendingTask.add(alertTask)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mIsAlertThisFocus = false
            processAlertTask()
        }
    }

    fun processAlertTask() {
        if (!mAlertPendingTask.isEmpty() && !isFinishing && !mIsAlertThisFocus) {
            runAlertTask(mAlertPendingTask.poll())
        }
    }

    private fun runAlertTask(alertTask: Runnable) {
        alertTask.run()
        mIsAlertThisFocus = true
    }

    companion object {
        val TAG = "BaseActivity"
        open fun startActivityAnim(activity: Activity, clazz: Class<*>) {
            val intent = Intent(activity, clazz)
            startActivityAnim(activity, intent)
        }

        open fun startActivityAnim(activity: Activity, intent: Intent) {
            activity.startActivity(intent)
            activity.overridePendingTransition(
                R.anim.anim_right_to_middle,
                R.anim.anim_middle_to_left
            )
        }
    }

    override fun isCanShowDialog(): Boolean {
       return (mIsResumed && hasWindowFocus())
    }

    open fun finishAnim() {
        finish()
        overridePendingTransition(R.anim.anim_left_to_middle, R.anim.anim_middle_to_right)
    }


}