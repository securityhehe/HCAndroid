package com.hc.uicomponent.timer

import java.util.*

interface Timer {
    fun reset()
    fun start(task: TimerTask)
    fun getElapsedTime(): Long
    fun updatePausedTime()
    fun getPausedTime() : Long
    fun resetStartTime()
    fun resetPauseTime()
    var isStart:Boolean
}