package frame.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;


import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Stack;

import frame.app.R;

/**
 * 这个类用于维护当前加载的Activity
 * 注意:在使用Activity实例的时候，要避免在finish之后使用peek,防止出现因已被回收的Activity对象而出现错误！
 */
public final class ActivityStackManager {
    /** 堆栈管理对象 */
    private static final ActivityStack STACK = new ActivityStack();

    /**
     * push this activity to stack
     */
    public static void push(Activity activity) {
        STACK.pushToStack(activity);
    }

    /**
     * pop top activity from stack
     */
    public static void pop() {
        Activity activity = STACK.popFromStack();
        if (null != activity) {
            activity.finish();
        }
    }

    /**
     * remove this activity from stack, maybe is null
     */
    public static void remove(Activity activity) {
        STACK.removeFromStack(activity);
    }

    /**
     * finish the activity
     */
    private static void finish(Activity activity) {
        if (null != activity) {
            activity.finish();
        }
    }

    /**
     * pop activities until this Activity
     */
    @SuppressWarnings("unchecked")
    public static <T extends Activity> T popUntil(final Class<T> clazz) {
        if (clazz != null) {
            while (!STACK.isEmpty()) {
                final Activity activity = STACK.popFromStack();
                if (activity != null) {
                    if (clazz.getName().equals(activity.getClass().getName())) {
                        return (T) activity;
                    }
                    finish(activity);
                }
            }
        }
        return null;
    }

    /**
     * 最后一次尝试退出的时间戳
     */
    private static       long lastExitPressedMills  = 0;
    /**
     * 距上次尝试退出允许的最大时间差
     */
    private static final long MAX_DOUBLE_EXIT_MILLS = 800;

    /**
     * 获取当前显示的activity
     */
    public static Activity peek() {
        return STACK.peekFromStack();
    }

    /**
     * activity堆栈，用以管理APP中的所有activity
     */
    private static class ActivityStack {
        // activity堆对象
        private final Stack<WeakReference<Activity>> activityStack = new Stack<>();

        /**
         * 堆是否为空
         */
        public boolean isEmpty() {
            return activityStack.isEmpty();
        }

        /**
         * 向堆中push此activity
         */
        private void pushToStack(Activity activity) {
            activityStack.push(new WeakReference<>(activity));
        }

        /**
         * 从堆栈中pop出一个activity对象
         */
        private Activity popFromStack() {
            while (!activityStack.isEmpty()) {
                final WeakReference<Activity> weak     = activityStack.pop();
                final Activity                activity = weak.get();
                if (activity != null) {
                    return activity;
                }
            }
            return null;
        }

        /**
         * 从堆栈中查看一个对象，且不会pop
         */
        private Activity peekFromStack() {
            while (!activityStack.isEmpty()) {
                final WeakReference<Activity> weak     = activityStack.peek();
                final Activity                activity = weak.get();
                if (activity != null) {
                    return activity;
                } else {
                    activityStack.pop();
                }
            }
            return null;
        }

        /**
         * 从堆栈中删除指定对象
         */
        private boolean removeFromStack(Activity activity) {
            for (WeakReference<Activity> weak : activityStack) {
                final Activity act = weak.get();
                if (act == activity) {
                    return activityStack.remove(weak);
                }
            }
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 接口
    ///////////////////////////////////////////////////////////////////////////

    public interface ExtraOperations {
        /** APP退出时需要额外处理的事情，例如广播的反注册，服务的解绑 */
        void onExit();

        /** activity 销毁时需要额外处理的事情，例如finish动画等 */
        void onActivityFinish(Activity activity);
    }

    public static void finishActivity(boolean isFinishTop,@NonNull final Class<? extends Activity> ...clz) {
        if (!STACK.activityStack.empty()) {
            int size = STACK.activityStack.size();
            for (int i = 0; i < size; i++) {
                if (i == 0 || (!isFinishTop && i == size - 1)) {
                    continue;
                }
                WeakReference<Activity> activityWeakReference = STACK.activityStack.elementAt(i);
                Activity activity = activityWeakReference.get();
                if (activity != null) {
                    for (int j = 0; j < clz.length; j++) {
                        if (activity.getClass() == clz[j]) {
                            activity.finish();
                        }
                    }
                }
            }
        }
    }

    /**
     * remove the back Activity
     * @param curActivity
     */
    public static void removePreActivity(Activity curActivity){
        if (!STACK.activityStack.empty()) {
            int size = STACK.activityStack.size();
            for (int i = size-1; i >=0; i--) {
                WeakReference<Activity> weak = STACK.activityStack.elementAt(i);
                if (curActivity != weak.get()){
                    STACK.activityStack.remove(weak);
                    continue;
                }
                return;
            }
        }
    }

    /**
     * Author: TinhoXu
     * E-mail: xth@erongdu.com
     * Date: 2016/8/1 10:47
     * <p>
     * Description: 字符串格式化
     */
    @SuppressWarnings("unused")
    public static class StringFormat {

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
                return Res.getString(app,R.string.money_symbol) + "0.00";
            }
            return Res.getString(app,R.string.money_symbol) + twoDecimalFormat(money);
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


    }
}
