package com.demos.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt


/**
 * by JFZ
 * 2025/6/6
 * desc：
 **/
class GradientColorBlock @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Mode { Level, Percent }

    private var mode = Mode.Level

    private var options = BlockOptions()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val blockPaint = Paint(paint)

    private val path = Path()

    private var level = 0

    private var percent = 0f

    @SuppressLint("DrawAllocation", "UseKtx")
    override fun onDraw(canvas: Canvas) {
        val rectWidth = (width - options.spacing * (options.colors.size - 1)) / options.colors.size
        options.colors.forEachIndexed { i, _ ->
            val left = i * (rectWidth + options.spacing).toFloat()
            val right = (left + rectWidth).toFloat()
            val blockRect = RectF(left, 0f, right, height.toFloat())
            blockPaint.color = options.blockBackgroundColor
            canvas.drawRoundRect(blockRect, options.radius, options.radius, blockPaint)
            path.reset()
            path.addRoundRect(blockRect, options.radius, options.radius, Path.Direction.CW)
            canvas.save()
            canvas.clipPath(path)
            if (mode == Mode.Level && i < level) {
                canvas.drawRect(RectF(0f, 0f, right, height.toFloat()), paint)
            } else if (mode == Mode.Percent) {
                val percentWidth = width.toFloat() * percent
                canvas.drawRect(RectF(0f, 0f, percentWidth, height.toFloat()), paint)
            }
            canvas.restore()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateShader()
    }

    private fun updateShader() {
        paint.shader =
            LinearGradient(0f, 0f, width.toFloat(), 0f, options.colors, null, Shader.TileMode.CLAMP)
    }

    fun setLevel(level: Int) {
        this.mode = Mode.Level
        this.level = level.coerceIn(0, options.colors.size)
        invalidate()
    }

    fun setPercent(percent: Float) {
        this.mode = Mode.Percent
        this.percent = percent.coerceIn(0f, 100f) / 100f
        invalidate()
    }

    fun setOptions(option: BlockOptions) {
        this.options = option
        updateShader()
        invalidate()
    }

    class BlockOptions {
        var blockBackgroundColor = Color.LTGRAY

        var spacing = 10

        var radius = 10f

        var colors = intArrayOf(
            "#E53935".toColorInt(), "#FB8C00".toColorInt(), "#FDD835".toColorInt(),
            "#8BC34A".toColorInt(), "#43A047".toColorInt(), "#00BFA5".toColorInt(),
            "#039BE5".toColorInt(), "#1E88E5".toColorInt(), "#3949AB".toColorInt(),
            "#8E24AA".toColorInt(),
        )
            set(colorArr) {
                if (colorArr.isNotEmpty()) {
                    field = colorArr
                }
            }
    }
}