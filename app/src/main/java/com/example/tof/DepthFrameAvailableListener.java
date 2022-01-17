package com.example.tof;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ShortBuffer;

public class DepthFrameAvailableListener implements ImageReader.OnImageAvailableListener {
    private static final String TAG = DepthFrameAvailableListener.class.getSimpleName();

    public static int WIDTH = 320;
    public static int HEIGHT = 240;
    public static int SIZE = WIDTH * HEIGHT;

    private static float RANGE_MIN = 10.0f;//200.0f;
    private static float RANGE_MAX = 500.0f;//1600.0f;
    private static float CONFIDENCE_FILTER = 0.1f;

    private DepthFrameVisualizer depthFrameVisualizer;
    private int[] rawMask;
    private byte[] badAreaMask;
    private DepthFrame depthFrame;

    private int[] noiseReduceMask;
    private int[] averagedMask;
    private int[] averagedMaskP2;
    private int[] blurredAverage;

    public DepthFrameAvailableListener(DepthFrameVisualizer depthFrameVisualizer) {
        this.depthFrameVisualizer = depthFrameVisualizer;

        int size = WIDTH * HEIGHT;
        rawMask = new int[SIZE];
        //noiseReduceMask = new int[SIZE];
       // averagedMask = new int[SIZE];
        //averagedMaskP2 = new int[SIZE];
        //blurredAverage = new int[SIZE];
       // badAreaMask = new byte[SIZE];
        depthFrame = new DepthFrame();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        try {
            Image image = reader.acquireNextImage();
            if (image != null && image.getFormat() == ImageFormat.DEPTH16) {
                processFrame(image);
                publishFrame();
            }
            image.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to acquireNextImage: " + e.getMessage());
        }
    }

    private void publishFrame() {
        if (depthFrameVisualizer != null) {
            Bitmap bitmap = convertToRGBBitmap(rawMask);
            depthFrame.frameImage = bitmap;
            depthFrameVisualizer.onDepthFrameAvailable(depthFrame);
            bitmap.recycle();
        }
    }

    /*private void publishNoiseReduction() {
        if (depthFrameVisualizer != null) {
            Bitmap bitmap = convertToRGBBitmap(noiseReduceMask);
            depthFrameVisualizer.onNoiseReductionAvailable(bitmap);
            bitmap.recycle();
        }
    }

    private void publishMovingAverage() {
        if (depthFrameVisualizer != null) {
            Bitmap bitmap = convertToRGBBitmap(averagedMask);
            depthFrameVisualizer.onMovingAverageAvailable(bitmap);
            bitmap.recycle();
        }
    }

    private void publishBlurredMovingAverage() {
        if (depthFrameVisualizer != null) {
            Bitmap bitmap = convertToRGBBitmap(blurredAverage);
            depthFrameVisualizer.onBlurredMovingAverageAvailable(bitmap);
            bitmap.recycle();
        }
    }*/

