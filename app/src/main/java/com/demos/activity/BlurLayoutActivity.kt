package com.demos.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible
import com.demos.databinding.ActivityBlurLayoutBinding


/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
class BlurLayoutActivity : AppCompatActivity() {

    private val binding: ActivityBlurLayoutBinding by lazy {
        ActivityBlurLayoutBinding.inflate(layoutInflater)
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.image_01)
//        val radius = 40f
//        BlurUtil.blurBitmap(this, bitmap, radius, .2f)?.let {
//            binding.image3.setImageBitmap(it)
//        }
//        BlurUtil.oldBlurBitmap(this, BlurUtil.scaleFactorBitmap(bitmap, .2f), radius).let {
//            binding.image2.setImageBitmap(it.scale(bitmap.width, bitmap.height, true))
//        }

        binding.ll.showLayer()

        binding.show.setOnClickListener {
            binding.ll.hideLayer()
        }

        binding.blur.setOnClickListener {
            binding.ll.showLayer()
        }

        binding.view.setOnClickListener {
            binding.view.isVisible = false
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun copyViewToBitmap(activity: Activity, view: View, callback: (Bitmap?) -> Unit) {
        // 获取目标 view 在屏幕上的位置
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val x = location[0]
        val y = location[1]
        val width = view.width
        val height = view.height

        if (width == 0 || height == 0) {
            callback(null)
            return
        }

        // 创建 bitmap 缓冲区
        val bitmap = createBitmap(width, height)

        // 设置 PixelCopy 范围
        val rect = Rect(x, y, x + width, y + height)

        // 调用 PixelCopy 复制像素
        PixelCopy.request(
            activity.window,
            rect,
            bitmap,
            { result ->
                if (result == PixelCopy.SUCCESS) {
                    callback(bitmap)
                } else {
                    callback(null)
                }
            },
            Handler(Looper.getMainLooper())
        )
    }


}