package com.demos.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


/**
 * by JFZ
 * 2025/5/20
 * desc:一个日历块组件
 * 示例：
 *         binding.block.setDateRange(2025, 5, 2026, 4)
 *         binding.block.setOptions(
 *             CalendarBlockView.Options()
 *                 .setBlockSize(38.dp)
 *                 .setBlockRadius(5.dp.toFloat())
 *                 .setBlockSpacing(5.dp)
 *                 .setMonthBlockMargin(15.dp)
 *                 .setMonthTextSize(16f.dp.toFloat())
 *                 .setMonthAlign(Paint.Align.CENTER)
 *                 .setMonthTextColor(Color.MAGENTA)
 *                 .setEmptyBlockColor(Color.parseColor("#c9c9c9"))
 *                 .setDefaultBlockColor(Color.parseColor("#c9c9c9"))
 *                 .setBlockTextColor(Color.BLACK)
 *                 .setBlockTextSize(15.dp.toFloat())
 *                 .setWeekdayTextSize(15.dp.toFloat())
 *                 .setWeekdayTextColor(Color.BLUE)
 *                 .setWeekdayHorizontalOffset(15.dp)
 *                 .setWeekdayAlign(Paint.Align.CENTER)
 *                 .setDrawBlockText(true)
 *                 .setDrawWeekdayText(true)
 *                 .setWeekdayLabelFormatter(object : CalendarBlockView.WeekdayLabelFormatter {
 *                     override fun formatted(pos: Int, label: String,paint: Paint,weekdayMode: CalendarBlockView.WeekdayMode): String {
 *                         if (weekdayMode == CalendarBlockView.WeekdayMode.MONDAY_FIRST) {
 *                             return when (pos) {
 *                                 0 -> "1"
 *                                 1 -> "二"
 *                                 3 -> "周4"
 *                                 6 -> "Sunday"
 *                                 else -> label
 *                             }
 *                         }
 *                         return when (pos) {
 *                             0 -> "7"
 *                             1 -> "一"
 *                             3 -> "周三"
 *                             6 -> "Saturday"
 *                             else -> label
 *                         }
 *                     }
 *                 })
 *                 .setMonthLabelFormatter(object : CalendarBlockView.MonthLabelFormatter {
 *                     override fun formatted(position: Int, year: Int, month: Int,paint: Paint): String {
 *                         return year.toString() + "年" + month.toString() + "月"
 *                     }
 *                 })
 *                 .setBlockTextFormatter(object : CalendarBlockView.BlockTextFormatter {
 *                     override fun formatted(
 *                         year: Int,
 *                         month: Int,
 *                         day: Int,
 *                         paint: Paint
 *                     ): String {
 *                         val calendar = Calendar.getInstance()
 *                         val nowYear = calendar.get(Calendar.YEAR)
 *                         val nowMonth = calendar.get(Calendar.MONTH) + 1
 *                         val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
 *                         if (year == nowYear && month == nowMonth && day == nowDay) {
 *                             return ""
 *                         }
 *                         return day.toString()
 *                     }
 *                 })
 *                 .setBlockLayerDraw(object : OnBlockLayerDraw {
 *                     override fun onDrawBlockLayer(
 *                         canvas: Canvas,
 *                         rect: RectF,
 *                         paint: Paint,
 *                         year: Int,
 *                         month: Int,
 *                         day: Int?,
 *                         position: Int,
 *                         colPos: Int,
 *                         rowPos: Int
 *                     ) {
 *                         if (day == null) {
 *                             paint.color = Color.MAGENTA
 *                             canvas.drawCircle(rect.centerX(), rect.centerY(), 5.dp.toFloat(), paint)
 *                         } else {
 *                             paint.color = Color.YELLOW
 *                             val calendar = Calendar.getInstance()
 *                             val nowYear = calendar.get(Calendar.YEAR)
 *                             val nowMonth = calendar.get(Calendar.MONTH) + 1
 *                             val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
 *                             if (year == nowYear && month == nowMonth && day == nowDay) {
 *                                 val path = Path()
 *                                 val centerX = rect.centerX()
 *                                 val centerY = rect.centerY()
 *                                 val radius = min(rect.width(), rect.height()) / 4f
 *                                 val angle = Math.toRadians(-90.0) // 从正上方开始
 *                                 val step = Math.toRadians(360.0 / 5) // 五角星的五个顶点
 *                                 val innerRadius = radius * 0.6f // 内部顶点的半径比例可以调整
 *                                 for (i in 0..9) {
 *                                     val r = if (i % 2 == 0) radius else innerRadius
 *                                     val theta = angle + step * i
 *
 *                                     val x = (centerX + r * Math.cos(theta)).toFloat()
 *                                     val y = (centerY + r * Math.sin(theta)).toFloat()
 *                                     if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
 *                                 }
 *                                 path.close()
 *                                 canvas.drawPath(path, paint)
 *                             }
 *                         }
 *                     }
 *                 })
 *         )
 *
 *         binding.block.setOnBlockClickListener(object : CalendarBlockView.OnBlockClickListener {
 *             override fun onBlock(data: CalendarBlockView.BlockData) {
 *                 Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_SHORT).show()
 *             }
 *         })
 *
 *         binding.block.setPercentForDays(
 *             mutableListOf(
 *                 CalendarBlockView.BlockData(2025, 5, 1, 0),
 *                 CalendarBlockView.BlockData(2025, 5, 1, 0),
 *                 CalendarBlockView.BlockData(2025, 5, 2, 10),
 *                 CalendarBlockView.BlockData(2025, 5, 3, 25),
 *                 CalendarBlockView.BlockData(2025, 5, 4, 35),
 *                 CalendarBlockView.BlockData(2025, 5, 5, 45),
 *                 CalendarBlockView.BlockData(2025, 5, 6, 55),
 *                 CalendarBlockView.BlockData(2025, 5, 7, 65),
 *                 CalendarBlockView.BlockData(2025, 6, 8, 76),
 *                 CalendarBlockView.BlockData(2025, 6, 9, 88),
 *                 CalendarBlockView.BlockData(2025, 6, 10, 99),
 *                 CalendarBlockView.BlockData(2025, 6, 11, 50),
 *                 CalendarBlockView.BlockData(2025, 6, 15, 70),
 *                 CalendarBlockView.BlockData(2025, 7, 10, 100),
 *             )
 *         )
 **/
class CalendarBlockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class WeekdayMode {
        MONDAY_FIRST,
        SUNDAY_FIRST
    }

    private var options = Options()

    private var currentWeekdayMode = WeekdayMode.SUNDAY_FIRST
    private var weekdayLabels = getWeekdayLabels(currentWeekdayMode)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = options.blockTextColor
        textSize = options.blockTextSize
        textAlign = Paint.Align.CENTER
    }

    private val blockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    private val monthTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = options.monthTextColor
        textSize = options.monthTextSizePx
        textAlign = options.monthAlign
        typeface = Typeface.DEFAULT_BOLD
    }
    private val monthFormatStr = "%d年%d月"

    private val weekdayLabelDraws = mutableListOf<String>()
    private val weekdayColors = mutableListOf<Int>()
    private val weekdayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = options.weekdayTextColor
        textSize = options.weekdayTextSize
        textAlign = options.weekdayAlign
    }
    private var weekdayWidth = 0f

    // 月份矩阵列表，格式：(year, month, [列][7行])
    private val dateBlocks = mutableListOf<Triple<Int, Int, List<List<Int?>>>>()

    // 日期 -> 百分比映射，key:YYYY-MM-dd
    private val percentMap = mutableMapOf<String, Int>()

    // 默认颜色配置
    var progressColors: List<Int> = listOf(
        0x1A7D69FF.toInt(), 0x337D69FF.toInt(), 0x4D7D69FF.toInt(),
        0x667D69FF.toInt(), 0x7F7D69FF.toInt(), 0x997D69FF.toInt(),
        0xB37D69FF.toInt(), 0xCC7D69FF.toInt(), 0xE67D69FF.toInt(),
        0xFF7D69FF.toInt()
    )

    private var startYear = -1
    private var startMonth = -1
    private var endYear = -1
    private var endMonth = -1

    private val blockParams = mutableMapOf<RectF, BlockData>()
    private var blockClickListener: OnBlockClickListener? = null

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val rectF = blockParams.keys.find { it.contains(e.x, e.y) }
            rectF?.let {
                val data = blockParams[it]
                data?.let { result -> blockClickListener?.onBlock(result) }
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (checkDate()) {
            val totalColumns = dateBlocks.sumOf { it.third.size }
            val width =
                weekdayWidth + totalColumns * (options.blockSize + options.blockSpacing) - options.blockSpacing
            val height = (options.monthTextSizePx + options.monthBlockMargin).toInt() +
                    7 * (options.blockSize + options.blockSpacing) - options.blockSpacing
            setMeasuredDimension(width.toInt(), height)
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gestureDetector.onTouchEvent(event)
        return handled || super.onTouchEvent(event)
    }

    private fun checkDate(): Boolean {
        return (startYear != -1 && startMonth != -1 && endYear != -1 && endMonth != -1)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (checkDate()) {

            // 绘制星期标签（左边一列）
            if (options.isDrawWeekdayText) {
                weekdayPaint.textAlign = options.weekdayAlign
                val topOffset = (options.monthTextSizePx + options.monthBlockMargin).toInt()
                val weekX = when (options.weekdayAlign) {
                    Paint.Align.LEFT -> options.weekdayHorizontalOffset / 2f
                    Paint.Align.CENTER -> weekdayWidth / 2f
                    else -> (weekdayWidth - options.weekdayHorizontalOffset / 2f).toFloat()
                }
                for (row in 0 until weekdayLabelDraws.size) {
                    weekdayPaint.color = weekdayColors[row]
                    val weekY =
                        topOffset + row * (options.blockSize + options.blockSpacing) + options.blockSize / 2 + weekdayPaint.textSize / 3
                    canvas.drawText(
                        weekdayLabelDraws[row],
                        weekX,
                        weekY,
                        weekdayPaint
                    )
                }
            }

            monthTextPaint.textAlign = options.monthAlign

            var colOffset = 0
            dateBlocks.forEachIndexed { position, (year, month, matrix) ->
                val totalCols = matrix.size
                val startX = colOffset * (options.blockSize + options.blockSpacing)
                val endX =
                    (colOffset + totalCols) * (options.blockSize + options.blockSpacing) - options.blockSpacing
                val centerX = when (options.monthAlign) {
                    Paint.Align.LEFT -> weekdayWidth + startX
                    Paint.Align.RIGHT -> weekdayWidth + endX
                    else -> weekdayWidth + (startX + endX) / 2f
                }

                val monthStr =
                    if (options.monthLabelFormatter != null) options.monthLabelFormatter!!.formatted(
                        position,
                        year,
                        month,
                        monthTextPaint
                    ) else monthFormatStr.format(year, month)

                canvas.drawText(
                    monthStr,
                    centerX.toFloat(),
                    options.monthTextSizePx,
                    monthTextPaint
                )

                val topOffset = (options.monthTextSizePx + options.monthBlockMargin).toInt()

                for (col in matrix.indices) {
                    for (row in 0 until 7) {
                        val left =
                            weekdayWidth + (colOffset + col) * (options.blockSize + options.blockSpacing)

                        val top = topOffset + row * (options.blockSize + options.blockSpacing)

                        val rect = RectF(
                            left.toFloat(),
                            top.toFloat(),
                            (left + options.blockSize).toFloat(),
                            (top + options.blockSize).toFloat()
                        )

                        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            style = Paint.Style.FILL
                        }

                        val day = matrix[col][row]
                        if (day != null) {
                            val percent = getPercentForDay(year, month, day)
                            paint.color = getColorForPercent(percent)
                            blockParams[rect] = BlockData(year, month, day, percent)
                        } else {
                            paint.color = options.emptyBlockColor
                        }

                        canvas.drawRoundRect(rect, options.blockRadius, options.blockRadius, paint)

                        if (day != null && options.isDrawBlockText) {
                            val blockText =
                                if (options.blockTextFormatter != null) options.blockTextFormatter!!.formatted(
                                    year, month, day, textPaint
                                ) else day.toString()
                            canvas.drawText(
                                blockText,
                                rect.centerX(),
                                rect.centerY() + textPaint.textSize / 3,
                                textPaint
                            )
                        }

                        //外部自定义图层
                        options.blockLayerDraw?.onDrawBlockLayer(
                            canvas,
                            rect,
                            blockPaint,
                            year,
                            month,
                            day,
                            position,
                            col,
                            row
                        )
                    }
                }

                colOffset += matrix.size
            }

        }
    }

    //获取星期的标签列表
    private fun getWeekdayLabels(mode: WeekdayMode): List<String> {
        return when (mode) {
            WeekdayMode.MONDAY_FIRST -> listOf(
                "周一",
                "周二",
                "周三",
                "周四",
                "周五",
                "周六",
                "周日"
            )

            WeekdayMode.SUNDAY_FIRST -> listOf(
                "周日",
                "周一",
                "周二",
                "周三",
                "周四",
                "周五",
                "周六"
            )
        }
    }

    //获取星期的最大宽度
    private fun weekdayLabelWidth(): Float {
        weekdayLabelDraws.clear()
        weekdayColors.clear()
        var maxWidth = 0f
        weekdayLabels.forEachIndexed { index, item ->
            val str =
                options.weekdayLabelFormatter?.formatted(
                    index,
                    weekdayLabels[index],
                    weekdayPaint,
                    currentWeekdayMode
                )
                    ?: weekdayLabels[index]
            weekdayLabelDraws.add(str)
            weekdayColors.add(weekdayPaint.color)
            val w = weekdayPaint.measureText(str)
            if (w > maxWidth) {
                maxWidth = w
            }
        }
        weekdayWidth =
            if (options.isDrawWeekdayText) (maxWidth + options.weekdayHorizontalOffset)
            else 0f
        return weekdayWidth
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = GregorianCalendar(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = GregorianCalendar(year, month - 1, 1)
        return cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, ..., 7=Saturday
    }

    private fun buildMatrixForMonth(year: Int, month: Int): List<List<Int?>> {
        val result = mutableListOf<MutableList<Int?>>()
        val firstDayWeek = getFirstDayOfWeek(year, month)
        val startOffset = when (currentWeekdayMode) {
            WeekdayMode.MONDAY_FIRST -> {
                when (firstDayWeek) {
                    Calendar.SUNDAY -> 6
                    Calendar.MONDAY -> 0
                    Calendar.TUESDAY -> 1
                    Calendar.WEDNESDAY -> 2
                    Calendar.THURSDAY -> 3
                    Calendar.FRIDAY -> 4
                    Calendar.SATURDAY -> 5
                    else -> 0
                }
            }

            WeekdayMode.SUNDAY_FIRST -> {
                firstDayWeek - 1 // Sunday -> 0, Monday -> 1, ... Saturday -> 6
            }
        }

        val totalDays = getDaysInMonth(year, month)
        val totalCells = startOffset + totalDays
        val columns = ceil(totalCells / 7.0).toInt()

        repeat(columns) { result.add(MutableList(7) { null }) }

        var day = 1
        for (col in 0 until columns) {
            for (row in 0 until 7) {
                if (col == 0 && row < startOffset) continue
                if (day > totalDays) break
                result[col][row] = day++
            }
        }
        return result
    }

    private fun buildAllMatrices() {
        dateBlocks.clear()
        var y = startYear
        var m = startMonth
        while (y < endYear || (y == endYear && m <= endMonth)) {
            dateBlocks.add(Triple(y, m, buildMatrixForMonth(y, m)))
            m++
            if (m > 12) {
                m = 1
                y++
            }
        }
    }

    private fun getPercentForDay(year: Int, month: Int, day: Int): Int {
        val key = "%04d-%02d-%02d".format(year, month, day)
        return percentMap[key] ?: 0
    }

    private fun getColorForPercent(percent: Int): Int {
        return if (percent <= 0) {
            options.defaultBlockColor
        } else {
            val index = ((percent - 1) / 10).coerceIn(0, 9)
            progressColors[index]
        }
    }

    fun setDateRange(startMills: Long, endMills: Long) {
        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = min(startMills, endMills)
        val sYear = startCalendar.get(Calendar.YEAR)
        val sMonth = startCalendar.get(Calendar.MONTH) + 1

        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = max(startMills, endMills)
        val eYear = endCalendar.get(Calendar.YEAR)
        val eMonth = endCalendar.get(Calendar.MONTH) + 1

        setDateRange(sYear, sMonth, eYear, eMonth)
    }

    fun setDateRange(startYear: Int, startMonth: Int, endYear: Int, endMonth: Int) {
        blockParams.clear()
        if (startYear < endYear || (startYear == endYear && startMonth <= endMonth)) {
            this.startYear = startYear
            this.startMonth = startMonth
            this.endYear = endYear
            this.endMonth = endMonth
        } else {
            this.startYear = endYear
            this.startMonth = endMonth
            this.endYear = startYear
            this.endMonth = startMonth
        }
        buildAllMatrices()
        requestLayout()
    }

    fun setPercentForDays(list: MutableList<BlockData>) {
        list.forEach {
            val key = "%04d-%02d-%02d".format(it.year, it.month, it.day)
            percentMap[key] = it.percent.coerceIn(0, 100)
        }
        if (checkDate()) {
            invalidate()
        }
    }

    fun setOptions(options: Options) {
        this.options = options
        weekdayPaint.color = options.weekdayTextColor
        weekdayPaint.textSize = options.weekdayTextSize
        textPaint.color = options.blockTextColor
        textPaint.textSize = options.blockTextSize
        monthTextPaint.textSize = options.monthTextSizePx
        monthTextPaint.color = options.monthTextColor
        weekdayLabels = getWeekdayLabels(currentWeekdayMode)
        weekdayLabelWidth()
        requestLayout()
    }

    fun getOptions(): Options {
        return options
    }

    fun setOnBlockClickListener(listener: OnBlockClickListener) {
        this.blockClickListener = listener
    }

    // 新增设置星期显示模式的方法
    fun setWeekdayMode(mode: WeekdayMode) {
        if (checkDate()) {
            if (currentWeekdayMode != mode) {
                currentWeekdayMode = mode
                weekdayLabels = getWeekdayLabels(currentWeekdayMode)
                buildAllMatrices() // 重新构建日期矩阵以适应新的星期排列
                weekdayLabelWidth() // 重新计算星期标签宽度
                requestLayout()
                invalidate()
            }
        }
    }

    class Options {
        //块的大小
        var blockSize = 100
            private set

        //块的圆角
        var blockRadius = 0f
            private set

        //块之间的间距
        var blockSpacing = 3
            private set

        //块的文字颜色
        var blockTextColor = Color.WHITE
            private set

        //块的文字大小
        var blockTextSize = 30f
            private set

        //是否绘制块文字
        var isDrawBlockText = true
            private set

        //默认块的颜色
        var defaultBlockColor = Color.LTGRAY
            private set

        //空白块的颜色
        var emptyBlockColor = Color.LTGRAY
            private set

        //自定义块的涂层
        var blockLayerDraw: OnBlockLayerDraw? = null
            private set

        //块文字格式化
        var blockTextFormatter: BlockTextFormatter? = null
            private set

        //年月份文字和方块之间的垂直间距
        var monthBlockMargin = 10
            private set

        //顶部年月份的文字大小
        var monthTextSizePx = 30f
            private set

        //顶部年月份文字颜色
        var monthTextColor = Color.BLACK
            private set

        //顶部年月格式化
        var monthLabelFormatter: MonthLabelFormatter? = null
            private set

        //顶部年月对齐方式
        var monthAlign = Paint.Align.CENTER
            private set

        //星期的文字颜色
        var weekdayTextColor = Color.BLACK
            private set

        //星期的文字大小
        var weekdayTextSize = 30f
            private set

        //星期的横向间距
        var weekdayHorizontalOffset = 10
            private set

        //是否绘制星期文字
        var isDrawWeekdayText = true
            private set

        //星期的对齐方式
        var weekdayAlign = Paint.Align.RIGHT
            private set

        //星期文字格式化
        var weekdayLabelFormatter: WeekdayLabelFormatter? = null

        fun setBlockSize(size: Int): Options {
            this.blockSize = size
            return this
        }

        fun setBlockSpacing(space: Int): Options {
            this.blockSpacing = space
            return this
        }

        fun setMonthBlockMargin(margin: Int): Options {
            this.monthBlockMargin = margin
            return this
        }

        fun setMonthTextSize(px: Float): Options {
            this.monthTextSizePx = px
            return this
        }

        fun setMonthTextColor(color: Int): Options {
            this.monthTextColor = color
            return this
        }

        fun setMonthLabelFormatter(formatter: MonthLabelFormatter): Options {
            this.monthLabelFormatter = formatter
            return this
        }

        fun setMonthAlign(align: Paint.Align): Options {
            this.monthAlign = align
            return this
        }

        fun setEmptyBlockColor(color: Int): Options {
            this.emptyBlockColor = color
            return this
        }

        fun setDefaultBlockColor(color: Int): Options {
            this.defaultBlockColor = color
            return this
        }

        fun setBlockTextColor(color: Int): Options {
            this.blockTextColor = color
            return this
        }

        fun setBlockTextSize(px: Float): Options {
            this.blockTextSize = px
            return this
        }

        fun setDrawBlockText(isDrawBlockText: Boolean): Options {
            this.isDrawBlockText = isDrawBlockText
            return this
        }

        fun setBlockRadius(radius: Float): Options {
            this.blockRadius = radius
            return this
        }

        fun setWeekdayTextColor(color: Int): Options {
            this.weekdayTextColor = color
            return this
        }

        fun setWeekdayTextSize(px: Float): Options {
            this.weekdayTextSize = px
            return this
        }

        fun setDrawWeekdayText(isDrawWeekdayText: Boolean): Options {
            this.isDrawWeekdayText = isDrawWeekdayText
            return this
        }

        fun setWeekdayAlign(align: Paint.Align): Options {
            this.weekdayAlign = align
            return this
        }

        fun setWeekdayHorizontalOffset(offset: Int): Options {
            this.weekdayHorizontalOffset = offset
            return this
        }

        fun setWeekdayLabelFormatter(formatter: WeekdayLabelFormatter): Options {
            this.weekdayLabelFormatter = formatter
            return this
        }

        fun setBlockLayerDraw(layer: OnBlockLayerDraw): Options {
            this.blockLayerDraw = layer
            return this
        }

        fun setBlockTextFormatter(formatter: BlockTextFormatter): Options {
            this.blockTextFormatter = formatter
            return this
        }

    }

    class BlockData(val year: Int, val month: Int, val day: Int, val percent: Int) {

        override fun toString(): String {
            return "year:${year},month:$month,day:$day,percent:$percent"
        }
    }

    interface OnBlockClickListener {

        fun onBlock(data: BlockData)
    }

    interface WeekdayLabelFormatter {

        fun formatted(pos: Int, label: String, paint: Paint, weekdayMode: WeekdayMode): String
    }

    interface MonthLabelFormatter {

        fun formatted(index: Int, year: Int, month: Int, paint: Paint): String
    }

    interface BlockTextFormatter {

        fun formatted(year: Int, month: Int, day: Int, paint: Paint): String
    }

    interface OnBlockLayerDraw {
        fun onDrawBlockLayer(
            canvas: Canvas,
            rect: RectF,
            paint: Paint,
            year: Int,
            month: Int,
            day: Int?,
            position: Int,
            colPos: Int,
            rowPos: Int
        )
    }

}