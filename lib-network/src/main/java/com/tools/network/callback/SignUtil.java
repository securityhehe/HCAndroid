package com.tools.network.callback;

import java.io.File;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import frame.utils.MDUtil;


public class SignUtil {
    /**
     * 通用参数 - H5请求时需要用到 ,正常接口请求无需使用
     */
    private TreeMap<String, String> commonParamsTreeMap;

    private SignUtil() {
        commonParamsTreeMap = new TreeMap<>();
        IHttpParamProvider.getInstance().addCommonHttpParam(commonParamsTreeMap);
    }

    public static SignUtil getInstance() {
        return SignUtilInstance.instance;
    }

    private static class SignUtilInstance {
        static SignUtil instance = new SignUtil();
    }

    /**
     * 往 body 中添加动态参数
     */
    Map<String, String> addParams() {
        Map<String, String> map = new TreeMap<>();
        map.putAll(commonParamsTreeMap);
        addUserId(map);
        return map;
    }

    /**
     * 往 header 中添加动态参数（可以做全参数验签）
     */
    Map<String, String> signParams(Map paramsMap) {
        TreeMap<String, String> map = new TreeMap<>();
        String key = IHttpParamProvider.getInstance().addSigParam(map);
        map.put(key, getSigna(new TreeMap<>(paramsMap)));
        return map;
    }

    /**
     * 往 map 中添加额外公共参数 and 签名
     */
    public Map<String, String> addCommonParamsAndSign(Map<String, Object> map) {
        // 添加额外公共参数
        map.putAll(commonParamsTreeMap);
        addUserId(map);
        // 签名
        return signParams(map);
    }

    /**
     * 对 TreeMap 数据进行签名，H5请求使用
     */
    public String getCommonParams(TreeMap<String, String> treeMap) {
        treeMap.putAll(commonParamsTreeMap);
        addUserId(treeMap);
        return getPostParamsStr(treeMap);
    }

    /**
     * 将 Map 拼装成请求字符串
     *
     * @return 返回请求参数
     */
    private String getPostParamsStr(TreeMap map) {
        Iterator it = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        try {
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                sb.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("&");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sb.toString().length() > 1) {
            return sb.toString().substring(0, sb.length() - 1);
        } else {
            return sb.toString();
        }
    }

    /**
     * 对所有参数进行验签
     *
     * @param map 有序请求参数map
     */
    private String getSigna(TreeMap map) {
        String signa = "";
        try {
            Iterator it = map.entrySet().iterator();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getValue() instanceof File)
                    continue;// URLEncoder.encode(, "UTF-8")
                sb.append(entry.getKey()).append("=").append(URLDecoder.decode(entry.getValue().toString(), "UTF-8")).append("|");
            }
            // 所有请求参数排序后的字符串后进行 MD5（32）
            // signa = MDUtil.encode(MDUtil.ENCRYPTION_MODE.MD5, sb.toString());
            // 得到的MD5串拼接 appsecret 再次 MD5，所得结果转大写
            String sign;
            if (sb.toString().length() > 1) {
                sign = sb.toString().substring(0, sb.length() - 1);
            } else {
                sign = sb.toString();
            }
            signa = MDUtil.encode(MDUtil.TYPE.MD5, IHttpParamProvider.getInstance().getKey() + IHttpParamProvider.getInstance().getToken() + sign).toUpperCase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signa;
    }

//    /**
//     * 获取 token
//     */
//    public static String getToken() {
//        OauthTokenRec mo = SpUtils.getInstance().getEntity(OauthTokenRec.class);
//        if (mo != null) {
//            return mo.getToken();
//        }
//        return "";
//    }
//

    /**
     * 往 map 中添加 userId
     */
    private void addUserId(Map map) {
//        String userId = getUserId();
//        if (!TextUtils.isEmpty(userId)) {
//            map.put(USER_ID, userId);
//        }
        IHttpParamProvider paramProvider = IHttpParamProvider.getInstance();
        map.put(paramProvider.getUserIdKey(), paramProvider.getUserId());
    }


//
//    /**
//     * 获取 userId
//     */
//    public static String getUserId() {
//        OauthTokenRec mo = SpUtils.getInstance().getEntity(OauthTokenRec.class);
//        if (mo != null) {
//            return mo.getUserId();
//        }
//        return "";
//    }
//
//    /**
//     * 获取 phone
//     */
//    public static String getPhone() {
//        OauthTokenRec mo = SpUtils.getInstance().getEntity(OauthTokenRec.class);
//        if (mo != null) {
//            return mo.getMobile();
//        }
//        return "";
//    }
}
