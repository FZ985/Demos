package com.demos.span

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.RequestOptions
import com.demos.R
import com.demos.databinding.ActivitySpanBinding
import com.demos.dp
import com.demos.getDrawableCompat
import com.demos.span.core.Span
import com.demos.span.impl.AnimatedColorSpan
import com.demos.span.impl.BlurSpan
import com.demos.span.impl.GlideSpannable
import com.demos.span.impl.ImageSpannable
import com.demos.span.impl.RainbowSpan
import com.demos.span.impl.ShortLabelSpannable


/**
 *  author : JFZ
 *  date : 2023/8/1 17:34
 *  description :
 */
class SpanActivity : AppCompatActivity() {
    private val binding: ActivitySpanBinding by lazy {
        ActivitySpanBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ddd.text =
            "1234567890好好好abcdefgopqrsty1234567890好好好abcdefgopqrsty1234567890好好好abcdefgopqrsty"

        val d = getDrawableCompat(R.mipmap.ic_launcher_round)
        d?.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        val imageSpan = ImageSpan(d!!, ImageSpan.ALIGN_BOTTOM)

        Span.with()
            .add("哈哈哈哈".backgroundColor(Color.BLUE).textColor(Color.WHITE))
            .add("\u2000".addSpan(imageSpan))
            .add("《百度》".addSpan(URLSpan("https://www.baidu.com")))
            .add("点击点击".click { _, t -> Toast.makeText(this, t, Toast.LENGTH_SHORT).show() }
                .textColor(Color.RED)
                .textSize(20))
            .add("倾斜".textStyle(Typeface.ITALIC))
            .add("\u2000加粗".textStyle(Typeface.BOLD))
            .add(ShortLabelSpannable(this, R.color.purple_500, "呕呕").leftMargin(5f).build())
            .add(
                GlideSpannable(binding.tv1, R.drawable.ic_gif)
                    .setRequestOption(RequestOptions.centerCropTransform())
                    .setDrawableSize(50.dp, 50.dp).build()
            )
            .add("啊啊".deleteLine().textColor(Color.BLACK))
            .add("哦哦哦哦哦哦".underLine())
            .add("\nCCCCCCCC".quoteLine(Color.BLUE, 15, 5))
            .add(
                ImageSpannable(this, "图片加文字", R.drawable.bg_date_label)
                    .setDrawableSize(-1, -1)
                    .setMarginHorizontal(15, 15)
                    .setTextVisibility(true).build().textColor(Color.BLUE)
            )
            .add(
                GlideSpannable(
                    binding.tv1,
                    "https://img2.baidu.com/it/u=3681172266,4264167375&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1690995600&t=1cff4e7d456c4118076598b7c03fe190"
                )
                    .setRequestOption(RequestOptions.circleCropTransform())
                    .setText("网络")
                    .setTextVisibility(true)
                    .setDrawableSize(50.dp, 50.dp).build()
                    .textColor(Color.GREEN)
            )
            .add(
                "包丰富多彩的包丰富多彩的包丰富多彩的包丰富多彩的".addSpan(
                    RainbowSpan(
                        Color.BLUE,
                        Color.RED
                    )
                )
            )
            .add("我是模糊的内容我是模糊的内容我是模糊的内容我是模糊的内容".addSpan(BlurSpan(15f)))
            .add(
                "动起来动起来".addSpan(
                    AnimatedColorSpan(
                        this,
                        binding.tv1,
                        true,
                        Color.BLUE,
                        Color.RED,
                        Color.YELLOW,
                        Color.GREEN,
                        Color.CYAN
                    )
                )
            )
            //总点击事件
            .totalClickListener { _, t -> Toast.makeText(this, t, Toast.LENGTH_SHORT).show() }
            .into(binding.tv1)


    }


}