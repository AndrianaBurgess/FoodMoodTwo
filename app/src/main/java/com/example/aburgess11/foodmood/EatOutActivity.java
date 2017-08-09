package com.example.aburgess11.foodmood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aburgess11.foodmood.models.FoodItem;
import com.example.aburgess11.foodmood.models.Group;
import com.example.aburgess11.foodmood.models.Match;
import com.example.aburgess11.foodmood.models.Restaurant;
import com.example.aburgess11.foodmood.models.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangelali on 7/13/17.
 */

public class EatOutActivity extends AppCompatActivity {
    private static final int GROUPSESSION = 888;

    // instance fields
    // the list of individual matches
    private ArrayList<Match> myMatches;
    // the list of group matches
    private ArrayList<Match> groupMatches;
    // the recycler view
    RecyclerView rvMatches;
    // the nested scroll view
    NestedScrollView nestedScrollView;
    // the adapter wired to the recycler view
    public User user;
    public Group group;
    public Context context;
    public String cityName;
    public static DatabaseReference userRef;
    public static DatabaseReference groupRef;
    public static ArrayList<Restaurant> restaurantList;
    public static MatchesAdapter adapter;
    public static boolean isAlone;
    public static int swipeCount = 0;
    public static final int LOGIN = 1000;
    public static final String TAG = "EatOutActivity";
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    static AppBarLayout appBarLayout;
    TextView matchesHeader;
    ImageView upArrow;
    SwipePlaceHolderView mSwipeView;
//    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
//    @BindView(R.id.rejectBtn) ImageButton rejectBtn;
//    @BindView(R.id.acceptBtn) ImageButton acceptBtn;
//    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
//    @BindView(R.id.profileBtn) ImageButton profileBtn;

    // boolean to keep track of whether the matches page is expanded or collapsed
    static boolean isAppBarExpanded = false;

    // top toolbar items
    ImageButton profileBtn;
    Switch groupToggle;
    ImageButton refreshBtn;
    TextView refreshText;
    ImageButton newSwipeSessionBtn;
    TextView newSwipeSessionText;
    CollapsingToolbarLayout collapsingToolbar;

    TextView tvGroupSwipingBar;

    // load current profile
    private com.facebook.Profile fbProfile;
    private AccessTokenTracker accessTokenTracker;

    private SwipePlaceHolderView mySwipeView;
    private FrameLayout mySwipeViewContainer;
    private SwipePlaceHolderView groupSwipeView;
    private FrameLayout groupSwipeViewContainer;

    private Context mContext;

    public EatOutActivity() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOGIN", "onCreate");

        if (savedInstanceState != null) {
            Log.d("Sean", "onCreate: " + savedInstanceState.getInt("saved"));
        }
        setContentView(R.layout.activity_main);
        // init the list of individual matches
        myMatches = new ArrayList<>();
        // init the list of group matches
        groupMatches = new ArrayList<>();

//        // resolve the recycler view and connect a layout manager
//        rvMatches = (RecyclerView) findViewById(R.id.rvMatches);
//        rvMatches.setLayoutManager(new LinearLayoutManager(this));
//        // configure recycler view to allow for smooth scrolling
//        rvMatches.setNestedScrollingEnabled(false);
//        rvMatches.setHasFixedSize(false);
//        rvMatches.getLayoutManager().setAutoMeasureEnabled(true);
//        // connect recycler view to adapter
//        rvMatches.setAdapter(adapter);
//
//        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        // resolve matches window items
//        ButterKnife.bind(this);
        context = this.getApplicationContext();

        restaurantList = new ArrayList<>();
        isAlone = true;
        fbProfile = Profile.getCurrentProfile();

