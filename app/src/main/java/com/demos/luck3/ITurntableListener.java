package com.demos.luck3;

/**
 * Created by shaohuachen on 2019/1/13.
 */

public interface ITurntableListener {
    void onStart();

    void onEnd(int position, String name);
}
