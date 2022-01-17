package com.example.tof;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/*  This is an example of getting and processing ToF data.

    This example will only work (correctly) on a device with a front-facing depth camera
    with output in DEPTH16. The constants can be adjusted but are made assuming this
    is being run on a Samsung S10 5G device.
 */
public class MainActivity extends AppCompatActivity implements DepthFrameVisualizer {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int CAM_PERMISSIONS_REQUEST = 0;

    private TextureView rawDataView;
    private TabLayout tabLayoutView;
    private TextureView noiseReductionView;
    private TextureView movingAverageView;
    private TextureView blurredAverageView;
    private Matrix defaultBitmapTransform;
    private DepthCamera camera;
    private TabLayout tabLayout;

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private TextView minValueTextView;
    private TextView maxValueTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rawDataView = findViewById(R.id.rawData);
        // get the reference of FrameLayout and TabLayout
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout1);
// Create a new Tab named "First"
        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("Statistics"); // set the Text for the first Tab
        //firstTab.setIcon(R.drawable.); // set an icon for the
// first tab
        tabLayout.addTab(firstTab); // add  the tab at in the TabLayout
// Create a new Tab named "Second"
        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Settings"); // set the Text for the second Tab
        //secondTab.setIcon(R.drawable.ic_launcher); // set an icon for the second tab
        tabLayout.addTab(secondTab); // add  the tab  in the TabLayout
// Create a new Tab named "Third"
        TabLayout.Tab thirdTab = tabLayout.newTab();
        thirdTab.setText("Operations"); // set the Text for the first Tab
        //thirdTab.setIcon(R.drawable.ic_launcher); // set an icon for the first tab
        tabLayout.addTab(thirdTab); // add  the tab at in the TabLayout
        //TabLayout.BaseOnTabSelectedListener baseOnTabSelectedListener = ;
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
             /*noiseReductionView = findViewById(R.id.noiseReduction);
        movingAverageView = findViewById(R.id.movingAverage);
        blurredAverageView = findViewById(R.id.blurredAverage);*/
        minValueTextView = findViewById(R.id.minValueTextView);
        maxValueTextView = findViewById(R.id.maxValueTextView);
        checkCamPermissions();
        camera = new DepthCamera(this, this);
        camera.openFrontDepthCamera();
    }

    private void checkCamPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAM_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDepthFrameAvailable(DepthFrame depthFrame) {
        //
        renderBitmapToTextureView(depthFrame.frameImage, rawDataView);
        adapter.getStatisticsFragment().setMinValText("" + depthFrame.minDepthValue);
        adapter.getStatisticsFragment().setMaxValText("" + depthFrame.maxDepthValue);
        adapter.getStatisticsFragment().setAvgValText("" + depthFrame.avgDepthValue);
        //minValueTextView.setText("" + depthFrame.minDepthValue);
        //maxValueTextView.setText("" + depthFrame.maxDepthValue);

    }


    /* We don't want a direct camera preview since we want to get the frames of data directly
        from the camera and process.

        This takes a converted bitmap and renders it onto the surface, with a basic rotation
        applied.
     */
    private void renderBitmapToTextureView(Bitmap bitmap, TextureView textureView) {
        Canvas canvas = textureView.lockCanvas();
        canvas.drawBitmap(bitmap, defaultBitmapTransform(textureView), null);
        textureView.unlockCanvasAndPost(canvas);
    }

    private Matrix defaultBitmapTransform(TextureView view) {
        if (defaultBitmapTransform == null || view.getWidth() == 0 || view.getHeight() == 0) {
            Matrix matrix = new Matrix();
            int centerX = view.getWidth() / 2;
            int centerY = view.getHeight() / 2;

            int bufferWidth = DepthFrameAvailableListener.WIDTH;
            int bufferHeight = DepthFrameAvailableListener.HEIGHT;

            RectF bufferRect = new RectF(0, 0, bufferWidth, bufferHeight);
            RectF viewRect = new RectF(0, 0, view.getWidth(), view.getHeight());
            matrix.setRectToRect(bufferRect, viewRect, Matrix.ScaleToFit.CENTER);
            matrix.postRotate(90, centerX, centerY);

            defaultBitmapTransform = matrix;
        }
        return defaultBitmapTransform;
    }

    @Override
    public boolean isStaticRange() {
        return adapter.getSettingsFragment().isStaticRange();
    }

    @Override
    public int getRangeMin() {
        return adapter.getSettingsFragment().getRangeMin();
    }

    @Override
    public int getRangeMax() {
        return adapter.getSettingsFragment().getRangeMax();
    }

    @Override
    public int getDepthMask() {
        return adapter.getSettingsFragment().getDepthMask();
    }

    @Override
    public boolean isLogScale() {
        return adapter.getSettingsFragment().isLogScale();
    }

    @Override
    public boolean showbadArea() {
        return adapter.getSettingsFragment().showbadArea();
    }
}






