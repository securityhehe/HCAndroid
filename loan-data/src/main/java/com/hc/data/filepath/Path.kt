package com.hc.data.filepath

import android.os.Environment

//保存文件路径。
val ROOT_PATH: String = getSDPath() + "/eCommerce/"
const val PHOTO = "/photo/"
val RELEASE_PHOTO_PATH_APP = "$ROOT_PATH$PHOTO/release/"
val TEMP_PHOTO_PATH_FOR_APP = "$ROOT_PATH$PHOTO/temp/"
fun getSDPath(): String? {
    var sdDir: String? = null
    // 判断sd卡是否存在
    val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    if (sdCardExist) {
        sdDir = Environment.getExternalStorageDirectory().absolutePath
    }
    return sdDir?:""
}
