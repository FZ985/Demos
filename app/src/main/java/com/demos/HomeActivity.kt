package com.demos

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.demos.activity.CalendarBlockActivity
import com.demos.activity.FloatBallActivity
import com.demos.activity.GradientColorActivity
import com.demos.activity.RoundWrapActivity
import com.demos.activity.SportBallActivity
import com.demos.anim.ConstraintLayoutAnimActivity
import com.demos.beziertest.TestBezierActivity
import com.demos.blur.BlurActivity
import com.demos.bottomsheet.BottomSheetActivity1
import com.demos.camera.CameraActivity
import com.demos.databinding.ActivityMainBinding
import com.demos.imagepoint.ImagePointActivity
import com.demos.insertvideo.InsertVideoActivity
import com.demos.layoutmanager.LayoutManagerUI1
import com.demos.live.LiveRecyclerActivity
import com.demos.luck1.Luck1Activity
import com.demos.luck2.Luck2Activity
import com.demos.luck3.Luck3Activity
import com.demos.luck4.Lucky4Activity
import com.demos.magic.MagicTabActivity1
import com.demos.marquee.MarqueeUI
import com.demos.merge.BitmapMergeActivity
import com.demos.other.AppbarLayoutActivity
import com.demos.password.PasswordActivity
import com.demos.span.SpanActivity
import com.demos.viewpager.toplinkcustom.TopLinkCustomActivity
import com.demos.viewpager.toplinkmagic.TopLinkMagicActivity
import com.demos.watermark.WaterMarkActivity
import com.demos.zztestmar.TestMarAct
import java.util.ArrayDeque
import java.util.Queue


/**
 * by JFZ
 * 2024/9/10
 * desc：
 **/
class HomeActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val queue: Queue<String> = ArrayDeque()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        val adapter = HomeAdapter()
        binding.recycler.adapter = adapter
        adapter.setNewInstance(homeList())
        adapter.setOnItemClickListener { _, _, pos ->
            val item = adapter.getItem(pos)
            startActivity(Intent(this, item.cls))

//            try {
//                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                intent.setData(Uri.parse("package:" + getPackageName()))
//                startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
//                // 可以采用备用方案
//            }

//            try {
//                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//                startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
                 ////备用方案
//            }

//            try {
//                val pm: PackageManager = getPackageManager()
//                val launchIntent: Intent? = pm.getLaunchIntentForPackage("com.miui.securitycenter")
//                if (launchIntent != null) {
//                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(launchIntent)
//                } else {
//                    // 没有找到启动 Intent，可能应用未安装或者没有 launcher activity
//                    Toast.makeText(this, "未找到应用", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        }
    }


    private fun queueTask() {
        val empty = queue.isEmpty()
        Logger.e("empty:" + empty + ",size:" + queue.size)
        if (!empty) {
            val poll = queue.poll()
            Logger.e("poll:$poll")
            queueTask()
        } else {
            Logger.e("队列为空")
        }
    }

    private fun int2Byte(value: Int): ByteArray {
        val bytes = ByteArray(2)
        bytes[1] = (value shr 8 and 255).toByte()
        bytes[0] = (value and 255).toByte()
        return bytes
    }


    private fun homeList(): MutableList<HomeItem> {
        return mutableListOf(
            HomeItem("悬浮view(应用内)", FloatBallActivity::class.java),
            HomeItem("渐变色块", GradientColorActivity::class.java),
            HomeItem("模糊相关", BlurActivity::class.java),
            HomeItem("运动球体", SportBallActivity::class.java),
            HomeItem("Github贡献度组件", CalendarBlockActivity::class.java),
            HomeItem("背景水印", WaterMarkActivity::class.java),
            HomeItem(
                "两个ViewPager联动效果自定义",
                TopLinkCustomActivity::class.java
            ),
            HomeItem("插入视频", InsertVideoActivity::class.java),
            HomeItem("两个ViewPager联动效果Magic实现", TopLinkMagicActivity::class.java),
            HomeItem("自定义layoutManager布局排名网格", LayoutManagerUI1::class.java),
            HomeItem("圆角包裹", RoundWrapActivity::class.java),
            HomeItem("tab导航1", MagicTabActivity1::class.java),
            HomeItem("自定义跑马灯", MarqueeUI::class.java),
            HomeItem("Span", SpanActivity::class.java),
            HomeItem("AppbarLayout", AppbarLayoutActivity::class.java),
            HomeItem("抽奖1", Luck1Activity::class.java),
            HomeItem("抽奖2", Luck2Activity::class.java),
            HomeItem("抽奖3", Luck3Activity::class.java),
            HomeItem("抽奖4", Lucky4Activity::class.java),
            HomeItem("贝塞尔曲线", TestBezierActivity::class.java),
            HomeItem("密码输入框", PasswordActivity::class.java),
            HomeItem("直播消息列表test", LiveRecyclerActivity::class.java),
            HomeItem("BottomSheet1", BottomSheetActivity1::class.java),
            HomeItem("图片合并", BitmapMergeActivity::class.java),
            HomeItem("单条跑马灯", TestMarAct::class.java),
            HomeItem("ConstraintLayout约束动画", ConstraintLayoutAnimActivity::class.java),
            HomeItem("图片点位", ImagePointActivity::class.java),
            HomeItem("废弃相机的使用", CameraActivity::class.java)
        )
    }


    class HomeAdapter : BaseQuickAdapter<HomeItem, BaseViewHolder>(R.layout.item_main_list) {
        override fun convert(holder: BaseViewHolder, item: HomeItem) {
            val name = holder.getView<TextView>(R.id.name)
            name.text = item.text
        }
    }

    data class HomeItem(val text: String, val cls: Class<*>)
}