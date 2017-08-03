package com.example.aburgess11.foodmood;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aburgess11.foodmood.models.FoodItem;
import com.example.aburgess11.foodmood.models.Match;
import com.example.aburgess11.foodmood.models.Restaurant;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.Collections;

import static com.example.aburgess11.foodmood.EatOutActivity.appBarLayout;
import static com.example.aburgess11.foodmood.EatOutActivity.matches;
import static com.example.aburgess11.foodmood.EatOutActivity.restaurantMap;
import static com.example.aburgess11.foodmood.EatOutActivity.swipeCount;


/**
 * Created by aburgess11 on 7/12/17.
 */


@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private SwipeProfile mSwipeProfile;
    private FoodItem mFoodItem;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

//    public TinderCard(Context context, SwipeProfile swipeProfile, SwipePlaceHolderView swipeView) {
//        mContext = context;
//        mSwipeProfile = swipeProfile;
//        mSwipeView = swipeView;
//    }

    public TinderCard(Context context, FoodItem foodItem, SwipePlaceHolderView swipeView) {
        mContext = context;
        mFoodItem = foodItem;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
//        Glide.with(mContext).load(mSwipeProfile.getImageUrl()).into(profileImageView);
        Glide.with(mContext).load(mFoodItem.getImageUrl()).into(profileImageView);
//        nameAgeTxt.setText(mSwipeProfile.getName() + ", " + mSwipeProfile.getTimeofday());
//        locationNameTxt.setText(mSwipeProfile.getLocation());
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    public void onSwipeIn(){
        if(restaurantMap.containsKey(this.mFoodItem.getRestaurantId())) {
            Restaurant restaurant = restaurantMap.get(this.mFoodItem.getRestaurantId());
            restaurant.setCounter(restaurant.getCounter() + 1);
            restaurantMap.put(restaurant.getRestaurauntId(), restaurant);
        } else {
            Restaurant restaurant = new Restaurant(this.mFoodItem.getRestaurantId(), 1);
            restaurantMap.put(restaurant.getRestaurauntId(), restaurant);
        }
        popUpList();
       // Toast.makeText(mContext, this.mSwipeProfile.getLocation(), Toast.LENGTH_SHORT ).show();
//        findRestAndIncr(this.mSwipeProfile.getLocation());
        Collections.sort(matches);
        EatOutActivity.adapter.notifyDataSetChanged();
        Log.d("EVENT", "onSwipedIn" + swipeCount);
    }


    public void findRestAndIncr(String name){

        for(Match m: matches){
            if ( m.getName().equals(name)){
                m.rank++;
                Toast.makeText(mContext,  m.rank + "" , Toast.LENGTH_SHORT ).show();

            }
        }
    }

    //Pops up the matches every
    public void popUpList(){
        // after every swipe, increment the swipeCount
        swipeCount++;

        // after 10 swipes, automatically pop up the matches page
        if (swipeCount == 10){

            EatOutActivity.isAppBarExpanded = true;
            appBarLayout.setExpanded(false);
            appBarLayout.setFitsSystemWindows(true);

            Log.d("EVENT", swipeCount + "" );
            swipeCount=0;
        }
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}

