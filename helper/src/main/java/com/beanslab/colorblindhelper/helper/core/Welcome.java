package com.beanslab.colorblindhelper.helper.core;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beanslab.colorblindhelper.helper.R;
import com.beanslab.colorblindhelper.helper.utils.Keys;
import com.beanslab.colorblindhelper.helper.utils.TYPEFACE;
import com.beanslab.colorblindhelper.helper.utils.Utils;

import java.util.Date;


/**
 * Created by andrew on 4/06/14.
 */
public class Welcome extends Activity{

    private SharedPreferences prefc;
    private SharedPreferences.Editor editorc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        prefc = getApplicationContext().getSharedPreferences(
                Keys.PREF_NAME_CONF, Keys.PRIVATE_MODE);
        editorc = prefc.edit();

        View rootView = findViewById(android.R.id.content);
        Utils.applyCustomFont((ViewGroup) rootView, TYPEFACE.AvantGarde(this));

        TextView welcomeText = (TextView) findViewById(R.id.welcome_text);
        RelativeLayout getStarted  = (RelativeLayout) findViewById(R.id.get_started_btn);

        String[] wordsToPaint = getResources().getStringArray(R.array.targets);
        Spannable text = Utils.paintSpanableText(getResources().getString(R.string.welcome), wordsToPaint , getResources().getColor(R.color.blue_splash));

        welcomeText.setText(text, TextView.BufferType.SPANNABLE);
        Utils.applyCustomFont( welcomeText, TYPEFACE.AvantGarde(this));

        TextView headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText(getResources().getString(R.string.welcome_title));

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        HomeDashboard.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                editorc.putBoolean(Keys.WELCOME_SHOWN, true);
                editorc.putLong(Keys.FIRST_INSTALL_DATE, new Date().getTime());
                editorc.commit();
                finish();
            }
        });
    }
}
