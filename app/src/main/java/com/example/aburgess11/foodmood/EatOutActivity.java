package com.example.aburgess11.foodmood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.example.aburgess11.foodmood.models.FoodItem;
import com.example.aburgess11.foodmood.models.Group;
import com.example.aburgess11.foodmood.models.Restaurant;
import com.example.aburgess11.foodmood.models.User;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liangelali on 7/13/17.
 */

public class EatOutActivity extends AppCompatActivity {

    // instance fields
    public User user;
    public Group group;
    public Context context;
    public String cityName;
    public static DatabaseReference userRef;
    public static DatabaseReference groupRef;
    public static ArrayList<Restaurant> restaurantList;
    public static Map<String, Object> restaurantMap;
    public static MatchesAdapter adapter;
    public static boolean isAlone;
    public static boolean isAppBarExpanded = false;
    public static int swipeCount = 0;
    public static final int LOGIN = 1000;
    public static final String TAG = "EatOutActivity";
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    static AppBarLayout appBarLayout;
    RecyclerView rvMatches;
    Switch groupToggle;
    TextView matchesHeader;
    ImageView upArrow;
    SwipePlaceHolderView mSwipeView;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.rejectBtn) ImageButton rejectBtn;
    @BindView(R.id.acceptBtn) ImageButton acceptBtn;
    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
    @BindView(R.id.profileBtn) ImageButton profileBtn;

    public EatOutActivity() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d("Sean", "onCreate: " + savedInstanceState.getInt("saved"));
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this.getApplicationContext();

        //initialize objects that cant use Butterknife
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        rvMatches = (RecyclerView) findViewById(R.id.rvMatches);
        matchesHeader = (TextView) findViewById(R.id.tvMatchesHeader);
        upArrow = (ImageView) findViewById(R.id.ivUpArrow);
        groupToggle = (Switch) findViewById(R.id.groupToggle);

        restaurantList = new ArrayList<>();
        isAlone = true;

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
//            HashMap<String, Object> restaurant = (HashMap) restaurants.get(restaurantId);
            Restaurant restaurant1 = restaurants.get(restaurantId);
//            Restaurant newRest = new Restaurant();
//            newRest.setName(restaurant.get("name").toString());
//            newRest.setCounter(restaurant.get("counter").toString());
//            newRest.setImageUrl(restaurant.get("image_url").toString());
//            newRest.setRestaurantId(restaurant.get("id").toString());
//            newRest.setPhoneNumber(restaurant.get("phone").toString());
//            newRest.setRating(restaurant.get("rating").toString());
//            newRest.setReviewCount(restaurant.get("review_count").toString());
//
//            HashMap<String, String> coordinates = (HashMap) restaurant.get("coordinates");
//            newRest.setLatitude(coordinates.get("latitude"));
//            newRest.setLongitude(coordinates.get("longitude"));

            restaurantList.add(restaurant1);

//            adapter.notifyItemInserted(restaurantList.size() - 1);
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

        if(isAlone) {
            foodItems = user.getFoodItems();
            restaurants = user.getRestaurants();
        } else {
            foodItems = group.getFoodItems();
            restaurants = group.getRestaurants();
        }
