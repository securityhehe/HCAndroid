package com.tools.network.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2017/9/6 14:23
 * <p/>
 * Description: 公共参数拦截器
 */
public class BasicParamsInterceptor implements Interceptor {

    /** header 参数Map */
    private Map<String, String> headerParamsMap = new HashMap<>();
    /** query 参数Map */
    private Map<String, String> queryParamsMap  = new HashMap<>();
    /** post 参数Map */
    private Map<String, String> postParamsMap   = new HashMap<>();
    /** 动态参数接口 */
    private IBasicDynamic iBasicDynamic;

    private BasicParamsInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        // inject query params into url whatever it's GET or POST Request
        if (queryParamsMap.size() > 0) {
            injectParamsIntoUrl(request, requestBuilder, queryParamsMap, false);
        }

        // inject post params into body when it's POST Request
        if (request.method().equals("POST")) {
            // MultipartBody date
            if (null != request.body() && request.body() instanceof MultipartBody) {
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                multipartBodyBuilder.setType(MultipartBody.FORM);
                // add new static params to new multipartBodyBuilder
                for (Map.Entry entry : postParamsMap.entrySet()) {
                    multipartBodyBuilder.addFormDataPart((String) entry.getKey(), (String) entry.getValue()).setType(MultipartBody.FORM);
                }
                // add new dynamic params to new multipartBodyBuilder
                // if (null != iBasicDynamic) {
                //     for (Map.Entry entry : iBasicDynamic.addParams().entrySet()) {
                //         multipartBodyBuilder.addFormDataPart((String) entry.getKey(), (String) entry.getValue()).setType(MultipartBody.FORM);
                //     }
                // }
                // add old parts to new multipartBodyBuilder
                for (MultipartBody.Part part : ((MultipartBody) request.body()).parts()) {
                    multipartBodyBuilder.addPart(part);
                }
                requestBuilder.post(multipartBodyBuilder.build());
            } else {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                // add new static params to new formBodyBuilder
                for (Map.Entry entry : postParamsMap.entrySet()) {
                    formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                }
                // add new dynamic params to new formBodyBuilder
                if (null != iBasicDynamic) {
                    for (Map.Entry entry : iBasicDynamic.addParams().entrySet()) {
                        formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                // add old params to new formBodyBuilder
                FormBody formBody       = formBodyBuilder.build();
                String   postBodyString = bodyToString(request.body());
                postBodyString += (TextUtils.isEmpty(postBodyString) ? "" : "&") + bodyToString(formBody);
                requestBuilder.post(RequestBody.create(formBody.contentType(), postBodyString));
            }
        } else {
            // if can't inject into body, then inject into url
            injectParamsIntoUrl(request, requestBuilder, postParamsMap, true);
        }

        request = requestBuilder.build();
        // inject header params into header
        Map<String, String> signParamsMap = new TreeMap<>();
        if (request.method().equals("POST")) {
            if (null != request.body() && !(request.body() instanceof MultipartBody)) {
                signParamsMap = iBasicDynamic.signParams(stringToMap(bodyToString(request.body())));
            }
        } else {
            signParamsMap = iBasicDynamic.signParams(stringToMap(getQueryFromUrl(request.url().toString())));
        }

        Headers.Builder headerBuilder = request.headers().newBuilder();
        // add new params to new headerBuilder
        if (signParamsMap.size() > 0) {
            for (Map.Entry entry : signParamsMap.entrySet()) {
                headerBuilder.add((String) entry.getKey(), (String) entry.getValue());
            }
        }
        // add old params to new headerBuilder
        if (headerParamsMap.size() > 0) {
            for (Map.Entry entry : headerParamsMap.entrySet()) {
                headerBuilder.add((String) entry.getKey(), (String) entry.getValue());
            }
        }

        requestBuilder.headers(headerBuilder.build());
        request = requestBuilder.build();

        Response originalResponse = chain.proceed(request);
        Response priorResponse    = originalResponse.priorResponse();
        // 如果是重定向，那么就执行重定向请求后再返回数据。
        if (null != priorResponse && priorResponse.isRedirect()) {
            Request redirectRequest = request.newBuilder().url(originalResponse.request().url()).build();
            originalResponse = chain.proceed(redirectRequest);
        }
        return originalResponse;
    }

    // function to inject query params into url whatever it's GET or POST
    private void injectParamsIntoUrl(Request request, Request.Builder requestBuilder, Map<String, String> paramsMap, boolean isPost) {
        HttpUrl.Builder httpUrlBuilder = request.url().newBuilder();
        if (isPost) {
            for (Map.Entry entry : iBasicDynamic.addParams().entrySet()) {
                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
        }

        for (Map.Entry entry : paramsMap.entrySet()) {
            httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
        }
        requestBuilder.url(httpUrlBuilder.build());
    }

    // RequestBody to String
    private String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            if (request != null) {
                request.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

    // get query params from url
    private String getQueryFromUrl(String urlStr) {
        String[] paramStr = urlStr.split("\\?");
        if (paramStr.length > 1) {
            return paramStr[1];
        } else {
            return "";
        }
    }

    // converts the & connection string to map
    private Map<String, String> stringToMap(String paramsStr) {
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(paramsStr)) {
            for (String str : paramsStr.split("&")) {
                if (str.contains("=")) {
                    int    index = str.indexOf("=");
                    String key   = str.substring(0, index);
                    String value = index + 1 >= str.length() ? "" : str.substring(index + 1);
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    public void setIBasicDynamic(IBasicDynamic iBasicDynamic) {
        this.iBasicDynamic = iBasicDynamic;
    }

    public static class Builder {
        BasicParamsInterceptor interceptor;

        public Builder() {
            interceptor = new BasicParamsInterceptor();
        }

        public Builder addHeaderParam(String key, String value) {
            interceptor.headerParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
            interceptor.headerParamsMap.putAll(headerParamsMap);
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            interceptor.queryParamsMap.put(key, value);
            return this;
        }

        public Builder addQueryParamsMap(Map<String, String> queryParamsMap) {
            interceptor.queryParamsMap.putAll(queryParamsMap);
            return this;
        }

        public Builder addPostParam(String key, String value) {
            interceptor.postParamsMap.put(key, value);
            return this;
        }

        public Builder addPostParamsMap(Map<String, String> paramsMap) {
            interceptor.postParamsMap.putAll(paramsMap);
            return this;
        }

        public BasicParamsInterceptor build() {
            return interceptor;
        }
    }
}
