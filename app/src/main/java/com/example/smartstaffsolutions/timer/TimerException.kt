package com.example.smartstaffsolutions.timer

class TimerException(type: TimerErrorTypes) : Exception(type.message)