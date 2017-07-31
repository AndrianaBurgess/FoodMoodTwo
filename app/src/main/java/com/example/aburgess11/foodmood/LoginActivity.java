package com.example.aburgess11.foodmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by liangelali on 7/24/17.
 */

public class LoginActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        info.setText("To use FoodMood features like GroupSwiping, sign into your Facebook account!");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent i = new Intent(LoginActivity.this, EatOutActivity.class);
                LoginActivity.this.startActivity(i);
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
                Toast.makeText(getApplicationContext(), "Login canceled. You can login anytime from your profile!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, EatOutActivity.class);
                LoginActivity.this.startActivity(i);
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
                Toast.makeText(getApplicationContext(), "Login failed. You can login anytime from your profile!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, EatOutActivity.class);
                LoginActivity.this.startActivity(i);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}