package com.hc.uicomponent.timer

import java.util.*

object DefaultTimer : Timer {

    private const val TIMER_PERIOD_MS = 1000L

    private var startTime = System.currentTimeMillis()
    private var pauseTime = 0L
    override var isStart = false

    override fun getPausedTime() : Long = pauseTime - startTime

    override fun getElapsedTime() = System.currentTimeMillis() - startTime

    override fun resetPauseTime() {
        pauseTime = System.currentTimeMillis()
    }

    override fun resetStartTime() {
        startTime = System.currentTimeMillis()
    }

    override fun updatePausedTime() {
        startTime += System.currentTimeMillis() - pauseTime
    }

    private var timer = Timer()

    override fun reset() {
        timer.cancel()
        isStart = false
    }

    override fun start(task: TimerTask) {
        timer = Timer()
        timer.scheduleAtFixedRate(task, 0, TIMER_PERIOD_MS)
        isStart = true
    }
}
