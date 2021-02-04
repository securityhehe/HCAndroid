package com.hc.load.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.blankj.utilcode.util.ToastUtils;
import com.hc.load.R;
import com.hc.uicomponent.utils.GsonUtils;
import com.hc.uicomponent.utils.TextUtil;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.Razorpay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : ZhouWei
 * @TIME : 2020/4/27 17:20
 * @DESC : Razorpay
 */
public class RazorPayClientUtils {

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
    private RazorPayMethodEnum payMethodEnum;
    //netbanding params
    private String netBankingCode;
    //credit card params
    private String creditCardNo;
    private String creditCardExpiryMM;
    private String creditCardExpiryYYYY;
    private String creditCardHodler;
    private String creditCardCvv;

    private Razorpay razorpay;
    private WebView webview;
    private ScrollView scrollView;
    private RazorPayResultListener resultListener;


    JSONObject data;
    private Map<String, String> params; //动态统计参数值

    private RazorPayClientUtils() {
    }

    public RazorPayClientUtils(RazorPayBuilder builder) {
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
        this.creditCardHodler = builder.creditCardHolder;
        this.creditCardCvv = builder.creditCardCvv;
        //
        this.upiAccount = builder.upiAccount;

        this.resultListener = builder.resultListener;
    }

    /**
     * 触发调用SDk支付
     */
    public void doPayment(Activity context) {
        if (params == null) {
            params = new HashMap<>();
        }
        //data.clear();
        data = new JSONObject();

        if (this.payMethodEnum == null) return;

        //params.put(PARAM_APP_ID, appId);//
        try {
            data.put("currency", "INR");
            data.put("order_id", orderId);//
            data.put("amount", orderAmount);//
            data.put("contact", customerPhone);//
            data.put("email", customerEmail);//
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Razorpay触发必要支付参数===>" + GsonUtils.toJsonString(params));

        if (TextUtil.isExistEmpty(token, appId, orderAmount, customerPhone, customerEmail)) {
            ToastUtils.showShort("Payment parameters are illegal!");
            return;
        }

        if (!isCallNativeAppForUpi) {
            switch (payMethodEnum) {
                case UPI_METHOD:
                    try {
                        data.put("method", "upi");
                        data.put("vpa", this.upiAccount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case NET_BANKING:
                    try {
                        data.put("method", "netbanking");
                        data.put("bank", this.netBankingCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case CREDIT_CARD:
                    try {
                        data.put("method", "card");
                        data.put("card[name]", this.creditCardHodler);
                        data.put("card[number]", this.creditCardNo);
                        data.put("card[expiry_month]", this.creditCardExpiryMM);
                        // 只需要后两位年份
                        data.put("card[expiry_year]", this.creditCardExpiryYYYY.substring(2));
                        data.put("card[cvv]", this.creditCardCvv);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        System.out.println("Razorpay触发实际支付参数===>" + GsonUtils.toJsonString(params));
        // TODO: 2020/4/27 要根据接口动态获取公司类型和key
        razorpay = new Razorpay(context, appId);
        webview = context.findViewById(R.id.payment_webview);
        scrollView = context.findViewById(R.id.sv_root);

        // Hide the webview until the payment details are submitted
        webview.setVisibility(View.GONE);
        razorpay.setWebView(webview);
        // 提供一个基础的数据判断，判断有效后再去提交
        razorpay.validateFields(data, new Razorpay.ValidationListener() {
            @Override
            public void onValidationSuccess() {
                try {
                    // Make webview visible before submitting payment details
                    scrollView.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    razorpay.submit(data, new PaymentResultWithDataListener() {
                        @Override
                        public void onPaymentSuccess(String s, PaymentData paymentData) {
                            // TODO: 2020/4/28 返回支付记录到服务器
                           /* new Handler(context.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    MmToastUtil.toast("支付成功");
                                }
                            });*/
                            Log.d("RAZORPAY", "onPaymentSuccess: ");
                            resultListener.onSucceeded(orderId, orderAmount, s, paymentData);

                        }

                        @Override
                        public void onPaymentError(int code, String description, PaymentData paymentData) {
                            resultListener.onFailed(description, paymentData);
                            Log.d("RAZORPAY", "onPaymentError: ");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onValidationError(Map<String, String> error) {
                /**
                 * The format for returned map is:
                 *   "field" : "contact"
                 *   "description" : "Descriptive error message"
                 */

                ToastUtils.showShort("Validation: " + error.get("field") + " " +
                        error.get("description"));
            }
        });


    }

    public static class RazorPayBuilder {
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
        private RazorPayMethodEnum payMethodEnum;
        private String upiAccount;
        private String netBankingCode;
        private String creditCardNo;
        private String creditCardExpiryMM;
        private String creditCardExpiryYYYY;
        private String creditCardHolder;
        private String creditCardCvv;
        private RazorPayResultListener resultListener;


        public RazorPayBuilder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public RazorPayBuilder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public RazorPayBuilder setOrderAmount(String orderAmount) {
            this.orderAmount = orderAmount;
            return this;
        }

        public RazorPayBuilder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public RazorPayBuilder setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public RazorPayBuilder setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public RazorPayBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public RazorPayBuilder setTestEnv(boolean isTestEnv) {
            this.isTestEnv = isTestEnv;
            return this;
        }

        public RazorPayBuilder setIsCallNativeAppForUpi(boolean isCallNativeAppForUpi) {
            this.isCallNativeAppForUpi = isCallNativeAppForUpi;
            return this;
        }

        public RazorPayBuilder setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }

        public RazorPayBuilder setPayMethod(RazorPayMethodEnum payMethodEnum) {
            this.payMethodEnum = payMethodEnum;
            return this;
        }

        public RazorPayBuilder setNetBankingCode(String netBankingCode) {
            this.netBankingCode = netBankingCode;
            return this;
        }

        public RazorPayBuilder setCreditCardNo(String creditCardNo) {
            this.creditCardNo = creditCardNo;
            return this;
        }

        public RazorPayBuilder setCreditCardExpiryMM(String expiryMM) {
            this.creditCardExpiryMM = expiryMM;
            return this;
        }

        public RazorPayBuilder setCreditCardExpiryYY(String expiryYY) {
            this.creditCardExpiryYYYY = expiryYY;
            return this;
        }

        public RazorPayBuilder setCreditCardHolder(String creditCardHolder) {
            this.creditCardHolder = creditCardHolder;
            return this;
        }

        public RazorPayBuilder setCreditCardCvv(String creditCardCvv) {
            this.creditCardCvv = creditCardCvv;
            return this;
        }

        public RazorPayBuilder setUpiAccount(String upiAccount) {
            this.upiAccount = upiAccount;
            return this;
        }

        public RazorPayBuilder setListener(RazorPayResultListener listener) {
            this.resultListener = listener;
            return this;
        }


        public RazorPayClientUtils build() {
            RazorPayClientUtils cashFree2PayUtils = new RazorPayClientUtils(this);
            return cashFree2PayUtils;
        }
    }

    public enum RazorPayMethodEnum {
        UPI_METHOD, NET_BANKING, CREDIT_CARD
    }


    public interface RazorPayResultListener {
        void onSucceeded(String orderId, String amount, String razorpayPaymentId, PaymentData paymentData);

        void onFailed(String description, PaymentData paymentData);
    }

}