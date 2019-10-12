package com.example.aluminifinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Permission;
import java.security.Permissions;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv_splash = findViewById(R.id.splash_tv);

        //animation
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.fade);
        tv_splash.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i =new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        },3000);

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
