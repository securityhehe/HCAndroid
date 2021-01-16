package com.hc.uicomponent.call

import android.app.Activity
import com.hc.uicomponent.R
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.base.PageBase
import com.hc.uicomponent.stack.ActivityStack
import com.tools.network.callback.RDClient
import com.tools.network.entity.HttpResult
import com.tools.network.exception.ApiException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume

open class NetCallBack<T>(var isLoadingVisible: Boolean = false) : Callback<T> {


    override fun onFailure(call: Call<T>, t: Throwable) {
        try {

            val url = call.request().url().toString()
            println("[network error ------------------>>START<<---------------]")
            println("[network error http url<$url>")
            t.printStackTrace()
            println("[network error ------------------->>END<<----------------]")

            if (!call.isCanceled) {
                if (t is ApiException) {
                    //这个是服务端抛出的特定的错误类型
                    if (IExceptionHandling.instance?.onSystemInterceptApiError(t.result) == false) {
                        onApiFailure(t.result)
                    }
                } else if (t is IOException) {
                    if (IExceptionHandling.instance?.onSystemInterceptIOError(call, t) == false) {
                        onUserHandlerIOError(t)
                    }
                    //你的是否消费掉这个异常。
                } else {
                    if (IExceptionHandling.instance?.onSystemInterceptOtherError(call, t) == false) {
                        onUserHandlerOtherError(t)
                    }
                }
            }
            if (isLoadingVisible) {
                dismissLoadingDialog()
                isLoadingVisible = false

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cancelContinuation()
            onCallDone()
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            if (isLoadingVisible) {
                dismissLoadingDialog()
                isLoadingVisible = false
            }
            if (response.isSuccessful) {
                response.body()?.let {
                    onApiSuccess(it)
                }
            }
        }catch (e:Exception){

        }finally {
            cancelContinuation()
            onCallDone()
        }
    }


    open fun onApiSuccess(result: T) {  //服务端返回正常数据。

    }

    open fun onCallDone() {

    }

    open fun onUserHandlerIOError(t: IOException) { //IO异常

    }

    open fun onApiDataNull() { //调用响应成功，但是数据有问题。

    }

    open fun onApiFailure(apiException: HttpResult<*>) { // api 异常

    }

    open fun onUserHandlerOtherError(t: Throwable) {

    }

    open fun cancelContinuation() {

    }

    fun showLoadingDialog(message: String = "", canCancel: Boolean = false, call: Call<R>) {
        val currentAct = ActivityStack.currentActivity()
        currentAct?.let {
            if (it is PageBase) {
                if (it.isCanShowDialog()) {
                    it.showLoadingDialog(true) {
                        call.cancel()
                    }
                }
            }
        }
    }

    private fun dismissLoadingDialog() {
        val currentAct = ActivityStack.currentActivity()
        currentAct?.let {
            if (it is PageBase) {
                it.dismissLoadingDialog()
            }
        }
    }


}

interface IExceptionHandling {
    companion object {
        var instance: IExceptionHandling? = null
    }

    fun onSystemInterceptApiError(result: HttpResult<*>): Boolean {
        return false
    }

    fun <T> onSystemInterceptIOError(call: Call<T>, t: Throwable): Boolean {
        return false
    }

    fun <T> onSystemInterceptOtherError(call: Call<T>, t: Throwable): Boolean {
        return false
    }
}


/**
 * (通用)负责请求服务端数据(协程方式)
 */
suspend fun <S, T, R : HttpResult<T>> BaseViewModel.reqApi(
    serviceClass: Class<S>,
    block: S.() -> Call<R>,
    callDone: (() -> Unit)? = null,
    apiFailure: ((apiExceptionCallback: HttpResult<*>) -> Boolean)? = null,
    otherExCallback: ((otherExCallback: Throwable) -> Unit)? = null,
    ioExCallBack: ((ioException: IOException) -> Boolean)? = null,
    apiDataNull: (() -> Unit)? = null,
    isShowLoading: Boolean = false,
    isCancelDialog: Boolean = true
) = suspendCancellableCoroutine<R> { continuation ->
    val service: S = RDClient.getService(serviceClass)
    val call = service.block()
    continuation.invokeOnCancellation {
        println("invokeOnCancellation: cancel the request.")
        call.cancel()
    }
    var dialogAct: Activity? = null
    if (isShowLoading) {//[处理点击请求情况下]显示dialog
        dialogAct = ActivityStack.currentActivity()
        dialogAct?.let {
            if (dialogAct is PageBase) {
                dialogAct.showLoadingDialog(isCancelDialog) {
                    call.cancel()
                }
            }
        }
    }
    val callback = object : NetCallBack<R>(isShowLoading) {
        override fun onApiDataNull() {
            apiDataNull?.invoke() ?: super.onApiDataNull()
        }

        override fun onApiFailure(apiException: HttpResult<*>) {
            apiFailure?.invoke(apiException) ?: super.onApiFailure(apiException)
        }

        override fun onCallDone() {
            callDone?.invoke() ?: super.onCallDone()
        }

        override fun onApiSuccess(result: R) {
            continuation.resume(result)
        }

        override fun onUserHandlerIOError(t: IOException) {
            ioExCallBack?.invoke(t) ?: super.onUserHandlerIOError(t)
        }

        override fun onUserHandlerOtherError(t: Throwable) {
            otherExCallback?.invoke(t) ?: super.onUserHandlerOtherError(t)
        }

        override fun cancelContinuation() {
            continuation.cancel()
        }
    }
    call.enqueue(callback)

}


/**
 * 负责请求服务端数据(非协程方式)
 */
fun <S, T, R : HttpResult<T>> reqApi(
    serviceClass: Class<S>,
    block: S.() -> Call<R>,
    successCallback: ((R) -> Unit)? = null,
    callDone: (() -> Unit)? = null,
    apiFailure: ((apiException: HttpResult<*>) -> Boolean)? = null,
    ioExCallback: ((ioEx: IOException) -> Boolean)? = null,
    emptyCallback: (() -> Unit)? = null,
    isShowLoading: Boolean = false,
    isCancelDialog: Boolean = false
) {
    val service: S = RDClient.getService(serviceClass)
    val call = service.block()
    if (isShowLoading) {
        val currAct = ActivityStack.currentActivity()
        currAct?.let {
            if (currAct is PageBase) {
                currAct.showLoadingDialog(isCancelDialog) {
                    call.cancel()
                }
            }
        }
    }
    val callback = object : NetCallBack<R>(isShowLoading) {
        override fun onApiDataNull() {
            emptyCallback?.invoke() ?: super.onApiDataNull()
        }

        override fun onApiFailure(apiException: HttpResult<*>) {
            apiFailure?.invoke(apiException) ?: super.onApiFailure(apiException)
        }

        override fun onCallDone() {
            callDone?.invoke() ?: super.onCallDone()
        }

        override fun onApiSuccess(result: R) {
            successCallback?.invoke(result)
        }

        override fun onUserHandlerIOError(t: IOException) {
            ioExCallback?.invoke(t) ?: super.onUserHandlerIOError(t)
        }

    }
    call.enqueue(callback)
}

