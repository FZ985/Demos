package com.demos.merge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.demos.R
import com.demos.databinding.ActivityBitmapMergeBinding
import com.demos.dp
import com.demos.utils.save.saveGifByGlide
import com.demos.utils.save.saveToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        //图片2
        val bitmapCode = BitmapFactory.decodeResource(resources, R.mipmap.qrcode_test)
        binding.i2.setImageBitmap(bitmapCode)

        lifecycleScope.launch {
            val img =
                "https://img0.baidu.com/it/u=2605876870,2798209052&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1399"
            val imgGif = "https://dingyue.ws.126.net/2021/0506/da35f1ddg00qsnuy300jzc000f000dkg.gif"

            val bitmap = scaleBitmap(withContext(Dispatchers.IO) {
                Glide.with(this@BitmapMergeActivity)
                    .asBitmap()
                    .load(img)
                    .submit()
                    .get()
            })

            binding.i1.setImageBitmap(bitmap)

            //生成的新图片
            val newBitmap = mergeBitmap(bitmap, bitmapCode)
            binding.iv.setImageBitmap(newBitmap)


            binding.save.setOnClickListener {
                newBitmap.saveToFile(this@BitmapMergeActivity)?.let {
                    Toast.makeText(this@BitmapMergeActivity, "保存成功", Toast.LENGTH_SHORT).show()
                }
            }

            binding.saveGif.setOnClickListener {
                saveGifByGlide(this@BitmapMergeActivity, imgGif, "gif", "gifDesc") {
                    it?.let {
                        Toast.makeText(this@BitmapMergeActivity, "保存成功", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    //将bitmap 基于宽度 进行等比 缩放
    private fun scaleBitmap(originalBitmap: Bitmap): Bitmap {
        val originalWidth: Int = originalBitmap.width
        val originalHeight: Int = originalBitmap.height
        // 你希望的目标宽度
        val targetWidth: Int = resources.displayMetrics.widthPixels

        if (originalWidth >= targetWidth) {
            return originalBitmap
        }

        val scaleFactor = targetWidth.toFloat() / originalWidth
        val targetHeight = (originalHeight * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
    }


    private fun mergeBitmap(bitmap: Bitmap, bmp: Bitmap): Bitmap {
        //以图片1 创建背景
        val backgroundBmp = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        return backgroundBmp.applyCanvas {
            //绘制背景
            drawBitmap(bitmap, 0f, 0f, null)

            //绘制素材
            drawBitmap(bmp, 15.dp.toFloat(), (height - 15.dp - bmp.height).toFloat(), null)

        }
    }
}