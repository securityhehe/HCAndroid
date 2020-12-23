package com.hc.login.api;

import com.hc.data.param.RequestParams;
import com.hc.login.data.PhoneHistory;
import com.hc.data.common.TokenData;
import com.hc.login.data.UserDeviceInfo;
import com.tools.network.entity.HttpResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginServer {

    @POST("user/login.htm")
    Call<HttpResult<TokenData>> doLogin(@Body UserDeviceInfo sub);


    @FormUrlEncoded
    @POST("user/isPhoneExists.htm")
    Call<HttpResult<PhoneHistory>> isPhoneExists(@Field(RequestParams.PHONE) String phone);


    @FormUrlEncoded
    @POST("user/sendSms.htm")
    Call<HttpResult> getSmsCode(@Field(RequestParams.PHONE_MARK) String phoneMark, @Field(RequestParams.PHONE) String phone, @Field(RequestParams.TYPE) String type);

}
