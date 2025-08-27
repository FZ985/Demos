package com.demos.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demos.R
import com.demos.databinding.ActivityFloatBallBinding
import com.demos.widgets.floatball.FloatingBallLayout
import com.demos.widgets.floatball.attachToActivity


/**
 * by DAD ZZ
 * 2025/8/27
 * desc：
 **/
class FloatBallActivity : AppCompatActivity() {

    private val binding: ActivityFloatBallBinding by lazy {
        ActivityFloatBallBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.showBall.setOnClickListener {
            val ball = FloatingBallLayout(this).apply {
                // 可往里加任意子 View（图标、菜单等），点击事件都能正常触发
                val icon = ImageView(context).apply {
                    setImageResource(R.mipmap.ic_launcher_round)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setOnClickListener {
                        Toast.makeText(this@FloatBallActivity, "子View被点击", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                addView(
                    icon, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )

                // 可选配置
                edgeMarginPx = (12 * resources.displayMetrics.density).toInt()
                reboundDuration = 280L
                reboundTension = 1.35f
                stickToHorizontalEdgesOnly = true  // 改成 false 可贴四边
            }

            ball.attachToActivity(
                activity = this,
                sizeDp = 44,
                startFromRight = true,
                marginDp = 16
            )
        }
    }
}