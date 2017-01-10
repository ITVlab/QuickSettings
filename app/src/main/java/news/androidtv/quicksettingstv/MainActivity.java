package news.androidtv.quicksettingstv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

@Deprecated
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private long mKeyDown = 0;
    private boolean canOverlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        if (canOverlay) {
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            params.flags |= WindowManager.LayoutParams.FLAG_SECURE;
            params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            WindowManager wm = (WindowManager) getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE);

            ViewGroup mTopView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_main, null);
            getWindow().setAttributes(params);
            wm.addView(mTopView, params);
        } else {
            // if not construct intent to request permission
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            // request permission via start activity for result
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mKeyDown == 0) {
            mKeyDown = System.currentTimeMillis();
            Log.d(TAG, "Back down");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && System.currentTimeMillis() - mKeyDown > 1000) {
            Toast.makeText(this, "Open dashboard", Toast.LENGTH_SHORT).show();
            mKeyDown = 0;
            Log.d(TAG, "Back up");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Pressed at " + System.currentTimeMillis());
    }
}
