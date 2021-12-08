package com.kyssanet.kikuyucalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private TextView appname;
    private ImageView logo;
    private Context mContext = SplashActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash);
        this.logo = (ImageView) findViewById(R.id.logo);
        this.appname = (TextView) findViewById(R.id.appname);
        new Handler().postDelayed(new Runnable() {
            /* class activity.SplashActivity.AnonymousClass1 */

            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, (long) SPLASH_TIME_OUT);

    }
}