package com.demos.utils.camera;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * by DAD ZZ
 * 2025/7/21
 * desc：
 **/
public class CaptureIntentHelper {

    private Uri uri;
    private File tempFile;
    private String mimeType = "";

    private final InnerCameraFragmentHelper innerCameraHelper = new InnerCameraFragmentHelper();

    public void startCapture(Activity activity, boolean isVideo, int code) {
        try {
            activity.startActivityForResult(getIntent(activity, isVideo), code);
            //拍出的照片在页面销毁是否删除,需要则放开这行代码
            //needDelete(activity);
        } catch (Exception e) {
            Toast.makeText(activity, "打开手机相机失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void startCaptureByFragment(Fragment fragment, boolean isVideo, int code) {
        try {
            fragment.startActivityForResult(getIntent(fragment.getActivity(), isVideo), code);
            //拍出的照片在页面销毁是否删除,需要则放开这行代码
            //needDelete(fragment.getActivity());
        } catch (Exception e) {
            Toast.makeText(fragment.getActivity(), "打开手机相机失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void startCaptureWithCall(AppCompatActivity activity, InnerCameraFragmentHelper.Fun2<String, Uri> success) {
        innerCameraHelper.startCamera(activity, success);
        //拍出的照片在页面销毁是否删除,需要则放开这行代码
        //needDelete(activity);
    }


    //拍出的照片在页面销毁是否删除
    private DefaultLifecycleObserver deleteObserver;

    private void needDelete(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            if (deleteObserver != null) {
                ((AppCompatActivity) activity).getLifecycle().removeObserver(deleteObserver);
                deleteObserver = null;
            }
            deleteObserver = new DefaultLifecycleObserver() {
                @Override
                public void onDestroy(@NonNull LifecycleOwner owner) {
                    if (activity != null) {
                        delete(activity.getApplicationContext());
                    }
                }
            };
            ((AppCompatActivity) activity).getLifecycle().addObserver(deleteObserver);
        }
    }

    @Nullable
    public Uri getUri() {
        if (tempFile != null) {
            return Uri.fromFile(tempFile);
        }
        return uri;
    }

    public void delete(Context context) {
        if (uri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                int delete = context.getContentResolver().delete(uri, null, null);
            } catch (Exception e) {
                Log.e("CaptureIntentHelper", "删除失败:" + e.getMessage());
            }
        }
        if (tempFile != null && tempFile.exists()) {
            try {
                tempFile.delete();
            } catch (Exception e) {
                Log.e("CaptureIntentHelper", "tempFile删除失败:" + e.getMessage());
            }
        }
    }

    private Intent getIntent(Context context, boolean isVideo) {
        delete(context);
        if (isVideo) {
            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            uri = createMediaUri(context, true);
            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            videoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return videoIntent;
        } else {
            Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            uri = createMediaUri(context, false);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            imageIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return imageIntent;
        }
    }

    private Uri createMediaUri(Context context, boolean isVideo) {
        ContentValues values = new ContentValues();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String imageFileName = String.format("MEDIA_%s.%s", timeStamp, isVideo ? "mp4" : "jpeg");

        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        if (isVideo) {
            mimeType = "video/mp4";
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
                ContentResolver resolver = context.getContentResolver();
                return resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
//                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                File picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                if (!picturesDir.exists()) picturesDir.mkdirs();
                tempFile = new File(picturesDir, imageFileName);
                return FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".fileprovider",
                        tempFile
                );
            }
        } else {
            mimeType = "image/jpeg";
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                ContentResolver resolver = context.getContentResolver();
                return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            } else {
//                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (!picturesDir.exists()) picturesDir.mkdirs();
                tempFile = new File(picturesDir, imageFileName);
                return FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".fileprovider",
                        tempFile
                );
            }
        }
    }


    public String getMimeType() {
        return mimeType == null ? "" : mimeType;
    }

}
