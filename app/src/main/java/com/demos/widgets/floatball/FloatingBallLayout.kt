package com.demos.widgets.floatball

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt


/**
 * by DAD ZZ
 * 2025/8/18
 * desc：
 **/
class FloatingBallLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // 拖拽相关
    private var downRawX = 0f
    private var downRawY = 0f
    private var lastRawX = 0f
    private var lastRawY = 0f
    private var isDragging = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    // 父容器尺寸 & 安全边距
    private var parentW = 0
    private var parentH = 0
    private var insetLeft = 0
    private var insetTop = 0
    private var insetRight = 0
    private var insetBottom = 0

    // 贴边回弹参数
    var edgeMarginPx = dp(8f)        // 与边缘保留的最小间距
    var reboundDuration = 280L       // 回弹动画时长
    var reboundTension = 1.25f       // Overshoot 张力，越大回弹越明显

    // 是否仅左右贴边（常见悬浮球行为）
    var stickToHorizontalEdgesOnly = true

    /** 停止后最终坐标回调 */
    var onPositionChanged: ((x: Float, y: Float) -> Unit)? = null

    init {
        // 处理 WindowInsets，避免顶到状态栏/导航栏
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            insetLeft = sys.left
            insetTop = sys.top
            insetRight = sys.right
            insetBottom = sys.bottom
            ViewCompat.setOnApplyWindowInsetsListener(this@FloatingBallLayout, null)
            insets
        }
        post { ViewCompat.requestApplyInsets(this) }

        // 监听父容器尺寸
        viewTreeObserver.addOnGlobalLayoutListener {
            (parent as? View)?.let { p ->
                parentW = p.width
                parentH = p.height
            }
        }

        isClickable = true
        isFocusable = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downRawX = ev.rawX
                downRawY = ev.rawY
                lastRawX = downRawX
                lastRawY = downRawY
                isDragging = false
                // 让父级不要拦截（如嵌套在可滚动容器里）
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = ev.rawX - downRawX
                val dy = ev.rawY - downRawY
                if (!isDragging && hypot(dx, dy) > touchSlop) {
                    isDragging = true
                    return true // 开始拖拽，拦截后续事件
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        // 不拦截，交给子 View 处理点击
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_MOVE -> {
                val rawX = event.rawX
                val rawY = event.rawY
                val dX = rawX - lastRawX
                val dY = rawY - lastRawY
                lastRawX = rawX
                lastRawY = rawY
                // 只在拖拽中更新
                if (!isDragging) {
                    val dx0 = rawX - downRawX
                    val dy0 = rawY - downRawY
                    if (hypot(dx0, dy0) <= touchSlop) return true
                    isDragging = true
                }
                moveBy(dX, dY)
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    snapToEdgeWithRebound()
                    return true
                }
                // 非拖拽，交给子 View 的点击
                return performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        // 不消费，让子 View 自己收到点击；但保持可达性一致性
        return super.performClick()
    }

    private fun moveBy(dx: Float, dy: Float) {
        if (parentW == 0 || parentH == 0) return

        val minX = (edgeMarginPx + insetLeft).toFloat()
        val maxX = (parentW - width - edgeMarginPx - insetRight).toFloat()
        val minY = (edgeMarginPx + insetTop).toFloat()
        val maxY = (parentH - height - edgeMarginPx - insetBottom).toFloat()

        val newX = (translationX + dx).coerceIn(minX, maxX)
        val newY = (translationY + dy).coerceIn(minY, maxY)

        translationX = newX
        translationY = newY
    }

    private fun snapToEdgeWithRebound() {
        if (parentW == 0 || parentH == 0) return

        val minX = (edgeMarginPx + insetLeft).toFloat()
        val maxX = (parentW - width - edgeMarginPx - insetRight).toFloat()
        val minY = (edgeMarginPx + insetTop).toFloat()
        val maxY = (parentH - height - edgeMarginPx - insetBottom).toFloat()

        // 计算最近边
        val centerX = translationX + width / 2f
        val centerY = translationY + height / 2f
        val nearestLeft = minX
        val nearestRight = maxX
        val nearestTop = minY
        val nearestBottom = maxY

        val targetX = if (stickToHorizontalEdgesOnly) {
            if (centerX < parentW / 2f) nearestLeft else nearestRight
        } else {
            // 选择四边中最近的
            val dLeft = abs(centerX - minX)
            val dRight = abs((parentW - centerX) - minX) // 等价比较右边
            val dTop = abs(centerY - minY)
            val dBottom = abs((parentH - centerY) - minY)

            val minD = min(min(dLeft, dRight), min(dTop, dBottom))
            when (minD) {
                dLeft -> nearestLeft
                dRight -> nearestRight
                dTop, dBottom -> translationX // 贴上下时不改变 X
                else -> nearestLeft
            }
        }

        val targetY = if (stickToHorizontalEdgesOnly) {
            translationY.coerceIn(nearestTop, nearestBottom)
        } else {
            // 若决定贴上下边
            val dTop = abs(centerY - minY)
            val dBottom = abs((parentH - centerY) - minY)
            val dLeft = abs(centerX - minX)
            val dRight = abs((parentW - centerX) - minX)
            val minD = min(min(dLeft, dRight), min(dTop, dBottom))
            when (minD) {
                dTop -> nearestTop
                dBottom -> nearestBottom
                else -> translationY.coerceIn(nearestTop, nearestBottom)
            }
        }

        animate()
            .translationX(targetX)
            .translationY(targetY)
            .setDuration(reboundDuration)
            .setInterpolator(OvershootInterpolator(reboundTension))
            .withEndAction {
                // 动画结束回调最终坐标
                onPositionChanged?.invoke(translationX, translationY)
            }
            .start()
    }

    private fun dp(v: Float): Int =
        (v * resources.displayMetrics.density + 0.5f).toInt()

    private fun hypot(x: Float, y: Float): Float = sqrt(x * x + y * y)


    /** 获取当前坐标点（左上角相对父容器的坐标） */
    fun getCurrentPosition(): PointF = PointF(translationX, translationY)

    /** 设置坐标点（可选动画） */
    fun setPosition(x: Float, y: Float, animate: Boolean = false) {
        if (animate) {
            animate()
                .translationX(x)
                .translationY(y)
                .setDuration(200L)
                .setInterpolator(OvershootInterpolator(1.0f))
                .withEndAction {
                    onPositionChanged?.invoke(translationX, translationY)
                }
                .start()
        } else {
            translationX = x
            translationY = y
            onPositionChanged?.invoke(translationX, translationY)
        }
    }
}

/**
 * 便捷方法：把悬浮球添加到 Activity 的内容根布局。
 * 可指定初始位置与尺寸。
 */
fun FloatingBallLayout.attachToActivity(
    activity: Activity,
    sizeDp: Int = 44,
    startFromRight: Boolean = true,
    marginDp: Int = 16
): FloatingBallLayout {
    val root = activity.findViewById<ViewGroup>(R.id.content)
    val sizePx = (sizeDp * activity.resources.displayMetrics.density + 0.5f).toInt()
    val lp = FrameLayout.LayoutParams(sizePx, sizePx)
    root.addView(this, lp)

    // 初始位置（先 post 等待拿到父宽高）
    post {
        val m = (marginDp * resources.displayMetrics.density + 0.5f).toInt()
        val parentW = (parent as View).width
        val parentH = (parent as View).height
        translationX = if (startFromRight) {
            (parentW - width - m).toFloat()
        } else {
            m.toFloat()
        }
        translationY = (parentH * 0.4f).coerceAtLeast(m.toFloat())
    }
    return this
}
