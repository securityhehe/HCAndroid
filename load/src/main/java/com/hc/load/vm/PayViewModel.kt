package com.hc.load.vm

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import com.hc.data.order.CFPayInfo
import com.hc.data.user.UserType
import com.hc.load.R
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.WEB_VIEW_OPERATE
import com.hc.uicomponent.provider.ContextProvider
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.utils.GsonUtils
import com.hc.uicomponent.utils.TextUtil
import kotlinx.coroutines.launch


/**
 * @Author : ZhouWei
 * @TIME   : 2020/2/28 17:54
 * @DESC   : 处理实际支付逻辑 ViewModel
 */
class PayViewModel  : BaseViewModel(){

    /**
     * req all select info
     */
    fun reqNetBankdingInfoEnum(isCashFreePay:Boolean,showMoreBank:(List<UserType>)-> Unit) {
        viewModelScope.launch {
            val reqNetBankingResult = reqApi(LoanInfoService::class.java, { queryListInfo() })
            reqNetBankingResult.data?.run {

                if (isCashFreePay) {
                    this.netBank?.run {
                        showMoreBank(this)
                    }
                } else {
                    this.razorNetBank?.run {
                        showMoreBank(this)
                    }
                }
            }
        }
    }

    /**
     * 加载历史支付
     */
    private lateinit var cfPayInfoList:MutableList<CFPayInfo>
    fun reqPayHistory(showHistory:(MutableList<CFPayInfo>)->Unit) {
        viewModelScope.launch {
            val payHistoryRecord = reqApi(LoanInfoService::class.java, block = { reqPayHistoryForSDK() })
            payHistoryRecord.data?.run {
                if (this.size == 0) return@run

                cfPayInfoList = mutableListOf<CFPayInfo>()

                for (cfPayHistory in this) {
                    if (cfPayHistory.payInfo != null && !TextUtil.isEmpty(cfPayHistory.payInfo)) {
                        val payInfo = GsonUtils.jsonString2Bean(cfPayHistory.payInfo, CFPayInfo::class.java)
                        if (payInfo != null) cfPayInfoList.add(payInfo)
                    }
                }
                if (cfPayInfoList.isNotEmpty()) {
                    showHistory(cfPayInfoList)
                }
            }
        }
    }

    /**
     * 定义三种支付枚举
     */
    enum class PayMethodEnum {
        UPI, NETBanking, CreditCard;

        /**
         * 更新支付方式的展示图片
         */
        fun showPayMethodIcon(): Drawable {
            return when (this) {
                UPI -> ContextProvider.getDrawable(R.mipmap.in_cf_upi)!!
                NETBanking -> ContextProvider.getDrawable(R.mipmap.in_cf_netbanking)!!
                CreditCard -> ContextProvider.getDrawable(R.mipmap.in_cf_credit_card)!!
            }
        }

        /**
         * 更新支付银行的展示文本
         */
        fun showPayMethodTxt(): Int {
            return when (this) {
                UPI -> R.string.pay_upi
                NETBanking -> R.string.pay_netbanking
                CreditCard -> R.string.pay_credit_or_debit_card
            }
        }
    }

    /**
     * 接收CF-SDK支付处理回调
     */
    fun onActivityResult(orderId:String,
                         orderAmount:String,

                         paymentMode:String?,
                         referenceId:String?,
                         txMsg:String?,
                         txStatus:String?,
                         txTime:String?,
                         payInfo:String,
                         payType:Int,
                         payAccount:String,
                         requestCode: Int,
                         resultCode: Int,
                         data: Intent?) {

        viewModelScope.launch {
            val payCallBackRes = reqApi(LoanInfoService::class.java,
                    block = { payOver2SyncInfo(
                            orderId,
                            orderAmount,
                            paymentMode,
                            referenceId,
                            txMsg,
                            txStatus,
                            txTime,
                            payInfo,
                            payType,
                            payAccount
                    )
                    },
                    apiFailure = {
                        finishCurrentPageAndSendBroadcast()
                        true
                    }

            )

            println("----->>>支付回传数据结果--> $this")
            finishCurrentPageAndSendBroadcast()
        }
    }

    /**
     * 发送通知给支付详情页 并 finish当前页面
     */
    private fun finishCurrentPageAndSendBroadcast() {
        val curActivity = ActivityStack.currentActivity()
        //关闭当前页面。
       // ActivityManageUtils.remove(curActivity)
        ContextProvider.app.sendBroadcast(Intent(WEB_VIEW_OPERATE))
    }

    /**
     * RazorPay 支付成功回调
     */
    fun onSucceeded(
        orderId:String,
        orderAmount:String,
        paymentMode:String = "",
        txMsg:String = "",
        txStatus:String = "SUCCESS",
        txTime:String = "",
        payInfo:String,
        payType:Int = 0,
        payAccount:String = "",

        razorpayPaymentId: String,
        paymentData: com.razorpay.PaymentData?) {

        try{
            viewModelScope.launch {
                val payCallBackRes = reqApi(LoanInfoService::class.java,
                        block = {
                            payOver2SyncInfo(
                                    orderId,
                                    orderAmount,
                                    paymentMode,
                                    razorpayPaymentId,
                                    txMsg,
                                    txStatus,
                                    txTime,
                                    payInfo,
                                    payType,
                                    payAccount
                            )
                        },
                        apiFailure = {
                            finishCurrentPageAndSendBroadcast()
                            true
                        }
                )
                println("----->>>支付回传数据结果--> $this")
                finishCurrentPageAndSendBroadcast()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}