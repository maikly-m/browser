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

class MergeEditView: View {

    private var preShiftRectHit: Int = -1
    private var shiftRectHit: Int = -1
    private var sufShiftRectHit: Int = -1

    // 上下或者左右 0 1
    private var moveDirection: Int = -1
    private var firstDbRect: Int = 0
    private var passByMove: Boolean = false
    private var selectIndex: Int = -1
    private lateinit var timeTouchRectF: RectF
    private var textBoundColor: Int = 0
    private var textColor: Int = 0
    private lateinit var textPaint: Paint
    private var startEndSignColor: Int = 0
    private lateinit var startEndSignPaint: Paint
    private var bgDefaultColor: Int = 0
    private var flagBarPaintColor: Int = 0
    private var timeScalePaintColor: Int = 0
    private var dbColor: Int = 0
    //当前位置，单位0.01s
    val DRAG_POSITION: String = "position"
    private var isDragHit: String = ""
    private var isShiftRectHit: String = ""
    private val dragHashMap: HashMap<String, DragBean> = hashMapOf()
    private val selectHashMap: HashMap<String, SelectBean> = hashMapOf()
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
    //时长 0.01秒
    private val allData: ArrayList<ArrayList<Double>> = arrayListOf()
    //文件索引,用于表示文件摆放的位置
    private val sortIndex: ArrayList<Int> = arrayListOf()

