package com.demos.marquee;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.Logger;
import com.demos.databinding.UiMarqueeBinding;
import com.demos.marquee.core.MarqueeView;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/30 14:53
 * description :
 */
public class MarqueeUI extends AppCompatActivity {
    private UiMarqueeBinding binding;
    MarqueeExampleAdapter adapter = new MarqueeExampleAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UiMarqueeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> d = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            d.add("iiiiii" + i);
        }

        binding.marquee.setAdapter(adapter);
        binding.btn.setOnClickListener(v -> {
            adapter.setData(d);
        });

        binding.btn1.setOnClickListener(v -> {
            binding.marquee.setMarquee(true);
        });
        binding.btn2.setOnClickListener(v -> {
            binding.marquee.setMarquee(false);
        });
        binding.btn4.setOnClickListener(v -> {
            binding.marquee.next();
        });
        binding.btn3.setOnClickListener(v -> {
            binding.marquee.setLoop(true);
        });
        binding.btn5.setOnClickListener(v -> {
            binding.marquee.setLoop(false);
        });

        binding.marquee.setLoopListener(new MarqueeView.OnMarqueeLoopListener() {
            @Override
            public void onMarqueeLoop(int position) {
                Logger.e("当前角标：" + position);
            }
        });

        binding.marquee.setOnItemClickListener(new MarqueeView.OnMarqueeItemClickListener() {
            @Override
            public void onMarqueeItemClick(View view, int position) {
                Toast.makeText(MarqueeUI.this, "clickPos:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
