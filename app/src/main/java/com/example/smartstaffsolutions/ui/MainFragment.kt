package com.example.smartstaffsolutions.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.smartstaffsolutions.DrawView
import com.example.smartstaffsolutions.R
import com.example.smartstaffsolutions.databinding.FragmentMainBinding
import com.example.smartstaffsolutions.extensions.onClick
import com.example.smartstaffsolutions.settings.SharedPreference
import android.view.ViewTreeObserver.OnGlobalLayoutListener as OnGlobalLayoutListener1

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val bind get() = _binding!!
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var orientation: Int = 0
    private lateinit var setting: SharedPreference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orientation = resources.configuration.orientation
        setting = SharedPreference(requireContext())
        val algo = arrayListOf("Algorithm 1", "Algorithm 2", "Algorithm 3")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, algo
        )
        bind.apply {
            image1.apply {
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?,
                        position: Int, id: Long,
                    ) {
                        if (position >= 0) drawView.algoType = position + 1
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                drawView.viewTreeObserver()
            }
            image2.apply {
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?,
                        position: Int, id: Long,
                    ) {
                        if (position >= 0) drawView.algoType = position + 1
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                drawView.viewTreeObserver()
            }

            slider.addOnChangeListener { _, value, _ ->
                if (value != 0F) {
                    image1.drawView.speed = 500 / value.toLong()
                    image2.drawView.speed = 500 / value.toLong()
                }
            }
            btnGenerate.onClick {
                bind.apply {
                    image1.drawView.apply {
                        width = mWidth
                        height = mHeight
                        generate()
                    }
                    image2.drawView.apply {
                        width = mWidth
                        height = mHeight
                        generate()
                    }
                }
            }
        }
    }

    fun DrawView.viewTreeObserver() {
        this.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener1 {
            override fun onGlobalLayout() {
                this@viewTreeObserver.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mWidth = this@viewTreeObserver.measuredWidth
                mHeight = this@viewTreeObserver.measuredHeight
                bind.apply {
                    image1.drawView.apply {
                        width = mWidth
                        height = mHeight
                    }
                    image2.drawView.apply {
                        width = mWidth
                        height = mHeight
                    }
                }
                bind.apply {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        etPixel?.setText(pixelSize.toString())
                        etPixel?.doOnTextChanged { text, _, _, _ ->
                            if (!text.isNullOrEmpty()) setting.size = text.toString().toInt()
                        }
                    }
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        btnSize?.onClick {
                            SizeDialog(requireContext(), pixelSize)
                        }
                    }
                }
                this@viewTreeObserver.init(mHeight, mWidth, pixelSize)
            }
        })
    }
}