package com.demos.marquee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.demos.R;
import com.demos.base.animator.ViewAnimator;
import com.demos.marquee.core.MarqueeView;

import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/30 16:11
 * description :
 */
public class MarqueeExampleAdapter extends MarqueeView.Adapter {
    private List<String> data;

    public void setData(List<String> data) {
        this.data = data;
        notifyChanged(true);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public View onCreateView(Context context, int position) {
        return LayoutInflater.from(context).inflate(R.layout.item_marquee, null);
    }

    @Override
    public void onConvert(Context context, View view, int position) {
        TextView tv = view.findViewById(R.id.tv);
        tv.setText(data.get(position));
    }

    @Override
    public void enterAnim(View view, int position) {
        view.setVisibility(View.VISIBLE);
        ViewAnimator.animate(view)
                .translationY(-view.getHeight(), 0)
                .alpha(0f, 1f)
                .duration(500)
                .onStop(() -> {
                    //要告诉adapter动画结束了，才能开始下一个
                    if (position == 0) {
                        animEndForFirst();
                    } else {
                        animEndForStart();
                    }
                }).start();

//        ViewAnimator.animate(view)
//                .pivotX(0)
//                .pivotY(0)
//                .scaleX(0, 1)
//                .scaleY(0, 1)
//                .alpha(0f, 1f)
//                .duration(500)
//                .onStop(new AnimationListener.Stop() {
//                    @Override
//                    public void onStop() {
//                        if (position == 0) {
//                            animEndForFirst();
//                        } else {
//                            animEndForStart();
//                        }
//                    }
//                })
//                .start();
    }

    @Override
    public void exitAnim(View view) {
        ViewAnimator.animate(view)
                .alpha(1f, 0f)
                .translationY(0, view.getHeight())
                .duration(500)
                .onStop(() -> {
                    animEndForEnd(view);
                    view.setVisibility(View.GONE);
                    view.setTranslationY(0);
                }).start();
    }
}
