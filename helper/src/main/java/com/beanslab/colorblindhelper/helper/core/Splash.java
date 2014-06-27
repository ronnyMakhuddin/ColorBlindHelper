package com.beanslab.colorblindhelper.helper.core;

/**
 * Created by andrew on 2/01/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.beanslab.colorblindhelper.helper.R;
import com.beanslab.colorblindhelper.helper.utils.Keys;

import java.io.File;

public class Splash extends Activity {

    private static final long SPLASH_DISPLAY_LENGTH = 3000;
    private SharedPreferences prefc;
    private SharedPreferences.Editor editorc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        Handler handler = new Handler();
        handler.postDelayed(getRunnableStartApp(), SPLASH_DISPLAY_LENGTH);

        prefc = getApplicationContext().getSharedPreferences(
                Keys.PREF_NAME_CONF, Keys.PRIVATE_MODE);

        HomeDashboard.opened = false;

        //
        editorc = prefc.edit();
        editorc.putBoolean(Keys.WELCOME_SHOWN, false);
        editorc.commit();
        //

        File folder = new File(Environment.getExternalStorageDirectory() + "/colorblindhelper");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

    }

    private Runnable getRunnableStartApp() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = prefc.getBoolean(Keys.WELCOME_SHOWN, false) ?
                        new Intent(Splash.this, HomeDashboard.class) : new Intent(Splash.this, Welcome.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                finish();
            }
        };
        return runnable;
    }
}
