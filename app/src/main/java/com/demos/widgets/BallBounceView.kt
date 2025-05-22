package com.demos.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt


class BallBounceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var ballX = 0f
    private var ballY = 0f
    private var velocityX = 0f
    private var velocityY = 0f
    private val ballRadius = 40f

    private var anchorX = 0f
    private var anchorY = 0f
    private val anchorRadius = 20f

    private var viewWidth = 0
    private var viewHeight = 0

    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
    }

    private val anchorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 5f
    }

    private val collidableRects = mutableListOf<RectF>()

    init {
        setSpeedFactor(2f)

        post(object : Runnable {
            override fun run() {
                updateBallPosition()
                invalidate()
                postDelayed(this, 16L)
            }
        })
    }

    fun setCollidableViews(vararg views: View) {
        collidableRects.clear()
        views.forEach { view ->
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val left = location[0].toFloat()
            val top = location[1].toFloat()
            val right = left + view.width
            val bottom = top + view.height
            collidableRects.add(RectF(left, top, right, bottom))
        }
    }

    fun setSpeedFactor(factor: Float) {
        val norm = sqrt(velocityX * velocityX + velocityY * velocityY)
        val newSpeed = max(1f, norm * factor)
//        val angle = atan2(velocityY, velocityX)
        val angle = ((0..360).random().toDouble() * PI / 180).toFloat()
        velocityX = cos(angle) * newSpeed
        velocityY = sin(angle) * newSpeed
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewWidth = w
        viewHeight = h
        if (ballX == 0f && ballY == 0f) {
            ballX = w / 2f
            ballY = h / 2f
            anchorX = w / 4f
            anchorY = h / 4f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(anchorX, anchorY, ballX, ballY, linePaint)
        canvas.drawCircle(anchorX, anchorY, anchorRadius, anchorPaint)
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)
    }

    private fun updateBallPosition() {
        ballX += velocityX
        ballY += velocityY

        // 边界反弹（考虑 ball 半径）
        if (ballX - ballRadius <= 0 || ballX + ballRadius >= viewWidth) {
            velocityX = -velocityX + (Math.random().toFloat() - 0.5f) * 0.5f
            ballX = ballX.coerceIn(ballRadius, viewWidth - ballRadius)
        }
        if (ballY - ballRadius <= 0 || ballY + ballRadius >= viewHeight) {
            velocityY = -velocityY + (Math.random().toFloat() - 0.5f) * 0.5f
            ballY = ballY.coerceIn(ballRadius, viewHeight - ballRadius)
        }

        // 屏幕位置（用于判断外部 View 碰撞）
        val location = IntArray(2)
        getLocationOnScreen(location)
        val screenBallX = location[0] + ballX
        val screenBallY = location[1] + ballY

        for (rect in collidableRects) {
            if (circleIntersectsRect(screenBallX, screenBallY, ballRadius, rect)) {
                // 判断碰撞方向：哪个边更近，就反转那一方向
                val closestX = screenBallX.coerceIn(rect.left, rect.right)
                val closestY = screenBallY.coerceIn(rect.top, rect.bottom)
                val dx = screenBallX - closestX
                val dy = screenBallY - closestY

                if (abs(dx) > abs(dy)) {
                    velocityX = -velocityX + (Math.random().toFloat() - 0.5f) * 0.5f
                } else {
                    velocityY = -velocityY + (Math.random().toFloat() - 0.5f) * 0.5f
                }

                // 避免卡住，强制推开
                ballX += velocityX
                ballY += velocityY
                break
            }
        }
    }

    private fun circleIntersectsRect(cx: Float, cy: Float, radius: Float, rect: RectF): Boolean {
        val closestX = cx.coerceIn(rect.left, rect.right)
        val closestY = cy.coerceIn(rect.top, rect.bottom)
        val dx = cx - closestX
        val dy = cy - closestY
        return dx * dx + dy * dy <= radius * radius
    }
}