    private val TAG = "MergeEditView"
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
        timeScalePaintColor = resources.getColor(R.color.time_scale_paint,null)
        flagBarPaintColor = resources.getColor(R.color.flag_bar_paint,null)
        bgDefaultColor = resources.getColor(R.color.bg_default,null)
        startEndSignColor = resources.getColor(R.color.start_End_Sign,null)
        textColor = resources.getColor(R.color.text_,null)
        textBoundColor = resources.getColor(R.color.text_bound,null)
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
        startEndSignPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = startEndSignColor
                pathEffect = DashPathEffect(floatArrayOf(dp2px(2f).toFloat(), dp2px(6f).toFloat()), 0f)
            }

        dbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = dbColor
                textSize = 10f
            }
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                strokeWidth = dp2px(1f).toFloat()
                color = textColor
                textSize = dp2px(8f).toFloat()
            }
        timeTouchRectF = RectF(0f, 0f, dp2px(30f).toFloat(), dp2px(20f).toFloat())
    }

    //test 数据
    private fun test() {
        mDuration = 462.5*showUnit
        mDuration += 262.5*showUnit
        mDuration += 362.3*showUnit
        allData.clear()
        sortIndex.clear()
        allData.add(arrayListOf<Double>())
        allData.add(arrayListOf<Double>())
        allData.add(arrayListOf<Double>())
        for (i in 0 until (462.5*showUnit).toInt()){
            allData[0].add(Math.random())
        }
        sortIndex.add(0)
        for (i in 0 until(262.5*showUnit).toInt()){
            allData[1].add(Math.random())
        }
        sortIndex.add(1)
        for (i in 0 until(362.3*showUnit).toInt()){
            allData[2].add(Math.random())
        }
        sortIndex.add(2)
        dragHashMap.clear()
        dragHashMap["0"] = DragBean(0, null)
        dragHashMap["1"] = DragBean((462.5*showUnit).toInt(), null)
        dragHashMap["2"] = DragBean(((462.5+262.5)*showUnit).toInt(), null)
        dragHashMap[DRAG_POSITION] = DragBean((mDuration/2).toInt(), null)

        selectHashMap["0"] = SelectBean()
        selectHashMap["1"] = SelectBean()
        selectHashMap["2"] = SelectBean()
        selectIndex = 0
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
        //宽高比值4:3(两个文件), 以宽度为准
        //根据文件数量添加高度
        if (allData.size <= 2){
            setMeasuredDimension(widthMeasureSpec, measuredWidth*3/4)
        }else if (allData.size==3) {
            setMeasuredDimension(widthMeasureSpec, measuredWidth)
        }else{
            //todo 不支持4个及其以上
            throw IllegalArgumentException("allData.size >= 4")
        }

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
                moveDirection = -1
                isFling = false
                stopFling()
                if (!isPointDrag()) {
                    //检测位置变换
                    isShiftRect()
                }
                passByMove = false
            }
            MotionEvent.ACTION_MOVE -> {
                passByMove = true
                val downPoint = PointF(event.getX(event.actionIndex), event.getY(event.actionIndex))
                val flx = downPoint.x - primaryLastPoint.x
                val fly = downPoint.y - primaryLastPoint.y
                if (isDragHit != ""){
                    moveDrag(flx, fly)
                }else{
                    moveX += abs(flx)
                    moveY += abs(fly)
                    if (moveDirection == -1){
                        //确定方向
                        moveDirection = if (moveX >= moveY) {
                            //move x
                            1
                        } else {
                            //move y
                            0
                        }
                    }
                    if (isShiftRectHit != "" && moveDirection==0){
                        //to shift rectangles up and down
                        Log.d(TAG, "onTouchEvent: moveY")
                        shiftRect(fly)
                    }else{
                        val moveScreen = moveScreen(flx)
                        if (moveX >= 20 || moveY >= 20) {
                            isFling = true && !moveScreen
                        }
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
                    val downPoint = PointF(event.getX(event.actionIndex), event.getY(event.actionIndex))
                    if (!passByMove){
                        //表示点击事件
                        clickArea(downPoint)

                    }
                }
            }
            else -> {}
        }
        //return super.onTouchEvent(event)
        return true
    }

    private fun shiftRect(fly: Float) {
        //todo
    }

    private fun isShiftRect():Boolean {
        isShiftRectHit != ""
        selectHashMap["$selectIndex"]?.run {
            if (rectF.contains(primaryDownPoint.x-width/2, primaryDownPoint.y)){
                isShiftRectHit = "$selectIndex"
                preShiftRectHit = - 1
                shiftRectHit = - 1
                sufShiftRectHit = - 1
                sortIndex.forEachIndexed { index, value->
                    if (value==selectIndex){
                        if (index-1 >= 0){
                            preShiftRectHit = index - 1
                        }
                        shiftRectHit = index
                        if (index+1 < sortIndex.size){
                            sufShiftRectHit = index + 1
                        }
                    }
                }
            }
        }
        return isShiftRectHit != ""
    }

    private fun clickArea(downPoint: PointF) {
        loop@for (j in sortIndex){
            val b = selectHashMap["$j"]?.run {
                if (rectF.contains(downPoint.x-width/2, downPoint.y)){
                    selectIndex = j
                    true
                }else{
                    false
                }
            } ?: false
            if (b){
                break
            }
        }
        invalidate()
    }

    private fun stopFling() {
        scroller.forceFinished(true)
    }

    private fun moveDrag(flx: Float, fly: Float) {
        dragHashMap[isDragHit]?.let {
            for (j in sortIndex){
                if (isDragHit=="$j"){
                    it.dragPoint?.run {
                        // val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
                        (((x+timeTouchRectF.width()/2 +originXIncrement-width/2+flx)/dbPointInterval-sideSize)*scale).let { d->
                            val minDuration = 0f
                            val maxDuration = mDuration - allData[j].size
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
           if (isDragHit==DRAG_POSITION) {
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

    private fun isPointDrag():Boolean {
        isDragHit = ""
        for (j in sortIndex){
            if (isDragHit == ""){
                // 第一个固定不能滑动
                if (firstDbRect == j){
                    continue
                }
                dragHashMap["$j"]?.let {
                    it.dragPoint?.let {p ->
                        ((primaryDownPoint.x-p.x)*(primaryDownPoint.x-p.x)
                                +(primaryDownPoint.y-p.y)*(primaryDownPoint.y-p.y)).let {f ->
                            if (sqrt(f.toDouble()) < it.radius*2){
                                //hit
                                isDragHit = "$j"
                                it.drag = true
                            }else{
                                isDragHit = ""
                                it.drag = false
                            }
                        }
                    }
                }
            }else{
                break
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
        return isDragHit != ""
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
        drawStartEndSign(canvas)
        drawScaleTab(canvas)
        drawDB(canvas)
        drawFlagBar(canvas)
        canvas.restore()
    }

    private fun drawStartEndSign(canvas: Canvas) {
        canvas.save()
        //移动到中间
        canvas.translate(width/2f, 0f)
        //start
        (0).let {
            val a = (it/scale + sideSize)*dbPointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                canvas.drawLine(a, 0f, a, height.toFloat(), startEndSignPaint)
            }
        }
        //end
        mDuration.let {
            val a = (it.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
            if (a >-halfScope && a < halfScope){
                //绘制
                canvas.drawLine(a, 0f, a, height.toFloat(), startEndSignPaint)
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
        val h = 150
        var pos = 1
        firstDbRect = sortIndex.first()
        for (j in sortIndex){
            //j表示绘制的第几个
            //快速定位
            var start:Int = ((originXIncrement-halfScope)/dbPointInterval).toInt()
            if (start < sideSize){
                start = sideSize
            }

            val data = allData[j]
            dbPaint.color = dbColor
            dbPaint.style = Paint.Style.FILL
            val y = 200+(height-100f)/2*pos
            pos++
            var hadDraw = false
            var firstDrawPoint = Float.MIN_VALUE
            var lastDrawPoint = Float.MIN_VALUE
            val offset = dragHashMap["$j"]?.run {
                duration.toFloat()/scale*dbPointInterval
            } ?: 0f
            //位置偏移余量
            var offsetDiff = 0f
            if (offset != 0f){
                //需要把快速定位的起点调整
                start = sideSize + (offset/dbPointInterval).toInt()
                offsetDiff = offset%dbPointInterval
            }
            forRun@for (i in start until dbPointInView+sideSize){
                val a = i*dbPointInterval - originXIncrement +offsetDiff
                //根据起点位置不一样来确定数据位置
                val index = if (offset != 0f){
                    (i-sideSize-(offset/dbPointInterval).toInt())*scale
                }else{
                    (i-sideSize)*scale
                }
                if (index < 0){
                    continue@forRun
                }
                if (a >-halfScope && a < halfScope){
                    if (firstDrawPoint==Float.MIN_VALUE){
                        firstDrawPoint = a
                    }
                    hadDraw = true
                    if (index >= data.size){
                        //Log.d(TAG, "drawDB: stop index=${index}")
                        break@forRun
                    }
                    val hh = h * data[index].toFloat()
                    canvas.drawLine(a, (y-hh)/2f, a, (y+hh)/2f, dbPaint)
                    lastDrawPoint = a
                }else{
                    if (hadDraw){
                        break@forRun
                    }
                }

            }
            if (firstDrawPoint != Float.MIN_VALUE && lastDrawPoint !=  Float.MIN_VALUE){
                //用矩形包括起来
                dbPaint.style = Paint.Style.STROKE
                if (selectIndex == j){
                    dbPaint.strokeWidth = dp2px(3f).toFloat()
                }else{
                    dbPaint.strokeWidth = dp2px(1f).toFloat()
                }
                RectF(firstDrawPoint,(y-h*1.5f)/2f, lastDrawPoint,(y+h*1.5f)/2f).let {
                    canvas.drawRect(it,dbPaint)
                    selectHashMap["$j"]?.rectF = it
                }
                dbPaint.strokeWidth = dp2px(1f).toFloat()
                //绘制名称
                drawTextMiddleY(canvas, textPaint, Point(firstDrawPoint.toInt(), (y/2+h/2+ dp2px(8f)).toInt()), "test$j")
            }
            //绘制拖拽区
            dragHashMap["$j"]?.let {
                //起点位置
                textPaint.style = Paint.Style.FILL
                val a = (it.duration.toFloat()/scale + sideSize)*dbPointInterval - originXIncrement
                if (a >-halfScope && a < halfScope){
                    //绘制
                    textPaint.color = textBoundColor
                    canvas.save()
                    canvas.translate(a-timeTouchRectF.width(), y/2-timeTouchRectF.height()/2)
                    canvas.drawRoundRect(timeTouchRectF,
                        dp2px(2f).toFloat(),
                        dp2px(2f).toFloat(), textPaint)
                    canvas.restore()
                    it.dragPoint = PointF(a-timeTouchRectF.width()/2+width/2f, y/2)
                    it.radius = (timeTouchRectF.width()/2).toInt()
                    //写入时长
                    textPaint.color = textColor
                    drawTextMiddle(canvas, textPaint, Point((it.dragPoint!!.x-width/2f).toInt(), it.dragPoint!!.y.toInt()),
                        formatTime(it.duration.toLong() *10))
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
                                drawTextMiddle(canvas, timeScalePaint, Point(a.toInt(), 50), this)
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
            drawTextMiddle(canvas, timeScalePaint, Point(lastX.toInt(), 50), lastTime)
        }
        canvas.restore()
    }

    private fun drawTextMiddle(canvas: Canvas, paint: Paint, point: Point, text: String){
        //计算baseline
        paint.fontMetrics.run {
            val distance = (bottom - top) / 2 - bottom
            paint.measureText(text).let {
                canvas.drawText(text, point.x-it/2, point.y + distance, paint)
            }
        }
    }

    private fun drawTextMiddleY(canvas: Canvas, paint: Paint, point: Point, text: String){
        //计算baseline
        paint.fontMetrics.run {
            val distance = (bottom - top) / 2 - bottom
            canvas.drawText(text, point.x.toFloat(), point.y + distance, paint)
        }
    }

}