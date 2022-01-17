package com.example.tof;

public interface CameraSettings {
    public boolean isStaticRange();
    public int getRangeMin();
    public int getRangeMax();
    public int getDepthMask();
    public boolean isLogScale();
    public boolean showbadArea();
}
