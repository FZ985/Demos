package com.demos.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demos.databinding.ActivitySportBallBinding


/**
 * by JFZ
 * 2025/5/22
 * desc：
 **/
class SportBallActivity : AppCompatActivity() {

    private val binding: ActivitySportBallBinding by lazy {
        ActivitySportBallBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.ball.post {
            binding.ball.setCollidableViews(binding.v1, binding.v2)
        }
    }
}