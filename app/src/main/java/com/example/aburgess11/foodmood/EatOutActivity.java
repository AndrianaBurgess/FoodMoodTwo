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
import com.example.aburgess11.foodmood.models.FoodItem;
import com.example.aburgess11.foodmood.models.Match;
import com.example.aburgess11.foodmood.models.Restaurant;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by liangelali on 7/13/17.
 */

public class EatOutActivity extends AppCompatActivity {
    private static final int LOGIN = 1000;
    // constants
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "EatOutActivity";

    Context context;

    // instance fields
//    AsyncHttpClient client;
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
    public static int swipeCount = 0;

    /* ***************YELP**************** */

    public static final String YELP_URL = "https://api.yelp.com/oauth2/token";
    public static final String CLIENT_ID = "FJbrnaXwVmgGJTk1xd2jwA";
    public static final String CLIENT_SECRET = "fAwIdrHUUvrHpbMbyOp4gVASjtH0TvJzj56TGgrXeyg3q994Nb6HWRgFGNWXTQ7z";
    public static final String ACCESS_TOKEN = "e6_8STuqCEF8pTolcFSMfZ77G1NZoCkPwDpj1_FYRY7zBr1QJKcnqVM03lrjVZug2sk54KocecFRpxPjpnGukGUJJR_HgkiUsTXU_-N6HhlC9EVPjpJRgp5bNKR7WXYx";
    public JSONArray businesses;
    public ArrayList<FoodItem> tinderPhotos;
    public ArrayList<Restaurant> restaurants;
    public OkHttpClient client;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d("Sean", "onCreate: " + savedInstanceState.getInt("saved"));
        }
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        // initialize lists
        businesses = new JSONArray();
        tinderPhotos = new ArrayList<>();
        restaurants = new ArrayList<>();

        //using a third party application to handle talking to API's
        client = new OkHttpClient();
        getYelpToken();

        // initialize the client
//        client = new AsyncHttpClient();

        // init the list of matches
        matches = new ArrayList<>();
        // initialize the adapter -- movies array cannot be reinitialized after this point
        try {
            adapter = new MatchesAdapter(restaurants);
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

//        createSwipeArray(businesses);

    }

    private void doPostNetworkStuff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // get the configuration on app creation
                //getConfiguration();
