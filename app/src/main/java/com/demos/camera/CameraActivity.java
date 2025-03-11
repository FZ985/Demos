package com.demos.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.databinding.ActivityCameraBinding;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;

/**
 * by JFZ
 * 2025/2/14
 * desc：
 **/
public class CameraActivity extends AppCompatActivity {

    private ActivityCameraBinding binding;

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        ImmersionBar.with(this)
                .fullScreen(true)
                .statusBarColorInt(Color.TRANSPARENT)
                .init();

        binding.preview.bindToLifecycle(this);

        // Create an instance of Camera
//        mCamera = getCameraInstance();
//
//        // Create our Preview view and set it as the content of our activity.
//        mPreview = new CameraPreview(this, mCamera);
//        FrameLayout preview = findViewById(R.id.camera_preview);
//        preview.addView(mPreview);

        binding.buttonCapture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
//                        mCamera.takePicture(null, null, mPicture);

                        binding.preview.takePicture(new JCameraPreview.TakePictureCallback() {
                            @Override
                            public void onTakePicture(byte[] data, Bitmap result) {
                                binding.image.setImageBitmap(result);
                            }
                        });
                    }
                }
        );
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    c = Camera.open(i);
                }
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            File pictureFile = getOutputMediaFile();
//            if (pictureFile == null) {
//                Log.e("TAG", "Error creating media file, check storage permissions");
//                return;
//            }
//
//            try {
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                fos.write(data);
//                fos.close();
//
//                Glide.with(binding.image)
//                        .load(pictureFile)
//                        .into(binding.image);
//            } catch (FileNotFoundException e) {
//                Log.e("TAG", "File not found: " + e.getMessage());
//            } catch (IOException e) {
//                Log.e("TAG", "Error accessing file: " + e.getMessage());
//            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                binding.image.setImageBitmap(bitmap);
            }
        }
    };

    private File getOutputMediaFile() {
        return new File(
                getCacheDir(),
                System.currentTimeMillis() + ".jpeg");
    }

}
