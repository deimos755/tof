package com.example.tof;

import android.graphics.Bitmap;

public interface DepthFrameVisualizer extends CameraSettings{
    void onDepthFrameAvailable(DepthFrame depthFrame);
}
