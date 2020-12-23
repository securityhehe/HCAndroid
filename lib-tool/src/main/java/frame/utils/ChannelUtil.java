package frame.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ChannelUtil {
	private static final String CHANNEL_KEY = "cztchannel";
	private static String mChannel;
	/**
	 * 返回市场渠道名称，如果获取失败返回默认值
	 * @param context
	 * @return
	 */
	public static String getChannel(Context context){
		return getChannel(context, "");
	}
	/**
	 * 返回市场。  如果获取失败返回defaultChannel
	 * @param context
	 * @param defaultChannel
	 * @return
	 */
	public static String getChannel(Context context, String defaultChannel) {
		//内存中获取
		if(!TextUtils.isEmpty(mChannel)){
			return mChannel;
		}
		//从apk中获取
		mChannel = getChannelFromApk(context, CHANNEL_KEY);
		if(!TextUtils.isEmpty(mChannel)){
			return mChannel;
		}
		//全部获取失败
		return defaultChannel;
    }
	/**
	 * 从apk中获取版本信息
	 * @param context
	 * @param channelKey
	 * @return
	 */
	private static String getChannelFromApk(Context context, String channelKey) {
		//从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("_");
        String channel = "";
        if (split != null && split.length >= 2) {
        	channel = ret.substring(split[0].length() + 1);
        }
        return channel;
	}
}
