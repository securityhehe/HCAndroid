package frame.utils;

import android.content.Context;
import android.text.TextUtils;


import java.math.BigDecimal;
import java.text.DecimalFormat;

import frame.app.R;
import kotlin.jvm.JvmStatic;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/8/1 10:47
 * <p>
 * Description: 字符串格式化
 */
@SuppressWarnings("unused")
public class StringFormat {

    /**
     * 保留小数点后两位
     */
    public static String twoDecimalFormat(String args) {
        if (StringUtil.isEmpty(args)) {
            return "0.00";
        }
        return new DecimalFormat("######0.00").format(getDouble(args));
    }

    /**
     * The output format is ₹0.00
     *
     * @param money
     * @return
     */
    public static String showMoneyWithSymbol(Context app,String money) {
        if (StringUtil.isEmpty(money)) {
            return app.getString(R.string.money_symbol) + "25000.00";
        }
        return app.getString(R.string.money_symbol) + twoDecimalFormat(money);
    }

    public static double getDouble(String args) {
        try {
            if (StringUtil.isEmpty(args)) {
                return 0.0D;
            } else {
                return new BigDecimal(args).doubleValue();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0D;
        }
    }

    /**
     * 10手机号格式：XXXXX XX123
     */
    public static String phone7HideFormat(String args) {
        if (TextUtils.isEmpty(args)) {
            return args;
        }
        return args.replaceAll("\\d{7}(\\d{3})", "XXXXX XX$1");
    }
    /**
     * The output format is ₹-10.00
     *
     * @param money
     * @return
     */
    public static String showMoneyWithNegativeSymbol(Context app,String money) {
        if (TextUtil.isEmpty(money)) {
            return app.getString(R.string.money_symbol) + "0.00";
        }
        return app.getString(R.string.money_symbol) + "-" + twoDecimalFormat(money);
    }

    public static boolean isOverZero(String args) {
        if (TextUtil.isEmpty(args)) {
            return false;
        }
        try {
            double value = Double.parseDouble(args);
            if (value > 0) return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    /**
     * 取整数
     */
    public static String integerFormat(String args) {
        if (TextUtil.isEmpty(args)) {
            return "0";
        }
        return new DecimalFormat("######0").format(ConverterUtil.getInteger(args));
    }


    /**
     * 数值格式化 - 12345.00
     */
    public static String doubleFormatNoSymbo(Object args) {
        if (args != null && !TextUtil.isEmpty(args.toString())) {
            String number = args.toString();
            try {
                String amount = new DecimalFormat("#0.00").format(ConverterUtil.getDouble(number));
                return amount;
            } catch (Exception e) {
                e.printStackTrace();
                return number;
            }
        } else {
            return "0.00";
        }
    }

    /**
     * 两数相加
     *
     * @param str
     * @return
     */
    public static String addingNumbers(Context app,double... str) {
        if (str == null || str.length == 0)
            return app.getString(R.string.money_symbol) + "0.00";
        double addResult = 0.0D;
        for (int i = 0; i < str.length; i++) {
            addResult += str[i];
        }
        return showMoneyWithSymbol(app,"" + addResult);
    }

}
