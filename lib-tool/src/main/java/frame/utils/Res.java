package frame.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.ContextCompat;


/**
 * author: rxwu
 * created on: 2018/9/5 12:06
 * description:资源文件类
 */
public class Res {
    public static String[] getStringArray(Context app, int main_control_circle_ids) {
        return app.getResources().getStringArray(main_control_circle_ids);
    }

    public static int[] getResIDs(Context app, int array_id) {
        TypedArray ar = app.getResources().obtainTypedArray(array_id);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);

        ar.recycle();
        return resIds;
    }

    public enum ResType {
        DRAWABLE("drawable"), STRING("string"), ID("id");
        String type;

        ResType(String type) {
            this.type = type;
        }
    }

    public static String getString(Context app, int res) {
        return app.getString(res);
    }

    /******
     * @detail 支持动态
     * @param params 替代占位符的参数列表
     * @return
     */
    public static String getStringDynamic(Context app, int res, Object... params) {
        try {
            String string = getString(app, res);
            return String.format(string, params);
        } catch (Exception ex) {
            return "";
        }


    }

    public static int getResIDByName(ResType type, String name, Context app) {
        Resources r = app.getResources();
        int id = r.getIdentifier(name, type.type, app.getPackageName());
        return id;
    }

    /****
     * @detail 根据资源id获取颜色
     * @param res
     * @return
     */
    public static int getColor(int res, Context app) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return app.getColor(res);
        } else {
            return app.getResources().getColor(res);
        }
    }

    /****
     * @detail 根据资源id获取dimension
     * @param res
     * @return
     */
    public static float getDimension(int res, Context app) {
        return app.getResources().getDimension(res);
    }


    /*******
     * @detail 根据id获取图片

     */
    public static Drawable getDrawalbe(int drawable, Context app) {
        return ContextCompat.getDrawable(app, drawable);

    }
}
