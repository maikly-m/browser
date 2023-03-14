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
    private var duration: Float = 0f
    private var currentY: Float = 0f
    private var currentX: Float = 0f
    private val scaleInView: Int = 30

    //使用中间点来做偏移计算
    private var originYIncrement: Float = 0f
    private var originXIncrement: Float = 0f

    private var maxScale: Int = 1
    private var scale: Int = 1
    private var timePointInView: Int = 1
    private var timePointInterval: Int = 100

    private lateinit var timeScalePaint: Paint


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
            strokeWidth = 10f
            color = Color.BLUE
            textSize = 16f
        }
    }

    private fun test() {
        duration = 302.5f
        maxScale = (duration/scaleInView).toInt()+1
        timePointInView = scale * scaleInView
    }

    fun changeScale(s: Int){
        val preScale = scale
        scale = if (s>0){
            if (s > maxScale){
                maxScale
            }else{
                s
            }
        }else{
            1
        }
        //居中偏移修改
        val preMaxLen = timePointInView * timePointInterval

        timePointInView = scale * scaleInView

        val maxLen = timePointInView * timePointInterval
        originXIncrement = (originXIncrement/preMaxLen)*maxLen

        postInvalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        originXIncrement = measuredWidth/2f
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
        canvas.restore()
    }

    fun addData(duration:Float){

    }

    private fun drawScaleTab(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        for (i in 0 .. timePointInView+1){
            val a = i*timePointInterval - originXIncrement
            if (a >-width/2 && a < width/2){
                canvas.drawText("${i*maxScale/scale}", a, 50f, timeScalePaint)
                canvas.drawLine(a, 100f, a, 200f, timeScalePaint)
            }
        }
        canvas.restore()
    }

}