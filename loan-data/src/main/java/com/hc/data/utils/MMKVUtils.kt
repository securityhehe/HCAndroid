package com.hc.data.utils

import android.content.Context
import android.content.SharedPreferences

import com.hc.uicomponent.provider.ContextProvider
import com.tencent.mmkv.MMKV
import com.test.loan_data.BuildConfig.mmkvpasswd

fun mmkv(): MMKV {
    return try {
        MMKV.defaultMMKV()
    } catch (e: Exception) {
        e.printStackTrace()
        MMKV.initialize(ContextProvider.app )
        MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, mmkvpasswd)
    }
}

fun mmkvWithId(id: String): MMKV {
    return try {
        MMKV.mmkvWithID(id)
    } catch (e: Exception) {
        e.printStackTrace()
        MMKV.initialize(ContextProvider.app)
        MMKV.mmkvWithID(id)
    }
}

object MMKVUtils {
    fun importFromSharedPreferencesAndGetMMKV(context: Context, name: String): SharedPreferences {
        val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val mmkv = mmkvWithId(name)
        mmkv.importFromSharedPreferences(prefs)
        prefs.edit().clear().apply()
        return mmkv
    }
}
