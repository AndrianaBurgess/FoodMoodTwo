package com.example.aburgess11.foodmood;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by snhoward on 7/31/17.
 */

public class YelpClient {

    private final static String clientId = "FJbrnaXwVmgGJTk1xd2jwA";
    private final static String clientSecret = "fAwIdrHUUvrHpbMbyOp4gVASjtH0TvJzj56TGgrXeyg3q994Nb6HWRgFGNWXTQ7z";

    private String latitude;
    private String longitude;
    private String radius;
    private Call<SearchResponse> call;
    private Response<SearchResponse> response;
    private SearchResponse searchResponse;
    private ArrayList<Business> businesses;
    private int totalNumberOfResult;


    public YelpClient(String nRadius, String nLatitude, String nLongitude)  {


        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        YelpFusionApi yelpFusionApi = null;
        try {
            yelpFusionApi = apiFactory.createAPI(clientId, clientSecret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<>();

        radius = nRadius;
        latitude = nLatitude;
        longitude = nLongitude;

        // general params
        params.put("radius", radius);
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        call = yelpFusionApi.getBusinessSearch(params);
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchResponse = response.body();
        totalNumberOfResult = searchResponse.getTotal();  // i.e. 3
        businesses = searchResponse.getBusinesses();
    }

    public void setRadius(String nRadius) {
        radius = nRadius;
    }

    public void setLatitude(String nLatitude) {
        latitude = nLatitude;
    }

    public void setLongitude(String nLongitude) {
        longitude = nLongitude;
    }

    public int getTotalNumberOfResult() {
        return totalNumberOfResult;
    }

    public Response<SearchResponse> getResponse() {
        return response;
    }

    public SearchResponse getSearchResponse() {
        return searchResponse;
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }
}
