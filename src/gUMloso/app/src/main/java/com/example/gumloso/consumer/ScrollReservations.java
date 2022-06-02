package com.example.gumloso.consumer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.Booking;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScrollReservations extends AppCompatActivity implements ReservationAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private List<Booking> bookings;
    RequestQueue queue;

    public class BookRestaurant {
        Restaurant restaurant;
        Reservation reservation;

        public class Reservation {
            @SerializedName("number_of_people")
            public int numberOfPeople;
            public long date;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_reservations);
        queue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.recyclerViewReservations);

        bookings = new ArrayList<>();


        adapter = new ReservationAdapter(bookings, true);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        String url = MainActivity.urlC + "reservation";

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<ArrayList<BookRestaurant>>() {
                        }.getType();
                        ArrayList<BookRestaurant> bookRestaurants = new Gson().fromJson(response, listType);

                        bookings = bookRestaurants.stream().map(x -> new Booking(LocalDateTime.ofInstant(Instant.ofEpochMilli(x.reservation.date), ZoneId.systemDefault()), x.reservation.numberOfPeople, x.restaurant)).collect(Collectors.toList());
                        adapter = new ReservationAdapter(bookings, true);
                        adapter.setClickListener(ScrollReservations.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ScrollReservations.this));
                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(ScrollReservations.this, DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(itemDecoration);
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
        Booking book = adapter.getItem(position);
        System.out.println(book.restaurant);
        book.restaurant.setImage(null);


        Context context = view.getContext();

        Intent intent = new Intent(context, ShowReservation.class);
        intent.putExtra("Book", (Serializable) book);
        context.startActivity(intent);

    }
}