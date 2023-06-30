package com.demos.layoutmanager;


import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RectangleLayoutManager extends RecyclerView.LayoutManager {
    private int rows; // 行数

    private int columns; // 列数


    public RectangleLayoutManager(Context context, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    @Override

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) {
            return;
        }

        int width = getWidth() / columns;
        int height = getHeight() / rows;

        for (int i = 0; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            //测量子View
            measureChildWithMargins(view, 0, 0);
            addView(view);
            int widthV= getDecoratedMeasuredWidth(view);
            int heightV = getDecoratedMeasuredHeight(view);

            int row = i / columns;
            int column = i % columns;
            int left = column * width;
            int top = row * heightV;
            int right = left + width;
            int bottom = top + heightV;
            layoutDecorated(view, left, top, right, bottom);
        }
    }
}

