package com.hc.data.mall

import com.google.gson.annotations.SerializedName
import com.hc.data.order.OrderBillRec
import java.io.Serializable


/**
 * main head page data rec
 */
data class MainDataRecLogout(
		var userCredit: String,// 2000
		var nbfcName:String    //显示处在“未登录 | 已登录未认证个人信息”时，显示支付公司名称
)

/**
 * main head page data rec
 */
data class MainDataRec(
		var isDeplay: Int = 0,// 1
		var historOrder: Int = 0,// 1
		var userType: Int = 0,// 1
		var userCredit: Double=0.00,// 2000
		var maxCredit: Double=0.00,// 20000
		var orderType: Int=0,// 1-正常下单文案；2-失效订单文案；3-关闭订单文案

		var deplayFee: Double,// 100
		var deplayDay: Int,// 10
		var deplayGstFee:Double,//续期税费
		var deplayHandlingFee:Double,//续期手续费
		var repaymentHandlingFee:Double,//还款手续费

		var orderInfo: OrderInfo?,
		var orderStages: List<OrderStage>,
		var orderRepay: OrderRepay,
		var userAuth: UserAuth?=null,
		var goodsSxs: MutableList<GoodsSx>? = null,

		@SerializedName("diversionProductList")
        var diversionList: List<DiversionProduct>?=null, // 导流APP列表

		var nextTime:String?=null, //拒绝后的再借时间
		var dpNodes: Int = 0,  // 导流的节点代码

        var otherOrderDisPlay:Int=0,//标识是否显示Order页,0-不显示，1-显示
		var nbfcName:String = "",//显示处在“未登录 | 已登录未认证个人信息”时，显示支付公司名称
        var lastOrderIsEnd:Int=0,//标识是否复贷用户最新一笔订单已结清,0-未结清，1-已结清 . 注：如果已经结清，处理自动跳转到订单提交页面

        var indexImgPath:String?=""//nbfc 图片

):Serializable

data class OrderInfo (
        var address: String,
        var coordinate: String,
        var createTime: String,
        var createUser: String,
        var fee: String,
        var goodLabelId: String,
        var goodsId: String,
        var goodsName: String,
        var goodsPrice: String,
        var holdTime: String,
        var id: String,
        var ip: String,
        var isNew: String,
        var nickName: String,
        var orderNo: String,
        var orderSn: String,
        var passType: String,
        var platformId: String,
        var presentRecord: String,
        var realPath: String,
        var remark: String,
        var stages: Int,
        var state: String,
        var sweepstake: String,
        var timeLimit: String,
        var timeType: String,
        var updateTime: String,
        var updateUser: String,
        var userId: String,
        var userIp: String
):Serializable

data class OrderRepay(
        var alreadyRepaid: String,
        var amount: String,
        var amountPaid: String,
        var amountRelief: String,
        var createTime: String,
        var createUser: String,
        var currentNode: Boolean,// false
        var customerIdNumber: String,
        var customerName: String,
        var deadline: String,
        var defaultSum: String,
        var deplayFee: String,
        var deplayRepayTimeStr: String,
        var expireNeedRepay: String,
        var id: String,
        var initRepayTime: String,
        var interestSum: String,
        var numberPeriods: String,
        var orderId: String,
        var orderNo: String,
        var penaltyAmout: String,
        var penaltyDay: String,
        var phone: String,
        var realRepayAmount: String,
        var realityRepayTime: String,
        var repaySchedule: String,
        var repayTime: String,
        var repayTimeStr: String,//
        var stageStr: String,
        var stages: String,
        var state: String,
        var stateStr: String,//
        var totalRepayment: Double,
        var type: String,
        var updateTime: String,
        var updateUser: String,
        var userId: String,
        var withhold: String
):Serializable

data class OrderStage(
        var amount: String,
        var amountPaid: String,
        var amountPayable: String,
        var amountRelief: String,
        var createTime: String,
        var czAmount: String,
        var czDefaultAmount: String,
        var czInterest: String,
        var czPenaltyAmount: String,
        var defaultAmount: String,
        var delayTimes: String,
        var deplayFee: String,
        var id: String,
        var initRepayEndTime: String,
        var initRepayStartTime: String,
        var initRepayTime: String,
        var interest: String,
        var isLast: String,
        var isWithhold: String,
        var isvalid: String,
        var maxTime: String,
        var maxTotalRepayment: String,
        var minTime: String,
        var minTotalRepayment: String,
        var num: String,
        var orderId: String,
        var orderNo: String,
        var originalRepayTime: String,
        var originalRepayTimeStr: String,
        var penaltyAmount: String,
        var penaltyDay: String,
        var phone: String,
        var platformId: String,
        var platformName: String,
        var realName: String,
        var realityRepayTime: String,
        var repayId: String,
        var repayStartTime: String,
        var repayTime: String,
        var repayTimeStr: String,//
        var state: String,
        var stateStr: String,//
        var totalRepayment: Double,
        var updateTime: String,
        var userId: String
):Serializable

