package com.demos.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * by JFZ
 * 2024/9/06
 * desc：使用glide 将图片加载并保存到本地相册中的帮助类
 **/
public class GlideSaveImageUtil {


    private static final String Tag = "GlideSaveImageUtil";

    /**
     * 保存静态图片 （jpeg,png）
     *
     * @param context  上下文
     * @param imageUrl 图片地址
     * @return 保存成功 返回 uri
     */
    @Nullable
    public static Uri saveImage(Context context, String imageUrl) {
        return saveImage(context, imageUrl, String.valueOf(System.currentTimeMillis()), "无描述", ImageMime.JPEG);
    }

    /**
     * 保存静态图片 （jpeg,png）
     *
     * @param context   上下文
     * @param imageUrl  图片地址
     * @param title     标题
     * @param desc      描述
     * @param imageMime 图片类型
     * @return 保存成功 返回 uri
     */
    @Nullable
    public static Uri saveImage(Context context, String imageUrl, String title, String desc, ImageMime imageMime) {
        try {
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get();
            Uri uri = saveBitmap(context, bitmap, title, desc, imageMime);
            if (uri != null) {
                Log.e(Tag, "保存成功:" + uri);
            }
            return uri;
        } catch (Exception e) {
            Log.e(Tag, "保存失败:" + e.getMessage());
            return null;
        }
    }


    /**
     * 保存gif 图片
     *
     * @param context  上下文
     * @param imageUrl gif图片地址
     * return 保存成功 返回 uri
     */
    public static void saveGif(Context context, String imageUrl, Callback<Uri> callback) {
        saveGif(context, imageUrl, String.valueOf(System.currentTimeMillis()), "无描述", callback);
    }

    final static Handler handler = new Handler(Looper.getMainLooper());


    /**
     * @param context  上下文
     * @param imageUrl gif图片地址
     * @param title    标题
     * @param desc     描述
     * return 保存成功 返回 uri
     */
    public static void saveGif(Context context, String imageUrl, String title, String desc, Callback<Uri> callback) {
        new Thread(() -> {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, title);
            values.put(MediaStore.Images.Media.DESCRIPTION, desc);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            try {
                File file = Glide.with(context)
                        .asFile()
                        .load(imageUrl)
                        .submit()
                        .get();

                File destinationFile = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        System.currentTimeMillis() + ".gif");

                copyFile(file, destinationFile);

                if (uri != null) {
                    // 将GIF文件写入到获取的URI中
                    try (FileInputStream inputStream = new FileInputStream(destinationFile);
                         OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            inputStream.close();
                            outputStream.close();
                            Log.e(Tag, "保存成功:" + uri);
                            handler.post(() -> callback.apply(uri));
                            return;
                        }
                    } catch (Exception e) {
                        // 如果写入失败，则删除插入的记录
                        context.getContentResolver().delete(uri, null, null);
                        Log.e(Tag, "保存失败2:" + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e(Tag, "保存失败1:" + e.getMessage());
            }
            handler.post(() -> callback.apply(null));
        }).start();
    }


    /**
     * 保存bitmap 到本地相册
     *
     * @param context 上下文
     * @param bitmap  bitmap
     * @param title   标题
     * @param desc    描述
     * @param mime    类型
     * @return 保存成功 返回 uri
     */
    public static Uri saveBitmap(Context context, Bitmap bitmap, String title, String desc, ImageMime mime) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, desc);
        values.put(MediaStore.Images.Media.MIME_TYPE, mime.getMime());
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(mime.getFormat(), 100, outputStream);
                    outputStream.close();
                }
            } catch (Exception e) {
                context.getContentResolver().delete(uri, null, null);
            }
        }
        return uri;
    }


    public interface Callback<T> {

        void apply(T t);

    }

    public enum ImageMime {
        JPEG(Bitmap.CompressFormat.JPEG.ordinal(), "image/jpeg"),

        PNG(Bitmap.CompressFormat.PNG.ordinal(), "image/png");

        private final int id;
        private final String mime;

        // 构造函数
        ImageMime(int id, String mime) {
            this.id = id;
            this.mime = mime;
        }

        public int getId() {
            return id;
        }


        public Bitmap.CompressFormat getFormat() {
            if (id == 0) {
                return Bitmap.CompressFormat.JPEG;
            }
            if (id == 1) {
                return Bitmap.CompressFormat.PNG;
            }
            return Bitmap.CompressFormat.JPEG;
        }

        public String getMime() {
            return mime;
        }
    }


    private static void copyFile(File sourceFile, File destinationFile) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
