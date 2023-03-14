package com.example.mylibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class BaseAudioEditView: View {
    //两边多余的位置大小
    private var sideSize: Int = 0
    private lateinit var dbPaint: Paint
    private lateinit var flagBarPaint: Paint
    private var duration: Float = 0f
    private var currentY: Float = 0f
    private var currentX: Float = 0f
    //满屏时的时间刻度尺个数
    private var scaleInView: Int = 0

    //使用中间点来做偏移计算
    private var originYIncrement: Float = 0f
    private var originXIncrement: Float = 0f

    //最大的比例尺
    private var maxScale: Int = 1
    //放大系数 越大表示比例尺越小，最小是1s
    private var scale: Int = 1
    //
    private var timePointInView: Int = 1
    //两个时间刻度尺之间的间隔
    private var timePointInterval: Int = dp2px(30f)

    private lateinit var timeScalePaint: Paint

    private val testData: ArrayList<Double> = arrayListOf()

    private val TAG = "BaseAudioEditView"
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init {
        init()
    }
    private fun init() {
        test()
        Log.d(TAG, "init()")
        //paint
        timeScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
            strokeWidth = dp2px(2f).toFloat()
            color = Color.BLUE
            textSize = 16f
        }

        flagBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = Color.RED
                textSize = 16f
            }

        dbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = Color.GRAY
                textSize = 10f
            }
    }

    //test 数据
    private fun test() {
        duration = 302.5f
        testData.clear()
        for (i in 0 ..(duration*10).toInt()){
            testData.add(Math.random())
        }
    }

    fun changeScale(s: Int): Int{
        var ss = 1
        for (i in 0 until s){
            ss *= 2
        }
        scale = if (ss > maxScale){
            maxScale
        }else{
            ss
        }
        //居中偏移修改
        val preMaxLen = timePointInView * timePointInterval

        timePointInView = scale * scaleInView+1

        var maxLen = timePointInView * timePointInterval
        originXIncrement = (originXIncrement/preMaxLen)*maxLen
        //适配缩放
        maxLen = timePointInView * timePointInterval - width/2
        if (originXIncrement < width/2) {
            originXIncrement = width/2f
        } else if (originXIncrement > maxLen){
            originXIncrement = maxLen.toFloat()
        }
        postInvalidate()

        return scale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        originXIncrement = measuredWidth/2f

        scaleInView = measuredWidth/timePointInterval-1
        sideSize = measuredWidth%timePointInterval + measuredWidth/timePointInterval

        maxScale = (duration/scaleInView).toInt()+1
        //把maxScale变为2的次幂函数值
        var expA = 1
        for (i in 0..12){
            expA *= 2
            if (maxScale>expA){
                //continue
            }else{
                maxScale = expA
                break
            }
        }
        timePointInView = scale * scaleInView+1
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                currentX = event.x
                currentY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val flx = event.x - currentX
                val fly = event.y - currentY
                currentX = event.x
                currentY = event.y
                val maxLen = timePointInView * timePointInterval - width/2
                if (originXIncrement >= width/2 && originXIncrement <= maxLen){
                    originXIncrement -= flx
                    if (originXIncrement < width/2) {
                        originXIncrement = width/2f
                    } else if (originXIncrement > maxLen){
                        originXIncrement = maxLen.toFloat()
                    }
                    invalidate()
                }

            }
            MotionEvent.ACTION_UP -> {

            }
            else -> {}
        }
        //return super.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        //bg
        canvas.drawColor(Color.parseColor("#66FFFF00"))
        drawScaleTab(canvas)
        drawDB(canvas)
        drawFlagBar(canvas)
        canvas.restore()
    }

    private fun drawFlagBar(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), flagBarPaint)
        canvas.restore()
    }

    private fun drawDB(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        val c = 10
        val h = 300
        val interval = timePointInterval / c
        var ss = 0
        loop@for (i in 1 until timePointInView-1){
            val a = i*timePointInterval - originXIncrement
            if (a >-width/2-timePointInterval && a < width/2+timePointInterval){
                if (ss==0){
                    //init
                    ss = maxScale/scale*10*(i-1)
                }
                for (j in 0 until c){
                    val aa = a + interval * j
                    if (ss >= testData.size){
                        break@loop
                    }
                    val hh = h * testData[ss].toFloat()
                    canvas.drawLine(aa, (height-hh)/2f, aa, (height+hh)/2f, dbPaint)
                    ss += maxScale/scale*(10/c)
                }
            }

        }
        canvas.restore()
    }

    private fun drawScaleTab(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        for (i in 1 until  timePointInView){
            val a = i*timePointInterval - originXIncrement
            if (a >-width/2-timePointInterval && a < width/2+timePointInterval){
                canvas.drawText("${(i-1)*maxScale/scale}", a, 50f, timeScalePaint)
                canvas.drawLine(a, 100f, a, 200f, timeScalePaint)
            }
        }
        canvas.restore()
    }

}