//        restaurantMap = restaurants;

        if (foodItems == null) {
            return;
        }

        for(int i = 0; i < foodItems.size(); i++) {
            Map<String, String> data = (Map<String, String>) foodItems.get(i);
            if (data == null) {
                continue;
            }

            mSwipeView.addView(new TinderCard(context, new FoodItem(data.get("restaurant"), data.get("image_url")), mSwipeView));
        }
        loadMatches(restaurants);
    }

    private void initializeOnClickListeners() {

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


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                Log.d("EVENT", "swipeCount");

            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                Log.d("EVENT", "swipeCount");
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

        groupToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                com.facebook.Profile profile = Profile.getCurrentProfile();

                if(isChecked == true) {
                    isAlone = false;
                } else {
                    isAlone = true;
                }

                if (profile == null && isChecked) {
                    Log.d("LOGIN STATUS", "user was not logged in. launching LoginActivity");
                    Intent i = new Intent(EatOutActivity.this, LoginActivity.class);
                    EatOutActivity.this.startActivityForResult(i, LOGIN);

                } else if (isChecked) {
                    Log.d("LOGIN STATUS:", "user was already logged in");
                    Toast.makeText(getApplicationContext(), "Welcome to GroupSwiping, " + profile.getFirstName() + "!", Toast.LENGTH_SHORT).show();
                }

//                if(profile != null && isChecked) {
//                    Intent i = new Intent(EatOutActivity.this, GroupActivity.class);
//                    EatOutActivity.this.startActivity(i);
//                }
            }
        });
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
        String userId = "some_user_id";
        user = new User();
        user.userId = userId;
        database.getReference("Users").child(userId).setValue(user);
        userRef = database.getReference("Users").child(userId);
        populateDatabase(database, userRef);
        listenForUpdates(database, userRef);

        //Group
        String groupId = "some_group_id";
        group = new Group();
        group.groupId = groupId;
        database.getReference("Groups").child(groupId).setValue(group);
        groupRef = database.getReference("Groups").child(groupId);
        populateDatabase(database, groupRef);
        listenForUpdates(database, groupRef);
    }

    private void listenForUpdates(final FirebaseDatabase database, final DatabaseReference sessionRef) {
        final DatabaseReference restaurantRef = sessionRef.child("Restaurants");
//        //listening if the values ever change
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                populateLocalData(sessionRef, dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("Database", "The database listening update failed: " + databaseError.getCode());
//            }
//        };
//        sessionRef.addValueEventListener(postListener);
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
        if(sessionRef.getParent() == database.getReference("Groups")) {
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

        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        // configure recycler view to allow for smooth scrolling
        rvMatches.setNestedScrollingEnabled(false);
        rvMatches.setHasFixedSize(false);
        rvMatches.getLayoutManager().setAutoMeasureEnabled(true);
        // connect recycler view to adapter
        rvMatches.setAdapter(adapter);
        // initialize the header and upArrow to the default collapsed layout
        matchesHeader.setText(getString(R.string.matches_header_collapsed));
        upArrow.setVisibility(ImageView.VISIBLE);
        groupToggle.setShowText(true);

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

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

//        populateUserInterface();
    }

//    public void loadMatches2(Context context, ArrayList<Match> list) {
//        try {
//            GsonBuilder builder = new GsonBuilder();
//            Gson gson = builder.create();
//
//            String jsonString = loadJSONFromAsset(context, "restaurants.json");
//            JSONObject obj = new JSONObject(jsonString);
//            JSONArray array = obj.getJSONArray("restaurants");
//            JSONArray array2 = businesses;
//
//            for (int i = 0; i < array.length(); i++) {
//                Match match = gson.fromJson(array.getString(i), Match.class);
////                businesses.get(i);
////                Match match = new Match();
////                match.setImageUrl();
//                list.add(match);
//                adapter.notifyItemInserted(list.size() - 1);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//    }

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

    @Override
    public void onBackPressed() {
        EatOutActivity.isAppBarExpanded = false;
        appBarLayout.setExpanded(true);
        appBarLayout.setFitsSystemWindows(false);
    }

    //    private void doPostNetworkStuff() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // get the configuration on app creation
//                //getConfiguration();
////        loadMatches(this.getApplicationContext(), matches);
////                loadMatches2(context, restaurantMap);
//
//                mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
//                mContext = getApplicationContext();
//
//                mSwipeView.getBuilder()
//                        .setDisplayViewCount(3)
//                        .setSwipeDecor(new SwipeDecor()
//                                .setPaddingTop(20)
//                                .setRelativeScale(0.01f)
//                                .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
//                                .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
//
//
////            for(SwipeProfile profile : Utils.loadProfiles(this.getApplicationContext())){
////                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
////            }
//
//                for(int i = 0; i < tinderPhotos.size(); i++) {
//                    // Write a message to the database
//                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    DatabaseReference myRef = database.getReference("Cities").child("Boston").child("Food Items");
//
//                    myRef.child("" + i).child("restaurant").setValue(tinderPhotos.get(i).getRestaurantId());
//                    myRef.child("" + i).child("image_url").setValue(tinderPhotos.get(i).getImageUrl());
//                }
//
//
//
//
//
//
//
//
//
//
//
//            }
//        });
//    }
//
//    private void populateTinderPhotos() {
//        //for each nearby restaurant, we want to get its photos and other information to display
//        Log.d("Sean", "businesses.length: " + businesses.length());
//        final int[] businessesLoaded = {0};
//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference cityRef = database.getReference("Cities");
//        final DatabaseReference myRef = cityRef.child("Boston").child("Restaurants");
//        Map<String, Object> map = new HashMap<>();
//
//        for (int i = 0; i < businesses.length(); i++) {
//            try {
//                JSONObject restaurant = businesses.getJSONObject(i);
//                Log.d("Sean", "restaurant: " + restaurant.get("name"));
//                final String businessId = restaurant.get("id").toString();
//
//                map.put(businessId, restaurant.toString());
//
////                HttpUrl businessUrl = new HttpUrl.Builder()
////                        .scheme("https")
////                        .host("api.yelp.com")
////                        .addPathSegment("v3")
////                        .addPathSegment("businesses")
////                        .addPathSegment(businessId)
////                        .build();
//
//                Request businessRequest = new Request.Builder()
//                        .url("https://api.yelp.com/v3/businesses/" + businessId)
//                        .header("Authorization", "Bearer " + ACCESS_TOKEN)
//                        .build();
//
//                client.newCall(businessRequest).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.d("Sean", "onFailure: " + e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        try {
//                            Log.d("Sean", "onResponse: " + response.toString());
//                            String jsonData = response.body().string();
//                            JSONObject body = new JSONObject(jsonData);
//                            JSONArray photos = body.getJSONArray("photos");
//
//                            DatabaseReference restaurantRef = myRef.child(businessId);
//
//                            restaurantRef.child("counter").setValue("0");
//                            restaurantRef.child("id").setValue(businessId);
//                            restaurantRef.child("name").setValue(body.getString("name"));
//                            restaurantRef.child("image_url").setValue(body.getString("image_url"));
//                            restaurantRef.child("rating").setValue(body.getString("rating"));
//                            restaurantRef.child("review_count").setValue(body.getString("review_count"));
//                            restaurantRef.child("phone").setValue(body.getString("phone"));
//
//                            Map<String, Object> coordinatesMap = new HashMap<>();
//                            JSONObject coordinates = body.getJSONObject("coordinates");
//                            coordinatesMap.put("latitude", coordinates.get("latitude").toString());
//                            coordinatesMap.put("longitude", coordinates.get("longitude").toString());
//                            restaurantRef.child("coordinates").updateChildren(coordinatesMap);
//
//
//                            Map<String, Object> photosMap = new HashMap<>();
//                            restaurantRef.child("photos");
//
//                            //iterating through every photo that is associated with the restaurant
//                            for (int j = 0; j < photos.length(); j++) {
//                                // Creating a new food item that will be swiped on connected to its restaurant by id
//                                FoodItem foodItem = new FoodItem();
//
//                                //getting the imageUrl for the food item from the array of photos within the restaurant object
//                                String imageUrl = photos.get(j).toString();
//                                foodItem.setImageUrl(imageUrl);
//
//                                //setting the restaurant ID for the food item to be the same as the restaurant it is associated with
//                                foodItem.setRestaurantId(businessId);
//
//                                //adding the food item to the array of food items from all the nearby restaurants
//                                tinderPhotos.add(foodItem);
//                                photosMap.put("" + j, imageUrl);
//                            }
//                            restaurantRef.child("photos").updateChildren(photosMap);
//                            businessesLoaded[0]++;
//                            if (businessesLoaded[0] == businesses.length()) {
//                                doPostNetworkStuff();
//                            }
//                            // Log.d("Sean", "tinderPhotos: " + tinderPhotos.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        myRef.updateChildren(map);
//
//    }
//
////    private void createSwipeArray(JSONArray businesses) {
////        ArrayList<String> photos = new ArrayList<>();
////        for(int i = 0; i < businesses.length(); i++) {
////            try {
////                JSONObject restaurant = businesses.getJSONObject(i);
//////                String name = restaurant.get("name").toString();
////                String imageUrl = restaurant.get("image_url").toString();
////                photos.add(imageUrl);
//////                String[] location = restaurant.get("location.display_address");
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////        }
////    }
//
//    public void getYelpToken() {
//        Log.d("Sean", "get Yelp token");
//
//        //getting an access token to access the Yelp API
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("grant_type", "client_credentials")
//                .addFormDataPart("client_id", CLIENT_ID)
//                .addFormDataPart("client_secret", CLIENT_SECRET)
//                .build();
//
//        Request request = new Request.Builder().url(YELP_URL).post(requestBody).build();
//
//        //using the access token to get an array of nearby restaurants using the Yelp API
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("Sean", "onResponse: " + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String jsonData = response.body().string();
//                    JSONObject body = new JSONObject(jsonData);
//                    String token = body.getString("access_token");
////                    String tokenType = body.getString("token_type");
//
//                    Log.d("Sean", "onResponse: " + jsonData);
//                    Log.d("Sean", "Token: " + token);
//
//                    HttpUrl url = new HttpUrl.Builder()
//                            .scheme("https")
//                            .host("api.yelp.com")
//                            .addPathSegment("v3")
//                            .addPathSegment("businesses")
//                            .addPathSegment("search")
//                            .addQueryParameter("term", "food")
//                            //radius is in meters
//                            .addQueryParameter("radius", "33000")
//                            //TODO hard coded to Facebook for the time being
//                            .addQueryParameter("latitude", "42.366309")
//                            .addQueryParameter("longitude", "-71.083806")
//                            .build();
//
//                    Request getRequest = new Request.Builder()
//                            .url(url)
//                            .header("Authorization", "Bearer " + token)
//                            .build();
//
//                    Log.d("Sean", "url: " + url.toString());
//
//                    client.newCall(getRequest).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Log.d("Sean", "onFailure: " + e.toString());
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            try {
//                                Log.d("Sean", "onResponse: " + response.toString());
//                                String jsonData = response.body().string();
//                                JSONObject body = new JSONObject(jsonData);
//                                //businesses is the JSON array of all of the nearby businesses based off of the given radius and location
//                                businesses = body.getJSONArray("businesses");
//                                Log.d("Sean", "body: " + body.toString());
//                                populateTinderPhotos();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
////            Request getRequest = new Request.Builder()
////                    .url("https://api.yelp.com/v3/businesses/search")
////                    .header("bearer", ACCESS_TOKEN)
////                    .build();
//
////            client.newCall(getRequest).enqueue(new Callback() {
////                @Override
////                public void onFailure(Call call, IOException e) {
////                    Log.d("Sean", "onFailure: " + e.toString());
////                }
////
////                @Override
////                public void onResponse(Call call, Response response) throws IOException {
////                    try {
////                        Log.d("Sean", "onResponse: " + response.toString());
////                        String jsonData = response.body().string();
////                        JSONObject body = new JSONObject(jsonData);
////                        JSONArray businesses = body.getJSONArray("businesses");
////                        Log.d("Sean", "onResponse: " + businesses.toString());
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////            });
//    }
////        try {
////
////            URL obj = new URL(YELP_URL);
////            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
////            con.setRequestMethod("POST");
////            con.setRequestProperty("grant_type", "client_credentials");
////            con.setRequestProperty("client_id", CLIENT_ID);
////            con.setRequestProperty("client_secret", CLIENT_SECRET);
////            con.setDoOutput(true);
////            int responseCode = con.getResponseCode();
////            System.out.println("Response Code: " + responseCode);
////    }
}
