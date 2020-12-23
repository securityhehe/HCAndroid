package com.hc.uicomponent.config;

/**
 * 常量
 */
public class Constants {

    public static final String PHONE_TYPE = "android";

    public static final String STATUS_TRUE = "true";
    public static final String STATUS_FALSE = "false";

    /**
     * number
     */
    public static final int NUMBER__1 = -1;
    public static final int NUMBER_0 = 0;
    public static final int NUMBER_1 = 1;
    public static final int NUMBER_2 = 2;
    public static final int NUMBER_3 = 3;
    public static final int NUMBER_4 = 4;
    public static final int NUMBER_5 = 5;
    public static final int NUMBER_6 = 6;
    public static final int NUMBER_7 = 7;
    public static final int NUMBER_8 = 8;
    public static final int NUMBER_9 = 9;
    public static final int NUMBER_10 = 10;
    public static final int NUMBER_11 = 11;
    public static final int NUMBER_12 = 12;
    public static final int NUMBER_20 = 20;
    public static final int NUMBER_30 = 30;
    public static final int NUMBER_40 = 40;
    public static final int NUMBER_200 = 200;
    public static final int NUMBER_300 = 300;
    public static final int NUMBER_400 = 400;
    public static final int NUMBER_500 = 500;
    public static final int NUMBER_600 = 600;
    public static final int NUMBER_700 = 700;
    public static final int NUMBER_1000 = 1000;
    public static final int NUMBER_2000 = 2000;
    public static final int NUMBER_3000 = 3000;
    public static final int NUMBER_4000 = 4000;

    /**
     * status
     */
    public static final String STATUS__2 = "-2";
    public static final String STATUS__1 = "-1";
    public static final String STATUS_0 = "0";
    public static final String STATUS_1 = "1";
    public static final String STATUS_2 = "2";
    public static final String STATUS_3 = "3";
    public static final String STATUS_4 = "4";
    public static final String STATUS_5 = "5";
    public static final String STATUS_6 = "6";
    public static final String STATUS_7 = "7";
    public static final String STATUS_8 = "8";
    public static final String STATUS_9 = "9";
    public static final String STATUS_10 = "10";
    public static final String STATUS_20 = "20";
    public static final String STATUS_21 = "21";
    public static final String STATUS_22 = "22";
    public static final String STATUS_23 = "23";
    public static final String STATUS_24 = "24";
    public static final String STATUS_25 = "25";
    public static final String STATUS_26 = "26";
    public static final String STATUS_27 = "27";
    public static final String STATUS_28 = "28";
    public static final String STATUS_29 = "29";
    public static final String STATUS_30 = "30";
    public static final String STATUS_40 = "40";
    public static final String STATUS_50 = "50";
    public static final String STATUS_52 = "52";
    public static final String STATUS_90 = "90";


    /**
     * network params
     */
    // 公共参数
    public static final String APP_KEY = "appkey";
    public static final String SIGNA = "signMsg";
    public static final String TS = "ts";
    public static final String MOBILE_TYPE = "mobileType";
    public static final String VERSION_NUMBER = "versionNumber";
    // 登录参数
    public static final String TOKEN = "token";//TODO 这个token名字变化为 ： Authorization
    public static final String USER_ID = "userId";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_LANG = "lang";
    // SP 字段
    public static final String IS_LAND = "isLand";
    public static final String IS_FIRST_IN = "isFirstIn";
    public static final String MAIN_SELECT_INDEX = "mainSelectIndex";

    public static String CACHE_LOGIN_PHONE = "cache_login_phone";      //缓存上次登录的手机号码
    public static String CACHE_NOFORCE_UPDATE_NUM = "cache_noforce_update_num";      //缓存非强制更新的提示次数
    public static String CACHE_NOFORCE_UPDATE_VERSION = "cache_noforce_update_version";      //缓存非强制更新的提示次数

    public static String CUR_DAY_HAS_UPLOAD_APP_LIST = "update_app_list";      //缓存当前是否已经上传成功过app列表
    public static String CUR_DAY_HAS_UPLOAD_APP_DATE = "update_app_list_date"; //缓存当前是否已经上传成功过app列表的时间

    public static String COLLECT_APP_LIST_FINISH_DATE = "collect_app_list_date"; //缓存收集完app列表的时间

    public static String APP_LIST_DATA = "appList";

    public static String IS_HAS_STAT_OPEN_AMOUNT = "isHasStatOpenAmount";//是否已经统计过打开量

    public static String STATE_OPEN_CHANNEL = "state_open_channel";//缓存统计到的打开渠道（是否来源于GP）

    /**
     * 银行列表，type=BANK_TYPE
     */
    public static String BANKTYPE = "BANK_TYPE";

//    public static List<UserEmunType> languageList = new ArrayList<>();
//
//    static {
//        languageList.add(new UserEmunType(null,"0", "English",null,"",""));
//        languageList.add(new UserEmunType(null,"1", "Hindi",null,"",""));
//    }

    ////////////////////////////  APP导流统计  ////////////////////////////
    public static String DIVERSION_COUNT_LIST = "DIVERSION_COUNT_LIST";// 导流APP点击统计列表

}
