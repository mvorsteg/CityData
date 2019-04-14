package com.esri.arcgisruntime.arvrdemos.arcoreinthearcgisruntimesdkforandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private TextView mText;
    private int mTextNum;
    private boolean mSemaphore;
    private Animation mIn;
    private LinearLayout mLayout;

    private Animation mOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSemaphore = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mLayout = (LinearLayout) findViewById(R.id.linearLayout);

        mText  = (TextView) findViewById(R.id.changeText);
        mIn = new AlphaAnimation(0.0f, 1.0f);
        mOut = new AlphaAnimation(1.0f, 0.0f);
        mText.setText("Welcome");
        mText.setTextSize(32);
        //mText.setFont
        mIn.setDuration(1000);
        mOut.setDuration(1000);
        mTextNum = 1;
        mOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTextNum == 1) {
                    mText.setText("Only 4% of people who are born into poverty will ever make it out");
                }else if (mTextNum == 2){
                    mText.setText("In this app, we will explore how economic factors impact your geographical environment");
                }else{
                    goToAR();
                    mText.setText("");
                    mTextNum = 0;
                }
                mTextNum ++;
                mText.startAnimation(mIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) { mSemaphore = true;}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSemaphore) {
                    mSemaphore = false;
                    mText.startAnimation(mOut);
                }
            }
        });
    }

    public void goToAR() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        return;
    }
}

