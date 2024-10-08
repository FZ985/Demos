package com.demos.nativedownload.core;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 下载管理器<br/>
 */
public class DownLoadManager {
    private DownLoadManager() {
    }

    private ExecutorService THREAD_POOL;

    public static DownLoadManager getInstance() {
        return DownLoadManagerHolder.INSTANCE;
    }

    private static final class DownLoadManagerHolder {
        private static volatile DownLoadManager INSTANCE = new DownLoadManager();
    }

    private DownLoadBean bean;

    public void downLoad(DownLoadBean bean,
                         DownLoadStatusCallback<DownLoadBean> mCallBack) {
        this.bean = bean;
        if (THREAD_POOL == null) {
            THREAD_POOL = Executors.newSingleThreadExecutor();
            Log.e("download", "THREAD_POOL is Null");
        }
        if (THREAD_POOL.isShutdown() || THREAD_POOL.isTerminated()) {
            Log.e("download", "exec task...........");
        } else {
            Log.e("download", "has task...........");
        }
        DownLoadTask downLoadTask = new DownLoadTask(this.bean, mCallBack);
        THREAD_POOL.execute(downLoadTask);
    }

    public void destroy() {
        IS_DOWN_LOADING = false;
        Log.e("download", "##销毁下载器##");
        if (DownLoadManagerHolder.INSTANCE != null) {
            if (bean != null) {
                bean.status = Status.CANCEL;
                if (THREAD_POOL != null) {
                    Log.e("download", "##shutdownNow##");
                    THREAD_POOL.shutdownNow();
                    THREAD_POOL = null;
                }
            } else {
                Log.e("download", "##bean is null##");
            }
            DownLoadManagerHolder.INSTANCE = null;
        }
    }

    private static boolean IS_DOWN_LOADING = false;

    public static class DownLoadTask implements Runnable {

        private final DownLoadBean bean;
        private final DownLoadStatusCallback<DownLoadBean> mCallBack;
        private RandomAccessFile raf;
        private final File mStoreFile;
        private HttpURLConnection connection;

        public DownLoadTask(DownLoadBean bean,
                            DownLoadStatusCallback<DownLoadBean> mCallBack) {
            this.bean = bean;
            this.mCallBack = mCallBack;
            mStoreFile = new File(bean.getPath());
        }

        @Override
        public void run() {
            if (IS_DOWN_LOADING) {
                // LogTrackerHelper.with(LogTaskParam.SYSTEM_MSG).exec(
                // LogTrackerHelper.logDetail() + "##下载中##");
                Log.e("download", "下载中....");
                return;
            }
            Log.e("download", "下载开始run..................");
            IS_DOWN_LOADING = true;
            if (mCallBack != null) {
                mCallBack.onStart(bean);
                // 当前下载的进度
                long compeleteSize = 0;
                Log.e("download", "bean:getTempPath:" + bean.getTempPath());
                File file = new File(bean.getTempPath());// 获取下载文件
                if (!file.exists()) {
                    // 如果文件不存在
                    bean.currentSize = 0;
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 如果存在就拿当前文件的长度，设置当前下载长度
                    // (这样的好处就是不用每次在下载文件的时候都需要写入数据库才能记录当前下载的长度，一直操作数据库是很费资源的)
                    compeleteSize = file.length();
                    bean.currentSize =  compeleteSize;
                }
                mCallBack.onPrepare(bean);
                try {
                    URL url = new URL(bean.downloadUrl);
                    Log.e("download", "#下载地址是#" + bean.downloadUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("connection", "close");
                    connection
                            .setRequestProperty("Accept-Encoding", "identity");
                    connection.setRequestProperty("Keep-Alive", "false");
                    System.setProperty("http.keepAlive", "false");
                    connection.setConnectTimeout(30 * 1000);
                    connection.setReadTimeout(1000000000);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Range", "bytes="
                            + compeleteSize + "-" /* + bean.appSize */);
                    // 获取的状态码
                    int code = connection.getResponseCode();
                    int contentLength = connection.getContentLength();
                    String mContentRange = connection
                            .getHeaderField("Content-Range");
                    if (!TextUtils.isEmpty(mContentRange)) {
                        int realLength = Integer.parseInt(mContentRange
                                .substring(mContentRange.lastIndexOf("/") + 1));
                        if (bean != null && bean.fileSize == 0) {
                            bean.fileSize = realLength;
                        }
                    }
                    Log.e("download", "mContentRange##=" + mContentRange + "##"
                            + "\n##=" + bean.fileSize + "##"
                            + "\ncontentLength##=" + contentLength + "##");
                    // 判断是否能够断点下载
                    Log.e("download", "download_code:" + code);
                    if (code == 206) {
                        raf = new RandomAccessFile(file, "rw");
                        raf.seek(compeleteSize);
                        // 将要下载的文件写到保存在保存路径下的文件中
                        InputStream is = connection.getInputStream();
                        BufferedInputStream bufferRead = new BufferedInputStream(
                                is);
                        byte[] buffer = new byte[1024 * 1024];
                        int length = -1;
                        // 进入下载中状态
                        // bean.downloadState = STATE_DOWNLOADING;
                        // DataBaseUtil.UpdateDownLoadById(bean);
                        while ((length = bufferRead.read(buffer)) != -1) {
                            if (Status.CANCEL.equals(bean.status)) {
                                IS_DOWN_LOADING = false;
                                mCallBack.onCancel(bean);
                                return;
                            }
                            raf.write(buffer, 0, length);
                            compeleteSize += length;
                            // 用消息将下载信息传给进度条，对进度条进行更新
                            bean.setCurrentSize(compeleteSize);
                            Log.e("download", "       length=" + length);
                            Log.e("download", "   threadname="
                                    + Thread.currentThread().getName());
                            Log.e("download", "  currentSize=" + bean.currentSize);
                            Log.e("download", "compeleteSize=" + compeleteSize);
                            mCallBack.onProgress(bean, compeleteSize);
                        }
                        if (bean.fileSize == bean.currentSize) {
                            if (file.canRead() && file.length() > 0) {
                                Log.e("download", "bean:file:" + file.getPath());
                                Log.e("download", "bean:mStoreFile:" + mStoreFile);

                                if (file.renameTo(mStoreFile)) {
                                    IS_DOWN_LOADING = false;
                                    bean.done = true;
                                    mCallBack.onFinished(bean);
                                } else {
                                    bean.error = "renameTo failed";
                                    IS_DOWN_LOADING = false;
                                    mCallBack.onError(bean);
                                    file.delete();
                                }
                            } else {
                                IS_DOWN_LOADING = false;
                                bean.error = "file status" + file.canRead()
                                        + " or length=" + file.length();
                                mCallBack.onError(bean);
                            }
                        } else {
                            IS_DOWN_LOADING = false;
                            mCallBack.onStop(bean); // 没下载完,停止了
                        }
                    } else {
                        IS_DOWN_LOADING = false;
                        bean.error = "unSupport this download.";
                        mCallBack.onError(bean);
                    }
                } catch (IOException e) {
                    IS_DOWN_LOADING = false;
                    bean.error = "" + e.getMessage();
                    mCallBack.onError(bean);
                    file.delete();
                    e.printStackTrace();
                } finally {
                    try {
                        if (connection != null) {
                            connection.disconnect();
                        }
                        if (raf != null) {
                            raf.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // mCallBack.onSuccess(bean);
            }
            IS_DOWN_LOADING = false;
        }
    }
}
