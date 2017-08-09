package com.example.aburgess11.foodmood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aburgess11.foodmood.models.Friend;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class GroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static JSONArray friends;
    AsyncHttpClient client;
    public static FriendsAdapter adapter;
    static RecyclerView rvFriends;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<Friend> friendsArray;
    String id;


    // friends
    RelativeLayout addFriendsContainer;
    Button friends_nextBtn;

    // location
    RelativeLayout locationContainer;
    Spinner locationDropDown;
    static String tvLocationCity = "Menlo Park, CA";
    Button location_backBtn;
    Button location_nextBtn;


    // timer
    RelativeLayout timerContainer;
    static int numHours;
    static int numMins;
    NumberPicker hoursPicker;
    NumberPicker minutesPicker;
    Button timer_backBtn;
    Button timer_startSessionBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_group);

        // friends
        addFriendsContainer = (RelativeLayout) findViewById(R.id.addFriendsContainer);
        friends_nextBtn = (Button) findViewById(R.id.friends_nextBtn);


        // location
        locationContainer = (RelativeLayout) findViewById(R.id.locationContainer);
        locationDropDown = (Spinner) findViewById(R.id.locationDropDown);
        location_backBtn = (Button) findViewById(R.id.location_backBtn);
        location_nextBtn = (Button) findViewById(R.id.location_nextBtn);


        // timer
        timerContainer = (RelativeLayout) findViewById(R.id.timerContainer);
        hoursPicker = (NumberPicker) findViewById(R.id.hoursPicker);
        minutesPicker = (NumberPicker) findViewById(R.id.minutesPicker);
        timer_backBtn = (Button) findViewById(R.id.timer_backBtn);
        timer_startSessionBtn = (Button) findViewById(R.id.timer_startSessionBtn);


        addFriendsContainer.setVisibility(View.VISIBLE);
        locationContainer.setVisibility(View.GONE);
        timerContainer.setVisibility(View.GONE);


        friends_nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendsContainer.setVisibility(View.GONE);
                locationContainer.setVisibility(View.VISIBLE);
            }
        });

        location_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationContainer.setVisibility(View.GONE);
                addFriendsContainer.setVisibility(View.VISIBLE);
            }
        });

        location_nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationContainer.setVisibility(View.GONE);
                timerContainer.setVisibility(View.VISIBLE);
            }
        });

        timer_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerContainer.setVisibility(View.GONE);
                locationContainer.setVisibility(View.VISIBLE);
            }
        });

        timer_startSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerContainer.setVisibility(View.GONE);
                addFriendsContainer.setVisibility(View.VISIBLE);
                Intent data = new Intent();
                data.putExtra("sessionStarted", true);
                setResult(RESULT_OK, data);
                Toast.makeText(getApplicationContext(), "Location: " + tvLocationCity + " Timer:" + numHours + ":" + numMins, Toast.LENGTH_LONG).show();
                finish();
            }
        });





        locationDropDown = (Spinner) findViewById(R.id.locationDropDown);
        String[] locations = new String[]{"Menlo Park, CA", "New York, NY", "Austin, TX", "Boston, MA"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<String>(GroupActivity.this,
                android.R.layout.simple_spinner_item, locations);

        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationDropDown.setAdapter(dropdownAdapter);
        locationDropDown.setOnItemSelectedListener(this);






        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(24);
        hoursPicker.setWrapSelectorWheel(true);
        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                numHours = newVal;
            }
        });

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setWrapSelectorWheel(true);
        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                numMins = newVal;
            }
        });








        GraphRequest graphRequest = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                friends= objects;
                loadFriends(context,friendsArray );
                Log.d("output", response.getJSONObject().toString());
            }
        });
        graphRequest.executeAsync();


    // initialize the client
    client = new AsyncHttpClient();
    // init the list of matches
    friendsArray = new ArrayList<>();
    // initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new FriendsAdapter(friendsArray);
        ButterKnife.bind(this);
        DatabaseReference fbGroups = database.getReference("Groups");

        rvFriends= (RecyclerView) findViewById(R.id.rvFriends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFriends.setLayoutManager(layoutManager);
        rvFriends.setAdapter(adapter);



    }

    public static void loadFriends(Context context , final ArrayList<Friend> list) {
        try {
            for (int i = 0 ; i < friends.length(); i ++) {
                final String name = friends.getJSONObject(i).getString("name");
                final String id = friends.getJSONObject(i).getString("id");
                final String[] url = {""};

                Bundle params = new Bundle();
                params.putBoolean("redirect", false);

                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + id + "/picture",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    url[0] = response.getJSONObject().getJSONObject("data").getString("url");
                                    Friend fbFriend = new Friend(id,name, url[0]);
                                    list.add(fbFriend);
                                    rvFriends.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    //adapter.notifyItemInserted(list.size() - 1);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.executeAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvLocationCity = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

}
