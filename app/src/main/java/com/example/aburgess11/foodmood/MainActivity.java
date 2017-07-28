package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        com.facebook.Profile profile = Profile.getCurrentProfile();
//
//        if (profile == null) {
//            Log.d("LOGIN STATUS", "user was not logged in. launching LoginActivity");
//            Intent i = new Intent(MainActivity.this, LoginActivity.class);
//            MainActivity.this.startActivity(i);
//
//
//        } else {
//            Log.d("LOGIN STATUS:", "user was already logged in");
//            Toast.makeText(getApplicationContext(), "Welcome back, " + profile.getFirstName() + "!", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(MainActivity.this, EatOutActivity.class);
//            MainActivity.this.startActivity(i);
//        }
        Intent i = new Intent(MainActivity.this, EatOutActivity.class);
        MainActivity.this.startActivity(i);
        finish();
    }




    public void reOrder(){
        return;
    }


}
