package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(MainActivity.this, EatOutActivity.class);
        MainActivity.this.startActivity(i);

    }

    public void reOrder(){
        return;
    }


}
