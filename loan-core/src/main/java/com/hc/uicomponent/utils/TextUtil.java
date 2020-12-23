package com.hc.uicomponent.utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    /**
     * 判断是否是空字符串 null 或者 长度为0
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.toString().trim().length() == 0 || "null".equalsIgnoreCase(str.toString().trim());
    }

    public static boolean isZero(String str) {
        if (str != null) {
            try {
                double value = Double.parseDouble(str);
                if (value == 0) {
                    return true;//不显示
                }
                return false;//显示
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return true;//不显示
            }
        }
        return true;//不显示
    }

    /**
     * 判断是否所有的元素全部是空
     *
     * @param obj
     * @return true表示全部为空，反之不是全部都是空值
     */
    public static boolean isAllEmpty(String... obj) {
        if (obj == null) {
            throw new IllegalArgumentException("传入的参数不能为空！");
        }

        boolean isAllEmpty = true;
        for (int i = 0; i < obj.length; i++) {
            if (!isEmpty(obj[i])) {
                isAllEmpty = false;
                break;
            }
        }
        return isAllEmpty;
    }

    /**
     * 判断是否存在空值
     *
     * @param obj
     * @return true表示存在空值，false表示全部不是空值
     */
    public static boolean isExistEmpty(String... obj) {
        try {
            if (obj == null) {
                throw new IllegalArgumentException("传入的参数不能为空！");
            }

            for (int i = 0; i < obj.length; i++) {
                if (isEmpty(obj[i])) {
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 非空字符串连接
     *
     * @param str
     * @return
     */
    public static String noNullJoinStr(String... str) {
        if (str == null || str.length == 0)
            return "";

        String result = "";
        for (int i = 0; i < str.length; i++) {
            if (!isStrNull(str[i])) {
                result += str[i];
            }
        }
        return result;
    }

    /**
     * 拼接字符串
     */
    public static String join(Object[] tokens, CharSequence delimiter) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     */
    public static String replaceBlank(String str) {
        if (!isEmpty(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            str = m.replaceAll("");
        }
        return str;
    }

    /**
     * 删除所有的标点符号
     */
    public static String trimPunctuation(String str) {
        return str.replaceAll("[\\pP\\p{Punct}]", "");
    }

    /**
     * 格式化一个float
     *
     * @param format 要格式化成的格式 such as #.00, #.#
     */
    public static String formatFloat(float f, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(f);
    }

    /**
     * 将list 用传入的分隔符组装为String
     */
    public static String listToStringSlipStr(List list, String slipStr) {
        StringBuilder builder = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i)).append(slipStr);
            }
        }
        if (builder.toString().length() > 0) {
            return builder.toString().substring(0, builder.toString().lastIndexOf(slipStr));
        } else {
            return "";
        }
    }

    /**
     * 全角括号转为半角
     */
    public static String replaceBracketStr(String str) {
        if (!isEmpty(str)) {
            str = str.replaceAll("（", "(");
            str = str.replaceAll("）", ")");
        }
        return str;
    }

    /**
     * 全角字符变半角字符
     */
    public static String full2Half(String str) {
        if (isEmpty(str)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 65281 && c < 65373) {
                builder.append((char) (c - 65248));
            } else {
                builder.append(str.charAt(i));
            }
        }
        return builder.toString();
    }

    /**
     * 解析字符串返回map键值对(例：a=1&b=2 => a=1,b=2)
     *
     * @param query   源参数字符串
     * @param split1  键值对之间的分隔符（例：&）
     * @param split2  key与value之间的分隔符（例：=）
     * @param dupLink 重复参数名的参数值之间的连接符，连接后的字符串作为该参数的参数值，可为null
     *                null：不允许重复参数名出现，则靠后的参数值会覆盖掉靠前的参数值。
     * @return map
     */
    public static Map<String, String> parseQuery(String query, char split1, char split2, String dupLink) {
        if (!isEmpty(query) && query.indexOf(split2) > 0) {
            Map<String, String> result = new HashMap<>();

            String name = null;
            String value = null;
            String tempValue;
            for (int i = 0; i < query.length(); i++) {
                char c = query.charAt(i);
                if (c == split2) {
                    value = "";
                } else if (c == split1) {
                    if (!isEmpty(name) && value != null) {
                        if (dupLink != null) {
                            tempValue = result.get(name);
                            if (tempValue != null) {
                                value += dupLink + tempValue;
                            }
                        }
                        result.put(name, value);
                    }
                    name = null;
                    value = null;
                } else if (value != null) {
                    value += c;
                } else {
                    name = (name != null) ? (name + c) : "" + c;
                }
            }

            if (!isEmpty(name) && value != null) {
                if (dupLink != null) {
                    tempValue = result.get(name);
                    if (tempValue != null) {
                        value += dupLink + tempValue;
                    }
                }
                result.put(name, value);
            }
            return result;
        }
        return null;
    }

    public static String stripHtml(String content) {
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        content = content.replaceAll("\\<.*?>", "");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        return content;
    }

    /**
     * 截取字符串中的数值部分
     */
    public static String substringNumber(String params) {
        Matcher matcher = Pattern.compile("[0-9,.%]+").matcher(params);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            return params.substring(start, end);
        } else {
            return "0";
        }
    }

    /**
     * 判断字符串是否为null
     *
     * @param str
     * @return
     */
    public static boolean isStrNull(String str) {
        return str == null || "".equals(str.trim()) || "null".equalsIgnoreCase(str);
    }


    public static boolean isHttpOrHttps(String httpUrl) {
        if (isStrNull(httpUrl)) {
            return false;
        }
        if (httpUrl.startsWith("http://") || httpUrl.startsWith("https://")) return true;

        return false;
    }

    public static boolean isHasEmoji(String source){
        if (source == null) return false;
        for (char s : source.toCharArray()) {
            int type = Character.getType(s);
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return true;
            }
        }
        return false;
    }
}
