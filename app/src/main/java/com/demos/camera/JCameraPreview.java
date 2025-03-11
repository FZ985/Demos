package com.demos.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * by JFZ
 * 2025/2/14
 * desc：
 **/
public class JCameraPreview extends RelativeLayout {

    private LandscapeCameraView cameraView;

    private final LifecycleEventObserver observer = new LifecycleEventObserver() {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (cameraView != null) {
                    cameraView.releaseCamera();
                }
            }
        }
    };

    public JCameraPreview(Context context) {
        this(context, null);
    }

    public JCameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JCameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindToLifecycle(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(observer);
        owner.getLifecycle().addObserver(observer);
        cameraView = new LandscapeCameraView(getContext());
        removeAllViews();
        addView(cameraView, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public void takePicture(TakePictureCallback callback) {
        if (cameraView != null) {
            cameraView.takePicture(new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (callback != null) {
                        callback.onTakePicture(data, flipImageHorizontally(bitmap));
                    }
                }
            });
        }
    }

    //将bitmap 水平翻转
    private Bitmap flipImageHorizontally(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public interface TakePictureCallback {

        void onTakePicture(byte[] data, Bitmap result);
    }

}
