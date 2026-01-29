package com.demos.jiguanganim

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import kotlin.math.cos
import kotlin.math.sin


/**
 * by DAD FZ
 * 2026/1/29
 * desc：
 **/
class AuroraBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var time = 0f

    private val animator = ValueAnimator.ofFloat(0f, (2f * Math.PI).toFloat()).apply {
        duration = 15_000L
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
        interpolator = LinearInterpolator()
        addUpdateListener {
            time = it.animatedValue as Float
            invalidate()
        }
    }

    /** 是否暗色主题（你也可以对外暴露 setDarkMode） */
    private val isDarkTheme: Boolean
        get() {
            val color = MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorSurface,
                Color.WHITE
            )
            return ColorUtils.calculateLuminance(color) < 0.5
        }

    private val backgroundColor: Int
        get() = MaterialColors.getColor(
            this,
            android.R.attr.colorBackground,
            Color.WHITE
        )

    /** 极光颜色 */
    private fun auroraColors(): List<Int> {
        return if (isDarkTheme) {
            listOf(
                Color.rgb(0, 79, 148),     // 暗蓝
                Color.rgb(135, 74, 38),    // 暗橙
                Color.rgb(117, 15, 69),    // 洋红
                Color.rgb(41, 31, 115)     // 靛蓝
            )
        } else {
            listOf(
                Color.rgb(145, 194, 250), // 亮蓝
                Color.rgb(250, 217, 173), // 亮橙
                Color.rgb(250, 191, 237), // 亮粉
                Color.rgb(186, 179, 250)  // 亮紫
            )
        }
    }

    /** 第五个光源：主题主色 */
    private fun primaryColor(): Int {
        return MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorPrimary,
            Color.BLUE
        )
    }

    init {
        // Android 12+ 模糊
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setRenderEffect(
                RenderEffect.createBlurEffect(
                    150f,
                    150f,
                    Shader.TileMode.CLAMP
                )
            )
        } else {
            // 老版本建议关闭硬件加速以获得较好的模糊效果
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator.start()
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // 背景
        canvas.drawColor(if (isDarkTheme) Color.BLACK else backgroundColor)

        val colors = auroraColors()
        val color5 = primaryColor()

        drawLight(
            canvas,
            centerX = w * (0.3f + 0.2f * cos(time)),
            centerY = h * (0.4f + 0.2f * sin(time)),
            radius = w * 0.7f,
            color = colors[0],
            alpha = 0.9f
        )

        drawLight(
            canvas,
            centerX = w * (0.7f + 0.2f * cos(time * 1.5f + Math.PI.toFloat())),
            centerY = h * (0.6f + 0.15f * sin(time * 1.5f + Math.PI.toFloat())),
            radius = w * 0.6f,
            color = colors[1],
            alpha = 0.9f
        )

        drawLight(
            canvas,
            centerX = w * (0.6f + 0.2f * cos(time * 0.8f + Math.PI.toFloat() / 2)),
            centerY = h * (0.3f + 0.2f * sin(time * 0.8f + Math.PI.toFloat() / 2)),
            radius = w * 0.8f,
            color = colors[2],
            alpha = 0.85f
        )

        drawLight(
            canvas,
            centerX = w * (0.2f + 0.1f * cos(time * 1.2f + Math.PI.toFloat() / 4)),
            centerY = h * (0.7f + 0.1f * sin(time * 1.2f + Math.PI.toFloat() / 4)),
            radius = w * 0.5f,
            color = colors[3],
            alpha = 0.9f
        )

        drawLight(
            canvas,
            centerX = w * (0.8f + 0.15f * cos(time * 0.9f + Math.PI.toFloat() * 1.5f)),
            centerY = h * (0.2f + 0.15f * sin(time * 0.9f + Math.PI.toFloat() * 1.5f)),
            radius = w * 0.7f,
            color = color5,
            alpha = 0.9f
        )

        // 底部渐变遮罩
        val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gradientPaint.shader = LinearGradient(
            0f, h * 0.1f,
            0f, h,
            Color.TRANSPARENT,
            backgroundColor,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, gradientPaint)
    }

    private fun drawLight(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        color: Int,
        alpha: Float
    ) {
        paint.shader = RadialGradient(
            centerX,
            centerY,
            radius,
            ColorUtils.setAlphaComponent(color, (alpha * 255).toInt()),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}
