package com.mercury.camera;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class CameraControlsFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    private ImageButton takeButton, changeButton;
    private SensorManager sensorManager;
    private float[] g;
    ValueAnimator va;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.camera_controls, container, false);
        takeButton = (ImageButton) v.findViewById(R.id.take);
        changeButton = (ImageButton) v.findViewById(R.id.change);
        takeButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        sensorManager = (SensorManager) getActivity().getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_NORMAL);
        if(!SuitableCamera.getInstance().isFrontCamAvaible()){
            changeButton.setEnabled(false);
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == changeButton.getId()) {
            SuitableCamera.getInstance().changeCamera();
        } else if (v.getId() == takeButton.getId()) {
            SuitableCamera.getInstance().takePicture(getActivity());
        }
    }

    private void animate(float start, int end) {
        if (va != null && va.isStarted()) return;
        va = ValueAnimator.ofFloat(start, end);
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                takeButton.setRotation(value);
                changeButton.setRotation(value);
            }
        });
        va.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            g = event.values.clone();
            double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);
            g[0] = (float) (g[0] / norm_Of_g);
            g[1] = (float) (g[1] / norm_Of_g);
            g[2] = (float) (g[2] / norm_Of_g);
            int rotation = (int) Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));
            if (rotation > -45 && rotation < 45 && takeButton.getRotation() != -90 && changeButton.getRotation() != -90) {
                animate((takeButton.getRotation() == 180) ? -180 : 0, -90);
                SuitableCamera.getInstance().setOrientation(270);
            } else if (rotation > 45 && rotation < 135 && takeButton.getRotation() != 0 && changeButton.getRotation() != 0) {
                animate(takeButton.getRotation(), 0);
                SuitableCamera.getInstance().setOrientation(0);
            } else if ((rotation > 135 || rotation < -135) && takeButton.getRotation() != 90 && changeButton.getRotation() != 90) {
                animate(takeButton.getRotation(), 90);
                SuitableCamera.getInstance().setOrientation(90);
            } else if (rotation > -135 && rotation < -45 && takeButton.getRotation() != 180 && changeButton.getRotation() != 180) {
                animate((takeButton.getRotation()==-90)?270:90, 180);
                SuitableCamera.getInstance().setOrientation(180);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_NORMAL);
    }
}
