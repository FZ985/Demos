package com.demos.zztest;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demos.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MarqueeView extends HorizontalScrollView {

    private TextView mTextView;
    private TextView mGhostTextView;

    private int viewWidth;

    private CharSequence mText;
    private int measureText;
    private int textColor = 0xff000000;

    private int mOffset = 0;
    private int mGhostOffset = 0;

    /**
     * 间隔
     */
    private int spacing = 100;

    /**
     * 移动速度
     */
    private int speed = 1;

    private ValueAnimator valueAnimator;
    private int textSize = 14;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initLayout();
        initAnim();
    }

    @SuppressLint("Recycle")
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);
        textColor = typedArray.getColor(R.styleable.MarqueeView_viewtextColor, textColor);
        if (typedArray.hasValue(R.styleable.MarqueeView_viewtextSize)) {
            textSize = (int) typedArray.getDimension(R.styleable.MarqueeView_viewtextSize, textSize);
            textSize = px2sp(context, textSize);
        }

        if (typedArray.hasValue(R.styleable.MarqueeView_spacing)) {
            spacing = (int) typedArray.getDimension(R.styleable.MarqueeView_spacing, spacing);
        }

        if (typedArray.hasValue(R.styleable.MarqueeView_speed)) {
            speed = (int) typedArray.getInteger(R.styleable.MarqueeView_speed, 2);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (measureText > viewWidth) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    private void initLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        addView(relativeLayout);

        mTextView = createTextView();
        mGhostTextView = createTextView();

        relativeLayout.addView(mTextView);
        relativeLayout.addView(mGhostTextView);
    }

    private void initAnim() {
        valueAnimator = ValueAnimator.ofFloat(0, measureText);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    protected void onDetachedFromWindow() {
        valueAnimator.removeAllUpdateListeners();
        valueAnimator.cancel();
        super.onDetachedFromWindow();
        Log.e("sjl", "onDetachedFromWindow: 跑马灯销毁");
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setText(CharSequence text) {
        this.mText = text;
//        buildMsgText(mTextView);
//        buildMsgText(mGhostTextView);
        mTextView.setText(mText);
        mGhostTextView.setText(mText);

        measureText = (int) mTextView.getPaint().measureText(mText, 0, mText.length());
        resetMarqueeView();
        if (measureText > viewWidth) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    public void setText(CharSequence text, int aTextColor) {
        this.mText = text;
        textColor = aTextColor;
//        buildMsgText(mTextView);
//        buildMsgText(mGhostTextView);
        mTextView.setText(mText);
        mTextView.setTextColor(aTextColor);
        mGhostTextView.setText(mText);
        mGhostTextView.setTextColor(aTextColor);
        measureText = (int) mTextView.getPaint().measureText(mText, 0, mText.length());
        resetMarqueeView();
        if (measureText > viewWidth) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setPadding(0, 0, 0, 0);
        textView.setSingleLine();
        textView.setTextColor(textColor);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return textView;
    }

    private void resetMarqueeView() {
        mOffset = 0;
        mGhostOffset = measureText + spacing;
        mGhostTextView.setX(mGhostOffset);
        invalidate();
    }

    public void startAnim() {
        valueAnimator.setDuration((long) measureText);
        stopAnim();
        valueAnimator.start();
    }

    public void stopAnim() {
        valueAnimator.cancel();
        resetMarqueeView();
    }


    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mOffset -= speed;
            mGhostOffset -= speed;
            if (mOffset + measureText < 0) {
                mOffset = mGhostOffset + measureText + spacing;
            }
            if (mGhostOffset + measureText < 0) {
                mGhostOffset = mOffset + measureText + spacing;
            }
            invalidate();
        }
    };


    // 将px值转换为sp值
    private int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextView == null || mGhostTextView == null) {
            return;
        }
        mTextView.setX(mOffset);
        mGhostTextView.setX(mGhostOffset);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    private String xmlMsgToStr(String xmlMsg) {
        String PATTEN_STR1 = "<span id=\"title\">.+?</span>|<span id=\"name\">.+?</span>|<p>|</p>|<TextFlow .+?>|</TextFlow>";
        String titelStr = "";
        String nameStr = "";
        InputStream inStream = new ByteArrayInputStream(xmlMsg.getBytes());
        XmlPullParser pullParser = Xml.newPullParser();
        try {
            pullParser.setInput(inStream, "UTF-8");
            int event = pullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        if ("span".equals(pullParser.getName())) {
                            if (pullParser.getAttributeValue(0).equals("title")) {
                                titelStr = pullParser.nextText() + " ";
                            } else if (pullParser.getAttributeValue(0).equals("name")) {
                                nameStr = pullParser.nextText();
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                }
                event = pullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern pattern = Pattern.compile(PATTEN_STR1);
        Matcher matcher = pattern.matcher(xmlMsg);
        while (matcher.find()) {
            xmlMsg = xmlMsg.replace(matcher.group(), "");
        }
        return titelStr + nameStr + xmlMsg;
    }

    private Spanned fromHtml(String source) {
        return Html.fromHtml(xmlMsgToStr(source));
    }

    private void buildMsgText(TextView textView) {
        textView.setText(mText);
        CharSequence str = textView.getText();

        if (str instanceof Spannable) {
            int end = str.length();
            Spannable sp = (Spannable) str;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            spannableStringBuilder.clearSpans();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, mText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFB00")), mText.length(), titelStr.length() + nameStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            for (URLSpan url : urls) {
                String jumpUrl = url.getURL();
                MarqueeLayoutUrlClickSpan myURLSpan = new MarqueeLayoutUrlClickSpan(jumpUrl);
                spannableStringBuilder.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(spannableStringBuilder);
        }
    }

    private static class MarqueeLayoutUrlClickSpan extends ClickableSpan {
        private final String url;

        public MarqueeLayoutUrlClickSpan(String url) {
            this.url = url;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            //设置超链接字体颜色
            ds.linkColor = Color.parseColor("#FFFFFF");
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget) {

        }
    }
}