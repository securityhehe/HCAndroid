package com.hc.load.net

import com.hc.data.mall.*
import com.hc.data.order.CFPayHistory
import com.hc.data.order.OrderBillRec
import com.hc.data.order.ReqPayToken
import com.hc.data.param.RequestParams
import com.hc.data.user.AuthInfo
import com.hc.data.user.UserInfoRange
import com.tools.network.entity.HttpResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoanInfoService {

    @POST("act/mine/userAuth/getUserAuth.htm")
    fun queryCreditState(): Call<HttpResult<AuthInfo>>

    @POST("act/index.htm")
    fun reqHeadPageData(): Call<HttpResult<MainDataRec>>


    @POST("index.htm")
    fun reqHeadPageDataLogout(): Call<HttpResult<MainDataRecLogout>>

    @POST("act/order/notice.htm")
    fun getNotice(): Call<HttpResult<NoticeEntity>>

    @FormUrlEncoded
    @POST("common/open.htm")
    fun getOpenAmount(
        @Field(RequestParams.CHANNEL_CODE) channelCode: String?,
        @Field(RequestParams.PHONE_ID) deviceId: String?,
        @Field(RequestParams.MAC) mac: String?
    ): Call<HttpResult<Any>>


    @FormUrlEncoded
    @POST("act/diversionProduct/count.htm")
    fun diversionCount(
        @Field(RequestParams.PRODUCT_ID) productId: Int,
        @Field(RequestParams.SOURCE_NODES) sourceNodes: String?,
        @Field(RequestParams.SOURCE_TYPE) sourceType: String?
    ): Call<HttpResult<Any>>


    @FormUrlEncoded
    @POST("act/mall/myOrder/checkOrder.htm")
    fun checkOrder(
        @Field(RequestParams.AMOUNT) goodsPrice: Double,
        @Field(RequestParams.GOODS_ID) goodsId: String?
    ): Call<HttpResult<CheckOrder>>


    /**
     * 保存短信记录
     */
    @FormUrlEncoded
    @POST("act/mine/sms/saveOrUpdate.htm")
    fun saveSmsList(@Field("smsList") smsList: String): Call<HttpResult<Any>>

    /**
     * 订单提交接口
     */
    @FormUrlEncoded
    @POST("act/mall/myOrder/commit.htm")
    fun commitOrder(
        @Field(RequestParams.AMOUNT) amount: String?,
        @Field(RequestParams.GOODS_ID) goodsId: String?,
        @Field(RequestParams.GOODS_NAME) goodsName: String?,
        @Field(RequestParams.ADDRESS) address: String?,
        @Field(RequestParams.COORDINATE) coordinate: String?,
        @Field(RequestParams.BLACK_BOX) blackBox: String?,
        @Field(RequestParams.PHONE_ID) deviceId: String?,
        @Field(RequestParams.IS_POSITION_SIMULATION) isPositionSimulation: Int,
        @Field(RequestParams.IS_SIMULATOR_PHONE) isSimulatorPhone: Int
    ): Call<HttpResult<OrderInfo>>


    /**
     * 保存app列表
     */
    @FormUrlEncoded
    @POST("act/mine/applist/saveOrUpdate.htm")
    fun saveNativeAppList(@Field(RequestParams.APP_LIST) appList: String?): Call<HttpResult<Any>>


    /** 获取订单提交后的轮询处理状态  */
    @FormUrlEncoded
    @POST("act/mall/myOrder/getOrderInfo.htm")
    fun getOrderStateInfo(@Field(RequestParams.ORDER_ID) orderId: String?): Call<HttpResult<OrderInfo>>

    @FormUrlEncoded
    @POST("act/mall/myOrder/getBillList.htm")
    fun getBillList(@Field(RequestParams.ORDER_ID) orderId: String?): Call<HttpResult<List<OrderBillRec>>>


    /** 续期请求token  */
    @FormUrlEncoded
    @POST("act/sdk/pay/requestDelayRepay.htm")
    fun reqDelayRepayToken(@Field(RequestParams.ORDER_ID) orderId: String?): Call<HttpResult<ReqPayToken>>


    /* -----------------> CashFree Pay SDK for Interfaces begin <------------------- */
    /** 还款请求token  */
    @FormUrlEncoded
    @POST("act/sdk/pay/requestRepay.htm")
    fun reqRepayToken(
        @Field(RequestParams.ORDER_ID) orderId: String?,
        @Field(RequestParams.BORROW_NUMS) nums: Int
    ): Call<HttpResult<ReqPayToken>> //nums传1

    /**
     * 查询用户枚举信息
     */
    @POST("act/mine/userInfo/ListInfo.htm")
    fun queryListInfo(): Call<HttpResult<UserInfoRange>>

    @POST("act/pay/payInfo.htm")
    fun reqPayHistoryForSDK(): Call<HttpResult<List<CFPayHistory>>>


    @FormUrlEncoded
    @POST("act/pay/repayReturn.htm")
    fun payOver2SyncInfo(
        @Field(RequestParams.ORDER_ID) orderId: String?,
        @Field(RequestParams.ORDER_AMOUNT) orderAmount: String?,
        @Field(RequestParams.PAYMENT_MODE) paymentMode: String?,
        @Field(RequestParams.REFERENCE_ID) referenceId: String?,
        @Field(RequestParams.TX_MSG) txMsg: String?,
        @Field(RequestParams.TX_STATUS) txStatus: String?,
        @Field(RequestParams.TX_TIME) txTime: String?,
        @Field(RequestParams.PAY_INFO) payInfo: String?,
        @Field(RequestParams.PAY_TYPE) payType: Int?,
        @Field(RequestParams.PAY_ACCOUNT) payAccount: String?
    ): Call<HttpResult<CFPayHistory>>

    /** 区分支付支付方式：1-sdk;2-h5  */
    @POST("act/pay/paymentChooseType.htm")
    fun reqPayType(): Call<HttpResult<Int>>


}