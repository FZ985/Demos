package com.demos.luck2;

import android.view.View;
import android.widget.Toast;

import com.demos.BaseApp;
import com.demos.R;
import com.demos.activity.BaseActivity;
import com.demos.databinding.ActivityLuck2Binding;

import org.jetbrains.annotations.NotNull;

/**
 * author : JFZ
 * date : 2023/10/20 10:52
 * description :
 */
public class Luck2Activity extends BaseActivity implements LuckPanLayout.AnimationEndListener {

    private ActivityLuck2Binding binding;

    private String[] strs = BaseApp.getInstance().getResources().getStringArray(R.array.names);

    @Override
    @NotNull
    public View getApplyWindowView() {
        binding = ActivityLuck2Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
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
