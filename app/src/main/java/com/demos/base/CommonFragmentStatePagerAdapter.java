package com.demos.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Description:
 * Author: jfz
 * Date: 2020-12-18 14:58
 */
public class CommonFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> frags;

    public CommonFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<Fragment> frags) {
        super(fm);
        this.frags = frags;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return frags.get(position);
    }

    @Override
    public int getCount() {
        return frags == null ? 0 : frags.size();
    }
}