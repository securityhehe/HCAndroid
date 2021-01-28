package com.hc.load.epoch.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import com.blankj.utilcode.util.ToastUtils
import com.hc.data.CUR_RUNTIME_EVN
import com.hc.data.EPOCH_FAILED_TIMES
import com.hc.data.EPOCH_FAILED_TIMES_MAX
import com.hc.data.common.CommonDataModel
import com.hc.load.epoch.receiver.EpochSyncedType
import com.hc.uicomponent.base.PageBase
import com.hc.uicomponent.config.EPOCH_APP_ID
import com.hc.uicomponent.config.EPOCH_POST_URL
import com.hc.uicomponent.config.EPOCH_SECRET
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.mmkv
import com.yinda.datasyc.http.SDKManage
import java.util.*

/**
 * epoch
 */
object EpochLogicUtils {
    private var epochMsgSuccess = false
    private var epochAppSuccess = false
    private var epochFailedTimes: Int = mmkv().decodeInt(EPOCH_FAILED_TIMES,0)
    private var isSubmit = false //控制防止多次提交订单，默认为false,true 标识已经触发了订单提交
    private var isControlStatCount = false //控制统计错误发生次数不重复统计（流程执行时:失败时仅允许统计一次）

    private const val EPOCH_FAILED_MESSAGE = 0x2f

    /**
     * 初始化Epoch-Sdk
     */
    fun initEpochSDk(context :Context): Unit {
        // epoch SDK init
        SDKManage.getInstance().init(context, EPOCH_APP_ID, EPOCH_SECRET)
    }


    fun showEpochDialog(): Unit {
        ActivityStack.currentActivity()?.let {
            if(!it.isDestroyed && !it.isFinishing){
                if (it is PageBase){
                    it.showLoadingDialog()
                }
            }
        }
        //重置可下单标记
        isSubmit = false
        //控制重复统计
        isControlStatCount = false

    }


    fun hideEpochDialog(): Unit {
        ActivityStack.currentActivity()?.let {
            if(!it.isDestroyed && !it.isFinishing){
                if (it is PageBase){
                    it.dismissLoadingDialog()
                }
            }
        }
    }

    private fun resetEpochCheck(isReset:Boolean = false) {
        epochMsgSuccess = false
        epochAppSuccess = false
        if (isReset) {
            epochFailedTimes = 0
            //重置缓存累计epoch获取数据的失败次数
            mmkv().encode(EPOCH_FAILED_TIMES,0)
        }
    }

    var handler: Handler? = EpochHandler()

    @SuppressLint("HandlerLeak")
    class EpochHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                EPOCH_FAILED_MESSAGE -> {
                    handleEpochFailed()
                }
            }
        }
    }

    private lateinit var callBackSubmitOrder: ISubmitOrderCallback
    interface ISubmitOrderCallback{
        fun handlerSubmitOrder()
//        fun requestSmsPermission()
    }

    /**
     * 设置 30s 后关闭对话框
     */
    fun startEpochTimer(callBackSubmitOrder:ISubmitOrderCallback) {
        this.callBackSubmitOrder = callBackSubmitOrder
        stopEpochTimer()
        if (handler == null) {
            handler = EpochHandler()
        }
        val message = handler!!.obtainMessage()
        message.what = EPOCH_FAILED_MESSAGE
        handler!!.sendMessageDelayed(message,30000L)
    }

    private fun stopEpochTimer() {
        handler?.removeMessages(EPOCH_FAILED_MESSAGE)
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    /**
     * Epoch 同步回调处理
     **/

    fun update(o: Observable?, arg: Any?) {
        // 如果没有返回值，不会出现这种情况还是判断下，提示重试
        if (arg == null) {
            handleEpochFailed()
            return
        }

        val intent = arg as Intent
        val syncedType: String? = intent.getStringExtra("SyncedType")


        // 如果没有返回值，不会出现这种情况还是判断下，提示重试
        if (syncedType.isNullOrBlank()) {
            handleEpochFailed()
            return
        }
        // 获取返回值信息打印
        val sysState: Boolean = intent.getBooleanExtra("SyncedState", false)
        val code: Int = intent.getIntExtra("SyncedCode", 0)
        if (CUR_RUNTIME_EVN) {
            val msg: String? = intent.getStringExtra("SyncedMsg")
            println("SyncedType: $syncedType,  同步状态: $sysState,  code: $code,   msg: $msg")
        }

        /**
         * 如果是 permission 则去请求权限
         * 如果 APP 以及 MSG 同步成功，则提交订单
         * 如果 APP 或者 MSG 失败，则返回重试
         * 当失败次数为 五次 或者更多的时候就去提交订单
         */
        when(syncedType) {
            EpochSyncedType.PERMISSION.toString() -> {
                hideEpochDialog()
//                if (this::callBackSubmitOrder.isInitialized) {
//                    this.callBackSubmitOrder.requestSmsPermission()
//                }
            }
            EpochSyncedType.APP.toString() -> {

                if (!sysState) {
                    handleEpochFailed()
                    return
                } else {
                    epochAppSuccess = true
                    if (epochMsgSuccess && epochAppSuccess) {
                        hideEpochDialog()

                        resetEpochCheck(true)
                        stopEpochTimer()
                        if (!isSubmit) {
                            isSubmit = true
                            if (this::callBackSubmitOrder.isInitialized) {
                                callBackSubmitOrder.handlerSubmitOrder()
                            }
                        }
                    }
                }
            }

            EpochSyncedType.MSG.toString() -> {
                if (!sysState) {
                    handleEpochFailed()
                    return
                } else {
                    epochMsgSuccess = true
                    if (epochMsgSuccess && epochAppSuccess) {
                        hideEpochDialog()

                        resetEpochCheck(true)
                        stopEpochTimer()
                        if (!isSubmit) {
                            isSubmit = true
                            if (this::callBackSubmitOrder.isInitialized) {
                                callBackSubmitOrder.handlerSubmitOrder()
                            }
                        }
                    }
                }
            }

            /*EpochSyncedType.IMG.toString() ->
            EpochSyncedType.DEVICE.toString() ->
            EpochSyncedType.CONTACT.toString() ->*/
        }

    }

    private fun handleEpochFailed() {
        hideEpochDialog()

        stopEpochTimer()
        //
        if (isControlStatCount){
            return
        }
        isControlStatCount = true
        //
        epochFailedTimes += 1
        val isOver5Times = epochFailedTimes >= EPOCH_FAILED_TIMES_MAX

        resetEpochCheck(isOver5Times)

        if (isOver5Times) {
            if (!isSubmit) {
                isSubmit = true
                if (this::callBackSubmitOrder.isInitialized) {
                    callBackSubmitOrder.handlerSubmitOrder()
                }
            }
        } else {
            mmkv().encode(EPOCH_FAILED_TIMES,epochFailedTimes)
            ToastUtils.showShort("The server is busy, please wait for minutes")
        }
    }

    /**
     * SDK同步数据
     */
    fun epochSynData(epochNo:String): Unit {
        SDKManage.getInstance().SynData(epochNo,CommonDataModel.mTokenData?.userId?:"", "", EPOCH_POST_URL)
    }

}