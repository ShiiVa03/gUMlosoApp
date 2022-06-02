package com.example.gumloso.manager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

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
import com.example.gumloso.consumer.ReservationAdapter;
import com.example.gumloso.consumer.ScrollReservations;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestaoReservas extends AppCompatActivity implements ReservationAdapter.ItemClickListener {
    private ReservationAdapter adapter;
    private DatePickerDialog datePickerDialog;
    private TextView yearBox, monthBox, dayBox;
    private RecyclerView recyclerView;
    private List<Booking> bookings;
    RequestQueue queue;

    public class GestReserv {
        ScrollReservations.BookRestaurant.Reservation reservation;
        @SerializedName("customer_username")
        String owner;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_reservas);

        recyclerView = findViewById(R.id.recyclerViewReservations);

        getSupportActionBar().setTitle("Gestão de Reservas"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(GestaoReservas.this);

        bookings = new ArrayList<>();

        initDatePicker();

        yearBox = findViewById(R.id.ano_reserva2);
        monthBox = findViewById(R.id.mes_reserva2);
        dayBox = findViewById(R.id.dia_reserva2);

        String url = MainActivity.urlG + "reservation";


        adapter = new ReservationAdapter(bookings, false);
        adapter.setClickListener(GestaoReservas.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(GestaoReservas.this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(GestaoReservas.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<ArrayList<GestReserv>>(){}.getType();
                        ArrayList<GestReserv> bookRestaurants = new Gson().fromJson(response, listType);

                        bookings = bookRestaurants.stream().map(x -> new Booking(LocalDateTime.ofInstant(Instant.ofEpochMilli(x.reservation.date), ZoneId.systemDefault()), x.reservation.numberOfPeople, null,x.owner)).collect(Collectors.toList());
                        adapter = new ReservationAdapter(bookings, false);
                        adapter.setClickListener(GestaoReservas.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(GestaoReservas.this));
                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(GestaoReservas.this, DividerItemDecoration.VERTICAL);
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

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                yearBox.setText(String.valueOf(year));
                monthBox.setText(String.valueOf(month));
                dayBox.setText(String.valueOf(day));

                LocalDate dateFilter = LocalDate.of(year, month, day);
                List<Booking> filteredBookings = bookings.stream().filter(x -> x.getDate().toLocalDate().isEqual(dateFilter)).collect(Collectors.toList());
                adapter = new ReservationAdapter(filteredBookings, false);
                adapter.setClickListener(GestaoReservas.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(GestaoReservas.this));
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(GestaoReservas.this, DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(itemDecoration);
                recyclerView.setLayoutManager(new LinearLayoutManager(GestaoReservas.this));
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.getItem(0).getSubMenu().getItem(1).setVisible(false);
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
                startActivity(new Intent(GestaoReservas.this, EditManagerProfile.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}