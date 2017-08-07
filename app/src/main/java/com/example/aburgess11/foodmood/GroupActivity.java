package com.example.aburgess11.foodmood;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupActivity extends AppCompatActivity {

    static JSONArray friends;
    AsyncHttpClient client;
    public static FriendsAdapter adapter;
    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<Friend> friendsArray;
    String name ;
    String id;
    String url ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        GraphRequest graphRequest = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {

                friends= objects;
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

        //rvFriends= (RecyclerView) findViewById(R.id.rvFriends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFriends.setLayoutManager(layoutManager);
        rvFriends.setAdapter(adapter);


     loadFriends(context,friendsArray );
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
                                    adapter.notifyItemInserted(list.size() - 1);

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





}
