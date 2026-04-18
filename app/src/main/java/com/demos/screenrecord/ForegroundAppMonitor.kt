package com.demos.screenrecord

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Handler
import android.os.Looper


/**
 * by DAD FZ
 * 2026/4/17
 * desc：
 **/
@Deprecated("未测试完")
class ForegroundAppMonitor(
    private val context: Context,
    private val interval: Long = 1000L // 1秒轮询
) {

    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private var lastPackageName: String? = null
    private var isRunning = false

    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            val currentApp = getForegroundApp()

            if (currentApp != null && currentApp != lastPackageName) {
                lastPackageName = currentApp
                onAppChanged?.invoke(currentApp)
            }

            if (isRunning) {
                handler.postDelayed(this, interval)
            }
        }
    }

    var onAppChanged: ((String) -> Unit)? = null

    fun start() {
        if (isRunning) return
        isRunning = true
        handler.post(runnable)
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    // ========================
    // 获取前台 App（核心）
    // ========================
    private fun getForegroundApp(): String? {
        val end = System.currentTimeMillis()
        val begin = end - 2000

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            begin,
            end
        )

        if (stats.isNullOrEmpty()) return null

        return stats.maxByOrNull { it.lastTimeUsed }?.packageName
    }
}