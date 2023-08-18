package com.demos.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demos.databinding.ActivityAppbarLayoutBinding


/**
 *  author : JFZ
 *  date : 2023/8/18 09:56
 *  description :
 */
class AppbarLayoutActivity : AppCompatActivity() {

    private val binding: ActivityAppbarLayoutBinding by lazy {
        ActivityAppbarLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}