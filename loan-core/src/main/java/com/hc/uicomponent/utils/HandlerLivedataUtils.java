package com.hc.uicomponent.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.hc.uicomponent.annotation.BindLiveData;
import com.hc.uicomponent.base.BaseActivity;
import com.hc.uicomponent.base.BaseFragment;
import com.hc.uicomponent.base.BaseViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class HandlerLivedataUtils {

    //注册监听者 && 以及回调
    public static void registerFragObserver(final BaseFragment fragment, BaseViewModel viewModel){
        registerObserver(fragment,viewModel);
    }

    //注册监听者 && 以及回调
    public static void registerActObserver(final BaseActivity activity, BaseViewModel viewModel) {
        registerObserver(activity,viewModel);
    }

    //注册监听者 && 以及回调
    private static void registerObserver(final Object object, BaseViewModel viewModel){
        try {
            //=============================//收集Activity|Fragment中定义注解为@BindLiveData的方法==========================
            HashMap<Integer, Method> actMethodMap = null;
            Method[] actMethods = object.getClass().getDeclaredMethods();
            if (actMethods.length == 0) return;

            for (int i = 0; i < actMethods.length; i++) {
                Method method = actMethods[i];
                method.setAccessible(true);
                if (!method.isAnnotationPresent(BindLiveData.class)) {
                    continue;
                }

                if (actMethodMap == null) {
                    actMethodMap = new HashMap<>();
                }
                BindLiveData actLiveDataAnno = method.getAnnotation(BindLiveData.class);
                actMethodMap.put(actLiveDataAnno.value(), method);
            }
            //没有收集到数据,return
            if (actMethodMap == null) return;

            //=============================//收集ViewModel中定义注解为@BindLiveData的方法==========================
            Field[] lvFields = viewModel.getClass().getDeclaredFields();
            HashMap<Integer, MutableLiveData> liveFieldMap = null;
            if (lvFields.length == 0) return;

            for (int i = 0; i < lvFields.length; i++) {
                Field lvField = lvFields[i];
                lvField.setAccessible(true);
                BindLiveData lvAnnotation = lvField.getAnnotation(BindLiveData.class);
                if (lvAnnotation == null) continue;
                if (liveFieldMap == null) liveFieldMap = new HashMap<>();
                liveFieldMap.put(lvAnnotation.value(), (MutableLiveData) lvField.get(viewModel));
            }
            //没有收集到数据,return
            if (liveFieldMap == null) return;
            //匹配注册观察关系
            for (Map.Entry<Integer, Method> entry : actMethodMap.entrySet()) {
                Integer key = entry.getKey();
                final Method method = entry.getValue();
                MutableLiveData mLiveData = liveFieldMap.get(key);
                if (mLiveData != null) {
                    LifecycleOwner lifecycleOwner = null;
                    if (object instanceof FragmentActivity){
                        lifecycleOwner = ((FragmentActivity) object);
                    }else if (object instanceof Fragment){
                        lifecycleOwner = ((Fragment)object).getViewLifecycleOwner();
                    }
                    mLiveData.observe(lifecycleOwner, (Observer<Object>) s -> {
                        try {
                            method.setAccessible(true);
                            method.invoke(object, s);
                        } catch (Exception e) {
    //                        e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println("关联关系错误！");
            e.printStackTrace();
        }
    }
}

