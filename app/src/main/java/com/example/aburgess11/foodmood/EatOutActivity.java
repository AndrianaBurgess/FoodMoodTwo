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

import com.example.aburgess11.foodmood.models.Config;
import com.example.aburgess11.foodmood.models.Match;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by liangelali on 7/13/17.
 */

public class EatOutActivity extends AppCompatActivity {
    private static final int LOGIN = 1000;
    // constants

    public final static String TAG = "EatOutActivity";

    // instance fields
    AsyncHttpClient client;
    // the list of individual matches
    private ArrayList<Match> myMatches;
    // the list of group matches
    private ArrayList<Match> groupMatches;
    // the recycler view
    RecyclerView rvMatches;
    // the nested scroll view
    NestedScrollView nestedScrollView;
    // the adapter wired to the recycler view
    public static MatchesAdapter adapter;
    // image config
    Config config;

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
    ImageButton newSwipeSessionBtn;
    TextView tvGroupSwipingBar;

    // load current profile
    private com.facebook.Profile fbProfile;
    private AccessTokenTracker accessTokenTracker;

    private SwipePlaceHolderView mySwipeView;
    private SwipePlaceHolderView groupSwipeView;

    private Context mContext;
    public static int swipeCount=0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        Log.d("LOGIN", "onCreate");

        if (savedInstanceState != null) {
            Log.d("Sean", "onCreate: " + savedInstanceState.getInt("saved"));
        }
        setContentView(R.layout.activity_main);
        // initialize the client
        client = new AsyncHttpClient();
        // init the list of individual matches
        myMatches = new ArrayList<>();
        // init the list of group matches
        groupMatches = new ArrayList<>();
        // initialize the adapter -- matches array cannot be reinitialized after this point
        try {
            adapter = new MatchesAdapter(myMatches);
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

        newSwipeSessionBtn = (ImageButton) findViewById(R.id.newSwipeSessionBtn);
        tvGroupSwipingBar = (TextView) findViewById(R.id.tvGroupSwipingBar);


        
        newSwipeSessionBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(EatOutActivity.this, GroupActivity.class);
                        startActivity(i);
                    }
                });





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

        loadMatches(this.getApplicationContext(), myMatches);
        loadMatches(this.getApplicationContext(), groupMatches);


        mySwipeView = (SwipePlaceHolderView)findViewById(R.id.mySwipeView);
        groupSwipeView = (SwipePlaceHolderView)findViewById(R.id.groupSwipeView);

        mContext = getApplicationContext();



        // init the myMatches and groupMatches cards
        mySwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        for(SwipeProfile profile : Utils.loadProfiles(this.getApplicationContext())){
            mySwipeView.addView(new TinderCard(mContext, profile, mySwipeView, myMatches));
        }

        groupSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        for(SwipeProfile profile : Utils.loadProfiles(this.getApplicationContext())){
            groupSwipeView.addView(new TinderCard(mContext, profile, groupSwipeView, groupMatches));
        }



        // listeners for swiping and pressing the accept / reject buttons
        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groupToggle.isChecked()) {
                    mySwipeView.doSwipe(false);
                    Log.d("EVENT",  "swipeCount" );
                } else {
                    groupSwipeView.doSwipe(false);
                    Log.d("EVENT",  "swipeCount" );
                }
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groupToggle.isChecked()) {
                    mySwipeView.doSwipe(true);
                    Log.d("EVENT",  "swipeCount" );
                } else {
                    groupSwipeView.doSwipe(true);
                    Log.d("EVENT",  "swipeCount" );
                }
            }
        });


        // sets listener for profile button to go to settings page
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EatOutActivity.this, SettingsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_left_to_right, R.anim.exit_from_right_to_left);
            }
        });


        // tracks if there are changes to the login / logout status
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                fbProfile = Profile.getCurrentProfile();

                Log.d(TAG, "onCurrentAccessTokenChanged()");
                if (fbProfile != null && accessToken == null) {
                    Log.d(TAG, "user logged in");
                    if (groupToggle.isChecked()) {
                        // if the user logged in from toggling the groupToggle switch, there was no change in
                        // groupToggle.isChecked, but the matches still need to be reloaded
                        tvGroupSwipingBar.setVisibility(TextView.VISIBLE);
                        reloadMatches(getApplicationContext(), groupMatches);
                        Toast.makeText(getApplicationContext(), "Welcome to GroupSwiping, " + fbProfile.getFirstName() + "!", Toast.LENGTH_SHORT).show();
                    }
                } else if (fbProfile == null && accessToken2 == null) {
                    Log.d(TAG, "user logged out");
                    if (groupToggle.isChecked()) {
                        // if the user logged out from the settings page, make sure that the groupToggle
                        // toggles back to individual swiping mode
                        groupToggle.setChecked(false);
                    }
                }
            }
        };


        // tracks if there are any changes to the groupToggle status
        groupToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("LOGIN STATUS", "isChecked " + isChecked);
                if (fbProfile == null && isChecked) {
                    Log.d("LOGIN STATUS", "user was not logged in. launching LoginActivity");
                    Intent i = new Intent(EatOutActivity.this, LoginActivity.class);
                    EatOutActivity.this.startActivityForResult(i, LOGIN);

                } else if (isChecked){
                    Log.d("LOGIN STATUS:", "user was already logged in");
                    tvGroupSwipingBar.setVisibility(TextView.VISIBLE);
                    reloadMatches(getApplicationContext(), groupMatches);
                    Toast.makeText(getApplicationContext(), "Welcome to GroupSwiping, " + fbProfile.getFirstName() + "!", Toast.LENGTH_SHORT).show();
                } else if (!isChecked) {
                    reloadMatches(getApplicationContext(), myMatches);
                    tvGroupSwipingBar.setVisibility(TextView.GONE);
                }
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
        Log.d("LOGIN", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("LOGIN", "onStop");
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN) {
            if (resultCode != RESULT_OK) {
                // login failed
                groupToggle.setChecked(false);
                return;
            }

            boolean isDismissed = data.getBooleanExtra("isDismissed", false);
            if (isDismissed) {
                // login canceled
                groupToggle.setChecked(false);
                return;
            }

            else if (resultCode == RESULT_OK && !isDismissed){
                groupToggle.setChecked(true);
                reloadMatches(this.getApplicationContext(), groupMatches);
            }
        }
    }

    public void reloadMatches(Context context, ArrayList<Match> matches) {

        // set the adapter for the new matches
        adapter.reloadMatches(matches);

        if (mySwipeView.getVisibility() == View.VISIBLE) {
            mySwipeView.setVisibility(View.GONE);
            groupSwipeView.setVisibility(View.VISIBLE);
        } else {
            mySwipeView.setVisibility(View.VISIBLE);
            groupSwipeView.setVisibility(View.GONE);
        }
    }

    public static void loadMatches(Context context , ArrayList<Match> list) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            String jsonString = loadJSONFromAsset(context, "restaurants.json");
            JSONObject obj = new JSONObject(jsonString);
            JSONArray array = obj.getJSONArray("restaurants");

            for (int i = 0; i < array.length(); i++) {
                Match match = gson.fromJson(array.getString(i), Match.class);
                list.add(match);
                adapter.notifyItemInserted(list.size() - 1);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
    }




    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is = null;
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



    @Override
    public void onBackPressed()
    {
        EatOutActivity.isAppBarExpanded = false;
        appBarLayout.setExpanded(true);
        appBarLayout.setFitsSystemWindows(false);

    }
}
