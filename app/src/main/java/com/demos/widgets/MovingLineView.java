package com.demos.widgets;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MovingLineView extends View {

    private static final String TAG = "MovingLineView"; // 用于日志

    private Paint circlePaint;
    private Paint linePaint;

    private float circleX;
    private float circleY;
    private float circleRadius = 50f; // 球的半径，可以根据需要调整

    private float anchorX; // 锚点X坐标 (相对于本View的左上角)
    private float anchorY; // 锚点Y坐标 (相对于本View的左上角)

    // 速度分量
    private float velocityX;
    private float velocityY;
    private float maxSpeed = 8f; // 球的最大移动速度 (像素/帧)

    private Random random;
    private Handler handler;
    private Runnable movementRunnable;

    private long updateInterval = 16; // 动画更新间隔，约60FPS (1000ms / 60 ≈ 16.6ms)

    // 用于实现“不规则”运动：周期性地改变球的整体方向
    private long directionChangeInterval = 2500; // 每2.5秒改变一次主方向
    private long lastDirectionChangeTime = 0;

    public MovingLineView(Context context) {
        super(context);
        init();
    }

    public MovingLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovingLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#800080")); // 紫色
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.RED); // 红色连线
        linePaint.setStrokeWidth(5f); // 连线粗细
        linePaint.setAntiAlias(true);

        random = new Random();
        handler = new Handler(Looper.getMainLooper());

        // 初始化球的初始速度和方向
        resetRandomVelocity();

        // 初始位置，将在onSizeChanged中根据View实际大小设置
        circleX = 0;
        circleY = 0;

        // 设置动画更新逻辑
        setupMovementRunnable();
    }

    /**
     * 设置锚点坐标。这些坐标应相对于 MovingLineView 的左上角。
     *
     * @param x 锚点X坐标
     * @param y 锚点Y坐标
     */
    public void setAnchorPoint(float x, float y) {
        this.anchorX = x;
        this.anchorY = y;
        invalidate(); // 锚点改变时重新绘制
        Log.d(TAG, "Anchor point set to: (" + anchorX + ", " + anchorY + ")");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 第一次 View 尺寸确定时，将球放置在 View 的某个位置 (例如左上角四分之一处)
        if (circleX == 0 && circleY == 0 && w > 0 && h > 0) {
            circleX = w / 4f;
            circleY = h / 2f;
            Log.d(TAG, "Initial circle position set to: (" + circleX + ", " + circleY + ") in view size (" + w + ", " + h + ")");
        }
    }

    /**
     * 生成新的随机速度向量，用于球的“不规则”运动。
     */
    private void resetRandomVelocity() {
        // 生成一个随机角度 (0到2PI)
        double angle = random.nextDouble() * 2 * Math.PI;
        // 速度在 maxSpeed 的 50% 到 100% 之间
        float speed = random.nextFloat() * maxSpeed * 0.5f + maxSpeed * 0.5f;

        velocityX = (float) (Math.cos(angle) * speed);
        velocityY = (float) (Math.sin(angle) * speed);

        Log.d(TAG, "New random velocity: (" + velocityX + ", " + velocityY + ")");
    }

    private void setupMovementRunnable() {
        movementRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                // 周期性地改变球的整体方向，制造“不规则”感
                if (currentTime - lastDirectionChangeTime > directionChangeInterval) {
                    resetRandomVelocity();
                    lastDirectionChangeTime = currentTime;
                }

                // 计算球的下一个可能位置
                float nextCircleX = circleX + velocityX;
                float nextCircleY = circleY + velocityY;

                int width = getWidth();
                int height = getHeight();

                // 边界碰撞检测和处理
                // 检查 View 的宽度和高度是否有效 (避免在 View 未布局时进行计算)
                if (width <= 0 || height <= 0) {
                    Log.w(TAG, "View size is not valid yet: (" + width + ", " + height + ")");
                    handler.postDelayed(this, updateInterval); // 延迟重试
                    return;
                }

                // 检查左右边界
                if (nextCircleX - circleRadius < 0) { // 撞到左边界
                    circleX = circleRadius; // 将球中心放在边界内
                    velocityX *= -1; // 反转X方向速度
                    resetRandomVelocity(); // 碰撞后略微随机化方向，增加不规则性
                    Log.d(TAG, "Hit Left! New VelX: " + velocityX);
                } else if (nextCircleX + circleRadius > width) { // 撞到右边界
                    circleX = width - circleRadius; // 将球中心放在边界内
                    velocityX *= -1; // 反转X方向速度
                    resetRandomVelocity(); // 碰撞后略微随机化方向
                    Log.d(TAG, "Hit Right! New VelX: " + velocityX);
                } else {
                    circleX = nextCircleX; // 没有碰撞，更新X位置
                }

                // 检查上下边界
                if (nextCircleY - circleRadius < 0) { // 撞到上边界
                    circleY = circleRadius; // 将球中心放在边界内
                    velocityY *= -1; // 反转Y方向速度
                    resetRandomVelocity(); // 碰撞后略微随机化方向
                    Log.d(TAG, "Hit Top! New VelY: " + velocityY);
                } else if (nextCircleY + circleRadius > height) { // 撞到下边界
                    circleY = height - circleRadius; // 将球中心放在边界内
                    velocityY *= -1; // 反转Y方向速度
                    resetRandomVelocity(); // 碰撞后略微随机化方向
                    Log.d(TAG, "Hit Bottom! New VelY: " + velocityY);
                } else {
                    circleY = nextCircleY; // 没有碰撞，更新Y位置
                }

                // 请求重绘 View
                invalidate();
                // 安排下一次更新
                handler.postDelayed(this, updateInterval);
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制连接线
        canvas.drawLine(circleX, circleY, anchorX, anchorY, linePaint);

        // 绘制紫色的球
        canvas.drawCircle(circleX, circleY, circleRadius, circlePaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startMovement();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMovement();
    }

    /**
     * 开始球的运动动画。
     */
    public void startMovement() {
        // 重置上次方向改变时间，确保动画开始时立即随机化方向
        lastDirectionChangeTime = System.currentTimeMillis();
        handler.post(movementRunnable);
        Log.d(TAG, "Movement started.");
    }

    /**
     * 停止球的运动动画。
     */
    public void stopMovement() {
        handler.removeCallbacks(movementRunnable);
        Log.d(TAG, "Movement stopped.");
    }
}