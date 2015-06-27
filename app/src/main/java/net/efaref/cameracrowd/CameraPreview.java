package net.efaref.cameracrowd;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private FrameLayout mFrame;
    private CaptureActivity mActivity;

    public CameraPreview(Context context, CaptureActivity activity, Camera camera, FrameLayout frame) {
        super(context);
        mActivity = activity;
        mCamera = camera;
        mFrame = frame;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.

        if (mCamera == null) {
            mCamera = mActivity.reopenCamera();
        }

        Camera.Size size = mCamera.getParameters().getPreviewSize();

        //landscape
        float ratio = (float)size.width/size.height;

        //portrait
        //float ratio = (float)size.height/size.width;
        Log.d("=======MBT==>", "old:" + size.width + " x " + size.height);

        setLayoutParams(new FrameLayout.LayoutParams((int)(480.0 * ratio), 480, Gravity.CENTER_VERTICAL));
        //Log.d("=======MBT==>", "new:" + new_width + " x " + new_height);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("CameraCrowd", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here


        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("CameraCrowd", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void cameraReleased() {
        mCamera = null;
    }
}