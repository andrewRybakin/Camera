package com.mercury.camera;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SuitableCamera implements Camera.PictureCallback, Camera.AutoFocusCallback {

    public static final String LOG_TAG = "SuitableCamera";
    public static final int BACK = Camera.CameraInfo.CAMERA_FACING_BACK; //Shortcut
    public static final int FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private static SuitableCamera instance;
    private int cameraId;
    private Camera camera;
    private boolean cameraOpened;
    private SurfaceHolder holder;
    private Context mContext;
    private int mOrientation;

    private SuitableCamera() {
        cameraId = BACK;
        cameraOpened = false;
    }

    public static SuitableCamera getInstance() {
        if (instance == null)
            instance = new SuitableCamera();
        return instance;
    }

    public SuitableCamera open() {
        if (!cameraOpened) {
            camera = null;
            try {
                camera = Camera.open(cameraId);
                Log.d(LOG_TAG, "Opening camera 1");
            } catch (Exception e) {
                Log.wtf(LOG_TAG, e.getMessage());
            }
            Log.d(LOG_TAG, "Opening camera 2");
            List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                camera.setParameters(parameters);
            }
        }
        cameraOpened = (camera != null);
        Log.d(LOG_TAG, "CameraOpened=" + cameraOpened);
        return this;
    }

    public void startPreviewWithHolder(SurfaceHolder surfaceHolder) {
        if (isCameraOpened()) {
            if (surfaceHolder != null) holder = surfaceHolder;
            try {
                camera.setPreviewDisplay(holder);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            camera.startPreview();
        } else throw new IllegalStateException("Camera doesn't open!");
    }

    public void setOrientation(int angle) {

        if (isCameraOpened())
            mOrientation = angle;
        else throw new IllegalStateException("Camera doesn't open!");
    }

    public boolean isCameraOpened() {
        return cameraOpened;
    }

    public void release() {
        if (isCameraOpened()) {
            camera.stopPreview();
            camera.release();
            cameraOpened = false;
        } else throw new IllegalStateException("Camera doesn't open!");
    }

    public boolean isFrontCamAvaible() {
        return Camera.getNumberOfCameras() == 2;
    }

    public void changeCamera() {
        camera.stopPreview();
        camera.release();
        cameraOpened = false;
        cameraId = (cameraId == BACK) ? FRONT : BACK;
        open();
        startPreviewWithHolder(null);
    }

    public void takePicture(Context context) {
        mContext = context;
        if (cameraId == BACK) {
            camera.autoFocus(this);
        } else
            camera.takePicture(null, null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            File temp = File.createTempFile("photo", ".jpg", mContext.getFilesDir());
            FileOutputStream fo = new FileOutputStream(temp);
            fo.write(data);
            fo.close();
            Intent i = new Intent(mContext, SaverActivity.class);
            i.putExtra(SaverActivity.PICTURE_EXTRA, temp);
            i.putExtra(SaverActivity.ORIENTATION_EXTRA,
                    (cameraId == FRONT && (mOrientation == 270 || mOrientation == 90)) ? -mOrientation : mOrientation);
            mContext.startActivity(i);
            Log.d(LOG_TAG, "SometimesShitHappens");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera) {
        camera.takePicture(null, null, null, this);
    }
}
