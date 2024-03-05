package com.demos.luck2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.BaseApp;
import com.demos.R;
import com.demos.databinding.ActivityLuck2Binding;

/**
 * author : JFZ
 * date : 2023/10/20 10:52
 * description :
 */
public class Luck2Activity extends AppCompatActivity implements LuckPanLayout.AnimationEndListener {

    private ActivityLuck2Binding binding;

    private String[] strs = BaseApp.getInstance().getResources().getStringArray(R.array.names);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLuck2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.luckpanLayout.setAnimationEndListener(this);
    }

    public void rotation(View view) {
        binding.luckpanLayout.rotate(-1, 100);
    }

    @Override
    public void endAnimation(int position) {
        Toast.makeText(this, "Position = " + position + "," + strs[position], Toast.LENGTH_SHORT).show();
    }
}
