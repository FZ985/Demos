package com.demos.luck4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.demos.R;

/**
 * author : JFZ
 * date : 2023/10/21 08:29
 * description :
 */
public class LotteryPanLayout extends FrameLayout {

    private String[] data = {"哈哈哈00", "哈哈哈11", "哈哈哈22", "哈哈哈33", "哈哈哈44", "哈哈哈55", "哈哈哈66", "哈哈哈77", "哈哈哈88", "哈哈哈99"};

    private int totalNum;

    private float itemAngle;
    private float mCurrentAngle = 0;

    private float percent;

    private Size screen;

    private int mCenterX;
    private int mCenterY;
    private float mStartX;
    private float mStartY;
    private int mRadius;

    public LotteryPanLayout(Context context) {
        this(context, null);
    }

    public LotteryPanLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("Recycle")
    public LotteryPanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.LotteryPanLayout);
        percent = type.getFloat(R.styleable.LotteryPanLayout_lottery_pan_size, 0.75f);
        type.recycle();
        int min = Math.min(width, height);
        int size = (int) (min * percent);
        screen = new Size(size, size);
//        setBackgroundColor(Color.TRANSPARENT);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screen.getWidth(), screen.getHeight());
    }

    private void init() {
        totalNum = data.length;
        itemAngle = (float) 360 / (float) totalNum;
        mRadius = screen.getWidth() / 2;

        mCenterX = screen.getWidth() / 2;
        mCenterY = mCenterX;

    }

    public void bindData() {
        removeAllViews();

        LotteryPanBackgroundView background = new LotteryPanBackgroundView(getContext(), mCurrentAngle, itemAngle, totalNum, screen);
        background.setLayoutParams(new FrameLayout.LayoutParams(screen.getWidth(), screen.getHeight()));
        addView(background);

        //绘制图片开始的角度位置
        float radian = mCurrentAngle + itemAngle / (float) 2;

        //使图像的宽度的一半为半径的1/7
        float imageOffset = (float) mRadius / (float) 7;

        for (int i = 0; i < totalNum; i++) {
            TextView text = new TextView(getContext());
            text.setText(data[i]);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            text.setRotation(radian + 90);
            text.setTextColor(Color.WHITE);
            text.setTextSize(13);
            params.gravity = Gravity.CENTER;
            text.setLayoutParams(params);

            //计算图片中心位置的坐标
            float x = (float) ((float) mRadius * (float) 0.85 * Math.cos(Math.toRadians(radian)));
            float y = (float) ((float) mRadius * (float) 0.85 * Math.sin(Math.toRadians(radian)));

            text.setX(x);
            text.setY(y);

            addView(text);

            ImageView image = new ImageView(getContext());
            image.setImageResource(R.mipmap.lucky2_iphone);

            float imageX = (float) ((float) mRadius * (float) 0.5 * Math.cos(Math.toRadians(radian)));
            float imageY = (float) ((float) mRadius * (float) 0.5 * Math.sin(Math.toRadians(radian)));

            RectF imageRect = new RectF(imageX - imageOffset, imageY - imageOffset, imageX + imageOffset, imageY + imageOffset);

            FrameLayout.LayoutParams imageLp = new FrameLayout.LayoutParams((int) imageRect.width(), (int) imageRect.height());
            image.setRotation(radian + 90);
            imageLp.gravity = Gravity.CENTER;
            image.setLayoutParams(imageLp);

            image.setX(imageX);
            image.setY(imageY);

            addView(image);

            radian = radian + itemAngle;
        }

    }


    /**
     * 转盘停止后停在某个item的某个比例的位置
     */
    private float mRandomPositionPro = (float) 0.2;
    /**
     * 是否正在抽奖
     */
    private boolean isDrawingLottery = false;

    private long mDuration = 6000;

    public void setScrollToPosition(final int position) {
        if (!isDrawingLottery) {
            mRandomPositionPro = getRandomPositionPro();
            //计算转动到position位置停止后的角度值
            float entAngle = 270 - itemAngle * ((float) position + mRandomPositionPro);
            entAngle = entAngle + 17 * 360;
            ValueAnimator animator = ValueAnimator.ofFloat(mCurrentAngle, entAngle);
            animator.setDuration(mDuration);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    startRotate(animatedValue);
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    isDrawingLottery = true;
//                if (listener != null) {
//                    listener.onStart();
//                }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isDrawingLottery = false;
//                if (listener != null) {
//                    listener.onEnd(position, mNamesStrs.get(position));
//                }
                }
            });
            animator.start();
        }
    }

    private void startRotate(float rotation) {
        //控制mCurrentAngle在0到360之间
        mCurrentAngle = (rotation % 360 + 360) % 360;
//        setRotation(mCurrentAngle);
        ViewCompat.setRotation(this,mCurrentAngle);
    }


    /**
     * 转盘滚动终点随机停止的位置
     *
     * @return
     */
    public float getRandomPositionPro() {
        float num = (float) Math.random();
        if (num > 0 && num < 1) {
            return num;
        } else {
            return (float) 0.5;
        }
    }


    private void log(String m) {
        Log.e("LotteryPanLayout", m);
    }
}
