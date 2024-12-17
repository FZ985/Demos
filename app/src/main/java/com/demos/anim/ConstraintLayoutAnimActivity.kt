package com.demos.anim

import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.demos.R
import com.demos.databinding.ActivityAnimConstraintLayoutBinding
import com.demos.dp


/**
 * by JFZ
 * 2024/10/26
 * desc：
 **/
class ConstraintLayoutAnimActivity : AppCompatActivity() {

    private var contentAreaHeight = 0;

    private val binding: ActivityAnimConstraintLayoutBinding by lazy {
        ActivityAnimConstraintLayoutBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.setDecorFitsSystemWindows(false)
        val controller = window.insetsController
        if (controller != null) {
            controller.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        binding.cl.post {
            contentAreaHeight = binding.cl.height
        }

        initData()
    }

    private fun initData() {
        binding.v1.setOnClickListener {
            showAnim1()
        }

        binding.v2.setOnClickListener {
            showAnim2()
        }

        binding.v3.setOnClickListener {
            showAnim3()
        }
    }


    private var anim1 = true
    private fun showAnim1() {
        if (anim1) {
            anim1 = false

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.cl)
            constraintSet.connect(
                R.id.v1,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )

            constraintSet.constrainHeight(R.id.v1, contentAreaHeight)

            TransitionManager.beginDelayedTransition(binding.cl)
            constraintSet.applyTo(binding.cl)

            binding.v2.isVisible = false
            binding.v3.isVisible = false

        } else {
            anim1 = true

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.cl)
            constraintSet.connect(R.id.v1, ConstraintSet.RIGHT, R.id.v2, ConstraintSet.LEFT)

            constraintSet.constrainHeight(R.id.v1, 200.dp)

            TransitionManager.beginDelayedTransition(binding.cl)
            constraintSet.applyTo(binding.cl)

            binding.v2.isVisible = true
            binding.v3.isVisible = true
        }
    }


    private var anim2 = true

    private fun showAnim2() {
        if (anim2) {
            anim2 = false

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.cl)

            constraintSet.connect(
                R.id.v2,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )

            constraintSet.connect(
                R.id.v2,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )

            constraintSet.constrainHeight(R.id.v2, contentAreaHeight)

            TransitionManager.beginDelayedTransition(binding.cl)
            constraintSet.applyTo(binding.cl)

            binding.v1.isVisible = false
            binding.v3.isVisible = false
        } else {
            anim2 = true

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.cl)

            constraintSet.connect(
                R.id.v2,
                ConstraintSet.LEFT,
                R.id.v1,
                ConstraintSet.RIGHT
            )

            constraintSet.connect(
                R.id.v2,
                ConstraintSet.RIGHT,
                R.id.v3,
                ConstraintSet.LEFT
            )
            constraintSet.constrainHeight(R.id.v2, 200.dp)

            TransitionManager.beginDelayedTransition(binding.cl)
            constraintSet.applyTo(binding.cl)

            binding.v1.isVisible = true
            binding.v3.isVisible = true
        }
    }


    private var anim3 = true

    private fun showAnim3() {
        if (anim3) {
            anim3 = false

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.cl)

            constraintSet.connect(
                R.id.v3,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )

            constraintSet.constrainHeight(R.id.v3, contentAreaHeight)

            TransitionManager.beginDelayedTransition(binding.cl)
            constraintSet.applyTo(binding.cl)

            binding.v1.isVisible = false
            binding.v2.isVisible = false
        } else {
            anim3 = true

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.cl)

            constraintSet.connect(
                R.id.v3,
                ConstraintSet.LEFT,
                R.id.v2,
                ConstraintSet.RIGHT
            )

            constraintSet.constrainHeight(R.id.v3, 200.dp)

            TransitionManager.beginDelayedTransition(binding.cl)
            constraintSet.applyTo(binding.cl)

            binding.v1.isVisible = true
            binding.v2.isVisible = true
        }
    }

}