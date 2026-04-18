package com.demos.layoutmanager;

import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.demos.R;
import com.demos.activity.BaseActivity;
import com.demos.databinding.ActivityLayoutmanager1Binding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/27 13:39
 * description :
 */
public class LayoutManagerUI1 extends BaseActivity {

    private ActivityLayoutmanager1Binding binding;

    @Override
    @NotNull
    public View getApplyWindowView() {
        binding = ActivityLayoutmanager1Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        binding.recycler.setLayoutManager(new TestGridManager());
//        binding.recycler.setLayoutManager(new ArcLayoutManager(this));
//        binding.recycler.setLayoutManager(new RectangleLayoutManager(this,3,4));
        changeData(binding.seek.getProgress());
        binding.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeData(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void changeData(int count) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add(String.valueOf(i));
        }
        TestAdapter adapter = new TestAdapter(data);
        binding.recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Toast.makeText(LayoutManagerUI1.this, "pos:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class TestAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public TestAdapter(@Nullable List<String> data) {
            super(R.layout.item_manager1, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String s) {
            holder.setText(R.id.tv, s);
        }
    }
}
