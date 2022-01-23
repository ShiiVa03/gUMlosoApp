package com.example.gumloso.consumer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.gumloso.Booking;
import com.example.gumloso.GeoLocation;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScrollReservations extends AppCompatActivity implements ReservationAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_reservations);
        recyclerView = findViewById(R.id.recyclerViewReservations);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(LocalDateTime.now(), 1, new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false), "João"));
        bookings.add(new Booking(LocalDateTime.now(), 2, new Restaurant(4, "R2", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false), "Carlos"));
        bookings.add(new Booking(LocalDateTime.now(), 3, new Restaurant(6, "R3", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true), "Miguel"));
        bookings.add(new Booking(LocalDateTime.now(), 5, new Restaurant(3, "R4", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true), "Pedro"));

        adapter = new ReservationAdapter(bookings, true);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(View view, int position) {
        TextView res = view.findViewById(R.id.reservation_name);
        Booking book = adapter.getItem(position);


        Context context = view.getContext();

        Intent intent = new Intent(context, ShowReservation.class);
        intent.putExtra("Book", (Serializable) book);
        context.startActivity(intent);

    }


}