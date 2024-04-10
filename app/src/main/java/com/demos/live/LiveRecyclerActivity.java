package com.demos.live;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.demos.R;
import com.demos.Tools;
import com.demos.databinding.ActivityLiveRecyclerBinding;

/**
 * by JFZ
 * 2024/4/10
 * desc：
 **/
public class LiveRecyclerActivity extends AppCompatActivity {

    private ActivityLiveRecyclerBinding binding;
    private final ItemAdapter adapter = new ItemAdapter();
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveRecyclerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setFadingEdgeLength(Tools.dip2px(10));
        binding.recycler.setAdapter(adapter);
        start();
    }

    private final Runnable mRun = () -> {
        String s = "哈";
        int count = Tools.randomNumber(5, 40);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        adapter.addData(sb.toString());
        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
        start();
    };

    private void start() {
        binding.recycler.postDelayed(mRun, 1000);
    }

    private static class ItemAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public ItemAdapter() {
            super(R.layout.item_live);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String s) {
            holder.setText(R.id.text, s);
        }
    }
}
