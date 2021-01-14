package com.hc.accountinfo.api

import com.hc.data.param.RequestParams
import com.hc.data.user.*
import com.tools.network.entity.HttpResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserInfoService {

    /**
     * 查询用户基本信息
     */
    @POST("act/mine/userInfo/getUserInfo.htm")
    fun queryUserBasicInfo(): Call<HttpResult<UserInfo>>

    /**
     * 查询用户填写的扩展信息
     */
    @POST("act/mine/userInfo/getUserBasicInfo.htm")
    fun queryUserExtraInfo(): Call<HttpResult<UserInfoExt>>

    /**
     * 查询用户地址信息
     */
    @FormUrlEncoded
    @POST("act/mine/userInfo/getAadress.htm")
    fun getAadress(@retrofit2.http.Field(RequestParams.PARENT_ID) parentId: Int?): retrofit2.Call<HttpResult<UserInfoRange?>?>?

    /**
     * 查询用户工作信息
     */
    @POST("act/mine/userInfo/getWorkInfo.htm")
    fun queryUserWorkInfo(): retrofit2.Call<HttpResult<UserWorkInfo?>?>?

    /**
     * 查询bank信息
     */
    @POST("act/mine/bankCard/myBankCardList.htm")
    fun queryUserBankInfo(): retrofit2.Call<HttpResult<UserBankRec>>

    /** 获取字典  */
    @GET("act/dict/list.htm")
    fun getBankListDictionary(@retrofit2.http.Query(RequestParams.TYPE) type: String?): Call<HttpResult<BankDictList>>

    /**
     * 查询认证状态
     */
    @POST("act/mine/userAuth/getUserAuth.htm")
    fun queryCreditState(): Call<HttpResult<AuthInfo>>

    @POST("act/mine/userInfo/ListInfo.htm")
    fun queryListInfo(): Call<HttpResult<UserInfoRange>>

    /**
     * 通话认证状态
     */
    @FormUrlEncoded
    @POST("act/operator/auth.htm")
    fun mobileCreditAuth(
        @retrofit2.http.Field(RequestParams.OPER_TYPE) operType: Int,
        @retrofit2.http.Field(RequestParams.PHONE) phone: String?,
        @retrofit2.http.Field(RequestParams.PHONE_ID) deviceId: String?
    ): Call<HttpResult<String>>

    /**
     * 提交补充资料
     */
    @FormUrlEncoded
    @POST("act/mine/supplementState/saveOrUpdate.htm")
    fun commitSupplementInfo(
        @retrofit2.http.Field(RequestParams.RELATIVES) relatives: Int,
        @retrofit2.http.Field(RequestParams.RELATIVES_NAME) relativesName: String?,
        @retrofit2.http.Field(RequestParams.RELATIVES_MOBILE) relativesMobile: String?,
        @retrofit2.http.Field(RequestParams.OTHER_RELATIVES) otherRelatives: Int,
        @retrofit2.http.Field(RequestParams.COLLEAGUE_NAME) colleagueName: String?,
        @retrofit2.http.Field(RequestParams.COLLEAGUE_MOBILE) colleagueMobile: String?
    ): Call<HttpResult<Any>>


    @POST("act/user/ocr/getFaceSdk.htm")
    fun getFaceSdkType(): Call<HttpResult<Int>>

    /**
     * 验证aadhaar正面
     */
    @Multipart
    @POST("act/user/ocr/aadhaarFront.htm")
    fun checkAdFrontImg(
        @HeaderMap head: Map<String, String>,
        @PartMap params: Map<String, RequestBody>
    ): Call<HttpResult<Any>>

    /**
     * 验证aadhaar反面
     */
    @Multipart
    @POST("act/user/ocr/aadhaarBack.htm")
    fun checkAdBackImg(
        @HeaderMap head: Map<String?, String?>,
        @PartMap params: Map<String?, RequestBody?>
    ): Call<HttpResult<String?>>

    /**
     * 验证pan正面
     */
    @Multipart
    @POST("act/user/ocr/pan.htm")
    fun checkPanFrontImg(
        @HeaderMap head: Map<String?, String?>,
        @PartMap params: Map<String?, RequestBody?>
    ): Call<HttpResult<String?>>

    /**
     * 验证face
     */
    @Multipart
    @POST("act/user/ocr/face.htm")
    fun checkFaceImg(
        @HeaderMap head: Map<String?, String?>,
        @PartMap params: Map<String?, RequestBody?>
    ): Call<HttpResult<String?>>

    /**
     * 保存或者更新用户基本信息
     */
    @POST("act/mine/userInfo/saveOrUpdate.htm")
    fun saveOrUpdateUserInfo(@Body sub: UserInfoSub?): Call<HttpResult<AuthInfo>>


    @FormUrlEncoded
    @POST("act/mine/bankCard/bind.htm")
    fun saveBankInfo(
        @Field(RequestParams.BANK_NAME) bankName: String?,
        @Field(RequestParams.BANK_NO) bankNo: String?,
        @Field(RequestParams.IF_SCCODE) ifscCode: String?,
        @Field(RequestParams.BANK_TYPE) bankType: Int,
        @Field(RequestParams.PHONE_MARK) phoneMark: String?,
        @Field("confirmSign") confirmIfsc: Boolean
    ): Call<HttpResult<Any>>

}