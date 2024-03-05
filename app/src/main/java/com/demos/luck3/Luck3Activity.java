package com.demos.luck3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.R;
import com.demos.databinding.ActivityLuck3Binding;

import java.util.ArrayList;

/**
 * author : JFZ
 * date : 2023/10/20 10:52
 * description :
 */
public class Luck3Activity extends AppCompatActivity {

    private ActivityLuck3Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLuck3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btPointTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.turntable.startRotate(7, new ITurntableListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(Luck3Activity.this, "开始抽奖", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEnd(int position, String name) {
                        binding.tvResult.setText("抽奖结束抽中第" + (position + 1) + "位置的奖品:" + name);
                    }
                });
            }
        });

        binding.btChangecolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置item的颜色
                changeColors();
            }
        });

        binding.btChangedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改转盘数据
                changeDatas();
            }
        });

        //开始抽奖
        binding.ivNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.turntable.startRotate(new ITurntableListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(Luck3Activity.this, "开始抽奖", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEnd(int position, String name) {
                        binding.tvResult.setText("抽奖结束抽中第" + (position + 1) + "位置的奖品:" + name);
                    }
                });
            }
        });

        //自动转
        binding.autoScroll.setOnClickListener(v -> {
            binding.turntable.autoScroll();
        });

    }


    private void changeColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ff8584"));
        colors.add(getResources().getColor(R.color.teal_200));
        colors.add(Color.parseColor("#000000"));
        binding.turntable.setBackColor(colors);
    }

    private void changeDatas() {
        int num = 10;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            names.add("第" + (i + 1));
            bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.v2_ic_public_no));
        }
        binding.turntable.setDatas(num, names, bitmaps);
    }
}
