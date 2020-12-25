package com.hc.data.utils

import android.content.Context
import android.content.SharedPreferences

import com.tencent.mmkv.MMKV
import com.test.loan_data.BuildConfig.mmkvpasswd

fun mmkv(app: Context): MMKV {
    return try {
        MMKV.defaultMMKV()
    } catch (e: Exception) {
        e.printStackTrace()
        MMKV.initialize(app )
        MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, mmkvpasswd)
    }
}

fun mmkvWithId(app:Context,id: String): MMKV {
    return try {
        MMKV.mmkvWithID(id)
    } catch (e: Exception) {
        e.printStackTrace()
        MMKV.initialize(app)
        MMKV.mmkvWithID(id)
    }
}

object MMKVUtils {
    fun importFromSharedPreferencesAndGetMMKV(context: Context, name: String): SharedPreferences {
        val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val mmkv = mmkvWithId(context,name)
        mmkv.importFromSharedPreferences(prefs)
        prefs.edit().clear().apply()
        return mmkv
    }
}
