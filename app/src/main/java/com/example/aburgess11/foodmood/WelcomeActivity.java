package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    public static boolean isAlone;
    public ArrayList<String> images;
    public String imageUrl;

    @BindView(R.id.btnEatAlone) Button btnEatAlone;
    @BindView(R.id.btnWithFriends) Button btnWithFriends;
//    @BindView(R.id.ivBackground) ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        images = new ArrayList<>();


        populateImages();
        initializeOnClickListeners();
    }

    private void initializeOnClickListeners() {
        btnEatAlone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlone = true;
                Intent intent = new Intent(WelcomeActivity.this, CityActivity.class);
                intent.putExtra("data", isAlone);
                startActivity(intent);
            }
        });

        btnWithFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlone = false;
                //TODO: Eventually this intent should go to the group creating activity
                Intent intent = new Intent(WelcomeActivity.this, CityActivity.class);
                intent.putExtra("data", isAlone);
                startActivity(intent);
            }
        });
    }

    private void populateImages() {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Welcome Images");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < 4; i++) {
                    final String imageUrl = (String) dataSnapshot.child("" + i).getValue();
                    images.add(imageUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Database", "The database read failed: " + databaseError.getCode());
            }
        });

//        imageUrl = "https://wallpaperscraft.com/image/burger_meat_chicken_cheese_bun_93265_640x1136.jpg";
//        Glide.with(getApplicationContext())
//                .load(imageUrl)
//                .centerCrop()
//                .into(ivBackground);
    }

}
