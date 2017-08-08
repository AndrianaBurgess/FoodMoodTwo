package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private static final int LOGIN = 1000;
    private static final String TAG = "SettingsActivity";


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private com.facebook.Profile fbProfile;

    @BindView(R.id.loginSettings)
    RelativeLayout loginSettings;
    @BindView(R.id.ibEatOut)
    ImageButton ibEatOut;

    CircleImageView ivProfilePicture;
    TextView tvName;

    Spinner locationDropdown;
    static String tvLocationCity;

    SeekBar radiusSeekbar;
    int seekbarProgress;
    TextView tvSeekbarNumber;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("onSaveInstanceState", 2);
        outState.putInt("progress", seekbarProgress);
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        seekbarProgress = savedInstanceState.getInt("progress");
        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState.getInt("saved"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: " + savedInstanceState.getInt("saved"));
        }

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        // create onClick for EatOut button
        ibEatOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, EatOutActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right_to_left, R.anim.exit_from_left_to_right);
            }
        });


        // profile
        fbProfile = Profile.getCurrentProfile();
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(fbProfile.getName());
        ivProfilePicture = (CircleImageView) findViewById(R.id.ivProfilePicture);
        Uri uri = fbProfile.getProfilePictureUri(150,150);
        Glide.with(this).load(uri).into(ivProfilePicture);

        locationDropdown = (Spinner) findViewById(R.id.locationDropDown);
        String[] locations = new String[]{"Menlo Park, CA", "New York, NY", "Austin, TX", "Boston, MA"};
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_spinner_item, locations);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationDropdown.setAdapter(adapter);
        locationDropdown.setOnItemSelectedListener(this);


        // seekbar
        radiusSeekbar = (SeekBar) findViewById(R.id.radiusSeekbar);
        radiusSeekbar.setOnSeekBarChangeListener(this);
        tvSeekbarNumber = (TextView) findViewById(R.id.tvSeekbarNumber);
        seekbarProgress = radiusSeekbar.getProgress();
        tvSeekbarNumber.setText(seekbarProgress + " mi");




        // create onClick for Login Button
        loginSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                SettingsActivity.this.startActivityForResult(i, LOGIN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        fbProfile = Profile.getCurrentProfile();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int seekbarProgress = progress;
        tvSeekbarNumber.setText(seekbarProgress + " mi");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvLocationCity = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
