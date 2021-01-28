package com.hc.load.vm

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.hc.data.OrderStateEnum
import com.hc.data.QUESTIONNAIRE_SURVE
import com.hc.load.R
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.provider.CommonProvider
import com.hc.uicomponent.provider.ContextProvider
import kotlinx.coroutines.launch
import java.util.*

class CommitOrderLoadingViewModel :BaseViewModel(){

    private val timer: Timer = Timer()
    lateinit var task: TimerTask
    var countNum = 15

    private var isTimerFinish = false
    private var mCowndown = MutableLiveData<String>()
    private var mClosePage = MutableLiveData<Unit>()

    fun startCountDownLogic(view: View, orderId:String) {
        mCowndown.value = "$countNum"
        task = object : TimerTask() {
            override fun run() {
                if (countNum == 1) {
                    cancelTimerLogic()
                    //到最后还是没有结果，默认跳转到首页
                    mClosePage.value = null
                    return
                }
                --countNum
                mCowndown.value = "$countNum"

                if (countNum % 2 != 0 && countNum != 1){
                    reqOrderState(view,orderId)
                }
            }
        }
        timer.scheduleAtFixedRate(task, 1000, 1000)
    }

    private fun cancelTimerLogic() {
        if (!isTimerFinish) {
            isTimerFinish = true
            if (this::task.isInitialized) {
                task.cancel()
            }
            timer.cancel()
        }
    }

    private fun reqOrderState(view: View,orderId:String) {
        viewModelScope.launch {
            val orderResult = reqApi(LoanInfoService::class.java,block = {getOrderStateInfo(orderId)})
            orderResult.data?.run {
                when(this.state){
                    //征信调查，跳转到回答问题Web页面
                    OrderStateEnum.CREDIT_VERIFIY_LOADING.state -> {
                        cancelTimerLogic()
                        CommonProvider.instance?.getWebViewNavId()?.let {
                            val a = ContextProvider.getString(R.string.mall_order_submit_loan_agreement)
                            val bundle = bundleOf(Pair("link", ContextProvider.getString(QUESTIONNAIRE_SURVE,orderId))
                                , Pair("title", ContextProvider.getString(R.string.order_credit_ask_question_title)))
                            Navigation.findNavController(view).navigate(it, bundle)
                        }
                        mClosePage.value = null
                    }
                    //自动审核不通过
                    OrderStateEnum.AUTO_REFUSED.state  -> {
                        cancelTimerLogic()
                        mClosePage.value = null
                    }
                }
            }
        }
    }

}