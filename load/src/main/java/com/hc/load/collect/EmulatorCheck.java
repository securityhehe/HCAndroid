package com.hc.load.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;

/**
 * 检验设备是否为模拟器
 */
public class EmulatorCheck {

    private EmulatorCheck() {
    }

    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};

    /*
     * 检测模拟器上特有的几个文件
     */
    public static boolean checkPipes() {
        try {
            for (int i = 0; i < known_pipes.length; i++) {
                String pipes = known_pipes[i];
                File qemu_socket = new File(pipes);
                if (qemu_socket.exists()) {
                    Log.v("Result:", "Find pipes!");
                    return true;
                }
            }
            Log.v("Result:", "Not Find pipes!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     *
     * 检测imesi is 是不是 31026  + 10个 0
     */
    private static String[] known_imsi_ids = {"310260000000000"};// 默认的 imsi id

    public static Boolean checkImsiIDS(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);

            @SuppressLint("MissingPermission") String imsi_ids = telephonyManager.getSubscriberId();

            for (String know_imsi : known_imsi_ids) {
                if (know_imsi.equalsIgnoreCase(imsi_ids)) {
                    Log.v("Result:", "Find imsi ids: 310260000000000!");
                    return true;
                }
            }
            Log.v("Result:", "Not Find imsi ids: 310260000000000!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkEmulatorAbsoluly() {
        try {
            if (Build.PRODUCT.contains("sdk") ||
                    Build.PRODUCT.contains("sdk_x86") ||
                    Build.PRODUCT.contains("sdk_google") ||
                    Build.PRODUCT.contains("Andy") ||
                    Build.PRODUCT.contains("Droid4X") ||
                    Build.PRODUCT.contains("nox") ||
                    Build.PRODUCT.contains("vbox86p")) {
                return true;
            }
            if (Build.MANUFACTURER.equals("Genymotion") ||
                    Build.MANUFACTURER.contains("Andy") ||
                    Build.MANUFACTURER.contains("nox") ||
                    Build.MANUFACTURER.contains("TiantianVM")) {
                return true;
            }
            if (Build.BRAND.contains("Andy")) {
                return true;
            }
            if (Build.DEVICE.contains("Andy") ||
                    Build.DEVICE.contains("Droid4X") ||
                    Build.DEVICE.contains("nox") ||
                    Build.DEVICE.contains("vbox86p")) {
                return true;
            }
            if (Build.MODEL.contains("Emulator") ||
                    Build.MODEL.equals("google_sdk") ||
                    Build.MODEL.contains("Droid4X") ||
                    Build.MODEL.contains("TiantianVM") ||
                    Build.MODEL.contains("Andy") ||
                    Build.MODEL.equals("Android SDK built for x86_64") ||
                    Build.MODEL.equals("Android SDK built for x86")) {
                return true;
            }
            if (Build.HARDWARE.equals("vbox86") ||
                    Build.HARDWARE.contains("nox") ||
                    Build.HARDWARE.contains("ttVM_x86")) {
                return true;
            }
            if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                    Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                    Build.FINGERPRINT.contains("Andy") ||
                    Build.FINGERPRINT.contains("ttVM_Hdragon") ||
                    Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                    Build.FINGERPRINT.contains("vbox86p") ||
                    Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     *  检测运营商,如果是Android,那么就是模拟器
     */
    public static boolean checkOperatorNameAndroid(Context context) {
        try {
            @SuppressLint("WrongConstant") String szOperatorName = ((TelephonyManager)
                    context.getSystemService("phone")).getNetworkOperatorName();

            if (szOperatorName.toLowerCase().equals("android")) {
                Log.v("Result:", "Find Emulator by OperatorName!");
                return true;
            }
            Log.v("Result:", "Not Find Emulator by OperatorName!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检验设备是否为模拟器(综合判断)
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        boolean oneResult = checkEmulatorAbsoluly();
        boolean isResult = oneResult == checkImsiIDS(context) == checkOperatorNameAndroid(context) == checkPipes();
        return isResult && (isResult && oneResult);
    }
}