//        loadMatches(this.getApplicationContext(), matches);
                loadMatches2(context, businesses);

                mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
                mContext = getApplicationContext();

                mSwipeView.getBuilder()
                        .setDisplayViewCount(3)
                        .setSwipeDecor(new SwipeDecor()
                                .setPaddingTop(20)
                                .setRelativeScale(0.01f)
                                .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                                .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


//            for(SwipeProfile profile : Utils.loadProfiles(this.getApplicationContext())){
//                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
//            }

                for (FoodItem foodItem : tinderPhotos) {
                    mSwipeView.addView(new TinderCard(mContext, foodItem, mSwipeView));
                }

                findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSwipeView.doSwipe(false);
                        Log.d("EVENT", "swipeCount");

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

                        } else if (isChecked) {
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
            }
        });
    }

    private void populateTinderPhotos() {
        //for each nearby restaurant, we want to get its photos and other information to display
        Log.d("Sean", "businesses.length: " + businesses.length());
        final int[] businessesLoaded = {0};
        for (int i = 0; i < businesses.length(); i++) {
            try {
                JSONObject restaurant = businesses.getJSONObject(i);
                Log.d("Sean", "restaurant: " + restaurant.get("name"));
                final String businessId = restaurant.get("id").toString();

//                HttpUrl businessUrl = new HttpUrl.Builder()
//                        .scheme("https")
//                        .host("api.yelp.com")
//                        .addPathSegment("v3")
//                        .addPathSegment("businesses")
//                        .addPathSegment(businessId)
//                        .build();

                Request businessRequest = new Request.Builder()
                        .url("https://api.yelp.com/v3/businesses/" + businessId)
                        .header("Authorization", "Bearer " + ACCESS_TOKEN)
                        .build();

                client.newCall(businessRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Sean", "onFailure: " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            Log.d("Sean", "onResponse: " + response.toString());
                            String jsonData = response.body().string();
                            JSONObject body = new JSONObject(jsonData);
                            JSONArray photos = body.getJSONArray("photos");

                            //iterating through every photo that is associated with the restaurant
                            for (int j = 0; j < photos.length(); j++) {
                                // Creating a new food item that will be swiped on connected to its restaurant by id
                                FoodItem foodItem = new FoodItem();

                                //getting the imageUrl for the food item from the array of photos within the restaurant object
                                String imageUrl = photos.get(j).toString();
                                foodItem.setImageUrl(imageUrl);

                                //setting the restaurant ID for the food item to be the same as the restaurant it is associated with
                                foodItem.setRestaurantId(businessId);

                                //adding the food item to the array of food items from all the nearby restaurants
                                tinderPhotos.add(foodItem);
                            }
                            businessesLoaded[0]++;
                            if (businessesLoaded[0] == businesses.length()) {
                                doPostNetworkStuff();
                            }
                            // Log.d("Sean", "tinderPhotos: " + tinderPhotos.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private void createSwipeArray(JSONArray businesses) {
//        ArrayList<String> photos = new ArrayList<>();
//        for(int i = 0; i < businesses.length(); i++) {
//            try {
//                JSONObject restaurant = businesses.getJSONObject(i);
////                String name = restaurant.get("name").toString();
//                String imageUrl = restaurant.get("image_url").toString();
//                photos.add(imageUrl);
////                String[] location = restaurant.get("location.display_address");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void getYelpToken() {
        Log.d("Sean", "get Yelp token");

        //getting an access token to access the Yelp API
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("grant_type", "client_credentials")
                .addFormDataPart("client_id", CLIENT_ID)
                .addFormDataPart("client_secret", CLIENT_SECRET)
                .build();

        Request request = new Request.Builder().url(YELP_URL).post(requestBody).build();

        //using the access token to get an array of nearby restaurants using the Yelp API
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Sean", "onResponse: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    JSONObject body = new JSONObject(jsonData);
                    String token = body.getString("access_token");
//                    String tokenType = body.getString("token_type");

                    Log.d("Sean", "onResponse: " + jsonData);
                    Log.d("Sean", "Token: " + token);

                    HttpUrl url = new HttpUrl.Builder()
                            .scheme("https")
                            .host("api.yelp.com")
                            .addPathSegment("v3")
                            .addPathSegment("businesses")
                            .addPathSegment("search")
                            .addQueryParameter("term", "food")
                            //radius is in meters
                            .addQueryParameter("radius", "2000")
                            //TODO hard coded to Facebook for the time being
                            .addQueryParameter("latitude", "37.479775")
                            .addQueryParameter("longitude", "-122.154089")
                            .build();

                    Request getRequest = new Request.Builder()
                            .url(url)
                            .header("Authorization", "Bearer " + token)
                            .build();

                    Log.d("Sean", "url: " + url.toString());

                    client.newCall(getRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("Sean", "onFailure: " + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                Log.d("Sean", "onResponse: " + response.toString());
                                String jsonData = response.body().string();
                                JSONObject body = new JSONObject(jsonData);
                                //businesses is the JSON array of all of the nearby businesses based off of the given radius and location
                                businesses = body.getJSONArray("businesses");
                                Log.d("Sean", "body: " + body.toString());
                                populateTinderPhotos();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


//            Request getRequest = new Request.Builder()
//                    .url("https://api.yelp.com/v3/businesses/search")
//                    .header("bearer", ACCESS_TOKEN)
//                    .build();

//            client.newCall(getRequest).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.d("Sean", "onFailure: " + e.toString());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    try {
//                        Log.d("Sean", "onResponse: " + response.toString());
//                        String jsonData = response.body().string();
//                        JSONObject body = new JSONObject(jsonData);
//                        JSONArray businesses = body.getJSONArray("businesses");
//                        Log.d("Sean", "onResponse: " + businesses.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
    }
//        try {
//
//            URL obj = new URL(YELP_URL);
//            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//            con.setRequestMethod("POST");
//            con.setRequestProperty("grant_type", "client_credentials");
//            con.setRequestProperty("client_id", CLIENT_ID);
//            con.setRequestProperty("client_secret", CLIENT_SECRET);
//            con.setDoOutput(true);
//            int responseCode = con.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
//    }

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

    public void loadMatches(Context context, ArrayList<Match> list) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            String jsonString = loadJSONFromAsset(context, "restaurants.json");
            JSONObject obj = new JSONObject(jsonString);
            JSONArray array = obj.getJSONArray("restaurants");
            JSONArray array2 = businesses;

            for (int i = 0; i < array.length(); i++) {
                Match match = gson.fromJson(array.getString(i), Match.class);
//                businesses.get(i);
//                Match match = new Match();
//                match.setImageUrl();
                list.add(match);
                adapter.notifyItemInserted(list.size() - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void loadMatches2(Context context, JSONArray businesses) {
        for (int i = 0; i < businesses.length(); i++) {
            try {
                Object business = businesses.get(0);
                Restaurant restaurant = new Restaurant((JSONObject) business);
                adapter.notifyItemInserted(businesses.length() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is = null;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG, "path " + jsonFileName);
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
    public void onBackPressed() {

        EatOutActivity.isAppBarExpanded = false;
        appBarLayout.setExpanded(true);
        appBarLayout.setFitsSystemWindows(false);

//        Intent i = new Intent(EatOutActivity.this, EatOutActivity.class);
//        startActivity(i);
    }

}
