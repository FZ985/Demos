package com.demos.beziertest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demos.databinding.ZzTestBezierBinding


/**
 *  author : JFZ
 *  date : 2023/8/21 09:54
 *  description :
 */
class TestBezierActivity : AppCompatActivity() {

    private val binding: ZzTestBezierBinding by lazy {
        ZzTestBezierBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.click.setOnClickListener {
            binding.bv.start()
        }
    }
}