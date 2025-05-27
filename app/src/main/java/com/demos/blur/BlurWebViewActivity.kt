package com.demos.blur

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.demos.blur.render.BlurConfig
import com.demos.blur.render.CompatBlurRender
import com.demos.databinding.ActivityBlurWebviewBinding

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
class BlurWebViewActivity : AppCompatActivity() {

    private val binding: ActivityBlurWebviewBinding by lazy {
        ActivityBlurWebviewBinding.inflate(layoutInflater)
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.web.settings.javaScriptEnabled = true
        binding.web.settings.domStorageEnabled = true
        binding.web.settings.blockNetworkImage = false
        binding.web.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return true
            }
        }
        binding.web.loadUrl("https://www.baidu.com")

        CompatBlurRender.get().bindBlur(
            this,
            binding.center,
            binding.web,
            BlurConfig().apply {
                this.radius = 40f
            })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun captureBitmap(activity: Activity, callback: (Bitmap) -> Unit) {
//        val window = activity.window
//        val view = binding.web
//
//        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
//
//        // PixelCopy 在主线程外工作
//        PixelCopy.request(
//            window,
//            bitmap,
//            { copyResult ->
//                if (copyResult == PixelCopy.SUCCESS) {
//                    callback(bitmap)
//                }
//            },
//            Handler(Looper.getMainLooper())
//        )


        val location = IntArray(2)
        binding.web.getLocationInWindow(location)
        val x = location[0]
        val y = location[1]
        val width = binding.web.width
        val height = binding.web.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        try {
            PixelCopy.request(
                window,
                Rect(x, y, x + width, y + height),
                bitmap,
                { result ->
                    if (result == PixelCopy.SUCCESS) {
                        callback(bitmap)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }


}