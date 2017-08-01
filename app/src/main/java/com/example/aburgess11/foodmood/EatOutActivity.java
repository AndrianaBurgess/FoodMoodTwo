package com.example.aburgess11.foodmood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aburgess11.foodmood.adapters.MatchesAdapter;
import com.example.aburgess11.foodmood.models.Config;
import com.example.aburgess11.foodmood.models.Match;
import com.example.aburgess11.foodmood.models.SwipeProfile;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by liangelali on 7/13/17.
 */

public class EatOutActivity extends AppCompatActivity {
    private static final int LOGIN = 1000;
    // constants

    // tag for logging from this activity
    public final static String TAG = "EatOutActivity";

    // instance fields
    AsyncHttpClient client;
    // the list of currently playing movies
    public static ArrayList<Match> matches;
    // the recycler view
    RecyclerView rvMatches;
    // the nested scroll view
    NestedScrollView nestedScrollView;
    // the adapter wired to the recycler view
    public static MatchesAdapter adapter;
    // image config
    Config config;
    //the client for Yelp
    YelpClient yelpClient;
    public static ArrayList<Business> businesses;

    // matches window items
    static AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbar;
    TextView matchesHeader;
    ImageView upArrow;

    // boolean to keep track of whether the matches page is expanded or collapsed
    static boolean isAppBarExpanded = false;

    // top toolbar items
    ImageButton profileBtn;
    Switch groupToggle;
    ImageButton messagesBtn;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    public static int swipeCount=0;
    private final static String clientId = "FJbrnaXwVmgGJTk1xd2jwA";
    private final static String clientSecret = "fAwIdrHUUvrHpbMbyOp4gVASjtH0TvJzj56TGgrXeyg3q994Nb6HWRgFGNWXTQ7z";

    public EatOutActivity() throws IOException {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("onSaveInstanceState", 1);
        Log.d("Sean", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("Sean", "onRestoreInstanceState: " + savedInstanceState.getInt("saved"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d("Sean", "onCreate: " + savedInstanceState.getInt("saved"));
        }
        setContentView(R.layout.activity_main);
        // initialize the client
        client = new AsyncHttpClient();
//        yelpClient = new YelpClient("2000","37.479733","-122.154078");

        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        YelpFusionApi yelpFusionApi = null;
        try {
            yelpFusionApi = apiFactory.createAPI(clientId, clientSecret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<>();

        String radius = "2000";
        String latitude = "37.479733";
        String longitude = "-122.154078";

        // general params
        params.put("radius", radius);
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        Response<SearchResponse> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchResponse searchResponse = response.body();
        int totalNumberOfResult = searchResponse.getTotal();  // i.e. 3
        businesses = searchResponse.getBusinesses();
        // init the list of matches
        matches = new ArrayList<>();
        businesses = yelpClient.getBusinesses();
        // initialize the adapter -- movies array cannot be reinitialized after this point
        try {
            adapter = new MatchesAdapter(businesses);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // resolve the recycler view and connect a layout manager
        rvMatches = (RecyclerView) findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        // configure recycler view to allow for smooth scrolling
        rvMatches.setNestedScrollingEnabled(false);
        rvMatches.setHasFixedSize(false);
        rvMatches.getLayoutManager().setAutoMeasureEnabled(true);
        // connect recycler view to adapter
        rvMatches.setAdapter(adapter);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);


        // resolve matches window items
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        matchesHeader = (TextView) findViewById(R.id.tvMatchesHeader);
        upArrow = (ImageView) findViewById(R.id.ivUpArrow);

        // initialize the header and upArrow to the default collapsed layout
        matchesHeader.setText(getString(R.string.matches_header_collapsed));
        upArrow.setVisibility(ImageView.VISIBLE);


        // resolve upper toolbar items
        profileBtn = (ImageButton) findViewById(R.id.profileBtn);
        groupToggle = (Switch) findViewById(R.id.groupToggle);
        groupToggle.setShowText(true);
        messagesBtn = (ImageButton) findViewById(R.id.messagesBtn);


        // offsetChangedListener detects when there is a change in vertical offset (i.e. change from
        // collapsed to expanded, and vice versa)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(EatOutActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //  Vertical offset == 0 indicates appBar is collapsed
                //  Range of vertical offset is from 0 to -1*(appBarLayout.getTotalScrollRange())

                if (verticalOffset < -10) {
                    // if the appbar goes from collapsed to expanded, change the state of isAppExpanded,
                    // the content and layout of matchesHeader, and remove the upArrow
                    isAppBarExpanded = true;
                    matchesHeader.setText(getString(R.string.matches_header_expanded));
                    matchesHeader.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
                    matchesHeader.setTextColor(Color.GRAY);
                    upArrow.setVisibility(ImageView.GONE);

                }else {
                    // if the appbar goes back to collapsed, scroll to top, change the state of isAppExpanded,
                    // the content and layout of matchesHeader, and reinstate the upArrow
                    isAppBarExpanded = false;
                    nestedScrollView.setScrollY(0);
                    matchesHeader.setText(getString(R.string.matches_header_collapsed));
                    matchesHeader.setTypeface(Typeface.DEFAULT);
                    matchesHeader.setTextAppearance(R.style.TextAppearance_AppCompat);
                    matchesHeader.setTextColor(Color.GRAY);
                    upArrow.setVisibility(ImageView.VISIBLE);

                }
            }
        });

        // onClickListener for expanding and collapsing after clicking the appbar
        collapsingToolbar.setOnClickListener(new View.OnClickListener() {

            /* IMPORTANT NOTE:

            tl;dr the built-in features for expanded and collapsed are opposite of what they actually are
                  (app:expanded="true" means collapsed, setExpanded(true) sets the window to collapsed)

            CollapsingToolbarLayout is designed to expand from the top, but because
            this CollapsingToolbarLayout needs to expand from the bottom, the built-in attributes for
            app:expanded / app:collapsed describe the opposite of what this CollapsingToolbarLayout
            shows (i.e. app:expanded="true" is the collapsed view of this CollapsingToolbarLayout) */

            @Override
            public void onClick(View v) {
                // if clicked while appbar is collapsed, expand the appbar, and vice versa
                if (isAppBarExpanded == false) {
                    isAppBarExpanded = true;
                    appBarLayout.setExpanded(false);
                    appBarLayout.setFitsSystemWindows(true);
                } else {
                    isAppBarExpanded = false;
                    appBarLayout.setExpanded(true);
                    appBarLayout.setFitsSystemWindows(false);
                }
            }
        });


        // get the configuration on app creation
        //getConfiguration();
        loadMatches(this.getApplicationContext(), businesses);



            mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
            mContext = getApplicationContext();

            mSwipeView.getBuilder()
                    .setDisplayViewCount(3)
                    .setSwipeDecor(new SwipeDecor()
                            .setPaddingTop(20)
                            .setRelativeScale(0.01f)
                            .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                            .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


            for(SwipeProfile profile : Utils.loadProfiles(this.getApplicationContext())){
                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
            }

            findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeView.doSwipe(false);
                    Log.d("EVENT",  "swipeCount" );

                }
            });

            findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeView.doSwipe(true);
                    Log.d("EVENT", "swipeCount");
                }
            });

        // TODO: groupToggle login on checked is being created here BUT NOTHING IS WORKING
        groupToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                com.facebook.Profile profile = Profile.getCurrentProfile();

                if (profile == null && isChecked) {
                    Log.d("LOGIN STATUS", "user was not logged in. launching LoginActivity");
                    Intent i = new Intent(EatOutActivity.this, LoginActivity.class);
                    EatOutActivity.this.startActivityForResult(i, LOGIN);

                } else if (isChecked){
                    Log.d("LOGIN STATUS:", "user was already logged in");
                    Toast.makeText(getApplicationContext(), "Welcome to GroupSwiping, " + profile.getFirstName() + "!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        profileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(EatOutActivity.this, SettingsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_left_to_right, R.anim.exit_from_right_to_left);
            }
        });

        //TODO FIREBASE
//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN) {
            if (resultCode != RESULT_OK) {
                groupToggle.setChecked(false);
                return;
            }

            boolean isDismissed = data.getBooleanExtra("isDismissed", false);
            if (isDismissed) {
                groupToggle.setChecked(false);
                return;
            }
        }
    }

    public static void loadMatches(Context context , ArrayList<Business> list) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            //JSONArray array = new JSONArray(loadJSONFromAsset(context, "foods.json"));

            String jsonString = loadJSONFromAsset(context, "restaurants.json");
            JSONObject obj = new JSONObject(jsonString);
            JSONArray array = obj.getJSONArray("restaurants");

            for (int i = 0; i < businesses.size(); i++) {
//                Match match = gson.fromJson(array.getString(i), Match.class);
//                list.add(match);
//                adapter.notifyItemInserted(list.size() - 1);

                Business business = gson.fromJson(array.getString(i), Business.class);
                list.add(business);
                adapter.notifyItemInserted(list.size() - 1);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
    }


    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is=null;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG,"path "+jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        log.e(TAG, message, error);
        //alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {

        EatOutActivity.isAppBarExpanded = false;
        appBarLayout.setExpanded(true);
        appBarLayout.setFitsSystemWindows(false);

//        Intent i = new Intent(EatOutActivity.this, EatOutActivity.class);
//        startActivity(i);
    }

}
