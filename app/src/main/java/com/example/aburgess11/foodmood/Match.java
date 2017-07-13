package com.example.aburgess11.foodmood;

import java.util.ArrayList;

/**
 * Created by liangelali on 7/12/17.
 */

// TODO: THIS NEEDS TO GO INTO THE MODELS FOLDER WHEN IT'S CREATED

public class Match {

    // values from API
    private String matchName;
    private String matchDetails;


    public String getMatchName() {
        return matchName;
    }

    public String getMatchDetails() {
        return matchDetails;
    }

    // TODO: Get actual images for matches later
    public String getMatchImageUrl() {
        return "https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjXt8ebpYXVAhUM6GMKHWmFAvoQjRwIBw&url=https%3A%2F%2Fwww.pinterest.com%2Fpin%2F674414112919048151%2F&psig=AFQjCNFA63YvhxGsbGPw9GR0Z3BUgM7Jaw&ust=1500002023849827";
    }

    // Returns a match TODO: need JSON data


    public Match() {
        Match match = new Match();
        match.matchName = "Match Name" /* getMatchName(jsonObject) */;
        match.matchDetails = "details about the match" /* getMatchDetails(jsonObject) */;
        match.matchDetails = getMatchDetails();

        ArrayList<Match> matches = new ArrayList<>();
        if (match != null) {
                matches.add(match);
        }
    }
}
