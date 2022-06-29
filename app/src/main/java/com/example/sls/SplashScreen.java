package com.example.sls;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener{

    ImageView logo;
    private UiModeManager uiModeManager;
    Handler handler;
    private static int SPLASH_SCREEN_TIME_OUT = 2000;
    Animation animZoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logo = findViewById(R.id.SPlashScreenImage);

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);

        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);

        animZoomOut.setAnimationListener(SplashScreen.this);

        logo.startAnimation(animZoomOut);

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animZoomOut) {

            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();
            //Animatoo.animateSlideRight(SplashScreen.this);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}