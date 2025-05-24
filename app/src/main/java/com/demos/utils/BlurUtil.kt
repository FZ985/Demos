package com.demos.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.RenderScript.RSMessageHandler
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi
import androidx.core.graphics.scale


/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
object BlurUtil {


    @SuppressLint("WrongConstant")
    @JvmStatic
    fun blurBitmap(
        context: Context,
        bitmap: Bitmap,
        radius: Float,
        scaleFactor: Float = 1f,
        oldRenderScriptRepeat: Boolean = true
    ): Bitmap? {
        val originBmp = bitmap.copy(bitmap.getConfig(), true)
        val originW = originBmp.width
        val originH = originBmp.height

        var resultBitmap = scaleFactorBitmap(originBmp, scaleFactor.coerceIn(0f, 1f))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val resultBitmap = blurBitmapBy31(resultBitmap, radius)
            if (resultBitmap != null) {
                return resultBitmap.scale(originW, originH)
            }
        } else {
            resultBitmap = blurBitmapBy30(context, resultBitmap, radius, oldRenderScriptRepeat)
            return resultBitmap.scale(originW, originH)
        }
        return null
    }

    @JvmStatic
    fun scaleFactorBitmap(
        bitmap: Bitmap,
        scaleFactor: Float = 1f
    ): Bitmap {
        val scaledWidth = (bitmap.width * scaleFactor.coerceIn(0f, 1f)).toInt()
        val scaledHeight = (bitmap.height * scaleFactor.coerceIn(0f, 1f)).toInt()
        return bitmap.scale(scaledWidth, scaledHeight)
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    @JvmStatic
    fun blurBitmapBy31(
        bitmap: Bitmap,
        radius: Float,
    ): Bitmap? {
        val imageReader = ImageReader.newInstance(
            bitmap.width, bitmap.height,
            PixelFormat.RGBA_8888, 1,
            HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )

        val renderNode = RenderNode("BlurEffect")
        val hardwareRenderer = HardwareRenderer()

        hardwareRenderer.setSurface(imageReader.surface)
        hardwareRenderer.setContentRoot(renderNode)
        renderNode.setPosition(0, 0, imageReader.width, imageReader.height)
        val blurRenderEffect = RenderEffect.createBlurEffect(
            radius, radius,
            Shader.TileMode.MIRROR
        )
        renderNode.setRenderEffect(blurRenderEffect)

        val renderCanvas = renderNode.beginRecording()
        renderCanvas.drawBitmap(bitmap, 0f, 0f, null)
        renderNode.endRecording()
        hardwareRenderer.createRenderRequest()
            .setWaitForPresent(true)
            .syncAndDraw()

        val image = imageReader.acquireNextImage()
        val hardwareBuffer = image.hardwareBuffer

        var resultBitmap: Bitmap? = null
        if (hardwareBuffer != null) {
            resultBitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
        }

        hardwareBuffer?.close()
        image.close()
        imageReader.close()
        renderNode.discardDisplayList()
        hardwareRenderer.destroy()
        return resultBitmap
    }

    @JvmStatic
    fun blurBitmapBy30(
        context: Context,
        bitmap: Bitmap,
        radius: Float,
        oldRenderScriptRepeat: Boolean = true
    ): Bitmap {
        var resultBitmap = bitmap
        if (radius > 25f) {
            if (oldRenderScriptRepeat) {
                //整数次模糊
                val rCount = (radius / 25f).toInt()
                for (i in 0 until rCount) {
                    resultBitmap = renderBlurBitmapBy30(context, resultBitmap, 25f)
                }

                //余数次模糊
                val rRemainder = (radius % 25f).toInt()
                for (i in 0 until rRemainder) {
                    resultBitmap =
                        renderBlurBitmapBy30(context, resultBitmap, rRemainder.toFloat())
                }
            } else resultBitmap = renderBlurBitmapBy30(context, resultBitmap, 25f)
        } else {
            resultBitmap = renderBlurBitmapBy30(context, resultBitmap, radius)
        }
        return resultBitmap
    }


    private fun renderBlurBitmapBy30(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        var rs: RenderScript? = null
        var mRadius = radius.coerceIn(0f, 25f)
        try {
            rs = RenderScript.create(context)
            rs.messageHandler = RSMessageHandler()
            val input = Allocation.createFromBitmap(
                rs,
                bitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(rs, input.type)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setInput(input)
            blurScript.setRadius(mRadius)
            blurScript.forEach(output)
            output.copyTo(bitmap)
        } finally {
            rs?.destroy()
        }
        return bitmap
    }

}