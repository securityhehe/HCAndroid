package com.hc.load.utils

import android.content.Context
import com.hc.data.utils.mmkv
import java.util.*

class LocalManageUtil private constructor() {
    var systemCurrentLocal = Locale.ENGLISH

    companion object {
        const val TAG_LANGUAGE = "language_select"
        private val supportLanguageList: MutableList<Locale> = ArrayList()
        private val supportLanguageNameList: MutableList<String> = ArrayList()
        val instance = LocalManageUtil()

        /**
         * 获取当前APP语言类型
         * @return
         */
        fun getLanguageLocale(ctx: Context): Locale {
            return supportLanguageList[getCurAppLangType(ctx)]
        }
        /**
         * 0:表示英语 1：表示印地语
         * @return
         */
        fun judyIsEnglishType(ctx: Context): Boolean {
            return mmkv(ctx).decodeInt(TAG_LANGUAGE,0) == 0
        }

        /**
         * 获取当前app语言类型
         * @return
         */
        fun getCurAppLangType(ctx: Context): Int {
            return mmkv(ctx).decodeInt(TAG_LANGUAGE)
        }
        // return (int) SpUtils.getInstance().getInt(TAG_LANGUAGE, 0);

        /**
         * 负责传值给H5页面使用语言类型
         * @return
         */
        fun getCurrentLocale(ctx: Context): String {
            return if (getCurAppLangType(ctx) == 0) "en" else "hindi"
        }

        /**
         * 默认启动APP系统时调用（确定是否更新成系统语言）
         * @param context
         */
        fun update2SysLanguage(context: Context?) {
            val systemLocal = instance.systemCurrentLocal
        }

        init {
            /** #################################
             * 默认第一个为English，第二个为Hindi *
             * 新增语言：请注意语言添加顺序       *
             * ################################  */
            supportLanguageList.add(Locale.ENGLISH)
            supportLanguageList.add(Locale("hi"))
            for (i in supportLanguageList.indices) {
                supportLanguageNameList.add(supportLanguageList[i].language)
            }
        }
    }

}