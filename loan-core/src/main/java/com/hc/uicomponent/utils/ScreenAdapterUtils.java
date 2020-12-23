package com.hc.uicomponent.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class ScreenAdapterUtils {

    private static final float WIDTH = 360;   //纵向适配，单位是dp
    private static final float HEIGHT  = 640; //横向适配，单位是dp

    private static float calcSize = HEIGHT;

    private static float appDensity;//表示屏幕密度
    private static float appScaleDensity; //字体缩放比例，默认appDensity

    public static void setDensity(final Application application, Activity activity){
        //从activity中获取最新状态，不要从application中获取resource
        int requestedOrientation = activity.getRequestedOrientation();

        if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == requestedOrientation){
            //System.out.println(">>>>>>>横屏");
            calcSize =  HEIGHT;

        } else if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == requestedOrientation){
            //System.out.println(">>>>>>>纵屏");
            calcSize = WIDTH;
        }

        //获取当前app的屏幕显示信息
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (appDensity == 0){
            //初始化赋值操作
            appDensity = displayMetrics.density;
            appScaleDensity = displayMetrics.scaledDensity;

            //添加字体变化监听回调
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体发生更改，重新对scaleDensity进行赋值
                    if (newConfig != null && newConfig.fontScale > 0){
                        appScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        //计算目标值density, scaleDensity, densityDpi
        float targetDensity = displayMetrics.widthPixels / calcSize; // 1080 / 360 = 3.0
        float targetScaleDensity = targetDensity * (appScaleDensity / appDensity);
        int targetDensityDpi = (int) (targetDensity * 160);

        //替换Activity的density, scaleDensity, densityDpi
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        dm.density = targetDensity;
        dm.scaledDensity = targetScaleDensity;
        dm.densityDpi = targetDensityDpi;
    }

    /**
     * 获取到原型UI尺寸(dp)转换成[适配的px值]
     * @param activity
     * @param dpValue
     * @return
     */
    public static int dp2px(Context activity, int dpValue){
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        return (int) (dpValue * dm.density);
    }

    /**
     * 获取到原型UI尺寸（px）对应的[适配大小(px)]
     * @param activity
     * @param size
     * @return
     */
    public static int adapterWidthSize(Context activity,int size){
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        return (int)(screenWidth / 720.0f * size);
    }

    /**
     * 获取到当前屏幕与原始设计屏幕的宽度比例
     * @param activity
     * @return
     */
    public static float adapterRate(Context activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        return screenWidth / 720.0f;
    }

    /**
     *  get screen width
     */
    public static int getScreenWidth(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
    /**
     *  get screen height
     */
    public static int getScreenHeight(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
