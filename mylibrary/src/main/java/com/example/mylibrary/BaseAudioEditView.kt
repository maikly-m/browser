package com.example.mylibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class BaseAudioEditView: View {
    val DRAG_LEFT: String = "left"
    val DRAG_RIGHT: String = "right"
    private var isDragHit: String = ""
    private val particlePerSecond = 10
    private val splitHashMap: HashMap<String, DragBean> = hashMapOf()
    private var halfScope: Int = 0
    private var lastScrollX: Int = -1
    private var lastScrollY: Int = -1
    private var moveX: Float = 0f
    private var moveY: Float = 0f
    private lateinit var primaryLastPoint: PointF
    private lateinit var primaryDownPoint: PointF
    private var isFling: Boolean = false
    private var primaryPointID: Int = 0
    private lateinit var scroller: Scroller
    private val velocityTracker = VelocityTracker.obtain()

    //左边多余的位置大小
    private var leftSideSize: Int = 2
    private lateinit var dbPaint: Paint
    private lateinit var flagBarPaint: Paint
    //0.1s
    private val timeSpaceUnit: Int = 10
    //时长 秒
    private var duration: Float = 0f
    //满屏时的时间刻度尺个数
    private var scaleInView: Int = 0

    //使用中间点来做偏移计算
    private var originYIncrement: Float = 0f
    private var originXIncrement: Float = 0f

    //最大的比例尺
    private var maxScale: Int = 1
    //放大系数 越大表示比例尺越小，最小是1s
    private var scale: Int = 1
    //总的有多少个时间刻度要绘制
    private var timePointInView: Int = 1
    //两个时间刻度尺之间的间隔
    private var timePointInterval: Int = dp2px(20f)

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
        scroller = Scroller(context)
        //paint
        timeScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
            strokeWidth = dp2px(2f).toFloat()
            color = Color.BLUE
            textSize = dp2px(10f).toFloat()
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
        duration = 662.5f
        testData.clear()
        for (i in 0 ..(duration*timeSpaceUnit).toInt()){
            testData.add(Math.random())
        }
        splitHashMap.clear()
        splitHashMap[DRAG_LEFT] = DragBean((duration*timeSpaceUnit/3).toInt(), null)
        splitHashMap[DRAG_RIGHT] = DragBean((duration*timeSpaceUnit*2/3).toInt(), null)
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
        //总个数
        timePointInView = scale * scaleInView+1
        (duration.toInt()*(scale.toFloat()/maxScale)+1).toInt().let {
            if (timePointInView > it){
                timePointInView = it
            }
            (scaleInView+1).run {
                if (timePointInView < this){
                    timePointInView = this
                }
            }

        }

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

        scaleInView = measuredWidth/timePointInterval
        //sideSize = measuredWidth%timePointInterval + measuredWidth/timePointInterval

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
        (duration.toInt()*(scale.toFloat()/maxScale)+1).toInt().let {
            if (timePointInView > it){
                timePointInView = it
            }
            (scaleInView+1).run {
                if (timePointInView < this){
                    timePointInView = this
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                velocityTracker.clear()
            }
            else ->{

            }
        }
        velocityTracker.addMovement(event)
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                primaryPointID = event.getPointerId(event.actionIndex)
                primaryDownPoint = PointF(event.getX(event.actionIndex), event.getY(event.actionIndex))
                primaryLastPoint = primaryDownPoint
                moveX = 0f
                moveY = 0f
                isFling = false
                isDrag()
            }
            MotionEvent.ACTION_MOVE -> {
                val downPoint = PointF(event.getX(event.actionIndex), event.getY(event.actionIndex))
                val flx = downPoint.x - primaryLastPoint.x
                val fly = downPoint.y - primaryLastPoint.y
                if (isDragHit != ""){
                    moveDrag(flx, fly)
                }else{
                    val moveScreen = moveScreen(flx)
                    moveX += abs(flx)
                    moveY += abs(fly)
                    if (moveX >= 20 || moveY >= 20) {
                        isFling = true && !moveScreen
                    }
                }
                primaryLastPoint = downPoint
            }
            MotionEvent.ACTION_UP -> {
                //Log.d(TAG, "onTouchEvent: isFling=$isFling")
                if (isFling){
                    velocityTracker.computeCurrentVelocity(1000)
                    val velocityX: Float = velocityTracker.getXVelocity(primaryPointID)
                    val velocityY: Float = velocityTracker.getYVelocity(primaryPointID)

                    lastScrollY = -1
                    lastScrollX = -1
                    scroller.fling(
                        0,
                        0,
                        velocityX.toInt(),
                        velocityY.toInt(),
                        Int.MIN_VALUE,
                        Int.MAX_VALUE,
                        Int.MIN_VALUE,
                        Int.MAX_VALUE
                    )
                    invalidate()
                }
            }
            else -> {}
        }
        //return super.onTouchEvent(event)
        return true
    }

    private fun moveDrag(flx: Float, fly: Float) {
        splitHashMap[isDragHit]?.let {
            if (isDragHit==DRAG_LEFT){
                it.dragPoint?.run {
                    //修改时间
                    val c = (maxScale/scale*particlePerSecond)
                    //val a = (it.duration.toFloat()/c + 1)*timePointInterval - originXIncrement
                    (((x+dp2px(10f)+originXIncrement-width/2+flx)/timePointInterval-leftSideSize)*c).let {d->
                        var maxDuration = duration*timeSpaceUnit
                        splitHashMap[DRAG_RIGHT]?.let {db->
                            maxDuration = db.duration.toFloat()
                        }
                        if (d < 0){
                            it.duration = 0
                        }else if (d > maxDuration) {
                            it.duration = maxDuration.toInt()
                        }else{
                            it.duration = d.toInt()
                        }
                    }
                }
                invalidate()
            }else if (isDragHit==DRAG_RIGHT) {
                it.dragPoint?.run {
                    val c = (maxScale/scale*particlePerSecond)
                    //val a = (it.duration.toFloat()/c + 1)*timePointInterval - originXIncrement
                    (((x-dp2px(10f)+originXIncrement-width/2+flx)/timePointInterval-leftSideSize)*c).let {d->
                        var minDuration = 0f
                        val maxDuration = duration*timeSpaceUnit
                        splitHashMap[DRAG_LEFT]?.let { db->
                            minDuration = db.duration.toFloat()
                        }
                        if (d > maxDuration){
                            it.duration = maxDuration.toInt()
                        }else if (d < minDuration) {
                            it.duration = minDuration.toInt()
                        }else{
                            it.duration = d.toInt()
                        }
                    }
                }
                invalidate()
            }
        }
    }

    private fun isDrag() {
        isDragHit = ""
        splitHashMap[DRAG_LEFT]?.let {
            it.dragPoint?.let {p ->
                ((primaryDownPoint.x-p.x)*(primaryDownPoint.x-p.x)
                    +(primaryDownPoint.y-p.y)*(primaryDownPoint.y-p.y)).let {f ->
                    if (sqrt(f.toDouble()) < it.radius*2){
                        //hit
                        isDragHit = DRAG_LEFT
                        it.drag = true
                    }else{
                        isDragHit = ""
                        it.drag = false
                    }
                }
            }
        }
        if (isDragHit == ""){
            splitHashMap[DRAG_RIGHT]?.let {
                it.dragPoint?.let {p ->
                    ((primaryDownPoint.x-p.x)*(primaryDownPoint.x-p.x)
                            +(primaryDownPoint.y-p.y)*(primaryDownPoint.y-p.y)).let {f ->
                        if (sqrt(f.toDouble()) < it.radius*2){
                            //hit
                            isDragHit = DRAG_RIGHT
                            it.drag = true
                        }else{
                            isDragHit = ""
                            it.drag = false
                        }
                    }
                }
            }
        }

    }

    private fun moveScreen(flx: Float):Boolean {
        val maxLen = timePointInView * timePointInterval - width / 2
        var stop = true
        if (originXIncrement >= width / 2 && originXIncrement <= maxLen) {
            originXIncrement -= flx
            if (originXIncrement < width / 2) {
                originXIncrement = width / 2f
            } else if (originXIncrement > maxLen) {
                originXIncrement = maxLen.toFloat()
            }
            if (originXIncrement > width / 2 && originXIncrement < maxLen){
                //Log.d(TAG, "moveScreen: flx=$flx")
                stop = false
                invalidate()
            }
        }
        return stop
    }

    override fun computeScroll() {

        if (scroller.computeScrollOffset()) {
            //Log.d(TAG, "computeScroll: ")
            val x = scroller.currX
            val y = scroller.currY
            if (lastScrollX == -1 && lastScrollY == -1) {
                lastScrollX = x
                lastScrollY = y
                invalidate()
                if (scroller.isFinished) {
                    //动画处理
                }
                return
            }
            val offsetX: Int = x - lastScrollX
            val offsetY: Int = y - lastScrollY
            //Log.d(TAG, "computeScroll: x=$x")
            //Log.d(TAG, "computeScroll: offsetX=$offsetX")
            if (moveScreen(offsetX.toFloat())) {
                scroller.forceFinished(true)
                lastScrollX = 0
                lastScrollY = 0
                return
            }
            lastScrollX = x
            lastScrollY = y
            if (scroller.isFinished) {
                lastScrollX = 0
                lastScrollY = 0
                //动画处理
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        halfScope = width/2+timePointInterval*2
        canvas.save()
        //bg
        canvas.drawColor(Color.parseColor("#66FFFF00"))
        drawScaleTab(canvas)
        drawDB(canvas)
        drawFlagBar(canvas)
        drawDragArea(canvas)
        canvas.restore()
    }

    private fun drawDragArea(canvas: Canvas) {
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        //绘制left split
        splitHashMap[DRAG_LEFT]?.let {
            val c = (maxScale/scale*particlePerSecond)
            val a = (it.duration.toFloat()/c + leftSideSize)*timePointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                dbPaint.color = Color.BLACK
                canvas.drawLine(a, 0f, a, height.toFloat(), dbPaint)
                Path().run {
                    moveTo(a, height.toFloat()/2-dp2px(10f))
                    lineTo(a-dp2px(20f), height.toFloat()/2-dp2px(10f))
                    lineTo(a-dp2px(20f), height.toFloat()/2+dp2px(10f))
                    lineTo(a, height.toFloat()/2+dp2px(10f))
                    close()
                    it.dragPoint = PointF(a-dp2px(10f)+width/2f,height.toFloat()/2)
                    it.radius = dp2px(10f)
                    canvas.drawPath(this, dbPaint)
                }

            }
        }
        splitHashMap[DRAG_RIGHT]?.let {
            val c = (maxScale/scale*particlePerSecond)
            val a = (it.duration.toFloat()/c + leftSideSize)*timePointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                dbPaint.color = Color.GREEN
                canvas.drawLine(a, 0f, a, height.toFloat(), dbPaint)
                Path().run {
                    moveTo(a, height.toFloat()/2-dp2px(10f))
                    lineTo(a+dp2px(20f), height.toFloat()/2-dp2px(10f))
                    lineTo(a+dp2px(20f), height.toFloat()/2+dp2px(10f))
                    lineTo(a, height.toFloat()/2+dp2px(10f))
                    close()
                    it.dragPoint = PointF(a+dp2px(10f)+width/2f,height.toFloat()/2)
                    it.radius = dp2px(10f)
                    canvas.drawPath(this, dbPaint)
                }
            }
        }
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
        val countPerSecond = 10
        val countPerInterval = 10
        val h = 300
        val interval = timePointInterval / countPerInterval
        var ss = 0
        dbPaint.color = Color.GRAY
        loop@for (i in leftSideSize until timePointInView){
            val a = i*timePointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                if (ss==0){
                    //init
                    ss = maxScale/scale*countPerSecond*(i-1)
                }
                for (j in 0 until countPerInterval){
                    val aa = a + interval * j
                    if (ss >= testData.size){
                        break@loop
                    }
                    val hh = h * testData[ss].toFloat()
                    canvas.drawLine(aa, (height-hh)/2f, aa, (height+hh)/2f, dbPaint)
                    ss += maxScale/scale*(countPerSecond/countPerInterval)
                }
            }

        }
        canvas.restore()
    }

    private fun drawScaleTab(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        for (i in leftSideSize until   timePointInView){
            val a = i*timePointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //间隔5个画一次时间
                (i-leftSideSize).let {
                    if(it%5==0){
                        drawText(canvas, timeScalePaint, Point(a.toInt(), 50), formatTime(it*maxScale/scale*1000L))
                    }
                }
                canvas.drawLine(a, 100f, a, 200f, timeScalePaint)
            }
        }
        canvas.restore()
    }

    private fun drawText(canvas: Canvas, paint: Paint, point: Point, text: String){
        //计算baseline
        paint.fontMetrics.run {
            val distance = (bottom - top) / 2 - bottom
            paint.measureText(text).let {
                canvas.drawText(text, point.x-it/2, point.y + distance, paint)
            }
        }
    }

}