package com.demos.imagepoint

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.applyCanvas
import androidx.core.view.isVisible
import com.demos.R
import com.demos.databinding.ActivityImagePointBinding
import com.demos.merge.MapPointHelper


/**
 * by JFZ
 * 2025/2/7
 * desc：
 **/
class ImagePointActivity : AppCompatActivity() {

    private val binding: ActivityImagePointBinding by lazy {
        ActivityImagePointBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val progressDialog = ProgressDialog.show(this, "", "loading...")
        MapPointHelper.getInstance().init(this, R.mipmap.v2_ic_loc_map) {
            progressDialog.dismiss()
            binding.btn.isVisible = true
            binding.btn.setOnClickListener {
                val edgePoints = MapPointHelper.getInstance().edgePoints

                val bitmap = Bitmap.createBitmap(
                    binding.layer.width,
                    binding.layer.height,
                    Bitmap.Config.ARGB_8888
                )

                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = Color.RED
                paint.style = Paint.Style.FILL

                bitmap.applyCanvas {
                    edgePoints.forEach {
                        val x = it[0].toFloat()
                        val y = it[1].toFloat()
//                        Logger.e("x:$x,y:$y")
                        drawCircle(x, y, 5f, paint)
                    }
                }

                binding.layer.setImageBitmap(bitmap)
            }

        }
    }
}