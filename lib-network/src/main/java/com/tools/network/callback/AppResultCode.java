package com.tools.network.callback;

/**
 * 定义服务端特定错误结果码
 */
public class AppResultCode {
    public static final int OFFSIDE_LOGIN_EXIT  = 410;


    /** RefreshToken 过期 - APP提示需要登录，跳转到登录页面 */
    public static final int TOKEN_REFRESH_TIMEOUT = 412;
    /** Token不唯一 - APP提示被顶号，跳转到登录页面 */
    public static final int TOKEN_NOT_UNIQUE      = 413;
    /** 签名缺失 - */
    public static final int SIGN_NOT_EXIST        = 414;

    public static final int LOGOUT              = 1000;

    public static final int CERTIFY_FINISH_STATE = 2000;
}
