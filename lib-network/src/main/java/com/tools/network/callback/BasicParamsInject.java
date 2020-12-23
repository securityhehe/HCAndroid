package com.tools.network.callback;

import com.tools.network.interceptor.BasicParamsInterceptor;
import com.tools.network.interceptor.IBasicDynamic;

import java.util.Map;

import okhttp3.Interceptor;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/4/5 17:59
 * <p/>
 * Description: 拦截器 - 用于添加签名参数
 */
class BasicParamsInject {
    private BasicParamsInterceptor interceptor;

    BasicParamsInject() {
        // 设置静态参数
        interceptor = new BasicParamsInterceptor.Builder().build();
        // 设置动态参数
        interceptor.setIBasicDynamic(new IBasicDynamic() {
            @Override
            public Map<String, String> addParams() {
                return SignUtil.getInstance().addParams();
            }

            @Override
            public Map<String, String> signParams(Map paramsMap) {
                return SignUtil.getInstance().signParams(paramsMap);
            }
        });
    }

    Interceptor getInterceptor() {
        return interceptor;
    }
}
