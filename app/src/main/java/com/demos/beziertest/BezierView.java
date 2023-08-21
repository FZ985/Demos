package com.demos.beziertest;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author : JFZ
 * date : 2023/8/21 09:59
 * description :
 */
public class BezierView extends View {

    public BezierView(Context context) {
        super(context);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
    }

    private final Path path = new Path();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private final List<Bv> points = new ArrayList<>();

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Bv b : points) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.BLACK);
            path.reset();
            path.moveTo(b.startX, b.startY);
            path.cubicTo(b.controlX1, b.controlY1, b.controlX2, b.controlY2, b.endX, b.endY);
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GREEN);
            canvas.drawCircle(b.cirX, b.cirY, 15, paint);
        }
    }

    Random random = new Random();

    private void initAnimation() {
        for (int i = 0; i < points.size(); i++) {
            Bv b = points.get(i);
            //创建贝塞尔曲线坐标的换算类                                 //x1y1是控制点1      x2,y2是控制点2
            BezierEvaluator evaluator = new BezierEvaluator(new PointF(b.controlX1, b.controlY1), new PointF(b.controlX2, b.controlY2));
            //指定动画移动轨迹
            ValueAnimator animator = ValueAnimator.ofObject(evaluator,
                    new PointF(b.startX, b.startY),//起始点
                    new PointF(b.endX, b.endY));//结束点
            animator.setDuration(1000);
            int finalI = i;
            animator.addUpdateListener(valueAnimator -> {
                //改变小球坐标，产生运动效果
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();
                /**
                 * mMovePointX 是最终贝塞尔曲线x坐标
                 * mMovePointY是最终贝塞尔曲线y坐标
                 */
                points.get(finalI).cirX = pointF.x;
                points.get(finalI).cirY = pointF.y;
                //刷新UI
                invalidate();
            });
            //添加加速插值器，模拟真实物理效果
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }


    public void start() {
        points.clear();
        float startX = 100;
        float startY = 500;
        float endX = 900;
        float endY = 500;

        for (int i = 0; i < 20; i++) {

            startY = random.nextInt(1000) + 200;
            endY = random.nextInt(1000) + 200;

            float controlX1 = random.nextInt(800) + 100;
            float controlY1 = random.nextInt(500) + 100;
            float controlX2 = random.nextInt(800) + 100;
            float controlY2 = random.nextInt(500) + 100;

            points.add(new Bv(startX, startY, endX, endY, controlX1, controlY1, controlX2, controlY2));
        }
        initAnimation();
    }


    public static class Bv {
        public float startX;
        public float startY;
        public float endX;
        public float endY;
        public float controlX1;
        public float controlY1;
        public float controlX2;
        public float controlY2;

        public float cirX;
        public float cirY;

        public Bv(float startX, float startY, float endX, float endY, float controlX1, float controlY1, float controlX2, float controlY2) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.controlX1 = controlX1;
            this.controlY1 = controlY1;
            this.controlX2 = controlX2;
            this.controlY2 = controlY2;
        }
    }


}
