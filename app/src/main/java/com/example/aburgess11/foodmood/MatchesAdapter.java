package com.example.aburgess11.foodmood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aburgess11.foodmood.models.Config;
import com.example.aburgess11.foodmood.models.Match;

import java.util.ArrayList;

/**
 * Created by liangelali on 7/12/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
    // list of matches
    ArrayList<Match> matches;
    // config needed for image urls
    Config config;
    // context for rendering
    Context context;

    // init with list
    public MatchesAdapter(ArrayList<Match> matches) {

        this.matches = matches;
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
        ViewHolder viewHolder = new ViewHolder(matchView);
        return viewHolder;
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the movie data at the specified position
        Match match = matches.get(position);
        //populate the view with the movie data
        holder.tvMatchName.setText(match.getMatchName());
        holder.tvMatchDetails.setText(match.getMatchDetails());
        holder.tvPercentMatch.setText(match.getPercentMatch() + "%  •");


        // load backdrop
        String imageUrl = config.getImageUrl(config.getBackdropSize(), match.getBackdropPath());


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

    // create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        ImageView ivMatchImage;
        TextView tvMatchName;
        TextView tvMatchDetails;
        TextView tvPercentMatch;

        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by id
            ivMatchImage = (ImageView) itemView.findViewById(R.id.ivMatchImage);
            tvMatchName = (TextView) itemView.findViewById(R.id.tvMatchName);
            tvMatchDetails = (TextView) itemView.findViewById(R.id.tvMatchDetails);
            tvPercentMatch = (TextView) itemView.findViewById(R.id.tvPercentMatch);
        }
    }
}