    private void processFrame(Image image) {
        ShortBuffer shortDepthBuffer = image.getPlanes()[0].getBuffer().asShortBuffer();
        int[] mask = new int[WIDTH * HEIGHT];
        int[] noiseReducedMask = new int[WIDTH * HEIGHT];
        depthFrame.rawData = new short[SIZE];
        int depthMask = depthFrameVisualizer.getDepthMask();
        if (depthMask == 0) {
            depthMask = 0xFFFF;
        }
        depthFrame.maxDepthValue = 0;
        depthFrame.minDepthValue = 8191;
        depthFrame.sumDepthValue = 0L;
        int pixelCount = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int index = y * WIDTH + x;
                short depthSource = shortDepthBuffer.get(index);
                short depthSample = (short) ((depthSource & depthMask));
                byte depthConfidence = (byte) ((depthSource >> 13) & 0x7);
                depthFrame.rawData[index] = depthSample;
                float depthPercentage = depthConfidence == 0 ? 1.f : (depthConfidence - 1) / 7.f;
                if (depthPercentage > CONFIDENCE_FILTER) {
                    if (depthSample > depthFrame.maxDepthValue)
                        depthFrame.maxDepthValue = depthSample;
                    if (depthSample < depthFrame.minDepthValue)
                        depthFrame.minDepthValue = depthSample;
                    depthFrame.sumDepthValue += depthSample;
                    badAreaMask[index] = 0;
                    pixelCount++;
                } else {
                    badAreaMask[index] = depthConfidence;
                }
                //int newValue = extractRange(depthSample, CONFIDENCE_FILTER);
                // Store value in the rawMask for visualization
                //rawMask[index] = newValue;

                //int p1Value = averagedMask[index];
                //int p2Value = averagedMaskP2[index];
                //int avgValue = (newValue + p1Value + p2Value) / 3;
                //if (p1Value < 0 || p2Value < 0 || newValue < 0) {
                //    Log.d("TAG", "WHAT");
                //}
                // Store the new moving average temporarily
                //mask[index] = avgValue;
            }
        }
        depthFrame.avgDepthValue = pixelCount > 0 ? (short) (depthFrame.sumDepthValue / pixelCount) : 0;
        int rangeMin = depthFrame.minDepthValue;
        int rangeMax = depthFrame.maxDepthValue;
        boolean isStaticRange = depthFrameVisualizer.isStaticRange();
        if (depthFrameVisualizer.isStaticRange()) {
            rangeMin = depthFrameVisualizer.getRangeMin();
            rangeMax = depthFrameVisualizer.getRangeMax();
        }
        boolean isLogScale = depthFrameVisualizer.isLogScale();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int index = y * WIDTH + x;
                float normalized = (float)depthFrame.rawData[index];
                normalized = Math.max(rangeMin, normalized);
                normalized = Math.min(rangeMax, normalized);
                normalized = normalized - rangeMin;
                if (isLogScale) {
                    normalized = (float) (255.0 * Math.log(1+ normalized)/Math.log(rangeMax));

                } else {
                    normalized = (float) (255.0 * normalized / (rangeMax - rangeMin));
                }
                //int newValue = extractRange(depthFrame.rawData[index], CONFIDENCE_FILTER);
                rawMask[index] = (int)normalized;
                //if (rawMask[index] < 0) rawMask[index] = 0;
                //if (rawMask[index] > 0) rawMask[index] = 0;
            }
        }

        // Produce a noise reduced version of the raw mask for visualization
        //System.arraycopy(rawMask, 0, noiseReducedMask, 0, rawMask.length);
        //noiseReduceMask = FastBlur.boxBlur(noiseReducedMask, WIDTH, HEIGHT, 1);

        // Remember the last two frames for moving average
        //averagedMaskP2 = averagedMask;
        //averagedMask = mask;

        // Produce a blurred version of the latest moving average result
        //System.arraycopy(averagedMask, 0, blurredAverage, 0, averagedMask.length);
        //blurredAverage = FastBlur.boxBlur(blurredAverage, WIDTH, HEIGHT, 1);
    }

    private int extractRange(short sample, float confidenceFilter) {
        int depthRange = sample; //(short) (sample & 0x1FFF);
        return normalizeRange(depthRange);
        /*int depthConfidence = (short) ((sample >> 13) & 0x7);
        float depthPercentage = depthConfidence == 0 ? 1.f : (depthConfidence - 1) / 7.f;
        if (depthPercentage > confidenceFilter) {
            return normalizeRange(depthRange);
        } else {
            return 0;
        }*/
    }

    private int normalizeRange(int range) {
        float normalized = (float)range; // - RANGE_MIN;
        // Clamp to min/max
        normalized = Math.max(RANGE_MIN, normalized);
        normalized = Math.min(RANGE_MAX, normalized);
        // Normalize to 0 to 255
        normalized = normalized - RANGE_MIN;
        normalized = normalized / (RANGE_MAX - RANGE_MIN) * 255;
        return (int)normalized;
    }

    private Bitmap convertToRGBBitmap(int[] mask) {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_4444);
        boolean showBadArea = depthFrameVisualizer.showbadArea();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int index = y * WIDTH + x;
                if(!showBadArea || badAreaMask[index] == 0) {
                    bitmap.setPixel(x, y, Color.argb(255, mask[index], mask[index], mask[index]));
                } else {
                    bitmap.setPixel(x, y, Color.argb(255, 255, 0, 0));
                }
            }
        }
        return bitmap;
    }
}