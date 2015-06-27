package net.efaref.cameracrowd;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.UnsupportedEncodingException;


public class CaptureActivity extends ActionBarActivity {

    Camera mCamera;
    CameraPreview mPreview;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://212.13.201.166:10080/add");
                    try

                    {
                        StringEntity se = new StringEntity(Base64.encodeToString(data, Base64.DEFAULT));
                        post.setEntity(se);
                        client.execute(post);
                    }

                    catch(
                    Exception e
                    )

                    {
                        Log.e("CameraCrowd", "Unexpected Exception", e);
                    }
                    return null;
                }

            }.execute();
            mCamera.startPreview();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        final FrameLayout mFrame = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(this, this, mCamera, mFrame);

        mFrame.post(new Runnable() {

            public void run() {
                mFrame.addView(mPreview);
            }
        });

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            mPreview.cameraReleased();
        }
    }

    public Camera reopenCamera(){
        mCamera = Camera.open();
        return mCamera;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
