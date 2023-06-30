package com.demos.marquee.core;

import android.content.Context;
import android.database.Observable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.util.Pools;

/**
 * author : JFZ
 * date : 2023/6/30 14:46
 * description : 自定义跑马灯
 */
public class MarqueeView extends FrameLayout {

    private final Pools.SimplePool<View> viewPool = new Pools.SimplePool<>(4);

    MarqueeView.Adapter mAdapter;

    private int currentPosition = -1;

    private OnMarqueeLoopListener loopListener;

    private OnMarqueeItemClickListener clickListener;

    private long duration = 3000;

    private boolean isStop = false;

    private boolean onStop = false;

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
                View firstView = getChildAt(0);
                mAdapter.exitAnim(firstView);
                currentPosition += 1;
                int itemCount = mAdapter.getItemCount();
                if (currentPosition >= itemCount) {
                    currentPosition = 0;
                }
                if (itemCount > 0) {
                    View itemView = buildItemView();
                    mAdapter.onConvert(getContext(), itemView, currentPosition);
                    MarqueeView.super.addView(itemView);
                    registerClick(itemView, currentPosition);
                    itemView.post(() -> mAdapter.enterAnim(itemView, currentPosition));
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

    void start() {
        if (mAdapter == null) return;
        if (mAdapter.getItemCount() <= 0) return;
        removeAllViews();
        currentPosition = 0;
        View view = buildItemView();
        super.addView(view);
        mAdapter.onConvert(getContext(), view, currentPosition);
        isStop = false;
        registerClick(view, currentPosition);
        view.post(() -> mAdapter.firstAnim(view, currentPosition));
    }

    private void registerClick(View view, int position) {
        view.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMarqueeItemClick(view, position);
            }
        });
    }

    private void startLoop() {
        postDelayed(run, duration);
    }

    void firstEnd() {
        if (loopListener != null) {
            loopListener.onMarqueeLoop(currentPosition);
        }
        startLoop();
    }

    void startEnd() {
        if (loopListener != null) {
            loopListener.onMarqueeLoop(currentPosition);
        }
        startLoop();
    }

    void endEnd(View view) {
        viewPool.release(view);
        removeView(view);
    }

    void stop() {
        isStop = true;
        removeCallbacks(run);
    }

    private View buildItemView() {
        View itemView = viewPool.acquire();
        if (itemView == null) {
            itemView = mAdapter.onCreateView(getContext(), currentPosition);
        }
        return itemView;
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        if (this.mAdapter != null && this.mAdapter.hasObservers()) {
            this.mAdapter.unregisterAdapterDataObserver(mObserver);
        }
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

    public static abstract class Adapter {
        private final MarqueeView.AdapterObservable mObservable = new MarqueeView.AdapterObservable();

        public abstract int getItemCount();

        public abstract View onCreateView(Context context, int position);

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

    public abstract static class AdapterDataObserver {
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

    public void onResume() {
        if (onStop && isStop && mAdapter != null && mAdapter.getItemCount() > 0) {
            onStop = false;
            startLoop();
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

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
