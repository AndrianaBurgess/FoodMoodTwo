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
import com.example.aburgess11.foodmood.models.City;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    ArrayList<City> cities;
    Context context;

    public CityAdapter(ArrayList<City> cities) throws IOException { this.cities = cities; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using the item_city layout
        View matchView = inflater.inflate(R.layout.item_city, parent, false);
        //cities = new ArrayList<>();

        //return a new ViewHolder
        final ViewHolder viewHolder = new ViewHolder(matchView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.d("CityAdapter", "onClick: " + position);
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the match at the position, this won't work if the class is static
                    City city = cities.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(context, EatOutActivity.class);
                    // serialize the Restaurant using parceler, use its restaurantId as a key
                    intent.putExtra("cityName", city.getName());
                    intent.putExtra("image_url",city.getImageUrl());
                    // show the activity
                    context.startActivity(intent);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get the city data at the specified position
        City city = cities.get(position);

        //populate the view with the movie data
        holder.tvCityName.setText(city.getName());

        // load backdrop
        String imageUrl = city.getImageUrl();

        // load the poster
        ImageView imageview = holder.ivBackground;

        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(imageview);
    }

    @Override
    public int getItemCount() { return cities.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //track view objects
        @BindView(R.id.ivBackground) ImageView ivBackground;
        @BindView(R.id.tvCityName) TextView tvCityName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
