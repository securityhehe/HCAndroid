package com.hc.uicomponent.stack

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import java.util.*

object ActivityStack {

    private var activityStack: Stack<Activity>? = null

    fun popLastActivity() {
        if (activityStack != null && (!activityStack.isNullOrEmpty())) {
            var activity = activityStack?.lastElement()
            if (activity != null) {
                activity.finish()
                activity = null
            }
        }
    }

    fun popActivity(activity: Activity?) {
        var activity = activity
        if (activity != null && !activityStack.isNullOrEmpty()) {
            activity.finish()
            activityStack?.remove(activity)
            activity = null
        }
    }

    fun currentActivity(): Activity? {
        return if (!activityStack.isNullOrEmpty()) {
            activityStack?.lastElement()
        } else null
    }

    fun pushActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack?.add(activity)
    }

    fun popAllActivityExceptOne(cls: Class<*>) {
        while (!activityStack.isNullOrEmpty()) {
            val activity = currentActivity() ?: break
            if (activity.javaClass == cls) {
                break
            }
            popActivity(activity)
        }
    }

    fun popActivity(cls: Class<*>) {
        while (!activityStack.isNullOrEmpty()) {
            val activity = currentActivity() ?: break
            if (activity.javaClass == cls) {
                popActivity(activity)
                break
            }
        }
    }
}