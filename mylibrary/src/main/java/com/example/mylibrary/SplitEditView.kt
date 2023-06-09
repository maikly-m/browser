package com.example.mylibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.sqrt

class SplitEditView: View {

    private var rightLimitColor: Int = 0
    private var leftLimitColor: Int = 0
    private var bgDefaultColor: Int = 0
    private var flagBarPaintColor: Int = 0
    private var timeScalePaintColor: Int = 0
    private var dbSelectColor: Int = 0
    private var dbColor: Int = 0
    val DRAG_LEFT: String = "left"
    val DRAG_RIGHT: String = "right"
    //当前位置，单位0.01s
    val DRAG_POSITION: String = "position"
    private var isDragHit: String = ""
    private val dragHashMap: HashMap<String, DragBean> = hashMapOf()
    private var halfScope: Float = 0f
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


    private lateinit var dbPaint: Paint
    private lateinit var flagBarPaint: Paint
    //一个刻度尺有几个db
    private val particlePerUnit = 10
    //两边多余的位置大小
    private var sideSize: Int = 2*particlePerUnit
    //时长 秒
    private var duration: Double = 0.0
    //秒换算为最小db的绘制单位
    private val showUnit = 100
    //时长 0.01秒
    private var mDuration: Double = 0.0
    //满屏时的db个数
    private var dbCountInView: Int = 0

    //使用中间点来做偏移计算
    private var originYIncrement: Float = 0f
    private var originXIncrement: Float = 0f

    //最大的比例尺
    private var maxScale: Int = 1
    //放大系数 越大表示比例尺越大，最小是0.01s,即系数为1
    private var scale: Int = 1
    //zoom
    private var zoomScale = 1
    //总的有多少个db要绘制
    private var dbPointInView: Int = 1
    //两个db之间的间隔
    private var dbPointInterval: Float = 0f
    //刻度尺个数
    private var scaleCount: Int = 20

    private lateinit var timeScalePaint: Paint

    private val testData: ArrayList<Double> = arrayListOf()

