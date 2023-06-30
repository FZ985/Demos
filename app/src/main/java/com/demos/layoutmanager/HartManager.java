package com.demos.layoutmanager;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.demos.Logger;

/**
 * author : JFZ
 * date : 2023/6/27 13:54
 * description :
 */
public class HartManager extends BaseLayoutManager {

    private int mTotalHeight;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

//    @Override
//    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
//        int widthMode = View.MeasureSpec.getMode(widthSpec);
//        int widthSize = View.MeasureSpec.getSize(widthSpec);
//        int heightMode = View.MeasureSpec.getMode(heightSpec);
//        int heightSize = View.MeasureSpec.getSize(heightSpec);
//
//        int width = 0;
//        int height = 0;
//
//        for (int i = 0; i < getItemCount(); i++) {
//            View view = recycler.getViewForPosition(i);
//            measureChildWithMargins(view, 0, 0);
//            int childWidth = getDecoratedMeasuredWidth(view);
//            int childHeight = getDecoratedMeasuredHeight(view);
//
//            width = Math.max(width, childWidth);
//            Logger.e("===child:"+childHeight);
//            height += childHeight;
//        }
//
//        if (widthMode == View.MeasureSpec.EXACTLY) {
//            width = widthSize;
//        }
//
//        if (heightMode == View.MeasureSpec.EXACTLY) {
//            Logger.e("====height1111:"+height);
//            height = heightSize;
//        }
//        Logger.e("===height:"+height);
//        setMeasuredDimension(width, height);
//    }

//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        detachAndScrapAttachedViews(recycler);
//        int itemCount = getItemCount();
//        Logger.e("count:" + itemCount);
//        if (itemCount == 0) {
//            return;
//        }
//
//        int centerX = getWidth() / 2;
//        int centerY = getHeight() / 2;
//        Logger.e("centerx:" + centerX + ",centery:" + centerY);
//        int radius = Math.min(centerX, centerY);
//
////        for (int i = 0; i < itemCount; i++) {
////            View view = recycler.getViewForPosition(i);
////            addView(view);
////
////            float angle = (float) (i * 2 * Math.PI / itemCount);
////            int x = (int) (centerX + radius * Math.sin(angle));
////            int y = (int) (centerY - radius * Math.cos(angle));
////
////            measureChildWithMargins(view, 0, 0);
////            int width = getDecoratedMeasuredWidth(view);
////            int height = getDecoratedMeasuredHeight(view);
////            layoutDecoratedWithMargins(view, x - width / 2, y - height / 2, x + width / 2, y + height / 2);
////        }
//
////        int rows = calculateRows2(getItemCount()); // 三角形的行数
////        int index = 0; // 数组元素的索引
////        for (int i = 0; i <= rows; i++) {
////            for (int j = 0; j <= 3; j++) {
////                if (index < getItemCount()) {
////                    View view = recycler.getViewForPosition(index);
////                    addView(view);
////                    measureChildWithMargins(view, 0, 0);
////
////                    int width = getDecoratedMeasuredWidth(view);
////                    int height = getDecoratedMeasuredHeight(view);
////
////                    int top = height * i;
////                    int left = width * j;
////
//////                        left = (getWidth() - width* j) / 2;
//////                        Logger.e("=====:" + left);
////
////                    layoutDecorated(view, left, top, width + left, height * i + height);
////                    index++;
////                }
////            }
////        }
//
//        if (getItemCount() == 0) {
//            removeAndRecycleAllViews(recycler);
//            return;
//        }
//
//        if (getChildCount() == 0 && state.isPreLayout()) {
//            return;
//        }
//
//        detachAndScrapAttachedViews(recycler);
//
//        int offsetY = 0;
//        int offsetX = 0;
//
//        for (int i = 0; i < state.getItemCount(); i++) {
//            View view = recycler.getViewForPosition(i);
//            //测量子View
//            measureChildWithMargins(view, 0, 0);
//            addView(view);
//
//            int left, top, right, bottom;
//            left = getPaddingLeft() + offsetX;
//            top = getPaddingTop() + offsetY;
//            right = left + getDecoratedMeasuredWidth(view);
//            bottom = top + getDecoratedMeasuredHeight(view);
//
//            layoutDecorated(view, left, top, right, bottom);
//
//            offsetY += getDecoratedMeasuredHeight(view);
//            offsetX += getDecoratedMeasuredWidth(view);
//        }
//
//
////        for (int i = 0; i < itemCount; i++) {
////            View view = recycler.getViewForPosition(i);
////            addView(view);
////
////            measureChildWithMargins(view, 0, 0);
////            int width = getDecoratedMeasuredWidth(view);
////            int height = getDecoratedMeasuredHeight(view);
////
////            int top = height * i;
////            Logger.e("top:" + top);
////            layoutDecorated(view, 0, top, width, height * i + height);
////        }
//
//
//    }


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
        //将所有的View先从RecyclerView中detach，然后放入Scrap中进行缓存，方便复用
        detachAndScrapAttachedViews(recycler);

//        int centerX = getWidth() / 2;
//        int centerY = getHeight() / 2;
//        Logger.e("centerx:" + centerX + ",centery:" + centerY);
//        int radius = Math.min(centerX, centerY);
//        for (int i = 0; i < itemCount; i++) {
//            View view = recycler.getViewForPosition(i);
//            addView(view);
//
//            float angle = (float) (i * 2 * Math.PI / itemCount);
//            int x = (int) (centerX + radius * Math.sin(angle));
//            int y = (int) (centerY - radius * Math.cos(angle));
//            Logger.e("x:"+x+",y:"+y);
//            measureChildWithMargins(view, 0, 0);
//            int width = getDecoratedMeasuredWidth(view);
//            int height = getDecoratedMeasuredHeight(view);
//            layoutDecoratedWithMargins(view, x - width / 2, y - height / 2, x + width / 2, y + height / 2);
//        }

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
//        offsetChildrenVertical(-dy);
//        return dy;
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
