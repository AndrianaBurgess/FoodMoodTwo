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
import com.example.aburgess11.foodmood.models.Config;
import com.example.aburgess11.foodmood.models.Match;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.login.widget.ProfilePictureView.TAG;


/**
 * Created by liangelali on 7/12/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {

    private static final String clientId = "FJbrnaXwVmgGJTk1xd2jwA";
    private static final String clientSecret = "fAwIdrHUUvrHpbMbyOp4gVASjtH0TvJzj56TGgrXeyg3q994Nb6HWRgFGNWXTQ7z";

    // list of matches
    ArrayList<Match> matches;
    // config needed for image urls
    Config config;
    // context for rendering
    Context context;

    // init with list
    public MatchesAdapter(ArrayList<Match> matches) throws IOException {

        this.matches = matches;
//        this.businesses = businesses;

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
        View matchView = inflater.inflate(R.layout.item_match, parent, false);
        // return a new ViewHolder
        final ViewHolder viewHolder = new ViewHolder(matchView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.d(TAG, "onClick: " + position);
                // make sure the position is valid, i.e. actually exists in the view
//            if (position2 != RecyclerView.NO_POSITION) {
                // get the match at the position, this won't work if the class is static
                Match match = matches.get(position);
//                TODO YELPED STUFF
//                Business business = businesses.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, RestaurantDetailsActivity.class);
//                // serialize the movie using parceler, use its short name as a key
                intent.putExtra("data", Parcels.wrap(match));
                // show the activity
                context.startActivity(intent);
//            }
            }
        });

        return viewHolder;
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // get the movie data at the specified position
        Match match = matches.get(position);
//        Business business = businesses.get(position);
        //populate the view with the movie data
        holder.tvMatchName.setText(match.getName());
//        holder.tvMatchName.setText(business.getName());

        holder.tvMatchDetails.setText(match.getLocation());
//        holder.tvMatchDetails.setText(business.getText());
       // holder.tvPercentMatch.setText(match.getRank() + "%  •");

        // load backdrop
        String imageUrl = match.getImageUrl();
//        String imageUrl = business.getImageUrl();


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
        return matches.size();
    }

    // create the viewholder as an inner class
    // class *cannot* be static
    // implements View.OnClickListener
    public class ViewHolder extends RecyclerView.ViewHolder{

        // track view objects
        @BindView(R.id.ivMatchImage) ImageView ivMatchImage;
        @BindView(R.id.tvMatchName) TextView tvMatchName;
        @BindView(R.id.tvMatchDetails) TextView tvMatchDetails;
        @BindView(R.id.tvPercentMatch) TextView tvPercentMatch;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
