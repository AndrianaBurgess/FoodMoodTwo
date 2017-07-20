package com.example.aburgess11.foodmood.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liangelali on 7/12/17.
 */


public class Match {

    // values from API
    private String matchName;
    private String matchDetails;
    private String backdropPath;
    private Double double_percentMatch;
    private String percentMatch;

    // initialize from JSON data
    public Match(JSONObject object) throws JSONException {
        matchName = object.getString("title");
        matchDetails = object.getString("overview");
        backdropPath = object.getString("backdrop_path");

        double_percentMatch = object.getDouble("vote_average");
        percentMatch = Double.toString(double_percentMatch);

    }

    public String getMatchName() {
        return matchName;
    }

    public String getMatchDetails() {
        return matchDetails;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getPercentMatch() { return percentMatch; }
}
