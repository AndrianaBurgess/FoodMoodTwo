package com.example.aburgess11.foodmood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by snhoward on 7/12/17.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    Context context;

    // for each row, inflate the layout and cache references into ViewHolder class
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View foodView = inflater.inflate(R.layout.item_food, parent, false);
        ViewHolder viewHolder = new ViewHolder(foodView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String imageUrl = "http://lorempixel.com/1600/1000/food";

        Glide.with(context)
                .load(imageUrl)
                .into(holder.ivBackground);
    }

    @Override
    public int getItemCount() {
        return 9;
    }


    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivBackground) public ImageView ivBackground;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {

            }
        }
    }

}
