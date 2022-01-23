package com.example.gumloso.manager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.gumloso.Booking;
import com.example.gumloso.GeoLocation;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;
import com.example.gumloso.consumer.ReservationAdapter;
import com.example.gumloso.consumer.ShowReservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class GestaoReservas extends AppCompatActivity implements ReservationAdapter.ItemClickListener {
    private ReservationAdapter adapter;
    private DatePickerDialog datePickerDialog;
    private TextView yearBox, monthBox, dayBox;
    private RecyclerView recyclerView;
    private List<Booking> bookings;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_reservas);

        recyclerView = findViewById(R.id.recyclerViewReservations);

        getSupportActionBar().setTitle("Gestão de Reservas"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bookings = new ArrayList<>();
        bookings.add(new Booking(LocalDateTime.now(), 1, new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false), "João"));
        bookings.add(new Booking(LocalDateTime.now(), 2, new Restaurant(4, "R2", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false), "Carlos"));
        bookings.add(new Booking(LocalDateTime.now(), 3, new Restaurant(6, "R3", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true), "Miguel"));
        bookings.add(new Booking(LocalDateTime.now(), 5, new Restaurant(3, "R4", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true), "Joana"));

        yearBox = findViewById(R.id.ano_reserva2);
        monthBox = findViewById(R.id.mes_reserva2);
        dayBox = findViewById(R.id.dia_reserva2);

        initDatePicker();

        adapter = new ReservationAdapter(bookings, false);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Booking book = adapter.getItem(position);


        Context context = view.getContext();

        Intent intent = new Intent(context, ShowReservation.class);
        intent.putExtra("Book", (Serializable) book);
        context.startActivity(intent);

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