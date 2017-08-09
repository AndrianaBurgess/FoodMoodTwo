package com.example.aburgess11.foodmood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.aburgess11.foodmood.models.City;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityActivity extends AppCompatActivity {

    public static CityAdapter adapter;
    public static ArrayList<City> cityList;
    public static Map<String, Object> cities;

    @BindView(R.id.rvCities) RecyclerView rvCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);

        cities = new HashMap<>();
        cityList = new ArrayList<>();

        getData();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeUserInterface();
            }
        });
    }

    private void getData() {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("City Details");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("cities", dataSnapshot.getValue().toString());
                cities = (HashMap) dataSnapshot.getValue();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    final String name = (String) messageSnapshot.child("name").getValue();
                    final String imageUrl = (String) messageSnapshot.child("image_url").getValue();

                    City city = new City();
                    city.setName(name);
                    city.setImageUrl(imageUrl);
                    cityList.add(city);
                    adapter.notifyItemInserted(cityList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Database", "The database read failed: " + databaseError.getCode());
            }
        });
    }

    private void initializeAdapter() {
        try {
            adapter = new CityAdapter(cityList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMatches(Map<String, Object> cities) {
        for(String cityName : cities.keySet()) {

            City city = (City) cities.get(cityName);
            cityList.add(city);
            adapter.notifyItemInserted(cities.size() - 1);
        }
    }

    private void initializeUserInterface() {
        initializeAdapter();
        rvCities.setLayoutManager(new LinearLayoutManager(this));
        // configure recycler view to allow for smooth scrolling
       // rvCities.setNestedScrollingEnabled(false);
       // rvCities.setHasFixedSize(false);
        //rvCities.getLayoutManager().setAutoMeasureEnabled(true);
        // connect recycler view to adapter
        rvCities.setAdapter(adapter);

//        loadMatches(cities);
    }

}
