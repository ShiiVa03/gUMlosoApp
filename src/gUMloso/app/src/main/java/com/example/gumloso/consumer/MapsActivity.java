package com.example.gumloso.consumer;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gumloso.BuildConfig;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;
import com.example.gumloso.Tuple;
import com.example.gumloso.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;

import java.util.List;


public class MapsActivity extends AppCompatActivity {

    private MapView mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Tuple> tList;
    private MapElementLayer mPinLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("-------------CHEGUEIAIINDCREATE");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tList = (List<Tuple>) getIntent().getExtras().get("Restaurants");

        mMap = new MapView(this, MapRenderMode.RASTER);  // or use MapRenderMode.RASTER for 2D map
        mMap.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        ((FrameLayout) findViewById(R.id.map_view)).addView(mMap);
        mMap.onCreate(savedInstanceState);

        mPinLayer = new MapElementLayer();
        mMap.getLayers().add(mPinLayer);

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("skaldfjlksdjfdslkfjlksjfl");
        GpsTracker gpsTracker = new GpsTracker(MapsActivity.this);

        Geopoint actual = new Geopoint(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        System.out.println("--------------------------" + actual);
        int radius = getIntent().getIntExtra("Radius", MainPageActivity.DEFAULT_RADIUS);
        System.out.println("---------------------" + radius);
        mMap.setScene(
                MapScene.createFromLocationAndRadius(actual, radius * 1000),
                MapAnimationKind.NONE);

        MapIcon pushpinActual = new MapIcon();
        pushpinActual.setLocation(actual);
        pushpinActual.setTitle("Atual");

        mPinLayer.getElements().add(pushpinActual);
        for (Tuple tuple : tList) {
            Geopoint location = new Geopoint(tuple.getLat(), tuple.getLon());
            String title = tuple.getName();

            MapIcon pushpin = new MapIcon();
            pushpin.setLocation(location);
            pushpin.setTitle(title);

            mPinLayer.getElements().add(pushpin);
        }


    }
}

