package com.demos.camera;

/**
 * by JFZ
 * 2025/2/14
 * desc：旧版的横向相机使用
 **/

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * A basic Camera preview class
 */
public class LandscapeCameraView extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private int mCameraId;

    public LandscapeCameraView(Context context) {
        super(context);
        mCamera = getCameraInstance();
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraId = i;
                    c = Camera.open(i);
                }
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        Camera.Parameters parameters = mCamera.getParameters();

        // 获取支持的拍照尺寸列表
        List<Camera.Size> supportedSizes = parameters.getSupportedPictureSizes();
        // 选择一个适当的尺寸
        Camera.Size optimalSize = getOptimalSize(supportedSizes, 16, 9);

        // 设置拍照尺寸
        parameters.setPictureSize(optimalSize.width, optimalSize.height);
        parameters.setPreviewSize(getWidth(), getHeight());

        // 将设置应用到 Camera 对象
        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    // 获取与目标纵横比最匹配的尺寸
    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int targetWidth, int targetHeight) {
        Camera.Size optimalSize = null;
        double targetRatio = (double) targetWidth / targetHeight;
        double minDiff = Double.MAX_VALUE;
        for (Camera.Size size : sizes) {
            double aspectRatio = (double) size.width / size.height;
            double ratioDiff = Math.abs(aspectRatio - targetRatio);
            // 选择与目标纵横比最接近的尺寸
            if (ratioDiff < minDiff) {
                optimalSize = size;
                minDiff = ratioDiff;
            }
        }

        if (optimalSize != null) {
            return optimalSize;

        }
        return chooseOptimalSize(sizes, getWidth(), getHeight());
    }

    private Camera.Size chooseOptimalSize(List<Camera.Size> choices, int targetWidth, int targetHeight) {
        Camera.Size optimalSize = null;
        for (Camera.Size size : choices) {
            // 根据宽高比选择一个最合适的尺寸
            if (size.width == targetWidth) {
                optimalSize = size;
                break;
            }
        }
        if (optimalSize == null) {
            // 如果没有找到合适的尺寸，返回列表中的第一个尺寸
            optimalSize = choices.get(0);
        }
        return optimalSize;
    }

    public void takePicture(Camera.PictureCallback callback) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, callback);
        }
    }

    public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
}
