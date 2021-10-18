package com.example.smartstaffsolutions.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.smartstaffsolutions.databinding.DialogSizeBinding
import com.example.smartstaffsolutions.extensions.onClick
import com.example.smartstaffsolutions.settings.SharedPreference

class SizeDialog(context: Context, private var size: Int) : Dialog(context) {
    init {
        show()
    }

    private val setting = SharedPreference(context)
    private lateinit var bind: DialogSizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DialogSizeBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.apply {
            etPixel.setText(size.toString())
            btnOk.onClick {
                setting.size = etPixel.text.toString().toInt()
                dismiss()
            }
            btnCancel.onClick { dismiss() }
        }

    }
}