package com.example.aburgess11.foodmood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aburgess11.foodmood.models.Restaurant;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by liangelali on 7/12/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {

    // list of matches
    List<Restaurant> restaurantList;
    // context for rendering
    Context context;

    public MatchesAdapter(List<Restaurant> restaurantList) throws IOException { this.restaurantList = restaurantList; }

    // creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_movie layout
        View matchView = inflater.inflate(R.layout.item_match, parent, false);

        // return a new ViewHolder
        final ViewHolder viewHolder = new ViewHolder(matchView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.d(TAG, "onClick: " + position);
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the match at the position, this won't work if the class is static
                    Restaurant restaurant = restaurantList.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                    // serialize the Restaurant using parceler, use its restaurantId as a key
                    intent.putExtra("data", Parcels.wrap(restaurant));
                    // show the activity
                    context.startActivity(intent);
                }
            }
        });
        return viewHolder;
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // get the restaurant data at the specified position
        Restaurant restaurant = restaurantList.get(position);

        //populate the view with the movie data
        holder.tvMatchName.setText(restaurant.getName());
        holder.tvMatchDetails.setText(restaurant.getAddress());

        // load backdrop
        String imageUrl = restaurant.getImageUrl();

        // load the poster
        ImageView imageview = holder.ivMatchImage;

        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(imageview);
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // create the viewholder as an inner class
    // class *cannot* be static
    // implements View.OnClickListener
    public class ViewHolder extends RecyclerView.ViewHolder{

        // track view objects
        @BindView(R.id.ivMatchImage) ImageView ivMatchImage;
        @BindView(R.id.tvMatchName) TextView tvMatchName;
        @BindView(R.id.tvMatchDetails) TextView tvMatchDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
