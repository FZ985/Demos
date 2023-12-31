package com.demos.marquee.core;

import android.content.Context;
import android.database.Observable;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.util.Pools;

import java.util.HashMap;
import java.util.Map;

/**
 * author : JFZ
 * date : 2023/6/30 14:46
 * description : 自定义跑马灯
 */
@SuppressWarnings("unused")
public class MarqueeView extends FrameLayout {

    //单类型使用
    private final Pools.SimplePool<View> viewPool = new Pools.SimplePool<>(4);
    //多类型使用
    private final Map<Integer, View> sparseArray = new HashMap<>();
    private final SparseIntArray viewTypeCache = new SparseIntArray();

    MarqueeView.Adapter mAdapter;//跑马灯适配器

    private int currentPosition = -1;//当前显示的角标

    private int oldPosition = -1;//避免 OnMarqueeLoopListener 重复回调

    private OnMarqueeLoopListener loopListener;//item position 轮播事件

    private OnMarqueeItemClickListener clickListener;//item点击事件

    private long duration = 3000;//轮播时间间隔

    private boolean isStop = false;//开始 与 结束 标志位

    private boolean onStop = false;//生命周期的标志位

    private boolean isLoop = true;//是否开启轮询

    private boolean isMarquee = true;//是否开启跑马灯

    private boolean isRunning = false;//是否在运行中

    private boolean isNextEnd = false;//下一个结束的标志位，解决快速next时的崩溃问题

    public interface OnMarqueeLoopListener {

        void onMarqueeLoop(int position);
    }

    public interface OnMarqueeItemClickListener {

        void onMarqueeItemClick(View view, int position);
    }

    private final AdapterDataObserver mObserver = new ViewDataObserverImpl();

    public MarqueeView(Context context) {
        super(context);
        removeAllViews();
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        removeAllViews();
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        removeAllViews();
    }

    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            int childCount = getChildCount();
            if (childCount > 0) {
                int itemCount = mAdapter.getItemCount();
                View firstView = getChildAt(0);
                currentPosition += 1;
                if (currentPosition >= itemCount) {
                    if (isLoop) {
                        currentPosition = 0;
                    } else {
                        isRunning = false;
                        return;
                    }
                }
                mAdapter.exitAnim(firstView);
                if (itemCount > 0) {
                    View itemView = buildItemView();
                    if (itemView != null) {
                        mAdapter.onConvert(getContext(), itemView, currentPosition);
                        MarqueeView.super.addView(itemView);
                        registerClick(itemView, currentPosition);
                        itemView.post(() -> mAdapter.enterAnim(itemView, currentPosition));
                    }
                }
            }
        }
    };

    @Override
    public void addView(View child) {

    }

    @Override
    public void addView(View child, int width, int height) {

    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void start() {
        if (mAdapter == null) return;
        if (mAdapter.getItemCount() <= 0) return;
        if (currentPosition == -1) {
            sparseArray.clear();
            viewTypeCache.clear();
            removeAllViews();
            currentPosition = 0;
            View view = buildItemView();
            if (view != null) {
                super.addView(view);
                mAdapter.onConvert(getContext(), view, currentPosition);
                isStop = false;
                registerClick(view, currentPosition);
                view.post(() -> mAdapter.firstAnim(view, currentPosition));
            }
        } else {
            startLoop(10);
        }
    }

    private void registerClick(View view, int position) {
        if (clickListener != null) {
            view.setOnClickListener(v -> clickListener.onMarqueeItemClick(view, position));
        }
    }

    private void startLoop(long duration) {
        isStop = false;
        isRunning = true;
        isNextEnd = false;
        if (loopListener != null && oldPosition != currentPosition && currentPosition < mAdapter.getItemCount()) {
            oldPosition = currentPosition;
            loopListener.onMarqueeLoop(currentPosition);
        }
        postDelayed(run, duration);
    }

    void firstEnd() {
        isNextEnd = true;
        startEnd();
    }

    void startEnd() {
        if (isMarquee)
            startLoop(duration);
        else {
            isRunning = false;
            if (loopListener != null && oldPosition != currentPosition && currentPosition < mAdapter.getItemCount()) {
                oldPosition = currentPosition;
                loopListener.onMarqueeLoop(currentPosition);
            }
        }
    }

    void endEnd(View view) {
        try {
            isNextEnd = true;
            if (viewTypeCache.size() > 1 && mAdapter.isMultiType()) {
                System.out.println("multiType end");
            } else {
                viewPool.release(view);
            }
            removeView(view);
        } catch (Exception e) {
            removeViewAt(0);
            Log.e("catch", e.getMessage());
        }
    }

    void stop() {
        isStop = true;
        isRunning = false;
        removeCallbacks(run);
    }

    private View buildItemView() {
        boolean isMultiType = mAdapter.isMultiType();
        if (isMultiType) {
            //至少需要两个类型才能使用所类型布局
            viewTypeCache.clear();
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                int viewType = mAdapter.getItemViewType(i);
                viewTypeCache.put(viewType, viewType);
            }
            if (viewTypeCache.size() > 1) {
                int itemViewType = mAdapter.getItemViewType(currentPosition);
                if (sparseArray.containsKey(itemViewType)) {
                    return sparseArray.get(itemViewType);
                } else {
                    View itemView = mAdapter.onCreateView(getContext(), currentPosition);
                    sparseArray.put(itemViewType, itemView);
                    return itemView;
                }
            }
        } else {
            View itemView = viewPool.acquire();
            if (itemView == null) {
                itemView = mAdapter.onCreateView(getContext(), currentPosition);
            }
            return itemView;
        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        if (this.mAdapter != null && this.mAdapter.hasObservers()) {
            this.mAdapter.unregisterAdapterDataObserver(mObserver);
        }
        viewTypeCache.clear();
        sparseArray.clear();
        super.onDetachedFromWindow();
    }

    private class ViewDataObserverImpl extends MarqueeView.AdapterDataObserver {
        @Override
        public void onChanged(boolean isFirst) {
            if (isFirst) {
                start();
            }
        }

        @Override
        public void firstAnimEnd() {
            firstEnd();
        }

        @Override
        public void startAnimEnd() {
            startEnd();
        }

        @Override
        public void endAnimEnd(View view) {
            endEnd(view);
        }
    }

    static class AdapterObservable extends Observable<MarqueeView.AdapterDataObserver> {

        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged(boolean isFirst) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged(isFirst);
            }
        }

        public void firstAnimEnd() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).firstAnimEnd();
            }
        }

        public void startAnimEnd() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).startAnimEnd();
            }
        }

        public void endAnimEnd(View view) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).endAnimEnd(view);
            }
        }
    }

    abstract static class AdapterDataObserver {
        public void onChanged(boolean isFirst) {
            // Do nothing
        }

        public void firstAnimEnd() {
            // Do nothing
        }

        public void startAnimEnd() {
            // Do nothing
        }

        public void endAnimEnd(View view) {
            // Do nothing
        }
    }

    public static abstract class Adapter {
        private final MarqueeView.AdapterObservable mObservable = new MarqueeView.AdapterObservable();

        public abstract int getItemCount();

        public abstract View onCreateView(Context context, int position);

        public int getItemViewType(int position) {
            return 0;
        }

        public boolean isMultiType() {
            return false;
        }

        public abstract void onConvert(Context context, View view, int position);

        public void firstAnim(View view, int position) {
            enterAnim(view, position);
        }

        public abstract void enterAnim(View view, int position);

        public abstract void exitAnim(View view);

        public void registerAdapterDataObserver(@NonNull MarqueeView.AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(@NonNull MarqueeView.AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        public final void notifyChanged(boolean isFirst) {
            mObservable.notifyChanged(isFirst);
        }

        public void animEndForFirst() {
            mObservable.firstAnimEnd();
        }

        public void animEndForStart() {
            mObservable.startAnimEnd();
        }

        public void animEndForEnd(View view) {
            mObservable.endAnimEnd(view);
        }

        public final boolean hasObservers() {
            return mObservable.hasObservers();
        }
    }

    public void onResume() {
        if (onStop && isStop && mAdapter != null && mAdapter.getItemCount() > 0) {
            onStop = false;
            startLoop(duration);
        }
    }

    public void onStop() {
        onStop = true;
        stop();
    }

    public void setAdapter(MarqueeView.Adapter adapter) {
        this.mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerAdapterDataObserver(mObserver);
        }
    }

    public void setLoopListener(OnMarqueeLoopListener loopListener) {
        this.loopListener = loopListener;
    }

    public void setOnItemClickListener(OnMarqueeItemClickListener listener) {
        this.clickListener = listener;
    }

    //设置时间间隔
    public void setDuration(long duration) {
        this.duration = duration;
    }

    //设置是否 开启跑马灯
    public void setMarquee(boolean marquee) {
        isMarquee = marquee;
        if (isMarquee && !isRunning) {
            startEnd();
        }
    }

    //手动下一个,只有为开启跑马灯的时候才可以
    public void next() {
        if (!isMarquee) {
            if (isRunning) {
                stop();
            }
            if (isNextEnd) {
                start();
            }
        }
    }

    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        if (isLoop && !isRunning) {
            start();
        }
    }
}