package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by snhoward on 7/12/17.
 */

public class EatInActivity  extends AppCompatActivity {

    FoodAdapter foodAdapter;
    @BindView(R.id.rvFood) RecyclerView rvFood;

    // top toolbar items
    ImageButton ibEatOut;
    ImageButton ibProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_in);
        ButterKnife.bind(this);

        // resolve upper toolbar items
        ibEatOut = (ImageButton) findViewById(R.id.ibEatOut);
        ibProfile = (ImageButton) findViewById(R.id.ibProfile);

        // construct the adapter from this data source
        foodAdapter = new FoodAdapter();
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFood.setLayoutManager(linearLayoutManager);
        // set the adapter
        rvFood.setAdapter(foodAdapter);

        // create onClick for EatOut button
        ibEatOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(EatInActivity.this, EatOutActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right_to_left, R.anim.exit_from_left_to_right);
            }
        });
    }

}
