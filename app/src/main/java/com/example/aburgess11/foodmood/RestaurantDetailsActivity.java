package com.example.aburgess11.foodmood;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aburgess11.foodmood.models.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.aburgess11.foodmood.R.id.btnPhone;

public class RestaurantDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // the restaurant to display
    Restaurant restaurant;
    Location location;
    Context context;
    LocationManager lm;

    private static final String TAG = RestaurantDetailsActivity.class.getSimpleName();
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_PHONE = "android.permission.CALL_PHONE";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    // the view objects
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvReviews)
    TextView tvReviews;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.ivBackdrop)
    ImageView ivBackDrop;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnOpenMaps)
    Button btnOpenMaps;
    @BindView(R.id.btnPhone)
    Button btnPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);
        context = this.getApplicationContext();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        restaurant = Parcels.unwrap(getIntent().getParcelableExtra("data"));
        Log.d("RestDetailsActivity", String.format("Showing details for '%s'", restaurant.getName()));

        tvName.setText(restaurant.getName());
        tvReviews.setText(restaurant.getReview_count());
        ratingBar.setRating((float) Double.parseDouble(restaurant.getRating()));
        retriveAddress();

        Glide.with(ivBackDrop.getContext())
                .load(restaurant.getImage_url())
                .centerCrop()
                .into(ivBackDrop);

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        btnOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGoogleMaps();
            }
        });

//        btnPhone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
//                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    if (!checkPermissions()) {
//                        requestPhonePermissions();
//                    } else {
//                        startActivity(callIntent);
//                    }
//                    return;
//                }
//            }
//        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));
//        googleMap.setBuildingsEnabled(true);

        // Add a marker,
        // and move the map's camera to the same location.
        final LatLng restaurantLocation = new LatLng(Double.parseDouble(restaurant.getCoordinates().getLatitude()), Double.parseDouble(restaurant.getCoordinates().getLongitude()));
        googleMap.addMarker(new MarkerOptions().position(restaurantLocation)
                .title(restaurant.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation));
        googleMap.setMinZoomPreference(15);
        googleMap.setBuildingsEnabled(true);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                launchGoogleMaps();
                return false;
            }
        });
    }

    public void launchGoogleMaps() {
        String origin = "&origin=" + location.getLatitude() + "," + location.getLongitude();
        String destination = "&destination=" + restaurant.getCoordinates().getLatitude() + "," + restaurant.getCoordinates().getLongitude();
        String travelMode = "&travelmode=driving";
        String uri = "https://www.google.com/maps/dir/?api=1" + origin + destination + travelMode;

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri));
        startActivity(intent);
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(RestaurantDetailsActivity.this,
                                    new String[]{ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(RestaurantDetailsActivity.this,
                    new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPhonePermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,ACCESS_PHONE);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(RestaurantDetailsActivity.this,
                                    new String[]{ACCESS_PHONE},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(RestaurantDetailsActivity.this,
                    new String[]{ACCESS_PHONE},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
//                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    public void retriveAddress() {
        String resultAddress;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(Double.parseDouble(restaurant.getCoordinates().getLatitude()), Double.parseDouble(restaurant.getCoordinates().getLongitude()),
                    1);
            if (list != null && list.size() > 0) {
                Address addressList = list.get(0);
                resultAddress = addressList.getAddressLine(0) + ", "
                        + addressList.getLocality();
                tvAddress.setText(resultAddress);
            } else {
                tvAddress.setText(R.string.no_addresses_retrieved);
            }
        } catch (IOException e) {
            tvAddress.setText(R.string.cannot_get_address_);
        }
    }
}
