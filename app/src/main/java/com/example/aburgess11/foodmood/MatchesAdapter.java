package com.example.aburgess11.foodmood;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangelali on 7/12/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
    private List<Match> mMatches;
    private Context mContext;

    // View lookup cache
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivMatchImage;
        public TextView tvMatchName;
        public TextView tvMatchDetails;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivMatchImage = (ImageView) itemView.findViewById(R.id.ivMatchImage);
            tvMatchName = (TextView) itemView.findViewById(R.id.tvMatchName);
            tvMatchDetails = (TextView) itemView.findViewById(R.id.tvMatchDetails);

        }
    }

    public MatchesAdapter(Context context, ArrayList<Match> matchesArrayList) {
        mMatches = matchesArrayList;
        mContext = context;
    }

    // create and inflate a new view
    @Override
    public MatchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View matchView = inflater.inflate(R.layout.activity_matches_list, parent, false);

        // Return a new holder instance
        MatchesAdapter.ViewHolder viewHolder = new MatchesAdapter.ViewHolder(matchView);
        return viewHolder;
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MatchesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Match match = mMatches.get(position);

        // Populate data into the template view using the data object
        viewHolder.tvMatchName.setText(match.getMatchName());
        viewHolder.tvMatchDetails.setText(match.getMatchDetails());

        Glide.with(getContext())
                .load(Uri.parse(match.getMatchImageUrl()))
                .into(viewHolder.ivMatchImage);
        // Return the completed view to render on screen
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mMatches.size();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

}
