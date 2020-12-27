package com.hc.accountinfo.api;

import com.tools.network.entity.HttpResult;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface IUserInfoService {
    @Multipart
    @POST("act/user/ocr/aadhaarFront.htm")
    Call<HttpResult> checkAdFrontImg(@HeaderMap Map<String, String> head, @PartMap Map<String, RequestBody> params);

    /**
     * 验证aadhaar反面
     */
    @Multipart
    @POST("act/user/ocr/aadhaarBack.htm")
    Call<HttpResult> checkAdBackImg(@HeaderMap Map<String, String> head, @PartMap Map<String, RequestBody> params);
    /**
     * 验证pan正面
     */
    @Multipart
    @POST("act/user/ocr/pan.htm")
    Call<HttpResult> checkPanFrontImg(@HeaderMap Map<String, String> head, @PartMap Map<String, RequestBody> params);

}
