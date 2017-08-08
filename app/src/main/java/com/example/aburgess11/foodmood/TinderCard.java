package com.example.aburgess11.foodmood;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aburgess11.foodmood.models.FoodItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import static com.example.aburgess11.foodmood.EatOutActivity.appBarLayout;
import static com.example.aburgess11.foodmood.EatOutActivity.groupRef;
import static com.example.aburgess11.foodmood.EatOutActivity.isAlone;
import static com.example.aburgess11.foodmood.EatOutActivity.swipeCount;
import static com.example.aburgess11.foodmood.EatOutActivity.userRef;


/**
 * Created by aburgess11 on 7/12/17.
 */


@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;
    private SwipeProfile mSwipeProfile;
    private FoodItem mFoodItem;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private DatabaseReference sessionRef;

    public TinderCard(Context context, FoodItem foodItem, SwipePlaceHolderView swipeView) {
        mContext = context;
        mFoodItem = foodItem;
        mSwipeView = swipeView;

        if(isAlone) {
            sessionRef = userRef;
        } else {
            sessionRef = groupRef;
        }
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext)
                .load(mFoodItem.getImageUrl())
                .into(profileImageView);
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
    private void onSwipeIn() {
        final DatabaseReference currCounter = sessionRef.child("Restaurants").child(this.mFoodItem.getRestaurantId()).child("counter");
        currCounter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currCount = dataSnapshot.getValue().toString();
                int countCurr = Integer.parseInt(currCount) + 1;
                currCounter.setValue(countCurr);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        popUpList();
        Toast.makeText(mContext, this.mSwipeProfile.getLocation(), Toast.LENGTH_SHORT ).show();
//        Collections.sort(restaurantMap);
        EatOutActivity.adapter.notifyDataSetChanged();
        Log.d("EVENT", "onSwipedIn" + swipeCount);
    }

    //Pops up the matches every
    public void popUpList(){
        // after every swipe, increment the swipeCount
        swipeCount++;

        // after 10 swipes, automatically pop up the matches page
        if (swipeCount == 10){
            EatOutActivity.loadMatches(EatOutActivity.restaurantMap);
            EatOutActivity.isAppBarExpanded = true;
            appBarLayout.setExpanded(false);
            appBarLayout.setFitsSystemWindows(true);

            Log.d("EVENT", swipeCount + "");
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

