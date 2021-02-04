package com.hc.load.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * @Author : ZhouWei
 * @TIME : 2018/10/19 15:27
 * @DESC :
 */

public class ViewAnimUtils {

    /**
     * 左右晃动以及缩放（警醒提示）
     * @param view
     * @param shakeFactor
     * @param duration
     * @return
     */
    public static ObjectAnimator shakeAnim(View view, float shakeFactor, long duration) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator objAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).setDuration(duration);
        objAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return objAnimator;
    }

    /**
     * 缩放（警醒提示）
     * @param view
     * @param duration
     * @return
     */
    public static ObjectAnimator sacleAnim(View view, long duration) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, 1.1f),
                Keyframe.ofFloat(.2f, 1.3f),
                Keyframe.ofFloat(.3f, 1.5f),
                Keyframe.ofFloat(.4f, 2.0f),
                Keyframe.ofFloat(.5f, 2.5f),
                Keyframe.ofFloat(.6f, 2.0f),
                Keyframe.ofFloat(.7f, 1.5f),
                Keyframe.ofFloat(.8f, 1.3f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, 1.1f),
                Keyframe.ofFloat(.2f, 1.3f),
                Keyframe.ofFloat(.3f, 1.5f),
                Keyframe.ofFloat(.4f, 2.0f),
                Keyframe.ofFloat(.5f, 2.5f),
                Keyframe.ofFloat(.6f, 2.0f),
                Keyframe.ofFloat(.7f, 1.5f),
                Keyframe.ofFloat(.8f, 1.3f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY).
                setDuration(duration);
    }

    private static void startSmallAnim(View view){
        ObjectAnimator objectAnimator = ViewAnimUtils.sacleAnim(view, 1000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    public static void alwaysRoate(View roateView,IRoateListener iRoateListener){
        if (roateView == null){
            return;
        }
        ObjectAnimator rotate = ObjectAnimator.ofFloat(roateView, "rotation", 0f, 360f);
        rotate.setDuration(800);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(ObjectAnimator.INFINITE);
        rotate.start();
        if (iRoateListener!=null) iRoateListener.stopAnim(rotate);
    }

    public interface IRoateListener{
        public void stopAnim(ObjectAnimator animator);
    }

    /**
     * translate view from x1 2 x2
     * @param transView
     * @param translate
     */
    public static void translate(View transView,float ... translate){
        ObjectAnimator tranlate = ObjectAnimator.ofFloat(transView, "translationX", translate);
        tranlate.setDuration(200);
        tranlate.setInterpolator(new AccelerateDecelerateInterpolator());
        tranlate.start();
    }

    /**
     * change height of the view
     * @param view
     * @param values
     */
    public static void changeViewHeightForAnim(final View view, int ... values){
        if (view == null) return;
        ValueAnimator animator = ValueAnimator.ofInt(values);

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setRepeatCount(0);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.height = (int)animation.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });
        animator.start();
    }

    public static void roateViewAngle(View roateView, float ... values){
        if (roateView == null){
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(roateView, "rotation", values);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setRepeatCount(0);
        animator.setDuration(500);
        animator.start();
    }
}
