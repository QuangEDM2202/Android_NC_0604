package com.quangedm2202.fithou_chat;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME = 3000;
    TextView txtPhienban;
    ImageView imageView;
    TextView txtwebcome;
    TextView txtTenCongTy;
    ProgressBar progressBar;
    TextView txtTai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        txtPhienban = (TextView) findViewById(R.id.txtPhienban);
        txtwebcome = findViewById(R.id.txtwebcome);
        txtTenCongTy = findViewById(R.id.txtTenCongTy);
        txtTai = findViewById(R.id.txtTai);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        //animation
        Animation animation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.myanim);
        imageView.startAnimation(animation);
        txtTai.startAnimation(animation);
        txtTenCongTy.startAnimation(animation);
        txtwebcome.startAnimation(animation);
        txtPhienban.startAnimation(animation);
        progressBar.startAnimation(animation);
        //Code to start timer and take action after the timer ends
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mySuperIntent = new Intent(SplashScreen.this, StartActivity.class);
                startActivity(mySuperIntent);
                finish();
            }
        }, SPLASH_TIME);
    }
}
