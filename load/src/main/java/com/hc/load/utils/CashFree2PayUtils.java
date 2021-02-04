package com.hc.load.utils;

import android.app.Activity;
import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.hc.uicomponent.utils.GsonUtils;
import com.hc.uicomponent.utils.TextUtil;

import java.util.HashMap;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_BANK_CODE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CARD_CVV;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CARD_HOLDER;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CARD_MM;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CARD_NUMBER;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CARD_YYYY;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_NOTIFY_URL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_PAYMENT_OPTION;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_UPI_VPA;

/**
 * @Author : ZhouWei
 * @TIME : 2020/3/3 17:20
 * @DESC : Cashfree sdk 2 Pay
 */
public class CashFree2PayUtils {

    private final String TEST = "TEST";
    private final String PROD = "PROD";

    /**
     * 以下都是必填项
     **/
    private String appId;
    private String orderId;
    private String orderAmount;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String token;
    private boolean isTestEnv;
    private boolean isCallNativeAppForUpi;
    private String notifyUrl;
    private String upiAccount;
    private CashFreePayMethodEnum payMethodEnum;
    //netbanding params
    private String netBankingCode;
    //credit card params
    private String creditCardNo;
    private String creditCardExpiryMM;
    private String creditCardExpiryYYYY;
    private String creditCardHodler;
    private String creditCardCvv;

    private Map<String, String> params; //动态统计参数值

    private CashFree2PayUtils() {
    }

    public CashFree2PayUtils(CFBuilder builder) {
        this.appId = builder.appId;
        this.orderId = builder.orderId;
        this.orderAmount = builder.orderAmount;
        this.customerName = builder.customerName;
        this.customerPhone = builder.customerPhone;
        this.customerEmail = builder.customerEmail;
        this.token = builder.token;
        this.isTestEnv = builder.isTestEnv;
        this.isCallNativeAppForUpi = builder.isCallNativeAppForUpi;
        this.notifyUrl = builder.notifyUrl;
        this.payMethodEnum = builder.payMethodEnum;
        //
        this.netBankingCode = builder.netBankingCode;
        //
        this.creditCardNo = builder.creditCardNo;
        this.creditCardExpiryYYYY = builder.creditCardExpiryYYYY;
        this.creditCardExpiryMM = builder.creditCardExpiryMM;
        this.creditCardHodler = builder.creditCardHodler;
        this.creditCardCvv = builder.creditCardCvv;
        //
        this.upiAccount = builder.upiAccount;
    }

    /**
     * 触发调用SDk支付
     */
    public void doPayment(Activity context) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.clear();

        if (this.payMethodEnum == null) return;

        params.put(PARAM_APP_ID, appId);//
        params.put(PARAM_ORDER_ID, orderId);//
        params.put(PARAM_ORDER_AMOUNT, orderAmount);//
        params.put(PARAM_CUSTOMER_PHONE, customerPhone);//
        params.put(PARAM_CUSTOMER_EMAIL, customerEmail);//

        System.out.println("CashFree触发必要支付参数===>" + GsonUtils.toJsonString(params));

        if (TextUtil.isExistEmpty(token,appId,orderId,orderAmount,customerPhone,customerEmail)){
            ToastUtils.showShort("Payment parameters are illegal!");
            return;
        }

        if (!TextUtil.isEmpty(notifyUrl)) {
            params.put(PARAM_NOTIFY_URL, notifyUrl);
        }

        if (!isCallNativeAppForUpi) {
            switch (payMethodEnum) {
                case UPI_METHOD:
                    params.put(PARAM_PAYMENT_OPTION, "upi");
                    params.put(PARAM_UPI_VPA, this.upiAccount);// Put correct upi vpa here
                    break;
                case NET_BANKING:
                    params.put(PARAM_PAYMENT_OPTION, "nb");
                    params.put(PARAM_BANK_CODE, this.netBankingCode);// Put correct bank code here
                    break;
                case CREDIT_CARD:
                    params.put(PARAM_PAYMENT_OPTION, "card");
                    params.put(PARAM_CARD_NUMBER, this.creditCardNo);//Replace Card number
                    params.put(PARAM_CARD_MM, this.creditCardExpiryMM); // Card Expiry Month in MM
                    params.put(PARAM_CARD_YYYY, this.creditCardExpiryYYYY); // Card Expiry Year in YYYY
                    params.put(PARAM_CARD_HOLDER, this.creditCardHodler); // Card Holder name
                    params.put(PARAM_CARD_CVV, this.creditCardCvv); // Card CVV
                    break;
            }
        }
        System.out.println("CashFree触发实际支付参数===>" + GsonUtils.toJsonString(params));

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation(0);

        if (isCallNativeAppForUpi) {
            cfPaymentService.upiPayment(context, params, token, isTestEnv ? TEST : PROD);
        } else {
            cfPaymentService.doPayment(context, params, token, isTestEnv ? TEST : PROD, "#000000", "#FFFFFF", true);
        }
    }

    public static class CFBuilder {
        private String appId;
        private String orderId;
        private String orderAmount;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private String token;
        private boolean isTestEnv;
        private boolean isCallNativeAppForUpi;
        private String notifyUrl;
        private CashFreePayMethodEnum payMethodEnum;
        private String upiAccount;
        private String netBankingCode;
        private String creditCardNo;
        private String creditCardExpiryMM;
        private String creditCardExpiryYYYY;
        private String creditCardHodler;
        private String creditCardCvv;

        public CFBuilder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public CFBuilder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public CFBuilder setOrderAmount(String orderAmount) {
            this.orderAmount = orderAmount;
            return this;
        }

        public CFBuilder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public CFBuilder setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public CFBuilder setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public CFBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public CFBuilder setTestEnv(boolean isTestEnv) {
            this.isTestEnv = isTestEnv;
            return this;
        }

        public CFBuilder setIsCallNativeAppForUpi(boolean isCallNativeAppForUpi) {
            this.isCallNativeAppForUpi = isCallNativeAppForUpi;
            return this;
        }

        public CFBuilder setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }

        public CFBuilder setPayMethod(CashFreePayMethodEnum payMethodEnum) {
            this.payMethodEnum = payMethodEnum;
            return this;
        }

        public CFBuilder setNetBankingCode(String netBankingCode) {
            this.netBankingCode = netBankingCode;
            return this;
        }
        public CFBuilder setCreditCardNo(String creditCardNo) {
            this.creditCardNo = creditCardNo;
            return this;
        }
        public CFBuilder setCreditCardExpiryMM(String expiryMM) {
            this.creditCardExpiryMM = expiryMM;
            return this;
        }
        public CFBuilder setCreditCardExpiryYY(String expiryYY) {
            this.creditCardExpiryYYYY = expiryYY;
            return this;
        }

        public CFBuilder setCreditCardHodler(String creditCardHodler) {
            this.creditCardHodler = creditCardHodler;
            return this;
        }

        public CFBuilder setCreditCardCvv(String creditCardCvv) {
            this.creditCardCvv = creditCardCvv;
            return this;
        }

        public CFBuilder setUpiAccount(String upiAccount) {
            this.upiAccount = upiAccount;
            return this;
        }

        public CashFree2PayUtils build() {
            CashFree2PayUtils cashFree2PayUtils = new CashFree2PayUtils(this);
            return cashFree2PayUtils;
        }
    }

    public enum CashFreePayMethodEnum {
        UPI_METHOD, NET_BANKING, CREDIT_CARD
    }


}