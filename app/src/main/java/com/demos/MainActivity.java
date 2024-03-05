package com.demos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.demos.activity.RoundWrapActivity;
import com.demos.beziertest.TestBezierActivity;
import com.demos.databinding.ActivityMainBinding;
import com.demos.layoutmanager.LayoutManagerUI1;
import com.demos.luck1.Luck1Activity;
import com.demos.luck2.Luck2Activity;
import com.demos.luck3.Luck3Activity;
import com.demos.luck4.Lucky4Activity;
import com.demos.magic.MagicTabActivity1;
import com.demos.marquee.MarqueeUI;
import com.demos.other.AppbarLayoutActivity;
import com.demos.password.PasswordActivity;
import com.demos.span.SpanActivity;
import com.demos.viewpager.toplinkcustom.TopLinkCustomActivity;
import com.demos.viewpager.toplinkmagic.TopLinkMagicActivity;

import java.util.ArrayDeque;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    Queue<String> queue = new ArrayDeque<>();

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
        click(binding.span, SpanActivity.class);
        click(binding.appbar, AppbarLayoutActivity.class);
        click(binding.testbezirer, TestBezierActivity.class);
        click(binding.password, PasswordActivity.class);
        click(binding.roundWrapText, RoundWrapActivity.class);
        click(binding.luck1, Luck1Activity.class);
        click(binding.luck2, Luck2Activity.class);
        click(binding.luck3, Luck3Activity.class);
        click(binding.luck4, Lucky4Activity.class);

//        queue.add("1");
//        queue.add("2");
//        queue.add("3");
//        queue.add("4");
//        queueTask();


//        byte[] bytes = int2Byte(1329);
//        Logger.e("ssssss:"+ Arrays.toString(bytes));
    }

    public static byte[] int2Byte(int value) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) ((value >> 8) & 255);
        bytes[0] = (byte) (value & 255);
        return bytes;
    }

    private void queueTask() {
        boolean empty = queue.isEmpty();
        Logger.e("empty:" + empty + ",size:" + queue.size());
        if (!empty) {
            String poll = queue.poll();
            Logger.e("poll:" + poll);
            queueTask();
        } else {
            Logger.e("队列为空");
        }
    }

    private void click(View view, Class<?> cls) {
        view.setOnClickListener(v -> startActivity(new Intent(this, cls)));
    }

}