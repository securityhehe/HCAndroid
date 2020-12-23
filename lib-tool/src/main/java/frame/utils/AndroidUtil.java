package frame.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2017/1/12 16:55
 * <p/>
 * Description:
 */
public class AndroidUtil {
    /**
     * 通过执行测试命令来检测
     */
    public static boolean isRoot() {
        return execRootCmdSilent("echo test") != -1;
    }

    public static int execRootCmdSilent(String paramString) {
        try {
            Process          localProcess          = Runtime.getRuntime().exec("su");
            Object           localObject           = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String           str                   = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            return localProcess.exitValue();
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }

    /**
     * copy字符串到粘贴板
     */
    public static void copy(Application application,String content) {
        ClipboardManager myClipboard = (ClipboardManager) application.getSystemService(CLIPBOARD_SERVICE);
        ClipData         myClip;
        myClip = ClipData.newPlainText("text", content);
        myClipboard.setPrimaryClip(myClip);
    }

    /**
     * 是否运行在主线程中
     */
    public static boolean isInMainThread() {
        return Looper.getMainLooper().equals(Looper.myLooper());
    }

    /**
     * 通过 View 获取Activity
     */
    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (Activity) view.getRootView().getContext();
    }

    /**
     * 关闭输入法弹出窗
     */
    public static void closedInputMethod(Context application) {
        InputMethodManager imm = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm && imm.isActive()) {
            imm.hideSoftInputFromWindow(ActivityStackManager.peek().getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 打开输入法弹出窗
     */
    public static void openInputMethod(Context application) {
        InputMethodManager imm = (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    /**
     * 获取SD卡的根目录
     */
    public static String getSDPath() {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 获取跟目录
            sdDir = Environment.getExternalStorageDirectory();
        }
        if (sdDir == null) {
            return "";
        } else {
            return sdDir.toString();
        }
    }

    /** 读取 assets 中的文件 */
    public static String readAssetsFile(Context context, String fileName) {
        try {
            // Return an AssetManager instance for your application's package
            InputStream is   = context.getAssets().open(fileName);
            int         size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "utf-8");
        } catch (IOException e) {
            // Should never happen!
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取通讯录的内容
     */
    public static String[] getPhoneContacts(Uri uri,Application application) {
        String[] contact = new String[2];
        try {
            // 得到ContentResolver对象
            ContentResolver cr = application.getContentResolver();
            // 取得电话本中开始一项的光标
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                // 取得联系人姓名
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact[0] = cursor.getString(nameFieldColumnIndex);
                // 取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                // 查看联系人有多少个号码，如果没有号码，返回0
                int    phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                Cursor phoneCursor;
                if (phoneCount > 0) {
                    // 获得联系人的电话号码列表
                    phoneCursor = application.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                    if (null == phoneCursor) {
                        return null;
                    }
                    if (phoneCursor.moveToFirst()) {
                        StringBuilder str = new StringBuilder();
                        do {
                            // 遍历所有的联系人下面所有的电话号码
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!TextUtil.isEmpty(phoneNumber) && phoneNumber.length() <=20){
                                str.append(phoneNumber);
                                str.append(",");
                            }
                            // 使用Toast技术显示获得的号码
                            // Toast.makeText(context, "联系人电话：" + phoneNumber, Toast.LENGTH_LONG).show();
                        }
                        while (phoneCursor.moveToNext());
                        if (str.toString().length() > 0) {
                            contact[1] = str.toString().substring(0, str.toString().length() - 1);
                        }
                    }
                    phoneCursor.close();
                }
                cursor.close();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return contact;
    }
}
