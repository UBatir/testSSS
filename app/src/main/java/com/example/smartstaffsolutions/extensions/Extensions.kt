package com.example.smartstaffsolutions.extensions

import android.view.View


inline fun <T : View> T.onClick(crossinline func: T.() -> Unit) = setOnClickListener { func() }

