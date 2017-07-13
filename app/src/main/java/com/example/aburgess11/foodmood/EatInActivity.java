package com.example.aburgess11.foodmood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by snhoward on 7/12/17.
 */

public class EatInActivity  extends AppCompatActivity {

    FoodAdapter foodAdapter;
    @BindView(R.id.rvFood)
    RecyclerView rvFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_in);
        ButterKnife.bind(this);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // construct the adapter from this data source
        foodAdapter = new FoodAdapter();
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFood.setLayoutManager(linearLayoutManager);
        // set the adapter
        rvFood.setAdapter(foodAdapter);
    }
}
