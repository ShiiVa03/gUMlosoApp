package com.example.gumloso.consumer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.GeoLocation;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;
import com.example.gumloso.Tuple;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity implements MyRecyclerViewAdapterConsumer.ItemClickListener {
    MyRecyclerViewAdapterConsumer adapter;
    private GeoLocation userLocation;
    private List<Restaurant> restaurants;
    private int radius = DEFAULT_RADIUS;
    final static int DEFAULT_RADIUS = 400;
    RecyclerView recyclerView;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        queue = Volley.newRequestQueue(MainPageActivity.this);

        getSupportActionBar().setTitle("Main Page"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GpsTracker gpsTracker = new GpsTracker(this);
        userLocation = new GeoLocation(gpsTracker.latitude, gpsTracker.longitude);
        restaurants = new ArrayList<>();

        recyclerView = findViewById(R.id.restaurantsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainPageActivity.this));
        adapter = new MyRecyclerViewAdapterConsumer(MainPageActivity.this, restaurants, userLocation);
        adapter.setClickListener(MainPageActivity.this);
        recyclerView.setAdapter(adapter);


        getRestaurants();


        EditText filterView = findViewById(R.id.editTextNumber);

        findViewById(R.id.app_bar_search).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                if (filterView.getVisibility() == View.INVISIBLE)
                    filterView.setVisibility(View.VISIBLE);
                else {
                    filterView.setVisibility(View.INVISIBLE);

                    String text = filterView.getText().toString();
                    boolean changed = false;

                    if (text.isEmpty()) {
                        if (DEFAULT_RADIUS != radius) {
                            changed = true;
                            radius = DEFAULT_RADIUS;
                        }
                    } else {
                        int newRadius = Integer.parseInt(text);
                        if (newRadius != radius) {
                            changed = true;
                            radius = newRadius;
                        }
                    }
                    if (changed) {
                        getRestaurants();
                    }
                }
            }
        });

        findViewById(R.id.mapItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainPageActivity.this, MapsActivity.class);
                List<Tuple> tList = new ArrayList<>();
                for (Restaurant restaurant : restaurants) {
                    tList.add(new Tuple(restaurant.getName(), restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude()));
                }
                i.putExtra("Restaurants", (Serializable) tList);
                i.putExtra("Radius", radius);

                startActivity(i);
            }
        });


    }

    public void getRestaurants() {
        String url = MainActivity.urlC + "restaurants_near/" + userLocation.getLatitude() + "/" + userLocation.getLongitude() + "/" + radius;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<ArrayList<Restaurant>>() {}.getType();
                        restaurants = new Gson().fromJson(response, listType);

                        adapter = new MyRecyclerViewAdapterConsumer(MainPageActivity.this, restaurants, userLocation);
                        adapter.setClickListener(MainPageActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.error.VolleyError error) {
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();

                params.put("Authorization", "Bearer " + MainActivity.apiKey);

                return params;
            }
        };
        req.setShouldCache(false);
        queue.add(req);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(this, CriarReserva.class);
        adapter.getItem(position).setImage(null);
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
