package com.example.gumloso.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumloso.GeoLocation;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;

import java.io.Serializable;
import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity implements MyRecyclerViewAdapterConsumer.ItemClickListener {

    MyRecyclerViewAdapterConsumer adapter;
    private GeoLocation userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        getSupportActionBar().setTitle("Main Page"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayList<Restaurant> restaurants = new ArrayList<>();

        restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(41.5625048,-8.4074581), new byte[0], new ArrayList<>(), false));
       // restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false));
        //restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true));
       // restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true));
        //restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true));
       // restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false));
       // restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true));

        findViewById(R.id.mapItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainPageActivity.this, MapsActivity.class);
                i.putExtra("Restaurants",(Serializable) restaurants);
                startActivity(i);
            }
        });


        GpsTracker gpsTracker = new GpsTracker(this);
        userLocation = new GeoLocation(gpsTracker.latitude, gpsTracker.longitude);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.restaurantsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapterConsumer(this, restaurants, userLocation);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(this, CriarReserva.class);
        i.putExtra("Restaurant", adapter.getItem(position));
        i.putExtra("UserLocation", userLocation);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            case R.id.perfilItem:
                startActivity(new Intent(MainPageActivity.this, EditProfileUser.class));
                return true;
            case R.id.bookingsItem:
                startActivity(new Intent(MainPageActivity.this, ScrollReservations.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
