package com.example.aburgess11.foodmood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by liangelali on 7/13/17.
 */

public class MatchesListActivity extends AppCompatActivity {
    private ImageView ivMatchImage;
    private TextView tvMatchName;
    private TextView tvMatchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        // Fetch views
        ivMatchImage = (ImageView) findViewById(R.id.ivMatchImage);
        tvMatchName = (TextView) findViewById(R.id.tvMatchName);
        tvMatchDetails = (TextView) findViewById(R.id.tvMatchDetails);


        // Use match object to populate data into views
        tvMatchName.setText("Match Name");    // get match name
        tvMatchDetails.setText("details of the match");    // get match details

        // get book cover using glide
        String imageUrl = "https://www.gimmesomeoven.com/wp-content/uploads/2014/01/Skinny-Taco-Salad-11.jpg";
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(ivMatchImage);
    }
}
