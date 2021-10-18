package com.example.smartstaffsolutions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.smartstaffsolutions.model.Stroke
import com.example.smartstaffsolutions.settings.SharedPreference
import com.example.smartstaffsolutions.timer.CoroutineTimer
import com.example.smartstaffsolutions.timer.CoroutineTimerListener
import java.util.*
import kotlin.random.Random.Default.nextInt

class DrawView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var mX = 0f
    private var mY = 0f
    private val settings = SharedPreference(context)
    private var mPath: Path? = null
    private val mPaint: Paint = Paint()
    private val paths = ArrayList<Stroke>()
    private var currentColor = 0
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var timer: CoroutineTimer? = null
    var width: Int? = null
    var height: Int? = null
    var speed = 1000L
    var algoType = 1
    private var myPaths = arrayListOf<Pair<Int, Int>>()
    var pixelSize = settings.size

    init {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.SQUARE
        mPaint.alpha = 0xff
    }

    fun init(height: Int, width: Int, pixelSize: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
        currentColor = Color.BLACK
        this.pixelSize = pixelSize
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCanvas!!.drawColor(Color.WHITE)
        try {
            for (fp in paths) {
                mPaint.color = fp.color
                mPaint.strokeWidth = fp.strokeWidth.toFloat()
                mCanvas!!.drawPath(fp.path, mPaint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        canvas.drawBitmap(mBitmap!!, 1.8f, 1.8f, mBitmapPaint)
        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x + 15
        val y = event.y + 15
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPaths.clear()
                val p = Pair((x / pixelSize).toInt(), (y / pixelSize).toInt())
                when (algoType) {
                    1 -> algorithm1(p)
                    2 -> algorithm2(p)
                    3 -> algorithm3(p)
                }
                startDraw()
            }
        }
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = Stroke(currentColor, pixelSize, mPath!!)
        paths.add(fp)
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
        invalidate()
    }

    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
        invalidate()
    }

    private fun pick(): List<Pair<Int, Int>> {
        val a = width!! / pixelSize
        val b = height!! / pixelSize
        val list = arrayListOf<Pair<Int, Int>>()
        var i = a * b * 2 / 5
        if (a > 1 && b > 1)
            while (i > 0) {
                val x = nextInt(1, a)
                val y = nextInt(1, b)
                val p = Pair(x, y)
                if (!myPaths.contains(p) && !list.contains(p)) {
                    list.add(p)
                    i--
                }
            }

        return list
    }

    private fun startDraw() {
        if (timer != null) timer!!.destroyTimer()
        val algorithm = touchPaths
        val size = algorithm.size
        currentColor = Color.BLUE
        timer = CoroutineTimer(object : CoroutineTimerListener {
            override fun onTick(timeLeft: Long?, error: Exception?) {
                val i = timeLeft!!.toInt()
                val x = algorithm[size - i].first * pixelSize
                val y = algorithm[size - i].second * pixelSize
                touchStart(x * 1.0f, y * 1.0f)
                touchUp()
            }
        })
        timer!!.startTimer(size.toLong(), speed)
    }

    fun generate() {
        pixelSize = settings.size
        currentColor = Color.BLACK
        paths.clear()
        myPaths.clear()
        if (timer != null) timer!!.destroyTimer()
        val generate = pick()
        val size = generate.size
        val random = when (size) {
            in 1..100 -> (10..100).random()
            in 100..200 -> (100..200).random()
            in 200..300 -> (200..300).random()
            in 300..400 -> (300..400).random()
            in 400..700 -> (450..700).random()
            else -> (700..1000).random()
        }
        for (i in 1 until random) {
            val x = generate[size - (1 until size).random()].first
            val y = generate[size - (1 until size).random()].second
            myPaths.add(Pair(x, y))
            touchStart(x * pixelSize * 1.0f, y * pixelSize * 1.0f)
            touchUp()
        }
    }

    private val touchPaths = mutableListOf<Pair<Int, Int>>()
    private fun algorithm1(pair: Pair<Int, Int>) {
        val t1 = pair.first > 0 && pair.first < width!! / pixelSize
        val t2 = pair.second > 0 && pair.second < height!! / pixelSize
        if (t1 && t2 && !touchPaths.contains(pair) && !myPaths.contains(pair)) {
            touchPaths.add(pair)
            algorithm1(Pair(pair.first + 1, pair.second))
            algorithm1(Pair(pair.first, pair.second + 1))
            algorithm1(Pair(pair.first - 1, pair.second))
            algorithm1(Pair(pair.first, pair.second - 1))
        } else return
    }

    private fun algorithm2(p: Pair<Int, Int>) {
        val t1 = p.first > 0 && p.first < width!! / pixelSize
        val t2 = p.second > 0 && p.second < height!! / pixelSize
        if (t1 && t2 && !touchPaths.contains(p) && !myPaths.contains(p)) {
            touchPaths.add(p)
            algorithm2(Pair(p.first, p.second + 1))
            algorithm2(Pair(p.first + 1, p.second))
            algorithm2(Pair(p.first, p.second - 1))
            algorithm2(Pair(p.first - 1, p.second))
        } else return
    }


    private fun algorithm3(p: Pair<Int, Int>) {
        val t1 = p.first > 0 && p.first < width!! / pixelSize
        val t2 = p.second > 0 && p.second < height!! / pixelSize
        if (t1 && t2 && !touchPaths.contains(p) && !myPaths.contains(p)) {
            touchPaths.add(p)
            algorithm3(Pair(p.first + 1, p.second))
            algorithm3(Pair(p.first, p.second - 1))
            algorithm3(Pair(p.first - 1, p.second))
            algorithm3(Pair(p.first, p.second + 1))
        } else return
    }
}
