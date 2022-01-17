package com.example.tof;

import android.graphics.Bitmap;

public class DepthFrame {
    public DepthFrame(){

    }
    public Bitmap frameImage;
    public short[] rawData;
    public short minDepthValue = 0;
    public short maxDepthValue = 0;
    public long sumDepthValue = 0;
    public short avgDepthValue = 0;
}
