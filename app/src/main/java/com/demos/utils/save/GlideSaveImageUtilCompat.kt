package com.demos.utils.save

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import java.io.File


/**
 * by DAD FZ
 * 2026/1/22
 * desc：低版本保存图片兼容类
 **/
object GlideSaveImageUtilCompat {

    private class InsertMediaScanner(context: Context, path: String, listener: Runnable) :
        MediaScannerConnection.MediaScannerConnectionClient {

        private var mMsc: MediaScannerConnection? = null
        private var mPath: String? = null
        private var mListener: Runnable? = null

        init {
            this.mPath = path
            this.mMsc = MediaScannerConnection(context, this)
            this.mMsc?.connect()
            this.mListener = listener
        }

        override fun onMediaScannerConnected() {
            mMsc?.scanFile(mPath, null)
        }

        override fun onScanCompleted(path: String?, uri: Uri?) {
            mMsc?.disconnect()
            mListener?.run()
        }
    }

    fun saveToFileCompat(
        context: AppCompatActivity,
        bitmap: Bitmap,
        title: String? = System.currentTimeMillis().toString(),
        desc: String? = "无描述",
        saveCall: (Uri?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveCall(bitmap.saveToFile(context, title, desc))
        } else {
            checkWrite(context, {
                saveCall(null)
            }) {
                bitmap.saveToFile(context, title, desc)?.let { uri ->
                    saveCall(uri)
                } ?: run { saveCall(null) }
            }
        }
    }

    fun saveImageByGlideCompat(
        activity: AppCompatActivity,
        imageUrl: String,
        title: String? = System.currentTimeMillis().toString(),
        desc: String? = "无描述",
        block: (Uri?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageByGlide(activity, imageUrl, title, desc, block)
        } else {
            checkWrite(activity, {
                block(null)
            }) {
                saveImageByGlide(activity, imageUrl, title, desc) { result ->
                    result?.let { uri ->
                        block(uri)
                    } ?: run { block(null) }
                }
            }
        }
    }

    fun saveGifByGlideCompat(
        activity: AppCompatActivity,
        imageUrl: String,
        title: String? = System.currentTimeMillis().toString(),
        desc: String? = "无描述",
        block: (Uri?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveGifByGlide(activity, imageUrl, title, desc, block)
        } else {
            checkWrite(activity, {
                block(null)
            }) {
                saveGifByGlide(activity, imageUrl, title, desc, block)
            }
        }
    }

    fun insertVideoToGalleryCompat(context: Activity, videoFile: File, call: (uri: Uri?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            insertVideoToGallery(context, videoFile, call)
        } else {
            checkWrite(context, {
                call(null)
            }) {
                insertVideoToGallery(context, videoFile, call)
            }
        }
    }

    @JvmStatic
    private val writePermissions = PermissionLists.getWriteExternalStoragePermission()

    fun checkWrite(activity: Activity, error: Runnable, success: Runnable) {
        XXPermissions.with(activity)
            .permission(writePermissions)
            .request(object : OnPermissionCallback {
                override fun onResult(
                    permissions: List<IPermission>,
                    deniedList: List<IPermission>
                ) {
                    val allGranted = deniedList.isEmpty()
                    if (allGranted) {
                        success.run()
                    } else {
                        val doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(
                            activity,
                            listOf(writePermissions)
                        )
                        if (doNotAskAgain) {
                            error.run()
                        }
                    }
                }
            })
    }


}