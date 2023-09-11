package com.demos.password

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demos.databinding.ActivityPasswordBinding


/**
 *  author : JFZ
 *  date : 2023/9/11 10:10
 *  description :
 */
class PasswordActivity : AppCompatActivity() {

    private val binding: ActivityPasswordBinding by lazy {
        ActivityPasswordBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}