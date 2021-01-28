package com.hc.load.net

import com.hc.data.mall.*
import com.hc.data.param.RequestParams
import com.hc.data.user.AuthInfo
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


}