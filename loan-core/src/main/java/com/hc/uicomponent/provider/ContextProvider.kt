package com.hc.uicomponent.provider

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.lang.IllegalArgumentException
import java.util.*

/**
 * 获取全局的Application-Context
 */
object ContextProvider {

    lateinit var app: Context
    var mNavIdProvider: INavContextProvider? = null
    var mPermissionsRationaleProvider :PermissionsRationaleProvider? = null
    fun init(application: Context) {
        app = application
    }

    fun checkApplicationContext(): Unit {
        if (!this::app.isInitialized) throw IllegalArgumentException("请在全局的自定义的Application中初始化context")
    }

    @JvmStatic
    fun getString(@StringRes id: Int): String {
        checkApplicationContext()
        return app.getString(id)
    }

    @JvmStatic
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        checkApplicationContext()
        return app.getString(resId, *formatArgs)
    }

    @JvmStatic
    fun getString(str: String, vararg formatArgs: Any?): String {
        checkApplicationContext()
        return String.format(str, *formatArgs)
    }

    @JvmStatic
    fun getDrawable(@DrawableRes id: Int): Drawable? {
        checkApplicationContext()
        return ContextCompat.getDrawable(app, id)
    }

    @JvmStatic
    fun getColor(@ColorRes id: Int): Int {
        checkApplicationContext()
        return ContextCompat.getColor(app, id)
    }

    //导航上下文提供者。提供给各种导航ID
    interface INavContextProvider {
        fun getKycNavId(): Int
        fun getMainNavId(): Int
        fun getRootNavId(): Int
        fun getInfoContainerId():Int
        fun getWebViewNavId():Int
        fun getSettingNavId():Int
        fun showHideMainMenu(isVisible:Int):Unit
        fun getToLogInActionId():Int
        fun mainNav(): Int
        fun getOrderDetail(): Int
        fun getCommitOrderNavId():Int
        fun getCommitOrderLoadingNavId():Int
        fun getPlanNavId():Int
        fun getPayNavId():Int

    }

    //解释权限说明。
    interface PermissionsRationaleProvider{
        fun getRationaleText(context: Context,vararg permissions :String ):String?{
            return ""
        }
    }

}

@SuppressLint("RestrictedApi")
fun NavController.navigationStackPrintln() {
    val iterator = backStack.descendingIterator()
    println("====================stack==================")
    while (iterator.hasNext()) {
        val entry = iterator.next()
        println(entry.destination.toString())
    }
    println("====================end==================")
}


@SuppressLint("RestrictedApi")
fun NavController.navigationStackPrintln(tag:String) {
    val iterator = backStack.descendingIterator()

    println("==================${tag}==stack==================")
    while (iterator.hasNext()) {
        val entry = iterator.next()
        println(entry.destination.toString())
    }
    println("==================${tag}==end==================")
}

val isTEST = false