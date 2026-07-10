package com.demos.systemdownload

import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap


/**
 * by DAD FZ
 * 2026/7/10
 * desc：
 **/
object DownloadHelper {

    /**
     * fileName -> downloadId
     */
    private val downloadTaskMap = HashMap<String, Long>()


    private val taskMap = ConcurrentHashMap<Long, MutableSharedFlow<SystemDownloadInfo>>()


    private val pollingJobs = ConcurrentHashMap<Long, Job>()

    /**
     * 下载文件
     *
     * @return DownloadManager 返回的 downloadId
     */
    @Synchronized
    fun download(
        context: Context,
        url: String,
        fileName: String
    ): Long {

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        // 1、取消之前的下载任务
        downloadTaskMap[fileName]?.let {
            manager.remove(it)
            downloadTaskMap.remove(fileName)
        }

        // 2、删除同名文件
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?.let { dir ->
                val file = File(dir, fileName)
                if (file.exists()) {
                    file.delete()
                }
            }

        // 3、创建下载请求
        val request = DownloadManager.Request(url.toUri()).apply {

            setTitle(fileName)

//            setDescription("正在下载...")

            //下载完成显示通知
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            setAllowedOverMetered(true)

            setAllowedOverRoaming(true)

            // Wifi + 数据网络
            setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or
                        DownloadManager.Request.NETWORK_MOBILE
            )

            // APK建议指定Mime
//            setMimeType("application/vnd.android.package-archive")

            //低版本需要写入权限
//            setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS,
//                fileName
//            )

            setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )

        }

        // 4、开始下载
        val downloadId = manager.enqueue(request)

        downloadTaskMap[fileName] = downloadId

        return downloadId
    }

    /**
     * 取消下载
     */
    @Synchronized
    fun cancel(
        context: Context,
        fileName: String
    ) {

        val manager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadTaskMap[fileName]?.let {

            manager.remove(it)

            downloadTaskMap.remove(fileName)
        }

        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?.let {

                val file = File(it, fileName)

                if (file.exists()) {
                    file.delete()
                }
            }
    }

    /**
     * 查询下载状态
     */
    fun queryStatus(
        context: Context,
        downloadId: Long
    ): Int {

        val manager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val cursor = manager.query(
            DownloadManager.Query().setFilterById(downloadId)
        )

        cursor.use {

            if (it.moveToFirst()) {

                return it.getInt(
                    it.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_STATUS
                    )
                )
            }
        }

        return DownloadManager.STATUS_FAILED
    }

    /**
     * 查询下载进度(0~100)
     */
    fun queryProgress(
        context: Context,
        downloadId: Long
    ): Int {

        val manager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val cursor = manager.query(
            DownloadManager.Query().setFilterById(downloadId)
        )

        cursor.use {

            if (it.moveToFirst()) {

                val downloaded = it.getLong(
                    it.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                    )
                )

                val total = it.getLong(
                    it.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_TOTAL_SIZE_BYTES
                    )
                )

                if (total > 0) {
                    return (downloaded * 100 / total).toInt()
                }
            }
        }

        return 0
    }

    /**
     * 获取下载后的文件
     */
    fun getDownloadedFile(
        context: Context,
        fileName: String
    ): File {

        return File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
    }

    /**
     * 获取下载Uri
     */
    fun getDownloadedUri(
        context: Context,
        downloadId: Long
    ): Uri? {

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        return manager.getUriForDownloadedFile(downloadId)
    }

    fun deletePublicDownload(
        context: Context,
        fileName: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME}=?",
                arrayOf(fileName),
                null
            )?.use { cursor ->

                while (cursor.moveToNext()) {

                    val id = cursor.getLong(0)

                    context.contentResolver.delete(
                        ContentUris.withAppendedId(collection, id),
                        null,
                        null
                    )
                }
            }

        } else {

            val file = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ),
                fileName
            )

            if (file.exists()) {
                file.delete()
            }
        }
    }

    /**
     * Activity:
     * lifecycleScope.launch {
     *     DownloadHelper.observeDownload(context, id)
     *         .collect {
     *         }
     * }
     *
     * Fragment:
     * viewLifecycleOwner.lifecycleScope.launch {
     *     DownloadHelper.observeDownload(requireContext(), id)
     *         .collect {
     *         }
     * }
     *
     */
    fun observeDownload(
        context: Context,
        downloadId: Long,
        interval: Long = 300L
    ): SharedFlow<SystemDownloadInfo> {

        val flow = taskMap.getOrPut(downloadId) {
            MutableSharedFlow(replay = 1)
        }

        if (!pollingJobs.containsKey(downloadId)) {
            pollingJobs[downloadId] = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {

                    val progress = queryProgress(context, downloadId)

                    val status = queryStatus(context, downloadId)

                    flow.emit(SystemDownloadInfo(progress, status))

                    if (status == DownloadManager.STATUS_SUCCESSFUL ||
                        status == DownloadManager.STATUS_FAILED
                    ) {
                        pollingJobs.remove(downloadId)
                        taskMap.remove(downloadId)
                        break
                    }

                    delay(interval)
                }
            }
        }
        return flow
    }

    data class SystemDownloadInfo(
        val progress: Int,
        val status: Int
    )
}