package com.demos.activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.demos.databinding.ActivityGithubCalendarBlockBinding
import com.demos.dp
import com.demos.widgets.CalendarBlockView
import com.demos.widgets.CalendarBlockView.OnBlockLayerDraw
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


/**
 * by JFZ
 * 2025/5/19
 * desc：
 **/
class CalendarBlockActivity : AppCompatActivity() {

    private val binding: ActivityGithubCalendarBlockBinding by lazy {
        ActivityGithubCalendarBlockBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.block.setDateRange(2025, 5, 2026, 4)

        binding.block.setOptions(
            CalendarBlockView.Options()
                .setBlockSize(25.dp)
                .setBlockRadius(5.dp.toFloat())
                .setBlockSpacing(5.dp)
                .setMonthBlockMargin(15.dp)
                .setMonthTextSize(13f.dp.toFloat())
                .setMonthAlign(Paint.Align.CENTER)
                .setMonthTextColor(Color.MAGENTA)
                .setEmptyBlockColor("#c9c9c9".toColorInt())
                .setDefaultBlockColor("#c9c9c9".toColorInt())
                .setBlockTextColor(Color.WHITE)
                .setBlockTextSize(12.dp.toFloat())
                .setWeekdayTextSize(12.dp.toFloat())
                .setWeekdayTextColor(Color.BLUE)
                .setWeekdayHorizontalOffset(15.dp)
                .setWeekdayAlign(Paint.Align.CENTER)
                .setDrawBlockText(true)
                .setDrawWeekdayText(true)
                .setWeekdayLabelFormatter(object : CalendarBlockView.WeekdayLabelFormatter {
                    override fun formatted(pos: Int, label: String, paint: Paint): String {
                        paint.color = randomColor()
                        return when (pos) {
                            0 -> "1"
                            1 -> "二"
                            3 -> "周4"
                            6 -> "Sunday"
                            else -> label
                        }
                    }
                })
                .setMonthLabelFormatter(object : CalendarBlockView.MonthLabelFormatter {
                    override fun formatted(
                        position: Int,
                        year: Int,
                        month: Int,
                        paint: Paint
                    ): String {
                        paint.color = randomColor()
                        return year.toString() + "年" + month.toString() + "月"
                    }
                })
                .setBlockTextFormatter(object : CalendarBlockView.BlockTextFormatter {
                    override fun formatted(
                        year: Int,
                        month: Int,
                        day: Int,
                        paint: Paint
                    ): String {
                        val calendar = Calendar.getInstance()
                        val nowYear = calendar.get(Calendar.YEAR)
                        val nowMonth = calendar.get(Calendar.MONTH) + 1
                        val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
                        if (year == nowYear && month == nowMonth && day == nowDay) {
                            return ""
                        }
                        if (month == 5 && day == 20) {
                            return ""
                        }
                        paint.color = randomColor()
                        return day.toString()
                    }
                })
                .setBlockLayerDraw(object : OnBlockLayerDraw {
                    override fun onDrawBlockLayer(
                        canvas: Canvas,
                        rect: RectF,
                        paint: Paint,
                        year: Int,
                        month: Int,
                        day: Int?,
                        position: Int,
                        colPos: Int,
                        rowPos: Int
                    ) {
                        if (day == null) {
                            paint.color = randomColor(100)
                            canvas.drawCircle(rect.centerX(), rect.centerY(), 5.dp.toFloat(), paint)
                        } else {
                            paint.color = Color.RED
                            val calendar = Calendar.getInstance()
                            val nowYear = calendar.get(Calendar.YEAR)
                            val nowMonth = calendar.get(Calendar.MONTH) + 1
                            val nowDay = calendar.get(Calendar.DAY_OF_MONTH)
                            if (year == nowYear && month == nowMonth && day == nowDay) {
                                val path = Path()
                                val centerX = rect.centerX()
                                val centerY = rect.centerY()
                                val radius = min(rect.width(), rect.height()) / 3f
                                val angle = Math.toRadians(-90.0) // 从正上方开始
                                val step = Math.toRadians(360.0 / 5) // 五角星的五个顶点
                                val innerRadius = radius * 0.6f // 内部顶点的半径比例可以调整
                                for (i in 0..9) {
                                    val r = if (i % 2 == 0) radius else innerRadius
                                    val theta = angle + step * i

                                    val x = (centerX + r * cos(theta)).toFloat()
                                    val y = (centerY + r * sin(theta)).toFloat()
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                path.close()
                                canvas.drawPath(path, paint)
                            } else if (month == 5 && (day == 20 || day == 21)) {
                                val width = rect.width()
                                val height = rect.height()
                                val heartSize = width.coerceAtMost(height) / 2f

                                val centerY = rect.centerY()
                                val leftHeartX = rect.left + width * 0.4f
                                val rightHeartX = leftHeartX + heartSize * 0.4f // 右边心偏移重叠约一半

                                // ❤️ 公共函数：绘制单个爱心
                                fun drawHeart(
                                    centerX: Float,
                                    centerY: Float,
                                    size: Float,
                                    paint: Paint
                                ) {
                                    val path = Path()
                                    val verticalScale = 1.3f // 拉高比例
                                    val scaledHeight = size * verticalScale
                                    val top = centerY - scaledHeight / 2 - 3  // ✅ 调整 top 以保持视觉中心

                                    path.moveTo(centerX, top + size / 4 * verticalScale)

                                    path.cubicTo(
                                        centerX + size / 2, top,
                                        centerX + size, top + size / 2 * verticalScale,
                                        centerX, top + size * verticalScale
                                    )

                                    path.cubicTo(
                                        centerX - size, top + size / 2 * verticalScale,
                                        centerX - size / 2, top,
                                        centerX, top + size / 4 * verticalScale
                                    )

                                    path.close()
                                    canvas.drawPath(path, paint)
                                }

                                // ❤️ 背后的心（右侧，颜色浅一些）
                                val backPaint = Paint().apply {
                                    color = "#FF0000".toColorInt() // 淡红色
                                    style = Paint.Style.FILL
                                    isAntiAlias = true
                                }

                                drawHeart(rightHeartX, centerY, heartSize, backPaint)

                                // ❤️ 前面的心（左侧，颜色深一些）
                                val frontPaint = Paint().apply {
                                    color = Color.RED // 深红色
                                    style = Paint.Style.FILL
                                    isAntiAlias = true
                                    setShadowLayer(5f, 3f, 3f, Color.DKGRAY) // 添加阴影增强立体感（需关闭硬件加速）
                                }

                                // 画前面的心（有层次）
                                drawHeart(leftHeartX, centerY, heartSize, frontPaint)
                            }
                        }
                    }
                })
        )

        binding.block.setOnBlockClickListener(object : CalendarBlockView.OnBlockClickListener {
            override fun onBlock(data: CalendarBlockView.BlockData) {
                Toast.makeText(applicationContext, data.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        binding.block.setPercentForDays(
            mutableListOf(
                CalendarBlockView.BlockData(2025, 5, 1, 0),
                CalendarBlockView.BlockData(2025, 5, 1, 0),
                CalendarBlockView.BlockData(2025, 5, 2, 10),
                CalendarBlockView.BlockData(2025, 5, 3, 25),
                CalendarBlockView.BlockData(2025, 5, 4, 35),
                CalendarBlockView.BlockData(2025, 5, 5, 45),
                CalendarBlockView.BlockData(2025, 5, 6, 55),
                CalendarBlockView.BlockData(2025, 5, 7, 65),
                CalendarBlockView.BlockData(2025, 6, 8, 76),
                CalendarBlockView.BlockData(2025, 6, 9, 88),
                CalendarBlockView.BlockData(2025, 6, 10, 99),
                CalendarBlockView.BlockData(2025, 6, 11, 50),
                CalendarBlockView.BlockData(2025, 6, 15, 70),
                CalendarBlockView.BlockData(2025, 7, 10, 100),
            )
        )
    }

    fun randomColor(alpha: Int = 255): Int {
        val r = (0..255).random()
        val g = (0..255).random()
        val b = (0..255).random()
        return Color.argb(alpha, r, g, b)
    }
}