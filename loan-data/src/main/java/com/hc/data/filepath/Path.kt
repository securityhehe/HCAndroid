package com.hc.data.filepath

import android.os.Environment

fun getSDPath(): String? {
    var sdDir: String? = null
    // 判断sd卡是否存在
    val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    if (sdCardExist) {
        sdDir = Environment.getExternalStorageDirectory().absolutePath
    }
    return sdDir?:""
}
