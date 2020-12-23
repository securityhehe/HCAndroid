package com.hc.data.param;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/11/16 15:24
 * <p/>
 * Description:
 */
public class RequestParams {
    public static final String PHONE         = "phone";
    public static final String USER_ID         = "userId";
    public static final String REAL_NAME         = "realName";
    public static final String ID_NO         = "idNo";
    public static final String NAME         = "name";
    public static final String IDCARD         = "idcard";
    public static final String TYPE          = "type";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ID            = "id";
    public static final String GOODS_ID      = "goodsId";
    public static final String ORDER_ID      = "orderId";
    public static final String ORDER_IDS      = "orderIds";
    public static final String BUSINESS_SCENE      = "businessScene";
    public static final String APP_LIST      = "applist";
    public static final String PAYMENT_TYPE_ID      = "paymentTypeId";
    public static final String PAY_ORDER_NO  = "payOrderNo";
    public static final String PARENT_ID     = "parentId";
    public static final String CURRENT_PAGE  = "currentPage";
    public static final String IS_BOUD       = "isBoud";
    public static final String PAY_RESULT    = "payResult";
    public static final String INFO          = "info";
    public static final String STATE         = "state";
    public static final String DATA          = "data";
    public static final String EMAIL          = "email";

    public static final String CREATE_TIME      = "createTime";

    public static final String systemVersions      = "systemVersions";

    //设备号
    public static final String PHONE_MARK      = "phoneMark";
    public static final String PHONE_ID        = "deviceId";

    public static final String IS_POSITION_SIMULATION        = "isPositionSimulation";
    public static final String IS_SIMULATOR_PHONE        = "isSimulatorPhone";

    public static final String SYSTEM_VERSIONS   = "systemVersions";
    public static final String PHONE_TYPE        = "phoneType";
    public static final String PHONE_BRAND       = "phoneBrand";
    public static final String MAC              = "mac";
    public static final String VERSION_NAME      = "versionName";
    public static final String VERSION_CODE      = "versionCode";
    public static final String APP_INSTALL_TIME   = "appInstallTime";
    public static final String APP_MARKET        = "appMarket";
    public static final String OPER_TYPE       = "operType";

    public static final String COORDINATE       = "coordinate";
    public static final String ADDRESS          = "address";
    public static final String GOODS_NAME        = "goodsName";
    public static final String GOODSID          = "goodsId";
    public static final String AMOUNT           = "amount";
    public static final String TOTAL_AMOUNT           = "totalAmount";

    public static final String CHANNEL_CODE           = "channelCode";

    public static final String ORDER_AMOUNT           = "orderAmount";
    public static final String PAYMENT_MODE           = "paymentMode";
    public static final String REFERENCE_ID           = "referenceId";
    public static final String TX_MSG	              = "txMsg";
    public static final String TX_STATUS              = "txStatus";
    public static final String TX_TIME	              = "txTime";
    public static final String PAY_INFO               = "payInfo";
    public static final String PAY_TYPE               = "payType";
    public static final String PAY_ACCOUNT            = "payAccount";


    //KYC
    public static final String PAN_NO          = "panNo";
    public static final String AADHAAR_NO      = "aadhaarNo";
    public static final String OCR_TYPE        = "ocrType";
    public static final String REQ_CODE      = "reqCode";
    public static final String AADHAAR_NOTFIY_URL  = "aadhaarNotifyUrl";
    public static final String PAN_NOTFIY_URL      = "panNotifyUrl";

    public static final String COMPARE_AAD_CON      = "compareAadhaarConfidence";
    public static final String COMPARE_AAD_SIMIL      = "compareAadhaarSimilarity";
    public static final String COMPARE_PAN_CON      = "comparePanConfidence";
    public static final String COMPARE_PAN_SIMIL      = "comparePanSimilarity";
    public static final String COMPARE_FACE_CON      = "compareFaceConfidence";
    public static final String COMPARE_FACE_SIMIL      = "compareFaceSimilarity";
    public static final String LIVE_IMG      = "liveImg";


    //Bank
    public static final String BANK_NAME       = "bankName";//银行名称
    public static final String BANK_NO         = "bankNo";//bankNo 银行卡号
    public static final String IF_SCCODE        = "ifscCode";
    public static final String BANK_TYPE       = "bankType";//绑卡类型1-主卡；2-副卡



    public static final String PAY_MONEY         = "actualAmount";//还款金额
    public static final String MEMBER_FEE         = "memberFee";//会员费
    public static final String BORROW_FEE         = "borrowFee";//可借费用
    public static final String BORROW_NUMS        = "nums";//分期数

    public static final String BANK_CARD_NO    = "cardNo"; //银行卡号

    public static final String BANK_PHONE    = "bankPhone"; //银行预留手机号
    //意见反馈字段
    public static final String ADVICE_FEED_BACK    = "opinion";
    //
    public static final String OCR_IMG    = "ocr";

    public static final String BLACK_BOX    = "blackBox";

    //分页
    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";

    public static class RequstType{
        public static final String LOGIN = "login";
    }

    // diversion
    public static final String PRODUCT_ID           = "productId";
    public static final String SOURCE_NODES           = "sourceNodes";
    public static final String SOURCE_TYPE	           = "sourceType";

    //
    public static final String RELATIVES	               = "relatives";
    public static final String RELATIVES_NAME	           = "relativesName";
    public static final String RELATIVES_MOBILE	           = "relativesMobile";
    public static final String OTHER_RELATIVES	           = "otherRelatives";
    public static final String COLLEAGUE_NAME	           = "colleagueName";
    public static final String COLLEAGUE_MOBILE	           = "colleagueMobile";

}
