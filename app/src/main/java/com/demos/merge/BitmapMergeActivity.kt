package com.demos.merge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.demos.R
import com.demos.databinding.ActivityBitmapMergeBinding
import com.demos.dp


/**
 * by JFZ
 * 2024/6/28
 * desc：
 **/
class BitmapMergeActivity : AppCompatActivity() {

    private val binding: ActivityBitmapMergeBinding by lazy {
        ActivityBitmapMergeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //图片1
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.test)
        binding.i1.setImageBitmap(bitmap)

        //图片2
        val bitmapCode = BitmapFactory.decodeResource(resources, R.mipmap.qrcode_test)
        binding.i2.setImageBitmap(bitmapCode)

        //生成的新图片
        val newBitmap = mergeBitmap(bitmap, bitmapCode)
        binding.iv.setImageBitmap(newBitmap)

        binding.save.setOnClickListener {
            newBitmap?.let {
                saveBitmap(newBitmap)
            }
        }
    }


    private fun mergeBitmap(bitmap: Bitmap, bmp: Bitmap): Bitmap? {
        //以图片1 创建背景
        val backgroundBmp = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        //创建画布
        val canvas = Canvas(backgroundBmp)

        val width = canvas.width
        val height = canvas.height

        //绘制背景
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        //绘制素材
        canvas.drawBitmap(bmp, 15.dp.toFloat(), (height - 15.dp - bmp.height).toFloat(), null)

        return backgroundBmp
    }


    private fun saveBitmap(bitmap: Bitmap) {
        try {
            val insertImage = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                System.currentTimeMillis().toString(),
                null
            )
            Log.e("BitmapMergeActivity", "====insertImage:$insertImage")
        } catch (e: Exception) {
        }
    }

}