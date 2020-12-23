package com.tools.network.exception;

/*{
    "timestamp":"2020-05-06T11:33:29.140+0000",
    "status":500,
    "error":"Internal Server Error",
    "message":"No message available",
    "trace":"java.lang.NullPointerException\n\tat com.miaorun.creditapp.controller.ScUserB
    ePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n\tat org.apache.catalina.core.A
    "path":"/scuserbaseinfo/save"
}*/

import com.tools.network.entity.HttpResult;

/**
 * 服务器约定异常类
 */
public class ApiException extends RuntimeException {

    private HttpResult result;

    public ApiException(HttpResult result) {
        this.result = result;
    }

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }
}
