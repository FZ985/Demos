package com.demos.watermark

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.applyCanvas
import com.demos.databinding.ActivityWatermarkBinding


/**
 * by JFZ
 * 2025/3/13
 * desc：
 **/
class WaterMarkActivity : AppCompatActivity() {

    private val binding: ActivityWatermarkBinding by lazy {
        ActivityWatermarkBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.root.background = WatermarkDrawable("哈哈哈").apply {
            alpha = 90
            setBackgroundColor(Color.RED)
            setTextSize(50)
            setTextColor(Color.BLUE)
        }

    }


    private fun createMirroredBitmap(sourceBitmap: Bitmap): Bitmap {
        val mirroredBitmap =
            Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mirroredBitmap)

        // Create a BitmapShader with mirror TileMode
        val shader = BitmapShader(
            sourceBitmap,
            Shader.TileMode.MIRROR,
            Shader.TileMode.MIRROR
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setShader(shader)

        // Draw on the canvas
        canvas.drawRect(0f, 0f, sourceBitmap.width.toFloat(), sourceBitmap.height.toFloat(), paint);

        return mirroredBitmap;
    }


    private fun createBitmap(): Bitmap {
        val size = resources.displayMetrics.widthPixels / 2
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLUE
        bitmap.applyCanvas {
            drawColor(Color.RED)
            drawText("123", 0f, 100f, paint)
        }
        return bitmap
    }

}