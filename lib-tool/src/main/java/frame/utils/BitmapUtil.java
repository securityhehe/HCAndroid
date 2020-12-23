package frame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Base64;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/11/23 16:34
 * <p/>
 * Description:
 */
public class BitmapUtil {
    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     *         上下文
     * @param resId
     *         资源ID
     *
     * @return bitmap
     */
    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is     = context.getResources().openRawResource(resId);
        Bitmap      bitmap = BitmapFactory.decodeStream(is, null, opt);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * KitKat不支持在Drawable上使用一个色彩过滤器(或隐式alpha)
     * 解决方案是使用相同的代码来构造复杂的Drawable，然后将其呈现为一个简单的位图绘制:
     */
    public static Drawable createDrawable(Context context, @DrawableRes int iconResource, int color, boolean disabled) {
        Drawable icon = ContextCompat.getDrawable(context, iconResource).mutate();
        return createDrawable(context, icon, color, disabled);
    }

    /**
     * KitKat不支持在Drawable上使用一个色彩过滤器(或隐式alpha)
     * 解决方案是使用相同的代码来构造复杂的Drawable，然后将其呈现为一个简单的位图绘制:
     */
    public static Drawable createDrawable(Context context, Drawable iconDrawable, int color, boolean disabled) {
        OvalShape     oShape     = new OvalShape();
        ShapeDrawable background = new ShapeDrawable(oShape);
        background.getPaint().setColor(Color.TRANSPARENT);

        ShapeDrawable shader = new ShapeDrawable(oShape);
        shader.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, 0, height,
                        new int[]{
                                Color.TRANSPARENT,
                                Color.TRANSPARENT,
                                Color.TRANSPARENT,
                                Color.TRANSPARENT
                        }, null, Shader.TileMode.REPEAT);
            }
        });

        iconDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        Drawable layer = new LayerDrawable(new Drawable[]{shader, background, iconDrawable});
        layer.setAlpha(disabled ? 128 : 255);

        // Note that on KitKat, setting a ColorFilter on a Drawable contained in a StateListDrawable
        // apparently doesn't work, although it does on later versions, so we have to render the colored
        // bitmap into a BitmapDrawable and then put that into the StateListDrawable
        Bitmap bitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        layer.setBounds(0, 0, layer.getIntrinsicWidth(), layer.getIntrinsicHeight());
        layer.draw(canvas);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
