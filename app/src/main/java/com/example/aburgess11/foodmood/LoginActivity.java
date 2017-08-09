package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

/**
 * Created by liangelali on 7/24/17.
 */

public class LoginActivity extends AppCompatActivity {

    String TAG ="LoginActivity";
    private FirebaseAuth mAuth;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    Switch groupToggle;
    private com.facebook.Profile fbProfile = Profile.getCurrentProfile();
    private TextView tvLoginTitle;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    public static String fbID;
    public static FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        groupToggle = (Switch) findViewById(R.id.groupToggle);
        tvLoginTitle = (TextView) findViewById(R.id.tvLoginTitle);

        if (fbProfile == null) {
           tvLoginTitle.setText("Login with Facebook");
            info.setVisibility(TextView.VISIBLE);
        } else {
            tvLoginTitle.setText("Logged in as: " + fbProfile.getName());
            info.setVisibility(TextView.GONE);
        }

        mAuth = FirebaseAuth.getInstance();

        info.setText("To use FoodMood features like GroupSwiping, sign into your Facebook account!");
        loginButton.setToolTipMode(LoginButton.ToolTipMode.NEVER_DISPLAY);

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                Intent data = new Intent(LoginActivity.this, EatOutActivity.class);
                data.putExtra("isDismissed", false);
                setResult(RESULT_OK, data);
                handleFacebookAccessToken(loginResult.getAccessToken());


                loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

                //requests name and facebook id from facebook
//                GraphRequest graphRequest1 = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        try {
//                            String string = response.getJSONObject().toString();
//                            fbID = response.getJSONObject().getString("id");
//                            FirebaseUser userser = mAuth.getCurrentUser();
//                            fbUser=userser;
//                            myRef.child("Users").child(fbID).setValue(fbUser.getDisplayName());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//
//                });

//                graphRequest1.executeAsync();

                //finish();
                startActivity(data);
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
                Log.d("Test", "onSuccess: ");
                Toast.makeText(getApplicationContext(), "Login canceled. You can login anytime from your profile!", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("isDismissed", true);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
                Log.d("Test", "onError: ");
                Toast.makeText(getApplicationContext(), "Login failed. You can login anytime from your profile!", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("isDismissed", true);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                Log.d(TAG, "onCurrentAccessTokenChanged()");
                if (accessToken == null) {
                    finish();
                } else if (accessToken2 == null) {
                    finish();
                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Authentication error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
