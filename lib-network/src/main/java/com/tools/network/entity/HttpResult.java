package com.tools.network.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Http响应结构体
 * @param <T>
 */
public class HttpResult<T> {
    /** 错误码 */
    @SerializedName(Params.RES_CODE)
    private int    code;
    /** 错误信息 */
    @SerializedName(Params.RES_MSG)
    private String msg;
    /** 消息响应的主体 */
    @SerializedName(Params.RES_DATA)
    private T      data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    @Nullable
    public T getData() {
        return data;
    }
}
