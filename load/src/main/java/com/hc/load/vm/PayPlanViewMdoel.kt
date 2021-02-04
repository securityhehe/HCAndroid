package com.hc.load.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.hc.data.mall.MainDataRec
import com.hc.data.order.PlatformOrder
import com.hc.data.order.ReqPayToken
import com.hc.load.net.LoanInfoService
import com.hc.uicomponent.base.BaseViewModel
import com.hc.uicomponent.call.reqApi
import com.hc.uicomponent.config.Constants
import com.hc.uicomponent.provider.ContextProvider
import kotlinx.coroutines.launch
import org.jetbrains.anko.bundleOf

class PayPlanViewModel : BaseViewModel() {

    val payOrderInfo = MutableLiveData<MainDataRec>()
    private var isMultiplePeriodFlag: Boolean = false//is multiple period flag : true=>多期 ,false==>单期

    /**
     * 获取支付详情
     */
    fun reqOrderData() {
        viewModelScope.launch {
            val reqMainData = reqApi(LoanInfoService::class.java, { reqHeadPageData() })
            reqMainData.data?.run {
                isMultiplePeriodFlag = (this.orderInfo!!.stages > Constants.NUMBER_1)
                //通知更新UI
                payOrderInfo.value = this
            }
        }
    }

    /**
     * 获取支付方式[H5|SDK]，以及计算还款金额
     */
    fun reqPayTypeAndCalcPayAmount(view: View, isDelay: Boolean): Unit {
        viewModelScope.launch {
            payUpiNativeAppOrMorePayMethod(isDelay) {
                it.run {
                    ContextProvider.mNavIdProvider?.getPayNavId()?.run {
                        val param = bundleOf(
                            Pair(Constants.PRICE, amount),
                            Pair(Constants.COMMISSION, it.handlingFee.toString()),
                            Pair(Constants.EMAIL, email),
                            Pair(Constants.ORDER_ID, orderNo),
                            Pair(Constants.PAY_TOKEN, url?:""),
                            Pair(Constants.PAY_NOTIFY_URL, notifyUrl?:""),
                            Pair(Constants.PAY_APP_ID, appId),
                            Pair(Constants.COMPANY_ID, companyId)
                        )
                        Navigation.findNavController(view).navigate(this,param)
                    }
                }
            }
        }
    }

    /**
     * 获取CF-Token(区分是续期请求还是正常还款请求)
     */
    private fun payUpiNativeAppOrMorePayMethod(isDelay: Boolean, reqPayToken: (ReqPayToken) -> Unit): Unit {
        payOrderInfo.value?.let {
            viewModelScope.launch {
                val reqPayToken = reqApi(LoanInfoService::class.java, block = {
                    if (isDelay)
                        reqDelayRepayToken(it.orderInfo?.id)
                    else
                        reqRepayToken(it.orderInfo?.id, Constants.NUMBER_1)
                })
                reqPayToken.data?.run {
                    reqPayToken(this)
                }
            }
        }

    }


    /**
     * 组装平台订单数据
     */
    fun groupPlatformOrderInfo2MainDataRec(platformOrder: PlatformOrder) {
        platformOrder.run {
            val mainDataRec = MainDataRec(
                isDeplay = this.isDeplay,
                deplayFee = this.deplayFee,
                deplayDay = this.deplayDay,
                deplayGstFee = this.deplayGstFee,
                deplayHandlingFee = this.deplayHandlingFee,
                repaymentHandlingFee = this.repaymentHandlingFee,
                orderInfo = this.orderInfo,
                orderStages = this.orderStages,
                orderRepay = this.orderRepay
            )
            isMultiplePeriodFlag = (mainDataRec.orderInfo!!.stages > Constants.NUMBER_1)
            //通知更新UI
            payOrderInfo.value = mainDataRec
        }
    }
}