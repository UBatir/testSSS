package com.example.smartstaffsolutions.settings

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context:Context) {

    companion object{
        const val SIZE="size"
    }

    private val preferences:SharedPreferences=context.getSharedPreferences("Test",Context.MODE_PRIVATE)

    var size:Int
    set(value) = preferences.edit().putInt(SIZE,value).apply()
    get() = preferences.getInt(SIZE,25)
}