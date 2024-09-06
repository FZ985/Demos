package com.demos;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ZzzTestBinding;

/**
 * by JFZ
 * 2024/7/8
 * desc：
 **/
public class TestActivity extends AppCompatActivity {

    private ZzzTestBinding binding;

    private boolean isChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ZzzTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.v2.setOnClickListener(v -> {
            Toast.makeText(this, "v2", Toast.LENGTH_SHORT).show();
        });

        binding.v3.setOnClickListener(v -> {
            Toast.makeText(this, "v3", Toast.LENGTH_SHORT).show();
        });
        binding.change.setOnClickListener(v -> {
//            ConstraintSet set = new ConstraintSet();
//
//            set.clone(binding.cl);


            View v2 = binding.v2;

            View v3 = binding.v3;
            if (!isChange) {

                isChange = true;
//
//                set.connect(R.id.v3, ConstraintSet.TOP, R.id.v1, ConstraintSet.BOTTOM);
//
//                set.connect(R.id.v2, ConstraintSet.TOP, R.id.v3, ConstraintSet.BOTTOM);
//

                binding.cl.removeView(v2);
                binding.cl.removeView(v3);
                binding.cl.addView(v3,0);
                binding.cl.addView(v2);


            } else {
                isChange = false;

                binding.cl.removeView(v2);
                binding.cl.removeView(v3);
                binding.cl.addView(v2,0);
                binding.cl.addView(v3);

//                set.connect(R.id.v2, ConstraintSet.TOP, R.id.v1, ConstraintSet.BOTTOM);
//
//                set.connect(R.id.v3, ConstraintSet.TOP, R.id.v2, ConstraintSet.BOTTOM);
            }
//
//            TransitionManager.beginDelayedTransition(binding.cl);
//
//            set.applyTo(binding.cl);


        });
    }
}
