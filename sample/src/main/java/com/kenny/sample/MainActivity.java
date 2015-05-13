package com.kenny.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.kenny.snackbar.SnackBar;
import com.kenny.snackbar.SnackBarItem;
import com.kenny.snackbar.SnackBarListener;
import com.kennyc.snackbar.R;


public class MainActivity extends AppCompatActivity implements SnackBarListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sb_action_toggle).setOnClickListener(this);
        findViewById(R.id.sb_toggle).setOnClickListener(this);
        findViewById(R.id.sb_cancel).setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        SnackBar.cancelSnackBars(this);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sb_toggle:
                new SnackBarItem.Builder(this)
                        .setMessage("This is a SnackBar")
                        .setObject("My Object")
                        .setSnackBarListener(this)
                        .show();
                break;

            case R.id.sb_action_toggle:
                SnackBar.show(MainActivity.this, "This is a SnackBar with an action", "COOL", (SnackBarListener) MainActivity.this);
                break;

            case R.id.sb_cancel:
                SnackBar.cancelSnackBars(this);
                break;
        }
    }

    @Override
    public void onSnackBarStarted(Object object) {
        Log.v(TAG, "SnackBar started with object " + object);
    }

    @Override
    public void onSnackBarFinished(Object object, boolean actionPressed) {
        Log.v(TAG, "SnackBar Finished with object " + object + ", action button pressed " + actionPressed);
    }
}
