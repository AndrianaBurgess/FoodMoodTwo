package com.example.aburgess11.foodmood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class YelpClient extends AppCompatActivity {

    public static final String YELP_URL = "https://api.yelp.com/oauth2/token";
    public static final String CLIENT_ID = "FJbrnaXwVmgGJTk1xd2jwA";
    public static final String CLIENT_SECRET = "fAwIdrHUUvrHpbMbyOp4gVASjtH0TvJzj56TGgrXeyg3q994Nb6HWRgFGNWXTQ7z";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_client);
        try {
            URL obj = new URL(YELP_URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("grant_type", "client_credentials");
            con.setRequestProperty("client_id", CLIENT_ID);
            con.setRequestProperty("client_secret", CLIENT_SECRET);
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
