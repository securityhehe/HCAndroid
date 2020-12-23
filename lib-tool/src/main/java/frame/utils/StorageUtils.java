package frame.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.util.Locale;

/**
 * @Author : ZhangHe
 * @Time : 8/21/2020 3:25 PM
 * @Desc :
 */
public class StorageUtils {

    public void queryStorage(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        //存储块总数量
        long blockCount = statFs.getBlockCountLong();
        //块大小
        long blockSize = statFs.getBlockSizeLong();
        //可用块数量
        long availableCount = statFs.getAvailableBlocksLong();
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间 
        long freeBlocks = statFs.getFreeBlocksLong();
        //这两个方法是直接输出总内存和可用空间，也有getFreeBytes
        //API level 18（JELLY_BEAN_MR2）引入
        long totalSize = statFs.getTotalBytes();
        long availableSize = statFs.getAvailableBytes();

        Log.d("statfs","total = " + getUnit(totalSize));
        Log.d("statfs","availableSize = " + getUnit(availableSize));

        //这里可以看出 available 是小于 free ,free 包括保留块。
        Log.d("statfs","total = " + getUnit(blockSize * blockCount));
        Log.d("statfs","available = " + getUnit(blockSize * availableCount));
        Log.d("statfs","free = " + getUnit(blockSize * freeBlocks));
    }

    public static long queryAvailableSize() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long availableSize = statFs.getAvailableBytes();
        return availableSize;
    }

    private String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 单位转换
     */
    private String getUnit(float size) {
        int index = 0;
        while (size > 1024 && index < 4) {
            size = size / 1024;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }
}
