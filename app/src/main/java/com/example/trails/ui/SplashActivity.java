package com.example.trails.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trails.MainActivity;
import com.example.trails.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView topSplash, logo,bottomSplash;
    private TextView textSplash;
    private CharSequence charSequence;
    private int index;
    private long delay = 200;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scren);

        topSplash = findViewById(R.id.topSplash);
        logo = findViewById(R.id.logo);
        bottomSplash = findViewById(R.id.bottomSplash);
        textSplash = findViewById(R.id.textSplash);

        //SetFullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Top Animation
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.top_wave);
        topSplash.setAnimation(animation1);

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(logo, PropertyValuesHolder.ofFloat("scaleX",1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        objectAnimator.setDuration(500);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();

        animateText("TRAILS");

        //Top Animation
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.bottom_wave);
        bottomSplash.setAnimation(animation2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        }, 4000);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            textSplash.setText(charSequence.subSequence(0, index++));
            if(index<= charSequence.length()){
                handler.postDelayed(runnable, delay);
            }
        }
    };

    public void animateText (CharSequence cs){
        charSequence = cs;
        index=0;
        textSplash.setText("");
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,delay);
    }
}