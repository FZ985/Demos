package com.demos.activity

import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import com.demos.databinding.ActivityGradientColorBinding
import com.demos.widgets.GradientColorBlock


/**
 * by JFZ
 * 2025/6/6
 * desc：
 **/
class GradientColorActivity : BaseActivity() {

    private val binding: ActivityGradientColorBinding by lazy {
        ActivityGradientColorBinding.inflate(layoutInflater)
    }

    val option = GradientColorBlock.BlockOptions()


    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
        binding.block.setOptions(option)
        binding.blockPercent.setOptions(option)

        binding.block.setLevel(binding.level.progress)
        binding.level.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.block.setLevel(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.blockPercent.setPercent(binding.percent.progress.toFloat())
        binding.percent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.blockPercent.setPercent(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.seekRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                updateOption()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.seekBlock.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                updateOption()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.seekSpace.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                updateOption()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        updateOption()
    }

    private fun updateOption() {
        option.radius = binding.seekRadius.progress.toFloat()
        option.blockBackgroundColor = ColorUtils.setAlphaComponent(
            Color.GRAY,
            (255 * (binding.seekBlock.progress.toFloat() / 100f)).toInt()
        )
        option.spacing = binding.seekSpace.progress
        binding.block.setOptions(option)
        binding.blockPercent.setOptions(option)
    }

}