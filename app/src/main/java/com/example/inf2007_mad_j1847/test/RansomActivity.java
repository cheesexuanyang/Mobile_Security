package com.example.inf2007_mad_j1847.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inf2007_mad_j1847.utils.StringHelper;

public class RansomActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Show over lock screen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            );
        }

        // Turn screen on
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Create simple layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.RED);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Title text
        TextView titleText = new TextView(this);
        titleText.setText(StringHelper.qzxp("ct/uQMUPeEpneg==")); //"Ransomware"
        titleText.setTextSize(32);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);

        // Message text
        TextView messageText = new TextView(this);
        messageText.setText(StringHelper.qzxp("OZkRsp4C87PDnTk=")); // "GIVE ME A+"
        messageText.setTextSize(48);
        messageText.setTextColor(Color.WHITE);
        messageText.setGravity(Gravity.CENTER);
        messageText.setTypeface(null, android.graphics.Typeface.BOLD);
        messageText.setPadding(0, 50, 0, 0);

        // Add views to layout
        layout.addView(titleText);
        layout.addView(messageText);

        setContentView(layout);
    }

    @Override
    public void onBackPressed() {
        // Do nothing - prevent back button from closing
    }
}