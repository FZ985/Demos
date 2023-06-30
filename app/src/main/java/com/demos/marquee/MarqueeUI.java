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
        for (int i = 0; i < 10; i++) {
            d.add("iiiiii" + i);
        }

        binding.marquee.setAdapter(adapter);
        binding.btn.setOnClickListener(v -> {
            adapter.setData(d);
        });

        binding.marquee.setLoopListener(new MarqueeView.OnMarqueeLoopListener() {
            @Override
            public void onMarqueeLoop(int position) {
                Logger.e("====pos:" + position);
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
