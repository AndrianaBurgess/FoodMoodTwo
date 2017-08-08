package com.example.aburgess11.foodmood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aburgess11.foodmood.models.Config;
import com.example.aburgess11.foodmood.models.Friend;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by aburgess11 on 8/4/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

        // list of matches
        ArrayList<Friend> friendsArray;
        // config needed for image urls
        Config config;
        // context for rendering
        Context context;

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // init with list


    public FriendsAdapter(ArrayList<Friend> friends) {
        this.friendsArray = friends;
    }



        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        // creates and inflates a new view
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // get the context and create the inflater
            context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            // create the view using the item_movie layout
            View friendView = inflater.inflate(R.layout.item_group, parent, false);

            // return a new ViewHolder
            final ViewHolder viewHolder = new ViewHolder(friendView);
            viewHolder.ibAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    Log.d(TAG, "onClick: " + position);
                    Friend friend = friendsArray.get(position);
                    DatabaseReference myRef = database.getReference();
                    myRef.child("Groups").child(LoginActivity.fbID).child("Users").child(friend.getId()).setValue(friend.getName());
                }
            });
            return viewHolder;
        }


    // binds an inflated view to a new item
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            Friend friend = friendsArray.get(position);

            holder.tvFriendName.setText(friend.getName());

            // load profile pic
            String imageUrl = friend.getUrl();


            // load the poster
            ImageView imageview = holder.ivProfileImage;

            // load image using glide
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageview);
        }

        // returns the total number of items in the list
        @Override
        public int getItemCount() {
            return friendsArray.size();
        }



        // create the viewholder as an inner class
        // class *cannot* be static
        // implements View.OnClickListener
        public class ViewHolder extends RecyclerView.ViewHolder{

            // track view objects
            @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
            @BindView(R.id.tvFriendName) TextView tvFriendName;
            @BindView(R.id.ibAddFriend) ImageButton ibAddFriend;



            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }



