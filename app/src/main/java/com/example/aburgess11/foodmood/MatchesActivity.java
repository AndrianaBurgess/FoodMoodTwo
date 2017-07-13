package com.example.aburgess11.foodmood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by liangelali on 7/12/17.
 */

public class MatchesActivity extends AppCompatActivity {

    // TODO: replace out movie stuff
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";


    private RecyclerView rvMatches;
    private MatchesAdapter matchesAdapter;
    private ArrayList<Match> matchesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        // rvMatches = (RecyclerView) findViewById(R.id.rvMatches);
        matchesArrayList = new ArrayList<>();

        // initialize the adapter
        matchesAdapter = new MatchesAdapter(this, matchesArrayList);

        // attach the adapter to the RecyclerVIew
        rvMatches.setAdapter(matchesAdapter);

        // Set layout manager to position the items
        rvMatches.setLayoutManager(new LinearLayoutManager(this));

    }
}