    private val TAG = "SplitEditView"
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
        dbColor = resources.getColor(R.color.db_default,null)
        dbSelectColor = resources.getColor(R.color.db_select,null)
        timeScalePaintColor = resources.getColor(R.color.time_scale_paint,null)
        flagBarPaintColor = resources.getColor(R.color.flag_bar_paint,null)
        bgDefaultColor = resources.getColor(R.color.bg_default,null)
        leftLimitColor = resources.getColor(R.color.left_limit,null)
        rightLimitColor = resources.getColor(R.color.right_limit,null)
        //paint
        timeScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
            strokeWidth = dp2px(2f).toFloat()
            color = timeScalePaintColor
            textSize = dp2px(10f).toFloat()
        }

        flagBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = flagBarPaintColor
                textSize = 16f
            }

        dbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = dbColor
                textSize = 10f
            }
    }

    //test 数据
    private fun test() {
        duration = 662.5
        mDuration = duration*showUnit
        testData.clear()
        for (i in 0 ..(mDuration).toInt()){
            testData.add(Math.random())
        }
        dragHashMap.clear()
        dragHashMap[DRAG_LEFT] = DragBean(testData.size/3, null)
        dragHashMap[DRAG_RIGHT] = DragBean(testData.size*2/3, null)
        dragHashMap[DRAG_POSITION] = DragBean(testData.size/2, null)
    }


    fun zoomIn():Int{
        zoomScale *= 2
        if (zoomScale > maxScale){
            zoomScale = maxScale
        }
        return changeScale()
    }

    fun zoomOut():Int{
        if (zoomScale == maxScale){
            var a = 1
            while (true){
                if (a > zoomScale){
                    break
                }
                a *= 2
            }
            zoomScale = a/2
        }else{
            zoomScale /= 2
        }
        if (zoomScale < 1){
            zoomScale = 1
        }
        return changeScale()
    }

    private fun changeScale(): Int{
        scale = if (zoomScale > maxScale){
            maxScale
        }else if (zoomScale < 1) {
            1
        }else{
            zoomScale
        }
        //居中偏移修改
        val preMaxLen = dbPointInView * dbPointInterval
        //总个数
        dbPointInView = if (scale==maxScale) {
            dbCountInView-2*sideSize
        } else {
            if (mDuration%scale==0.0) {
                (mDuration/scale).toInt()
            } else {
                (mDuration/scale).toInt()+1
            }
        }
        var maxLen = dbPointInView * dbPointInterval
        originXIncrement *= maxLen / preMaxLen
        //两边空余的大小
        val localSideSize = 2*sideSize
        //适配缩放
        maxLen = (dbPointInView+localSideSize) * dbPointInterval - width/2
        if (originXIncrement <= width/2) {
            originXIncrement = width/2f
        } else if (originXIncrement > maxLen){
            originXIncrement = maxLen
        }
        postInvalidate()

        return scale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //宽高比值4:3, 以宽度为准
        setMeasuredDimension(widthMeasureSpec, measuredWidth*3/4)
        originXIncrement = measuredWidth/2f
        dbPointInterval = measuredWidth.toFloat()/(scaleCount*particlePerUnit)
        dbCountInView = scaleCount*particlePerUnit

        //计算铺满屏幕时刻度尺之间的时间值，要除去两边占位，各占两格刻度,即最大比例
        //时间单位 0.1s
        val distance: Int = (mDuration/(dbCountInView-2*sideSize)).run {
            if (this==0.0){
                //时长太短
                1
            }else{
                this.toInt()
            }
        }
        maxScale = distance
        scale = maxScale
        zoomScale = maxScale
        //最开始用最大比例
        dbPointInView = dbCountInView-2*sideSize
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
                stopFling()
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
                }else{
                }
            }
            else -> {}
        }
        //return super.onTouchEvent(event)
        return true
    }

    private fun stopFling() {
        scroller.forceFinished(true)
    }

    private fun moveDrag(flx: Float, fly: Float) {
        dragHashMap[isDragHit]?.let {
            if (isDragHit==DRAG_LEFT){
                it.dragPoint?.run {
                    //修改时间
                    // val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
                    (((x+dp2px(10f)+originXIncrement-width/2+flx)/dbPointInterval-sideSize)*scale).let { d->
                        var maxDuration = mDuration
                        dragHashMap[DRAG_RIGHT]?.let { db->
                            maxDuration = db.duration.toDouble()
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
                    // val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
                    (((x-dp2px(10f)+originXIncrement-width/2+flx)/dbPointInterval-sideSize)*scale).let { d->
                        var minDuration = 0f
                        val maxDuration = mDuration
                        dragHashMap[DRAG_LEFT]?.let { db->
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
            }else if (isDragHit==DRAG_POSITION) {
                it.dragPoint?.run {
                    // val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
                    (((x+originXIncrement-width/2+flx)/dbPointInterval-sideSize)*scale).let { d->
                        val minDuration = 0f
                        val maxDuration = mDuration
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
        dragHashMap[DRAG_LEFT]?.let {
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
            dragHashMap[DRAG_RIGHT]?.let {
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
        if (isDragHit == ""){
            dragHashMap[DRAG_POSITION]?.let {
                it.dragPoint?.let {p ->
                    ((primaryDownPoint.x-p.x)*(primaryDownPoint.x-p.x)
                            +(primaryDownPoint.y-p.y)*(primaryDownPoint.y-p.y)).let {f ->
                        if (sqrt(f.toDouble()) < it.radius*2){
                            //hit
                            isDragHit = DRAG_POSITION
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
        var adjustDbPointInView = dbPointInView
        if (dbPointInView%10!=0){
            adjustDbPointInView = (dbPointInView/10+1)*10
        }
        val maxLen = (adjustDbPointInView+2*sideSize) * dbPointInterval - width / 2
        var stop = true
        if (originXIncrement >= width / 2 && originXIncrement <= maxLen) {
            originXIncrement -= flx
            if (originXIncrement < width / 2) {
                originXIncrement = width / 2f
            } else if (originXIncrement > maxLen) {
                originXIncrement = maxLen
            }
            if (originXIncrement >= width / 2 && originXIncrement <= maxLen){
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
        halfScope = width.toFloat()/2+sideSize
        canvas.save()
        //bg
        canvas.drawColor(bgDefaultColor)
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
        dragHashMap[DRAG_LEFT]?.let {
            val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                dbPaint.color = leftLimitColor
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
        //绘制right split
        dragHashMap[DRAG_RIGHT]?.let {
            val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                dbPaint.color = rightLimitColor
                canvas.drawLine(a, 0f, a, height.toFloat(), dbPaint)
                Path().run {
                    moveTo(a, height.toFloat()/2-dp2px(10f))
                    lineTo(a+dp2px(20f), height.toFloat()/2-dp2px(10f))
                    lineTo(a+dp2px(20f), height.toFloat()/2+dp2px(10f))
                    lineTo(a, height.toFloat()/2+dp2px(10f))
                    close()
                    //添加 width/2 便于碰撞计算
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
        dragHashMap[DRAG_POSITION]?.let {
            val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                canvas.drawLine(a, 0f, a, height.toFloat(), flagBarPaint)
                //添加 width/2 便于碰撞计算
                it.dragPoint = PointF(a+width/2f,height.toFloat()/2)
                it.radius = dp2px(10f)
            }
        }
        canvas.restore()
    }

    private fun drawDB(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        val h = 300
        dbPaint.color = Color.GRAY
        var leftLimit = 0f
        var rightLimit = 0f
        dragHashMap[DRAG_LEFT]?.let {
            leftLimit = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
        }
        dragHashMap[DRAG_RIGHT]?.let {
            rightLimit = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
        }
        //快速定位
        var start:Int = ((originXIncrement-halfScope)/dbPointInterval).toInt()
        if (start < sideSize){
            start = sideSize
        }
        var hadDraw = false
        for (i in start until dbPointInView+sideSize){
            val a = i*dbPointInterval - originXIncrement
            val index = (i-sideSize)*scale
            if (a >-halfScope && a < halfScope){
                hadDraw = true
                if (index >= testData.size){
                    Log.d(TAG, "drawDB: stop index=${index}")
                    break
                }
                val hh = h * testData[index].toFloat()
                if (a > leftLimit && a < rightLimit) {
                    dbPaint.color = dbSelectColor
                } else {
                    dbPaint.color = dbColor
                }
                canvas.drawLine(a, (height-hh)/2f, a, (height+hh)/2f, dbPaint)
            }else{
                if (hadDraw){
                    break
                }
            }
        }
        canvas.restore()
    }

    private fun drawScaleTab(canvas: Canvas){
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        //换算,只绘制到有db的最后一个刻度
        var adjustDbPointInView = dbPointInView
        if (dbPointInView%10!=0){
            adjustDbPointInView = (dbPointInView/10+1)*10
        }
        var lastX = 0f
        var preTime = ""
        var lastTime = ""
        //快速定位
        var start:Int = ((originXIncrement-halfScope)/dbPointInterval).toInt()
        if (start < sideSize){
            start = sideSize
        }
        var hadDraw = false
        for (i in start .. adjustDbPointInView+sideSize){
            val a = i*dbPointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                hadDraw = true
                if (i%10==0){
                    canvas.drawLine(a, 100f, a, 200f, timeScalePaint)
                    //间隔10*particlePerUnit个画一次时间
                    (i-sideSize).let {
                        if(it%(10*particlePerUnit)==0){
                            formatTime(it*scale*10L).run {
                                drawText(canvas, timeScalePaint, Point(a.toInt(), 50), this)
                                preTime = this
                                lastX = 0f
                            }
                        }else{
                            lastX = a
                            lastTime = formatTime(it*scale*10L)
                        }
                    }

                }
            }else{
                if (hadDraw){
                    //绘制最后一个时间刻度
                    if (i%10==0){
                        lastX = a
                        lastTime = formatTime((i-sideSize)*scale*10L)
                        break
                    }
                }

            }
        }
        if (lastX != 0f && preTime != lastTime){
            //绘制最后一个时间
            drawText(canvas, timeScalePaint, Point(lastX.toInt(), 50), lastTime)
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