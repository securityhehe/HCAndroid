package com.tools.network.callback;

import com.tools.network.converter.RDConverterFactory;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 *  LOAN - Retrofit client
 */
public class RDClient {
    // 网络请求超时时间值(s)
    private static final int    CONNECT_TIMEOUT = 30;
    private static final int    WRITE_TIMEOUT = 30;
    private static final int    READ_TIMEOUT = 30;
    // 请求地址
    private static final String BASE_URL = IHttpParamProvider.getInstance().getLoanUrl();
    // retrofit实例
    private Retrofit retrofit;

    private static OkHttpClient.Builder builder;

    /**
     * 私有化构造方法
     */
    private RDClient() {
        // 创建一个OkHttpClient
        builder = new OkHttpClient.Builder()
                // 设置网络请求超时时间
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
//                // 添加签名参数
                .addInterceptor(new BasicParamsInject().getInterceptor())
                // 失败后尝试重新请求
                .retryOnConnectionFailure(true);
       Boolean  b=  IHttpParamProvider.getInstance().getCurRuntimeEvn();
        if (b){
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // 打印参数
            builder.addInterceptor(httpLoggingInterceptor);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(RDConverterFactory.create())
                .build();
    }

    /**
     * 调用单例对象
     */
    private static RDClient getInstance() {
        return RDClientInstance.instance;
    }

    /**
     * 创建单例对象
     */
    private static class RDClientInstance {
        static RDClient instance = new RDClient();
    }

    ///////////////////////////////////////////////////////////////////////////
    // service
    ///////////////////////////////////////////////////////////////////////////
    private static TreeMap<String, Object> serviceMap;

    private static TreeMap<String, Object> getServiceMap() {
        if (serviceMap == null)
            serviceMap = new TreeMap<>();
        return serviceMap;
    }

    /**
     * @return 指定service实例
     */
    public static <T> T getService(Class<T> clazz) {
        if (getServiceMap().containsKey(clazz.getSimpleName())) {
            return (T) getServiceMap().get(clazz.getSimpleName());
        }
        T service = RDClient.getInstance().retrofit.create(clazz);
        getServiceMap().put(clazz.getSimpleName(), service);
        return service;
    }
}
