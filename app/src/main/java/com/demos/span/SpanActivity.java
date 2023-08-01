package com.demos.span;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.request.RequestOptions;
import com.demos.R;
import com.demos.databinding.ActivitySpanBinding;
import com.demos.span.core.Span;
import com.demos.span.impl.GlideSpannable;
import com.demos.span.impl.ImageSpannable;
import com.demos.span.impl.ShortLabelSpannable;

/**
 * author : JFZ
 * date : 2023/7/31 09:19
 * description : span
 */
public class SpanActivity extends AppCompatActivity {
    private ActivitySpanBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Drawable d = ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        int dp50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, getResources().getDisplayMetrics());

        Span.with()
                .add(Span.build("哈哈哈哈").backgroundColor(Color.BLUE).textColor(Color.WHITE))

                .add(Span.build("\u2000").addSpan(imageSpan))

                .add(Span.build("《隐私政策》").addSpan(new URLSpan("https://www.baidu.com")))

                .add(Span.build("点击点击").click((v, t) -> {
                    Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
                }).textColor(Color.RED).textSize(20))

                .add(Span.build("倾斜").textStyle(Typeface.ITALIC))

                .add(Span.build("\u2000加粗").textStyle(Typeface.BOLD))

                .add(Span.build(new ShortLabelSpannable(this, R.color.purple_500, "呕呕").leftMargin(5)))

                .add(Span.build(new GlideSpannable(binding.tv1, R.drawable.ic_gif)
                        .setRequestOption(RequestOptions.centerCropTransform())
                        .setDrawableSize(dp50, dp50)))

                .add(Span.build("啊啊啊啊啊啊啊啊啊啊啊啊").deleteLine().textColor(Color.BLACK))

                .add(Span.build("哦哦哦哦哦哦").underLine())

                .add(Span.build("\nCCCCCCCC").quoteLine(Color.BLUE, 15, 5))

                .add(Span.build(new ImageSpannable(this, "图片加文字", R.drawable.bg_date_label)
                        .setDrawableSize(-1, -1)
                        .setMarginHorizontal(15, 15)
                        .setTextVisibility(true)).textColor(Color.BLUE))

                .add(Span.build(new GlideSpannable(binding.tv1, "https://img2.baidu.com/it/u=3681172266,4264167375&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1690995600&t=1cff4e7d456c4118076598b7c03fe190")
                        .setRequestOption(RequestOptions.circleCropTransform())
                        .setText("网络")
                        .setDrawableSize(dp50, dp50)).textColor(Color.GREEN))

                //总点击事件
                .totalClickListener((v, t) -> {
                    Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
                })
                .into(binding.tv1);
    }
}
