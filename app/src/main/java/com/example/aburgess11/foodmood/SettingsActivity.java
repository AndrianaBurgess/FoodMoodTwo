package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements OnSeekBarChangeListener {

    private static final int LOGIN = 1000;
    private static final String TAG = "SettingsActivity";


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private com.facebook.Profile fbProfile;

    @BindView(R.id.settings_login_button)
    LoginButton settings_login_button;
    @BindView(R.id.ibEatOut)
    ImageButton ibEatOut;

    CircleImageView ivProfilePicture;
    TextView tvName;

    SeekBar radiusSeekbar;
    TextView tvSeekbarNumber;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("onSaveInstanceState", 1);
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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


        // seekbar
        radiusSeekbar = (SeekBar) findViewById(R.id.radiusSeekbar);
        radiusSeekbar.setOnSeekBarChangeListener(this);
        tvSeekbarNumber = (TextView) findViewById(R.id.tvSeekbarNumber);
        tvSeekbarNumber.setText(radiusSeekbar.getProgress() + " mi");


        settings_login_button.setToolTipMode(LoginButton.ToolTipMode.NEVER_DISPLAY);

        // create onClick for Login Button
        settings_login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                SettingsActivity.this.startActivityForResult(i, LOGIN);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        tvSeekbarNumber.setText(progress + " mi");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

}
