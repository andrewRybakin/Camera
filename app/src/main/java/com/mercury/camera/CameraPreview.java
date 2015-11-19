package com.mercury.camera;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = "CameraPreview";

    private SuitableCamera mCamera;
    private SurfaceHolder mHolder;

    private View mCameraView;

    public CameraPreview(Context context, View cameraView) {
        super(context);
        mCameraView = cameraView;
        mCamera=SuitableCamera.getInstance().open();
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
        mCamera.startPreviewWithHolder(mHolder);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera.isCameraOpened())mCamera.startPreviewWithHolder(mHolder);
        Log.d(LOG_TAG, "SurfaceCreated");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = height/3*4;
        setMeasuredDimension(width, height);
        Log.d(LOG_TAG, "onMeasure " + width + " " + height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;
            mCameraView.layout(0, 0, width/3*4, height);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

