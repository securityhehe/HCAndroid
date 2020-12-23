package com.hc.data.order

import com.hc.data.mall.OrderInfo
import com.hc.data.mall.OrderRepay
import com.hc.data.mall.OrderStage
import java.io.Serializable

data class OtherPlatformOrder(
    val deplayFee: Double,
    var deplayGstFee:Double,//续期税费
    var deplayHandlingFee:Double,//续期手续费
    var repaymentHandlingFee:Double,//还款手续费

    var deplayDay: Int,
    var isDeplay: Int,

    var orderInfo: OrderInfo,
    var orderRepay: OrderRepay,
    var orderStages: List<OrderStage>,
    var platform :PlatformData
)


data class PlatformData (
    val authTemplate: String,
    val createCredit: String,
    val createTime: String,
    val downloadUrl: String,
    val feeTemplate: String,
    val h5PayReturnPath: String,
    val h5Url: String,
    val h5WebUrl: String,
    val id: String,
    val initCredit: String,
    val isPush: String,
    val loanTemplate: String,
    val logoUrl: String,
    val maxCredit: String,
    val merchantId: String,
    val penaltyAmoutMax: String,
    val platformCode: String,
    val platformMode: String,
    val platformAppName: String,
    val platformName: String,
    val platformShortName: String,
    val platformType: String,
    val platformUrl: String,
    val relatedIds: String,
    val smsSign: String,
    val updateTime: String
):Serializable



/**
 * pay type bean
 */
data class PayType(
    var id: Int,// 1
    var name: String// Cashfree
)

/**
 * pay address
 */
data class PayUrl(
    var isContinue: Boolean,// true
    var url: String// https://test.cashfree.com/billpay/order/744kypein9ooo0c1crzci
)

data class OrderBillRec(
    var amountYouGet: String,
    var commissionPayment: String,
    var createTime: String,
    var gst: String,
    var id: String,
    var interest: String,
    var loanAmount: String,
    var loanDuration: String,
    var num: String,
    var orderId: String,
    var orderNo: String,
    var repaymentAmount: Double,
    var repaymentDays: String,
    var technicalServiceFee: String,
    var updateTime: String,
    var userId: String
)


class HistoryOrder {
    var list: MutableList<HistoryOrderList>? = null
    var size: Int = 0
}

class HistoryOrderList {
    var createTime: String? = null
    var goodsPrice: String? = null
    var interestSum: String? = null
    var state: String? = null
    var timeLimit: Int = 0
    var timeType: Int = 0
}

/**
 * 触发主动|续期还款请求的实体类
 */
data class ReqPayToken(
    var url: String,//CashFree的Token；RazorPay的orderId
    var orderNo: String,// pgxxxxxxxxxxxxxxx
    var notifyUrl: String,// www.baidu.com
    var email :String,
    var handlingFee:Double, //实际应付手续费
    var appId: String,
    var companyId: Int,
    var amount :String
)

/**
 * 历史支付记录
 */
data class CFPayHistory(
    var createTime: String,
    var id: String,
    var orderId: String,
    var orderNo: String,
    var payInfo: String?,
    var payType: Int,
    var state: String,
    var updateTime: String,
    var userId: String
)

data class CFPayInfo(
    var payType:Int,
    //
    var cardNo:String? = null,
    var cardCvv:String? = null,
    var cardHolderName:String? = null,
//		var cardExpiryMmYy:String? = null,
    var cardNoMmYy:String? = null,
    //
    var upiAccount:String? = null
)

//data class CFUpiPayInfo(
//		var upiAccount:String
//)