        initializeDatabase();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeUserInterface();
                initializeOnClickListeners();
            }
        });
    }

    public static void loadMatches(Map<String, Restaurant> restaurants) {
        for(String restaurantId : restaurants.keySet()) {
            Restaurant restaurant = restaurants.get(restaurantId);
            restaurantList.add(restaurant);
        }
        Collections.sort(restaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                Integer o1Counter = Integer.valueOf(o1.getCounter());
                Integer o2Counter = Integer.valueOf(o2.getCounter());
                return o2Counter - o1Counter;
            }
        });
        adapter.notifyDataSetChanged();
    }


    private void populateUserInterface() {
        ArrayList<Object> foodItems = new ArrayList<>();
        Map<String, Restaurant> restaurants = new HashMap<>();

         if(!groupToggle.isChecked()) {
            foodItems = user.getFoodItems();
            restaurants = user.getRestaurants();

            for(int i = 0; i < foodItems.size(); i++) {
                Map<String, String> data = (Map<String, String>) foodItems.get(i);
                if (data == null) {
                    continue;
                }

                mSwipeView.addView(new TinderCard(context, new FoodItem(data.get("restaurant"), data.get("image_url")), mSwipeView));
            }
        } else {
            foodItems = group.getFoodItems();
            restaurants = group.getRestaurants();

            for(int i = 0; i < foodItems.size(); i++) {
                Map<String, String> data = (Map<String, String>) foodItems.get(i);
                if (data == null) {
                    continue;
                }

                groupSwipeView.addView(new TinderCard(context, new FoodItem(data.get("restaurant"), data.get("image_url")), groupSwipeView));
            }
        }
        if (foodItems == null) {
            return;
        }

        loadMatches(restaurants);
    }

    private void initializeOnClickListeners() {

        // offsetChangedListener detects when there is a change in vertical offset (i.e. change from
        // collapsed to expanded, and vice versa)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(EatOutActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //  Range of vertical offset is from 0 to -1*(appBarLayout.getTotalScrollRange())
                if (verticalOffset < -10) {
                    // if the appbar goes from collapsed to expanded, change the state of isAppExpanded,
                    // the content and layout of matchesHeader, and remove the upArrow
                    isAppBarExpanded = true;
                    matchesHeader.setText(getString(R.string.matches_header_expanded));
                    matchesHeader.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
                    matchesHeader.setTextColor(Color.GRAY);
                    upArrow.setVisibility(ImageView.GONE);

                } else {
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

        newSwipeSessionBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creates a new group swiping session on firebase with your facebook id being the group identifier
                    final String id = Profile.getCurrentProfile().getId();
                    final DatabaseReference groups = database.getReference().child("Groups");

                    groups.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> nodes = dataSnapshot.getChildren();
                            boolean isInGroup = false;
                            for (  DataSnapshot d  : nodes ){
                                if(!d.getKey().equals(id) && d.child("Users").hasChild(id)){
                                    Toast.makeText(getApplicationContext(),"You are already in a group", Toast.LENGTH_LONG).show();
                                    isInGroup = true;
                                }
                            }
                            if (!isInGroup) {
                                String name =  Profile.getCurrentProfile().getName();
//                                DatabaseReference myRef = database.getReference();
//                                myRef.child("Groups").child(id).child("Users").child(id).setValue(name);

                                String groupId = Profile.getCurrentProfile().getId();
                                group = new Group();
                                group.groupId = groupId;
                                Map<String, String> users = new HashMap<>();
                                users.put(groupId, name);
                                group.setUsers(users);
                                database.getReference("Groups").child(groupId).setValue(group);
                                groupRef = database.getReference("Groups").child(groupId);
                                populateDatabase(database, groupRef);
                                listenForUpdates(database, groupRef);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        groupSwipeView = (SwipePlaceHolderView) findViewById(R.id.groupSwipeView);
                                        groupSwipeView.getBuilder()
                                                .setDisplayViewCount(3)
                                                .setSwipeDecor(new SwipeDecor()
                                                        .setPaddingTop(20)
                                                        .setRelativeScale(0.01f)
                                                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                                                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

                                        Intent i = new Intent(EatOutActivity.this, GroupActivity.class);
                                        startActivityForResult(i, GROUPSESSION);
                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                myMatches = refreshIndividualSwiping(getApplicationContext());
                initializeDatabase();
                switchViews(restaurantList, false);
            }
        });

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
//        loadMatches(this.getApplicationContext(), myMatches);
//        loadMatches(this.getApplicationContext(), groupMatches);

        mySwipeViewContainer = (FrameLayout)findViewById(R.id.mySwipeViewContainer);
        mySwipeView = (SwipePlaceHolderView)findViewById(R.id.mySwipeView);
        groupSwipeViewContainer = (FrameLayout) findViewById(R.id.groupSwipeViewContainer);
        groupSwipeView = (SwipePlaceHolderView)findViewById(R.id.groupSwipeView);

        mContext = getApplicationContext();

//        // init the myMatches and groupMatches cards
//        mySwipeView.getBuilder()
//                .setDisplayViewCount(3)
//                .setSwipeDecor(new SwipeDecor()
//                        .setPaddingTop(20)
//                        .setRelativeScale(0.01f)
//                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
//                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

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

        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener() {
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
                        switchViews(restaurantList, true);
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

//        rejectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSwipeView.doSwipe(false);
//                Log.d("EVENT", "swipeCount");
//
//            }
//        });
//
//        acceptBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSwipeView.doSwipe(true);
//                Log.d("EVENT", "swipeCount");
//            }
//        });
//
//        profileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(EatOutActivity.this, SettingsActivity.class);
//                startActivity(i);
//                overridePendingTransition(R.anim.enter_from_left_to_right, R.anim.exit_from_right_to_left);
//            }
//        });

        // tracks if there are any changes to the groupToggle status
        groupToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("LOGIN STATUS", "isChecked " + isChecked);
                if(isChecked == true) {
                    isAlone = false;
                } else {
                    isAlone = true;
                }
                if (fbProfile == null && isChecked) {
                    Log.d("LOGIN STATUS", "user was not logged in. launching LoginActivity");
                    Intent i = new Intent(EatOutActivity.this, LoginActivity.class);
                    EatOutActivity.this.startActivityForResult(i, LOGIN);
                } else if (isChecked) {
                    Log.d("LOGIN STATUS:", "user was already logged in");
                    switchViews(restaurantList, true);
                    Toast.makeText(getApplicationContext(), "Welcome to GroupSwiping, " + fbProfile.getFirstName() + "!", Toast.LENGTH_SHORT).show();
                } else if (!isChecked) {
                    switchViews(restaurantList, false);
                }
            }
        });
    }

