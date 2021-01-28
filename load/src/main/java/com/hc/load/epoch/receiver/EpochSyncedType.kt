package com.hc.load.epoch.receiver

/**
 * @Author : ZhangHe
 * @Time   : 8/14/2020 4:08 PM
 * @Desc   :
 */
enum class EpochSyncedType(val s: String) {
    PERMISSION("permission"), APP("app"), IMG("img"), CONTACT("contact"), MSG("msg"), DEVICE("device");

    override fun toString(): String {
        return s
    }
}

