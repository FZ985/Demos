package com.demos.viewpager.toplinkcustom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demos.Tools;
import com.demos.databinding.FragmentHomeChildBinding;

/**
 * Description:
 * Author: jfz
 * Date: 2020-12-18 14:54
 */
public class ChildFragment extends Fragment {
    private FragmentHomeChildBinding binding;
    private int index;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeChildBinding.inflate(inflater, null, false);
        View view = binding.getRoot();
        init();
        return view;
    }

    private void init() {
        index = getArguments().getInt("index", 0);
        binding.childTv.setText(String.valueOf(index));
        binding.childTv.setBackgroundColor(Tools.randomColor());
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    public static ChildFragment instance(int index) {
        ChildFragment fragment = new ChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }
}