//    private ArrayList<Match> refreshIndividualSwiping(Context context) {
//        myMatches = new ArrayList<>();
//        loadMatches(getApplicationContext(), myMatches);
//
//        // delete mySwipeView from its container and create a new one
//        mySwipeViewContainer.removeAllViews();
//        mySwipeView = new SwipePlaceHolderView(EatOutActivity.this);
//        mySwipeViewContainer.addView(mySwipeView);
//
//        swipeCount=0;
//
//        // init the myMatches and groupMatches cards
//        mySwipeView.getBuilder()
//                .setDisplayViewCount(3)
//                .setSwipeDecor(new SwipeDecor()
//                        .setPaddingTop(20)
//                        .setRelativeScale(0.01f)
//                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
//                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
//
//        for(SwipeProfile profile : Utils.loadProfiles(getApplicationContext())){
//            mySwipeView.addView(new TinderCard(mContext, profile, mySwipeView, myMatches));
//        }
//
//        return myMatches;
//    }

//        private ArrayList<Restaurant> refreshIndividualSwiping(Context context) {
//            Map<String, Restaurant> restaurants = new HashMap<>();
//            myMatches = new ArrayList<>();
//
//            // delete mySwipeView from its container and create a new one
//            mySwipeViewContainer.removeAllViews();
//            mySwipeView = new SwipePlaceHolderView(EatOutActivity.this);
//            mySwipeViewContainer.addView(mySwipeView);
//
//            swipeCount=0;
//
//            // init the myMatches and groupMatches cards
//            mySwipeView.getBuilder()
//                    .setDisplayViewCount(3)
//                    .setSwipeDecor(new SwipeDecor()
//                            .setPaddingTop(20)
//                            .setRelativeScale(0.01f)
//                            .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
//                            .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
//
//            for(SwipeProfile profile : Utils.loadProfiles(getApplicationContext())){
//                mySwipeView.addView(new TinderCard(mContext, profile, mySwipeView, myMatches));
//            }
//
//            loadMatches();
//            return myMatches;
//        }

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
            boolean isDismissed;
            if (data == null) {
                isDismissed = true;
            } else {
                isDismissed = data.getBooleanExtra("isDismissed", false);
            }

            if (resultCode != RESULT_OK || isDismissed) {
                // login failed or canceled
                groupToggle.setChecked(false);
                switchViews(restaurantList, false);
                return;
            }

            else if (resultCode == RESULT_OK && !isDismissed){
                // if login is successful and the dialog was not dismissed, then groupToggle.setChecked(true);
                switchViews(restaurantList, true);
            }
        }

        if (requestCode == GROUPSESSION) {

            boolean sessionStarted = (data != null) && data.getBooleanExtra("sessionStarted", false);

            if (resultCode != RESULT_OK || !sessionStarted) {
                // session failed or canceled
                Toast.makeText(getApplicationContext(), "session failed or cancelled", Toast.LENGTH_LONG).show();
            }

            else if (resultCode == RESULT_OK && sessionStarted){
                // if login is successful and the dialog was not dismissed, then groupToggle.setChecked(true);

                int hours = GroupActivity.numHours;
                final int mins = GroupActivity.numMins;

                long millisUntilFinished = (long) (3600000 * hours) + (60000 * mins);

                new CountDownTimer(millisUntilFinished, 1000) {

                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                        int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                        int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);

                        String remainingTime;

                        if (hours != 0) {
                            remainingTime = hours + "hr " + minutes + "min " + seconds + "s ";
                        } else if (minutes != 0) {
                            remainingTime = minutes + "min " + seconds + "s ";
                        } else {
                            remainingTime = seconds + "s ";
                        }

                        tvGroupSwipingBar.setVisibility(View.VISIBLE);
                        tvGroupSwipingBar.setText("Swipe session in progress! " + remainingTime + "left");
                    }

                    public void onFinish() {
                        // TODO: notify user that session is done

                        tvGroupSwipingBar.setVisibility(View.GONE);
                    }
                }.start();
            }
        }
    }

    public void switchViews(List<Restaurant> restaurants, Boolean isCheckedStatus) {

        // set the adapter for the new matches
        adapter.reloadMatches(restaurants);

        if (isCheckedStatus) {
            // switch to groupSwiping mode
            mySwipeView.setVisibility(View.GONE);
            groupSwipeView.setVisibility(View.VISIBLE);

            refreshBtn.setVisibility(ImageButton.GONE);
            refreshText.setVisibility(TextView.GONE);
            newSwipeSessionBtn.setVisibility(ImageButton.VISIBLE);
            newSwipeSessionText.setVisibility(TextView.VISIBLE);
        } else {
            // switch to individual mode
            mySwipeView.setVisibility(View.VISIBLE);
            groupSwipeView.setVisibility(View.GONE);

            refreshBtn.setVisibility(ImageButton.VISIBLE);
            refreshText.setVisibility(TextView.VISIBLE);
            newSwipeSessionBtn.setVisibility(ImageButton.GONE);
            newSwipeSessionText.setVisibility(TextView.GONE);
        }
    }

    private void initializeAdapter() {
        try {
            adapter = new MatchesAdapter(restaurantList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        //getting reference to the sessions and populating the data
        //User
        fbProfile = Profile.getCurrentProfile();
        String userId = fbProfile.getId();
        user = new User();
        user.name = fbProfile.getName();
        database.getReference("Users").child(userId).setValue(user);
        userRef = database.getReference("Users").child(userId);
        populateDatabase(database, userRef);
        listenForUpdates(database, userRef);
    }

    private void listenForUpdates(final FirebaseDatabase database, final DatabaseReference sessionRef) {
        final DatabaseReference restaurantRef = sessionRef.child("Restaurants");
        restaurantRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Child", "Child added: " + s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateLocalData(sessionRef, dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Child", "Child removed: " + dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("Child", "Child moved: " + s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Child", "Child error: " + databaseError.getCode());

            }
        });
    }

    private void populateDatabase(final FirebaseDatabase database, final DatabaseReference sessionRef) {
        //getting the references to the data once to populate it to the user/group
        cityName = getIntent().getStringExtra("cityName");

        DatabaseReference citiesRef = database.getReference("Cities");
        Log.d("Sean", "citiesRef: " + database.getReference("Cities").child(cityName).toString());

        citiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Sean", "Restaurants: " + dataSnapshot.child(cityName).child("Restaurants").getValue().toString());
                Map<String, Restaurant> restaurants = new HashMap();
                for (DataSnapshot restaurantData : dataSnapshot.child(cityName).child("Restaurants").getChildren()) {
                    restaurants.put(restaurantData.getKey(), restaurantData.getValue(Restaurant.class));
                }
                DatabaseReference restaurantsRef = sessionRef.child("Restaurants");
                restaurantsRef.setValue(restaurants);

                Log.d("Sean", "Food Items: " + dataSnapshot.child(cityName).child("Food Items").getValue().toString());
                Log.d("Sean", "Food Items Class: " + dataSnapshot.child(cityName).child("Food Items").getClass().toString());

                ArrayList<Object> foodItems = (ArrayList) dataSnapshot.child(cityName).child("Food Items").getValue();
                DatabaseReference foodItemsRef = sessionRef.child("Food Items");
                foodItemsRef.setValue(foodItems);

                populateLocalData(sessionRef, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Sean", "The database failed to read: " + databaseError.getCode());
            }
        });
    }

    public void updateLocalData(DatabaseReference sessionRef, DataSnapshot dataSnapshot) {
        //if the session is a single user, update the user info; if a group, update group info
        if(sessionRef.getParent() == database.getReference("Groups")) {
            Restaurant rest = group.restaurants.get(dataSnapshot.getKey());
            rest.setCounter(dataSnapshot.child("counter").getValue().toString());
            group.restaurants.put(dataSnapshot.child("id").getValue().toString(), rest);
            restaurantList.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadMatches(group.restaurants);
                }
            });
        } else {
            Restaurant rest = user.restaurants.get(dataSnapshot.getKey());
            rest.setCounter(dataSnapshot.child("counter").getValue().toString());
            user.restaurants.put(dataSnapshot.child("id").getValue().toString(), rest);
            restaurantList.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadMatches(user.restaurants);
                }
            });
        }
    }

    public void populateLocalData(DatabaseReference sessionRef, DataSnapshot dataSnapshot) {
        if(sessionRef.getParent().getKey() == database.getReference("Groups").getKey()) {
            group.foodItems = (ArrayList) dataSnapshot.child(cityName).child("Food Items").getValue();
            //group.restaurants = (HashMap) dataSnapshot.child(cityName).child("Restaurants").getValue();
            group.restaurants = new HashMap<>();
            for (DataSnapshot restaurantData : dataSnapshot.child(cityName).child("Restaurants").getChildren()) {
                group.restaurants.put(restaurantData.getKey(), restaurantData.getValue(Restaurant.class));
            }
        } else {
            user.foodItems = (ArrayList) dataSnapshot.child(cityName).child("Food Items").getValue();
            //user.restaurants = (HashMap) dataSnapshot.child(cityName).child("Restaurants").getValue();
            user.restaurants = new HashMap<>();
            for (DataSnapshot restaurantData : dataSnapshot.child(cityName).child("Restaurants").getChildren()) {
                user.restaurants.put(restaurantData.getKey(), restaurantData.getValue(Restaurant.class));
            }
        }

        // notify the adapter
        populateUserInterface();
    }

    private void initializeUserInterface() {
        initializeAdapter();

        // resolve upper toolbar items
        profileBtn = (ImageButton) findViewById(R.id.profileBtn);
        groupToggle = (Switch) findViewById(R.id.groupToggle);
        groupToggle.setShowText(true);
        refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
        refreshText = (TextView) findViewById(R.id.refreshText);
        newSwipeSessionBtn = (ImageButton) findViewById(R.id.newSwipeSessionBtn);
        newSwipeSessionText = (TextView) findViewById(R.id.tvNewSwipeSessionText);
        tvGroupSwipingBar = (TextView) findViewById(R.id.tvGroupSwipingBar);

        //initialize objects that cant use Butterknife
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        // initialize the header and upArrow to the default collapsed layout
        matchesHeader = (TextView) findViewById(R.id.tvMatchesHeader);
        matchesHeader.setText(getString(R.string.matches_header_collapsed));
        upArrow = (ImageView) findViewById(R.id.ivUpArrow);
        upArrow.setVisibility(ImageView.VISIBLE);

        rvMatches = (RecyclerView) findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        // configure recycler view to allow for smooth scrolling
        rvMatches.setNestedScrollingEnabled(false);
        rvMatches.setHasFixedSize(false);
        rvMatches.getLayoutManager().setAutoMeasureEnabled(true);
        // connect recycler view to adapter
        rvMatches.setAdapter(adapter);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.mySwipeView);
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

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
    public void onBackPressed() {
        EatOutActivity.isAppBarExpanded = false;
        appBarLayout.setExpanded(true);
        appBarLayout.setFitsSystemWindows(false);
    }
}
