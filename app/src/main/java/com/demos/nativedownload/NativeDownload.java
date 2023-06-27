package com.demos.nativedownload;

import android.annotation.SuppressLint;

import com.demos.nativedownload.core.DefaultDownLoadStatusCallbackAdapter;
import com.demos.nativedownload.core.DownLoadBean;
import com.demos.nativedownload.core.DownLoadListener;
import com.demos.nativedownload.core.DownLoadManager;

import java.io.File;


/**
 * Create by JFZ
 * date: 2020-06-23 11:00
 **/
public class NativeDownload {

    public static void download(String url, String path, String fileName, DownLoadListener call) {
        DownLoadBean bean = new DownLoadBean(fileName, path, url, 0, 0);
        DownLoadManager.getInstance().downLoad(bean, new DefaultDownLoadStatusCallbackAdapter<DownLoadBean>() {
            private long sleep;

            @Override
            public void onStart(DownLoadBean bean) {
                super.onStart(bean);
            }

            @Override
            public void onError(DownLoadBean bean) {
                super.onError(bean);
                if (call != null) {
                    call.error(new Exception(bean.error));
                }
            }

            @Override
            public void onFinished(final DownLoadBean bean) {
                super.onFinished(bean);
                if (call != null) {
                    call.complete(new File(bean.getPath()));
                }
            }

            @Override
            public void onCancel(DownLoadBean bean) {
                super.onCancel(bean);
                if (call != null) {
                    call.cancel();
                }
            }

            @Override
            public void onProgress(final DownLoadBean bean, final long currentSize) {
                super.onProgress(bean, currentSize);
                long current = bean.currentSize;
                if (currentSize > current) {
                    current = currentSize;
                }
                long total = bean.fileSize;
                if (total <= 0) {
                    return;
                }
                float percent = (float) (current * 100f / total);
                long t_temp = System.currentTimeMillis();
                if (call != null && (t_temp - sleep > 300)) {
                    sleep = t_temp;
                    call.update(bean.currentSize, number2(percent), total, bean.done);
                }
            }

            @Override
            public void onPrepare(DownLoadBean bean) {
                super.onPrepare(bean);
                sleep = System.currentTimeMillis();
            }
        });
    }


    @SuppressLint("DefaultLocale")
    private static float number2(float f) {
        return Float.parseFloat(String.format("%.2f", f));
    }
}
