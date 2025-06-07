package com.demos.insertvideo

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.demos.Logger
import com.demos.databinding.ActivityInsertVideoBinding
import com.demos.nativedownload.NativeDownload
import com.demos.nativedownload.core.DownLoadListener
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files


/**
 * by JFZ
 * 2024/11/15
 * desc：
 **/
class InsertVideoActivity : AppCompatActivity() {

    private val binding: ActivityInsertVideoBinding by lazy {
        ActivityInsertVideoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btn.setOnClickListener {
            val url = "https://video-pro.hk.ufileos.com/Record_2024-11-14-21-17-07.mp4"
            NativeDownload.download(
                url,
                cacheDir.absolutePath,
                "video44.mp4",
                object : DownLoadListener {
                    override fun update(
                        progress: Long,
                        percent: Float,
                        contentLength: Long,
                        done: Boolean
                    ) {

                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun complete(file: File?) {
                        file?.let {
                            Logger.e("下载成功:" + it.absolutePath)
                            insertVideoToGalleryCompat(this@InsertVideoActivity, file) {}
                        }

                    }

                    override fun error(e: Exception?) {
                        Logger.e("下载失败：" + e?.message)
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertVideoToGallery(context: Context, videoFile: File) {
        val values = ContentValues()
        values.put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES) // 设置相对路径
        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        try {
            contentResolver.openOutputStream(uri!!).use { out ->
                Files.copy(videoFile.toPath(), out) // 将视频文件内容写入相册
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun insertVideoToGalleryCompat(context: Context, videoFile: File, call: (uri: Uri) -> Unit) {
        val values = ContentValues()
        values.put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(
            MediaStore.Video.Media.RELATIVE_PATH,
            Environment.DIRECTORY_MOVIES
        )
        // 设置相对路径，适用于Android 10及以上
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


}