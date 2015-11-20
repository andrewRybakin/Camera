package com.mercury.camera;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CameraFragment extends Fragment {

    private static final String LOG_TAG = "CameraFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.camera_fragment, container, false);
        CameraPreview mPreview = new CameraPreview(getActivity().getBaseContext(), v);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        return v;
    }

    @Override
    public void onDestroyView() {
        if (SuitableCamera.getInstance().isCameraOpened())
            SuitableCamera.getInstance().release();
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (SuitableCamera.getInstance().isCameraOpened())
            SuitableCamera.getInstance().release();
    }

    @Override
    public void onResume() {
        super.onResume();
        SuitableCamera.getInstance().open();
        SuitableCamera.getInstance().startPreviewWithHolder(null);
    }
}
