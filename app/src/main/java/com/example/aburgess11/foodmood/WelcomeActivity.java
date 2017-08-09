package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    public static boolean isAlone;
    public ArrayList<String> images;
    public String imageUrl;


    @BindView(R.id.ivLogo) ImageView ivLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        new CountDownTimer(1500, 1000) {

            @Override
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                // TODO: notify user that session is done

                Intent intent = new Intent(WelcomeActivity.this, CityActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }.start();


    }
}
