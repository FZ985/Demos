package com.demos.utils.camera;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.demos.R;

import java.util.Random;


/**
 * by DAD FZ
 * 2026/1/4
 * desc：
 **/
public class InnerCameraFragmentHelper {

    private static InnerCameraFragment fragment = null;

    private final int vId = R.id.camera_trans_fragment_id;

    public void startCamera(AppCompatActivity activity, Fun2<String, Uri> call) {
        FrameLayout view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        FrameLayout innerView = view.findViewById(vId);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        if (innerView != null) {
            if (fragment != null) {
                if (fragment.isAdded()) {
                    fragmentTransaction.hide(fragment);
                    fragmentTransaction.remove(fragment);
                }
            }
        } else {
            innerView = new FrameLayout(activity);
            innerView.setId(vId);
            view.addView(innerView, new FrameLayout.LayoutParams(10, 10));
        }
        fragment = null;
        fragment = new InnerCameraFragment();
        fragment.callback = call;
        fragmentTransaction.add(vId, fragment);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    public static class InnerCameraFragment extends Fragment {

        private final CaptureIntentHelper cameraHelper = new CaptureIntentHelper();

        private final int cameraCode = Math.abs("InnerCameraFragment".hashCode()) + randomNumber(10, 9999);

        public Fun2<String, Uri> callback;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.camera_trans_fragment, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            cameraHelper.startCaptureByFragment(this, false, cameraCode);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == cameraCode && resultCode == Activity.RESULT_OK) {
                Uri uri = cameraHelper.getUri();
                if (uri != null && callback != null) {
                    callback.apply(cameraHelper.getMimeType(), uri);
                }
            }
        }

        private int randomNumber(int min, int max) {
            Random rand = new Random();
            return rand.nextInt(max - min) + min;
        }
    }


    public interface Fun2<A, B> {
        void apply(A a, B b);
    }
}
