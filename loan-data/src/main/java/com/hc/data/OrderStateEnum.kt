package com.hc.data

enum class OrderStateEnum(
    var state: String, // 中文
    private val zhCn: String, // 英文
    private val en: String, // 印地语
    private val hindi: String
) {
    SUBMIT_NO_COMMIT_ORDER("-1", "未下单", "", ""),
    // 审核流程

    /** 10-下单成功  */
    SUBMIT_ORDER_SUCCESS("10", "下单成功", "successfully ordered", "सफल क्रम"),

    /** 11-预审中  */
    FIRST_REVIEW_ING("11", "预审中", "First review", "प्रारंभिक सुनवाई"),

    /** 12-自动审核中  */
    AUTO_REVIEW_ING("12", "自动审核中", "Auto review", "स्वचालित समीक्षा"),

    /** 13-人工复审中  */
    MANUAL_REVIEW_ING("13", "人工复审中", "Manual review", "मैनुअल समीक्षा"),

    /** 14-自动审核不通过  */
    AUTO_REFUSED("14", "自动审核不通过", "Automatic review does not pass", "स्वचालित समीक्षा पास नहीं होती है"),

    /** 15-人工复审不通过  */
    MANUAL_REFUSED("15", "人工复审不通过", "Manual review fails", "मैनुअल समीक्षा विफल"),

    /** 16-自动审核通过  */
    AUTO_REVIEW_PASS("16", "自动审核通过", "Auto review pass", "स्वचालित समीक्षा"),

    /** 17-人工复审通过  */
    MANUAL_REVIEW_PASS("17", "人工复审通过", "Manual review pass", "मैनुअल समीक्षा"),

    /** 18-征信认证中  */
    CREDIT_VERIFIY_LOADING("18", "征信认证中", "Credit verification", "क्रेडिट सत्यापन"),


    // 提现流程
    /** 20-待签名  */
    WAIT_SIGN("20", "待签名", "To be signed", "हस्ताक्षरित होना"),

    /** 21-待提现  */
    WAIT_CASH("21", "待提现", "Pending cash", "लंबित नकदी"),

    /** 22-失效  */
    INVALID("22", "失效", "Invalid", "असफल"),

    /** 23-订单关闭  */
    CLOSE("23", "订单关闭", "Order close", "आर्डर बंद हो गया"),

    /** 24-提现处理中  */
    CASH_ING("24", "付款处理中", "Cash processing", "भुगतान प्रसंस्करण"),

    /** 25-提现失败  */
    CASH_FAIL("25", "付款失败", "Cash Fail", "भुगतान विफल रहा"),


    // 还款流程
    /** 30-待还款  */
    REPAY("30", "待还款", "Repayment", "लंबित चुकौती"),

    /** 31-还款处理中  */
    REPAY_ING("31", "还款处理中", "Repayment processing", "चुकौती प्रसंस्करण"),

    /** 32-续期中  */
    RENEWAL("32", "续期中", "Renewal", "का नवीकरण"),

    /** 33-续期处理中  */
    RENEWAL_ING("33", "续期处理中", "Renewal processing", "नवीकरण प्रसंस्करण"),

    /** 34-逾期  */
    OVERDUE("34", "逾期", "Overdue", "अतिदेय"),

    /** 35-坏账  */
    BAD("35", "坏账", "Bad debt", "बुरा कर्ज"),


    // 结清
    /** 40-结清  */
    FINISH("40", "结清", "settle", "बसना"),

    /** 41-结清-减免  */
    REMISSION_FINISH("41", "结清-减免", "Relief settlement", "राहत बस्ती"),

    /** 42-结清-续期还款  */
    RENEWAL_FINISH("42", "结清-续期还款", "Renewal settlement", "नवीकरण का बंदोबस्त"),

    /** 43-结清-减免和续期  */
    RENEWAL_REMISSION_FINISH("43", "结清-续期和减免", "Renewal and relief settlement", "कटौती और विस्तार");

}
