package com.demos.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


/**
 * by JFZ
 * 2024/9/06
 * desc：使用glide 将图片加载并保存到本地相册中的帮助类
 **/


/**
 * 插入图片到本地相册
 */
fun Bitmap.insertToLocal(
    contentResolver: ContentResolver,
    title: String? = System.currentTimeMillis().toString(),
    desc: String? = "无描述"
): String? {
    try {
        return MediaStore.Images.Media.insertImage(
            contentResolver,
            this,
            title,
            desc
        )
    } catch (e: Exception) {
    }
    return ""
}


/**
 * 保存bitmap 到本地相册
 */
fun Bitmap.saveToFile(
    context: AppCompatActivity,
    title: String? = System.currentTimeMillis().toString(),
    desc: String? = "无描述"
): Uri? {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, title)
        put(MediaStore.Images.Media.DESCRIPTION, desc)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    }
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    uri?.let { _ ->
        try {
            val outputStream = context.contentResolver.openOutputStream(uri)

            outputStream?.let {
                compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.close()
            }
        } catch (e: Exception) {
            context.contentResolver.delete(uri, null, null)
            Log.e("error", "exception11:$e")
        }
    }
    return uri
}


//通过glide加载，获取bitmap并保存到本地
fun saveImageByGlide(
    activity: AppCompatActivity,
    imageUrl: String,
    title: String? = System.currentTimeMillis().toString(),
    desc: String? = "无描述",
    block: (Uri?) -> Unit
) {
    try {
        activity.lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(activity)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()
            }
            block(bitmap.saveToFile(activity, title, desc))
        }
    } catch (e: Exception) {
        block(null)
    }
}

//通过glide加载，获取file,并保存gif到本地
fun saveGifByGlide(
    activity: AppCompatActivity,
    imageUrl: String,
    title: String? = System.currentTimeMillis().toString(),
    desc: String? = "无描述",
    block: (Uri?) -> Unit
) {
    activity.lifecycleScope.launch {
        val file = withContext(Dispatchers.IO) {
            Glide.with(activity)
                .asFile()
                .load(imageUrl)
                .submit()
                .get()
        }
        val destinationFile = File(
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            System.currentTimeMillis().toString() + ".gif"
        )
        //copy file
        FileInputStream(file).use { fis ->
            FileOutputStream(destinationFile).use { fos ->
                val buffer = ByteArray(1024)
                var length: Int
                while (fis.read(buffer).also { length = it } > 0) {
                    fos.write(buffer, 0, length)
                }
            }
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, title)
            put(MediaStore.Images.Media.DESCRIPTION, desc)
            put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
        val uri =
            activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            try {
                activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    FileInputStream(destinationFile).use { inputStream ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        block(uri)
                    }
                }
            } catch (e: IOException) {
                // 如果写入失败，则删除插入的记录
                activity.contentResolver.delete(uri, null, null)
                block(null)
            }
        } else {
            block(null)
        }
    }
}

//插入视频
fun insertVideoToGalleryCompat(context: Context, videoFile: File, call: (uri: Uri) -> Unit) {
    val values = ContentValues()
    values.put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
    values.put(
        MediaStore.Video.Media.RELATIVE_PATH,
        Environment.DIRECTORY_MOVIES
    ) // 设置相对路径，适用于Android 10及以上
    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        try {
            FileInputStream(videoFile).use { inStream ->
                contentResolver.openOutputStream(uri).use { out ->
                    if (out != null) {
                        val buffer = ByteArray(1024)
                        var len: Int
                        while (inStream.read(buffer).also { len = it } != -1) {
                            out.write(buffer, 0, len)
                        }
                        out.flush()

                        call(uri)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
