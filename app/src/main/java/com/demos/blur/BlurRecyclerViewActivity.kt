package com.demos.blur

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.demos.R
import com.demos.blur.render.BlurConfig
import com.demos.blur.render.CompatBlurRender
import com.demos.databinding.ActivityBlurRecyclerBinding

/**
 * by JFZ
 * 2025/5/23
 * desc：
 **/
class BlurRecyclerViewActivity : AppCompatActivity() {

    private val binding: ActivityBlurRecyclerBinding by lazy {
        ActivityBlurRecyclerBinding.inflate(layoutInflater)
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)

        val adapter = Adapter()
        binding.recycler.adapter = adapter

        adapter.setOnItemChildClickListener { _, v, pos ->
            if (pos % 2 == 0) {
                val view = v.findViewById<View>(R.id.view)
                view.isVisible = false
            }
        }

        adapter.addData("")
        adapter.addData("")
        adapter.addData("")
        adapter.addData("")
        adapter.addData("")
        adapter.addData("")
        adapter.addData("")

        CompatBlurRender.get().bindBlur(
            this,
            binding.view,
            binding.recycler,
            BlurConfig().apply {
                this.radius = 40f
            })

    }


    class Adapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_blur_list) {
        override fun convert(
            holder: BaseViewHolder,
            item: String
        ) {

        }
    }
}