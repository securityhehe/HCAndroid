package com.hc.uicomponent.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hc.uicomponent.annotation.BindViewModel
import com.hc.uicomponent.utils.HandlerLivedataUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class BaseFragment<B : ViewBinding> constructor(@LayoutRes var fragLayout: Int, var isRetry: Boolean = true) : Fragment()  {

    lateinit var mActivity: FragmentActivity
    lateinit var mFragmentBinding: B

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = LayoutInflater.from(mActivity).inflate(fragLayout, container, false)
        //创建Databinding对象
        createBindingObj(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //获取配置参数
        initConfig(savedInstanceState)
        //创建ViewModel对象
        createViewModelObject()
        //绑定ViewModel对象-XML
    }


    open fun initConfig(savedInstanceState: Bundle?) {}

    private fun createBindingObj(rootView: View) {
        try {
            val superClass: Type? = javaClass.genericSuperclass

            if (superClass is ParameterizedType) {
                val type: Type = superClass.actualTypeArguments[0]
                val bindClazz = type as Class<*>
                if (bindClazz.superclass == null) return
                val method: Method = bindClazz.getMethod("bind", View::class.java)
                mFragmentBinding = method.invoke(null, rootView) as B
            }
        } catch (e: Exception) {
            println("实例化Binding对象出错！")
            e.printStackTrace()
        }
    }


    private fun createViewModelObject() {
        val thisFields = this.javaClass.declaredFields
        thisFields.forEach {
            it.isAccessible = true
            val annotation = it.getAnnotation(BindViewModel::class.java)
            if (annotation != null) {
                val viewModelClazz: Class<BaseViewModel> = it.type as Class<BaseViewModel>
                val viewModel = this.createGetViewModel(viewModelClazz)
                //赋值Vm
                it.set(this, viewModel)
                //利用反射动态建立[liveData&Observer]观察者关系
                HandlerLivedataUtils.registerFragObserver(this, viewModel)
            }
        }
    }

    open fun <M : BaseViewModel> createGetViewModel(clazz: Class<M>) = ViewModelProvider(this).get(clazz)


}