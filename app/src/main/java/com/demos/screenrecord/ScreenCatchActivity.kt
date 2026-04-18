package com.demos.screenrecord

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.view.View
import com.demos.activity.BaseActivity
import com.demos.databinding.ActivityScreenCatchBinding

/**
 * by DAD FZ
 * 2026/4/17
 * desc：
 **/
@Deprecated("未测试完")
class ScreenCatchActivity : BaseActivity() {

    private val binding: ActivityScreenCatchBinding by lazy {
        ActivityScreenCatchBinding.inflate(
            layoutInflater
        )
    }

    private val handler = Handler(Looper.getMainLooper())

    private val run = {
        loopScreen()
    }

    override fun initView() {

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )

        handler.postDelayed(run, 2000)

//        val displayManager = getSystemService(DISPLAY_SERVICE) as? DisplayManager
//        displayManager?.registerDisplayListener(object : DisplayManager.DisplayListener {
//            override fun onDisplayAdded(displayId: Int) {
//                val display = displayManager.getDisplay(displayId)
//                log("onDisplayAdded:$displayId,${display?.toString()}")
//                val displays = displayManager.displays
//                displays?.forEach {
//                    log("${it.name}《〈Display:${it.toString()}")
//                }
//            }
//
//            override fun onDisplayRemoved(displayId: Int) {
//                log("onDisplayRemoved:$displayId")
//            }
//
//            override fun onDisplayChanged(displayId: Int) {
//                val display = displayManager.getDisplay(displayId)
//                log("onDisplayChanged:$displayId,${display?.toString()}")
//            }
//        }, handler)
//
//        //-------------------------------------------------------------------------------------------------------------------
//        if (!hasUsageStatsPermission(this)) {
//            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//        } else {
//            val monitor = ForegroundAppMonitor(this)
//
//            monitor.onAppChanged = { packageName ->
//                log("ForegroundApp,当前前台App: $packageName")
//
//                if (packageName.contains("screenrecorder")) {
//                    log("Detect,⚠️ 可能在录屏")
//                }
//            }
//
//            monitor.start()
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
//            binding.text.setContentSensitivity(View.CONTENT_SENSITIVITY_SENSITIVE)
//        }

    }

    override fun onStart() {
        super.onStart()
        // Android 14 (API 34) 开始，官方提供回调，无需额外权限
//        registerScreenCaptureCallback(mainExecutor) {
//            // 用户正在录屏！
//            log("registerScreenCaptureCallback")
//        }
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    private fun loopScreen() {
        handler.removeCallbacks(run)
        handler.removeCallbacksAndMessages(null)

//        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//        val tasks = activityManager.getRunningTasks(3)
//        tasks.forEach { item ->
//            item?.topActivity.let {
//                log("topActivity:${it?.packageName}")
//            }
//        }

//        val dm = getSystemService(DISPLAY_SERVICE) as DisplayManager
//        val isRecording = dm.getDisplays().any { display ->
//            log("====flag:${display.flags},"+(display.flags and Display.FLAG_SECURE))
//            display.flags and Display.FLAG_SECURE != 0
//        }
//        log("isRecording:$isRecording")
//
//        handler.postDelayed(run, 2000)
    }

    private fun log(m: String) {
        Log.e("screen", m)
    }

    override fun onDestroy() {
        handler.removeCallbacks(run)
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }


    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )

        return if (mode == AppOpsManager.MODE_DEFAULT) {
            context.checkCallingOrSelfPermission(
                Manifest.permission.PACKAGE_USAGE_STATS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            mode == AppOpsManager.MODE_ALLOWED
        }
    }
}