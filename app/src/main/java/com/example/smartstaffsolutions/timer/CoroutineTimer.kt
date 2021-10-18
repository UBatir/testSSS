package com.example.smartstaffsolutions.timer

import kotlinx.coroutines.*


class CoroutineTimer(
    private val listener: CoroutineTimerListener,
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
) {

    private val scope: CoroutineScope by lazy { CoroutineScope(Job() + dispatcher) }
    var state: CurrentTimerState = CurrentTimerState.STOPPED
        private set(value) {
            if (field == CurrentTimerState.DESTROYED) {
                return
            }
            field = value
        }

    fun startTimer(countDownTime: Long, delayMillis: Long = 1000) {
        when (state) {
            CurrentTimerState.RUNNING -> {
                listener.onTick(null, TimerException(TimerErrorTypes.ALREADY_RUNNING))
            }
            CurrentTimerState.PAUSED -> {
                listener.onTick(null, TimerException(TimerErrorTypes.CURRENTLY_PAUSED))
            }
            CurrentTimerState.DESTROYED -> {
                listener.onTick(null, TimerException(TimerErrorTypes.DESTROYED))
            }
            else -> {
                timerCanStart(countDownTime, delayMillis)
            }
        }
    }

    fun destroyTimer() {
        scope.cancel("Timer was now destroyed. Need a new instance to work")
        listener.onDestroy()
        state = CurrentTimerState.DESTROYED
    }

    private fun timerCanStart(countDownTime: Long, delayMillis: Long = 1000) {
        scope.launch {
            state = CurrentTimerState.RUNNING
            var timeLeft = countDownTime

            timerLoop@ while (true) {
                when {
                    timeLeft < 1 -> {
                        state = CurrentTimerState.STOPPED
                        listener.onStop()
                        break@timerLoop
                    }
                    timeLeft > 0 && state == CurrentTimerState.RUNNING -> {
                        listener.onTick(timeLeft)
                        delay(delayMillis)
                        timeLeft -= 1
                    }
                    state == CurrentTimerState.PAUSED -> {
                        listener.onPause(timeLeft)
                    }
                    state == CurrentTimerState.STOPPED -> {
                        listener.onStop()
                        break@timerLoop
                    }
                }
            }
        }
    }
}

