package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.ibEatOut)
    ImageButton ibEatOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new PreferenceFragmentCustom();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.pref_container, preferenceFragment);
            ft.commit();
        }

        // create onClick for EatOut button
        ibEatOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, EatOutActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right_to_left, R.anim.exit_from_left_to_right);
            }
        });

    }
}
