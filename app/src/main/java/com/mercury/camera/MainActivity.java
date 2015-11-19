package com.mercury.camera;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.FrameLayout;


public class MainActivity extends FragmentActivity {

    private static final String LOG_TAG = "MainActivity";

    public static final String CAMERA_FRAGMENT_TAG = "CameraFragment";
    public static final String CAMERA_CONTROLS_TAG = "CameraControls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout fl=new FrameLayout(this);
        fl.setId(R.id.main_frame_layout);
        FragmentManager fm=getFragmentManager();
        fm.beginTransaction()
                .add(fl.getId(), new CameraFragment(), CAMERA_FRAGMENT_TAG)
                .add(fl.getId(), new CameraControlsFragment(), CAMERA_CONTROLS_TAG)
                .commit();
        setContentView(fl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }
}
