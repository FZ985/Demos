package com.demos.bottomsheet;

import android.content.Context;

import androidx.annotation.NonNull;

import com.demos.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.lxj.xpopup.impl.FullScreenPopupView;

/**
 * by JFZ
 * 2024/6/24
 * desc：
 **/
public class BottomSheetPop1 extends FullScreenPopupView {

    private BottomSheetBehavior behavior;
    private BottomSheetView sheet_ll;

    public BottomSheetPop1(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        sheet_ll = findViewById(R.id.sheet_ll);
        behavior = BottomSheetBehavior.from(sheet_ll);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.sheet_pop;
    }
}
