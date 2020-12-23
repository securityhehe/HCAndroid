package com.hc.main.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.multidex.MultiDex
import com.hc.uicomponent.stack.ActivityStack
import com.hc.uicomponent.stack.LoanTaskStack

class LoanApp: Application() {
    companion object {
        private var isAppBackground = true

        @JvmStatic
        val mainHandler = Handler(Looper.getMainLooper())

        //@JvmStatic
        //lateinit var appConfiguration: AppConfiguration

        @JvmStatic
        lateinit var application: Context

        @JvmStatic
        fun runOnMainThread(task: Runnable) {
            runOnMainThread(task, 0L)
        }

        @JvmStatic
        fun runOnMainThread(task: Runnable, delay: Long) {
            if (delay > 0) {
                mainHandler.postDelayed(task, delay)
            } else {
                mainHandler.post(task)
            }
        }

        @JvmStatic
        fun removeTask(task: Runnable) {
            mainHandler.removeCallbacks(task)
        }

        @JvmStatic
        fun isAppBackground() = isAppBackground

        @JvmStatic
        fun setAppBackground(isBackground: Boolean) {
            isAppBackground = isBackground
        }
    }

    override fun onCreate() {
        super.onCreate()
        LoanBase.initVideoChat(this)
        registerActivityLifecycleCallbacks(LoanTaskStack)
    }


    fun runOnUIThread(task: Runnable?, delay: Long) {
        runOnMainThread(task!!, delay)
    }

    fun runOnUIThread(task: Runnable?) {
        runOnUIThread(task, 0)
    }


    fun removePendingTask(task: Runnable?) {
        removeTask(task!!)
    }

    fun getTaskStackSize(): Int {
        return LoanTaskStack.taskSize
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun getCurrentActivity(): Activity? {
        return ActivityStack.currentActivity()
    }
}