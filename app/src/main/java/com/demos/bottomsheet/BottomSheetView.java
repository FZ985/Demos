package com.demos.bottomsheet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.demos.R;
import com.demos.databinding.BottomSheetViewBinding;
import com.demos.databinding.SheetHeadBinding;

/**
 * by JFZ
 * 2024/6/24
 * desc：
 **/
public class BottomSheetView extends FrameLayout {

    private BottomSheetViewBinding binding;

    private SheetHeadBinding head;

    private final TestAdapter adapter = new TestAdapter();

    public BottomSheetView(@NonNull Context context) {
        this(context, null);
    }

    public BottomSheetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomSheetView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = BottomSheetViewBinding.inflate(LayoutInflater.from(context), this, true);
//        head = SheetHeadBinding.inflate(LayoutInflater.from(context));
//        adapter.setHeaderWithEmptyEnable(true);
//        adapter.setHeaderView(head.getRoot());

    }


    private static class TestAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        @Override
        protected void convert(@NonNull BaseViewHolder holder, String s) {

        }

        public TestAdapter() {
            super(R.layout.sheet_item_test);
        }
    }
}
