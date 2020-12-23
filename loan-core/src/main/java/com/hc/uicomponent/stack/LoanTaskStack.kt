package com.hc.uicomponent.stack

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

object LoanTaskStack : Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null
    var taskSize = 0
    private var finalCount: Int = 0

    private fun addTaskStackSize() {
        taskSize++
    }

    private fun deleteTaskStackSize() {
        taskSize--
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addTaskStackSize()
        ActivityStack.pushActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        finalCount++ //如果mFinalCount ==1，说明是从后台到前台 Log.e("onActivityStarted", mFinalCount +"");
        if (finalCount == 1) {
            //说明从后台回到了前台
            runAppForeground()
        }

    }

    private fun runAppForeground() {
        broadcastAppVisibilityChanged(true)
    }

    private fun broadcastAppVisibilityChanged(visibility: Boolean) {

    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity == currentActivity) {
            currentActivity = null
        }
    }

    fun taskStack() = ActivityStack

    override fun onActivityStopped(activity: Activity) {
        finalCount--
        //如果mFinalCount ==0，说明是前台到后台 Log.i("onActivityStopped", mFinalCount +"");
        if (finalCount == 0) {
            //说明从前台回到了后台
            runAppBackground()
        }
    }

    private fun runAppBackground() {
        broadcastAppVisibilityChanged(false)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        deleteTaskStackSize()
        ActivityStack.popActivity(activity)
    }
}