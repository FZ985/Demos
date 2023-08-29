package com.demos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.demos.beziertest.TestBezierActivity;
import com.demos.databinding.ActivityMainBinding;
import com.demos.layoutmanager.LayoutManagerUI1;
import com.demos.magic.MagicTabActivity1;
import com.demos.marquee.MarqueeUI;
import com.demos.other.AppbarLayoutActivity;
import com.demos.prizedraw.PrizeDrawActivity;
import com.demos.span.SpanActivity;
import com.demos.textview.PicTextActivity;
import com.demos.viewpager.toplinkcustom.TopLinkCustomActivity;
import com.demos.viewpager.toplinkmagic.TopLinkMagicActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        click(binding.viewpager1, TopLinkCustomActivity.class);
        click(binding.viewpager2, TopLinkMagicActivity.class);
        click(binding.recycler1, LayoutManagerUI1.class);
        click(binding.magic1, MagicTabActivity1.class);
        click(binding.marquee, MarqueeUI.class);
        click(binding.prizeDraw, PrizeDrawActivity.class);
        click(binding.picText, PicTextActivity.class);
        click(binding.span, SpanActivity.class);
        click(binding.appbar, AppbarLayoutActivity.class);
        click(binding.testbezirer, TestBezierActivity.class);

    }

    private void click(View view, Class<?> cls) {
        view.setOnClickListener(v -> startActivity(new Intent(this, cls)));
    }

}