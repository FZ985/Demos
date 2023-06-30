package com.demos.layoutmanager;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.demos.Logger;

/**
 * author : JFZ
 * date : 2023/6/27 13:54
 * description :
 */
public class TestGridManager extends BaseLayoutManager {

    private int mTotalHeight;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }
        int itemCount = getItemCount();
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);

        int mWidth = getWidth();
        int offsetY = 0;
        mSumDy = 0;
        for (int i = 0; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            int left = 0, top = 0, right = 0, bottom = 0;
            if (i == 0) {
                left = mWidth / 2 - width / 2;
                top = 0;
                right = left + width;
                bottom = top + height;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
                offsetY = offsetY + bottom;
            } else if (i == 1) {
                left = mWidth / 4 - width / 2;
                top = height / 2;
                right = left + width;
                bottom = top + height;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
                offsetY = offsetY + bottom;
            } else if (i == 2) {
                left = mWidth * 3 / 4 - width / 2;
                top = height / 2;
                right = left + width;
                bottom = top + height;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
            } else {
//                int row = ((itemCount - 3) + 4 - 1) / 4;
                int inRow = ((i - 3) / 4 + 1);
                int column = (i - 3) % 4 + 1;
//                left = column * (mWidth / 4);
                left = column * (mWidth / 4) - width;
                if (column % 2 == 0) {
                    left = left - width / 3;
                }
                top = inRow * height + height / 2;
                right = left + width;
                bottom = top + height;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
            }
        }
        if (itemCount > 3) {
            int row = ((itemCount - 3) + 4 - 1) / 4;
            View view = recycler.getViewForPosition(3);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int height = getDecoratedMeasuredHeight(view);
            offsetY = offsetY + row * height - height;
        }
        mTotalHeight = Math.max(offsetY, getRecyclerRealHeight());
        Logger.e("total:" + mTotalHeight);
    }

    private int getRecyclerRealHeight() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private int mSumDy;

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offsety = dy;
        if (mSumDy + offsety < 0) {
            offsety = -mSumDy;
        } else if (mSumDy + offsety > mTotalHeight - getRecyclerRealHeight()) {
            offsety = mTotalHeight - getRecyclerRealHeight() - mSumDy;
        }
        mSumDy += offsety;
        offsetChildrenVertical(-offsety);
        return dy;
    }

}
