package com.example.aburgess11.foodmood;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

/**
 * Created by snhoward on 7/24/17.
 */

public class PreferenceSeekbar extends Preference {

    public PreferenceSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.pref_seekbar);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
    }
}
