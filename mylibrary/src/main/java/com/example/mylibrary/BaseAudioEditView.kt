package com.example.mylibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class BaseAudioEditView: View {
    private lateinit var timeScalePaint: Paint
    private val tag = "BaseAudioEditView"
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init {
        init()
    }
    private fun init() {
        Log.d(tag, "init(): ")
        //paint
        timeScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
            strokeWidth = 10f
            color = Color.BLUE
        }
    }



    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawCircle(0f, 0f, 300f, timeScalePaint)
        canvas.restore()
    }

}