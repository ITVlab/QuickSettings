package news.androidtv.quicksettingstv;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by firem on 1/10/2017.
 */

public class ShutterActivity extends Activity {
    private static final String TAG = ShutterActivity.class.getSimpleName();

    private static final int PERMISSION_START_SCREENSHOT = 101;

    private boolean mBurst = false;
    private boolean mStart = false;
    private long mDelay = 5000; // 1 second
    private ScreenshotHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutter);
        ((Switch) findViewById(R.id.option_burst)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBurst = isChecked;
            }
        });
        mBurst = ((Switch) findViewById(R.id.option_burst)).isChecked();

        ((Switch) findViewById(R.id.option_start)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStart = isChecked;
                if (isChecked && mHandler == null) {
                    mHandler = new ScreenshotHandler();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
        mStart = ((Switch) findViewById(R.id.option_start)).isChecked();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_START_SCREENSHOT) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private class ScreenshotHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_START_SCREENSHOT);
                return;
            }
            saveScreenshot();
            if (mStart) {
                sendEmptyMessageDelayed(0, mDelay);
            }
        }

        private Bitmap captureScreenshot() {
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            return bitmap;
        }

        private void saveScreenshot() {
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

            try {
                // image naming and path  to include sd card  appending name you choose for file
                String mPath = Environment.getExternalStorageDirectory().toString() + "/PICTURES/Screenshots/" + now + ".jpg";

                File imageFile = new File(mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                captureScreenshot().compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                Log.d(TAG, "Saving file " + mPath);
            } catch (Throwable e) {
                // Several error may come out with file handling or OOM
                e.printStackTrace();
            }
        }
    }
}
