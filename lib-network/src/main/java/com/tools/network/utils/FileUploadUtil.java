package com.tools.network.utils;

import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/5/23 15:59
 * <p/>
 * Description: 文件上传工具类
 */
@SuppressWarnings("unused")
public class FileUploadUtil {
    public static Map<String, RequestBody> getRequestMap(Map<String, ?> map) {
        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry entry : map.entrySet()) {
            if (TextUtils.isEmpty((String) entry.getKey()) || null == entry.getValue()) {
                continue;
            }
            if (entry.getValue() instanceof File) {
                File file = (File) entry.getValue();
                params.put(entry.getKey() + "\"; filename=\"" + file.getName() + "", RequestBody.create(MultipartBody.FORM, file));
            } else {
                String name  = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                params.put(name, RequestBody.create(MultipartBody.FORM, value));
            }
        }
        return params;
    }

    public static Map<String, RequestBody> getRequestMap(Object args) {
        Map<String, RequestBody> params = new HashMap<>();
        // 反射获取属性值
        obj2postData(args, args.getClass(), params);
        return params;
    }

    /**
     * object 转 post 数据。如果成员变量是对象，则重写 toString() 为 new Gson().toJson(this);
     */
    private static void obj2postData(Object args, Class<?> clazz, Map<String, RequestBody> params) {
        Field fields[] = clazz.getDeclaredFields();
        Field.setAccessible(fields, true);
        for (Field field : fields) {
            try {
                Object[] result = SerializedUtil.convertToRequestContent(args, field);
                if (null != result) {
                    String key   = result[0].toString();
                    Object value = result[1];
                    if (value instanceof File) {
                        File file = (File) value;
                        params.put(key + "\"; filename=\"" + file.getName() + "", RequestBody.create(MultipartBody.FORM, file));
                    } else {
                        params.put(key, RequestBody.create(MultipartBody.FORM, value.toString()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!clazz.getSuperclass().equals(Object.class)) {
            obj2postData(args, clazz.getSuperclass(), params);
        }
    }
}
