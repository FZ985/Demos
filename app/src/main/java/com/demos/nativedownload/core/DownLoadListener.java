package com.demos.nativedownload.core;

import java.io.File;


public interface DownLoadListener {

    void update(long progress, float percent, long contentLength, boolean done);

    void complete(File file);

    void error(Exception e);

    default void cancel() {
    }
}