data class UserAuth(
        var bankCardState: String,
        var bankCardTime: String,
        var id: String,
        var idState: String,
        var idTime: String,
        var kycState: String,
        var kycTime: String,
        var userId: String,
        var userState: String, //10-不允许；20-允许
        var workInfoState: String,
        var workInfoTime: String
):Serializable


data class GoodsSx(
		var borrowDays: Int,// 2
		var continuouRepayNum: String,
		var createTime: String,// 2019-09-11 16:00:09
		var dateType: Int,// 1
		var dayRate: Double,// 10.00
		var goodLabelId: Int,// 1
		var goodLabelName: String,
		var goodsSxStagings: List<GoodsSxStagings>,
		var id: String,// 1
		var increment: Double,// 100.00
		var incrementRemark: String,
		var maxAmount: Double,// 0.0
		var maxMoney: String,
		var minAmount: Double,// 100
		var name: String,// 111
		var numberPeriods: Int,// 1
		var overdueRate: Double,// 10.00
		var overdueRemark: String,
		var payRemark: String,
		var platformId: Int,// 1
		var platformName: String,
		var platformType: Int,
		var prepaymentRemark: String,
		var productRemark: String,
		var rate: Double,// 10
		var state: Int,// 10
		var updateTime: String,// 2019-09-23 17:53:08
		var historOrder:Int
):Serializable{
	var isCanBuy :Boolean = true
	var choseAmount:Double = 0.00//所选金额
}


data class GoodsSxStagings(
		var createTime: String,// 2019-09-24 14:23:07
		var createUser: String,// jiangshuaili
		var dayAmountRate: Double,// 20.00
		var goodsSxId: Int,// 6
		var id: Int,// 80
		var num: Int,// 1
		var repayAmountRate: Double,// 80.00
		var repayDate: Int,// 20
		var updateTime: String,// 2019-09-24 14:23:07
		var updateUser: String// jiangshuaili
):Serializable

/**
 * 导流APP实体
 */
data class DiversionProduct(
    val borrowLimit: Double, // 额度
    val createTime: String,
    val dayRate: Double,  // 比率
    val id: Int,     // 产品ID
    val isTop: Int, // 是否优先展示（首页）
    val logoUrl: String,
    val nodes: String, //  节点
    val productCode: String,
    val productName: String,//  平台名字
    val sort: Int,
    val state: Int,
    val timeLimit: Int,   // 时间
    val updateTime: String,
    val url: String  // APP的URL跳转路径
)

/**
 * 用来记录导流APP点击
 */
data class DiversionCount(
    var id: Int,     // 产品ID
    var uploadTime: String // APP导流点击上传时间
)

/**
 * 提交订单-前期检测
 */
data class CheckOrder(
    var epochNo: String?,// 1233
    var appContractSigning: Int,// 1
    var signImgPath: String?,// http://www.baidu.com
    var content: String,
    val orderBills: List<OrderBillRec>?,
    val platformExt: PlatformExt,
    var bankCard:BankCardInfo?, //用户绑定的银行卡号
    var contentUrlV1:String?, //借款协议
    var contentUrlV2:String? //借款批准函
) :Serializable

data class BankCardInfo(
    val accountAddress: String,
    val accountUser: String,
    val agreeNo: String,
    val agreementPath: Any,
    val bankName: String,
    val bankNo: String,
    val bankPhone: String,
    val bindTime: String,
    val bindType: Any,
    val createTime: Any,
    val id: Int,
    val ifscCode: String,
    val isvalid: Any,
    val orderId: Int,
    val state: Int,
    val updateTime: Any,
    val userId: Int
): Serializable

data class PlatformExt(
		val commissionPaymentEn: String,
		val commissionPaymentIn: String,
		val createTime: String,
		val cusEmail: String,
		val emailCusPhone: String,
		val gstEn: String,
		val gstIn: String,
		val h5RepayUrl: String,
		val id: String,
		val isShow: Int, // 1 展示，2 隐藏，3 不展示
		val platformFirebaseKey: String,
		val platformFirebasePackagePath: String,
		val platformFirebaseUriProfix: String,
		val platformId: Int,
		val platformOriginUri: String,
		val shortLink: String,
		val smsCusPhone: String,
		val technicalServiceFeeEn: String,
		val technicalServiceFeeIn: String,
		val updateTime: String
)