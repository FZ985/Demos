package com.demos.ijkplayer

import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.demos.databinding.ActivityIjkplayerBinding
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils


/**
 * by JFZ
 * 2025/4/10
 * desc：
 **/
class IJKPlayerActivity : AppCompatActivity() {

    private val binding: ActivityIjkplayerBinding by lazy {
        ActivityIjkplayerBinding.inflate(layoutInflater)
    }

    private val src =
        "https://mv6.music.tc.qq.com/6E452216552BCF6BCFA1E925921BC65A19B6884168C1C0F58A0E6135C5688C6364C17828E2340FE4E25904B49AA52229ZZqqmusic_default__v21ea0f986/qmmv_0b5324aakaaadiakx7l6yfrvjvyaaxlqabka.f9835.mp4?fname=qmmv_0b5324aakaaadiakx7l6yfrvjvyaaxlqabka.f9835.mp4"
//    private val src =
//        "https://mv6.music.tc.qq.com/F1C0765FB7DAB7BA30EB4BABE1D0620F7C904E7C5B769D6DB936921F1781168C356492B4F652F2EF9432670DBA11C24CZZqqmusic_default__v215192fdc/qmmv_0b53w4a2aaabyeam3qxw5vtvlnyauc3qdica.f9845.mp4?fname=qmmv_0b53w4a2aaabyeam3qxw5vtvlnyauc3qdica.f9845.mp4"

    private val orientationUtils: OrientationUtils by lazy {
        OrientationUtils(this, binding.empty)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

//        binding.rg.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.rb1 -> binding.video.setIJKScaleType(NoneType())
//                R.id.rb2 -> binding.video.setIJKScaleType(CenterCropType())
//                R.id.rb3 -> binding.video.setIJKScaleType(AutoWrapType())
//            }
//        }
//        binding.rg.check(R.id.rb1)
//        binding.video.bindLifecycle(this)
//        binding.video.setDataSource(src)
//
        binding.start.setOnClickListener {
//            binding.video.starAfterPrepared()
            binding.empty.setUp(src, false, "")
            binding.empty.startPlayLogic()
        }
        orientationUtils.isEnable = false


        binding.fullscreen.setOnClickListener {
//            orientationUtils.resolveByClick()
//            binding.empty.startWindowFullscreen(this, true, true)
            binding.empty.showSmallVideo(Point(400, 300), true, true)

            binding.empty.currentState
        }


//
//        binding.play.setOnClickListener {
//            binding.video.play()
//        }
//
//        binding.stop.setOnClickListener {
//            binding.video.stop()
//        }
//
//        binding.pause.setOnClickListener {
//            binding.video.pause()
//        }

    }

//    override fun onBackPressed() {
//        if (!binding.video.onBackPressed()) finish()
//    }

    override fun onBackPressed() {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)


        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        orientationUtils.backToProtVideo()

        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e("onConfigurationChanged", newConfig.toString())
    }

}