package com.example.smartstaffsolutions.timer

interface CoroutineTimerListener {
    fun onTick(timeLeft: Long?, error: Exception? = null)
    fun onStop(error: Exception? = null) {}
    fun onPause(remainingTime: Long) {}
    fun onDestroy() {}
}