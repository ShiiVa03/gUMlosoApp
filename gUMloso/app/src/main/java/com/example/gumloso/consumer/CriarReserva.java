package com.example.gumloso.consumer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gumloso.GeoLocation;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;

public class CriarReserva extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Restaurant restaurant;
    private GeoLocation userLocation;
    private ImageButton dateButton;
    private LocalDate dateReservation;
    private TextView restNameBox;
    private TextView capBox;
    private TextView distBox;
    private TextView ratBox;
    private TextView dayBox;
    private TextView monthBox;
    private TextView yearBox;
    private TextView minuteBox;
    private TextView hourBox;
    private EditText peopleBox;
    private ImageView imageReserv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_reserva);

        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getExtras().get("Restaurant");
        userLocation = (GeoLocation) intent.getExtras().get("UserLocation");

        restNameBox = findViewById(R.id.restaurantName);
        capBox = findViewById(R.id.gerarReservaCapacidade);
        distBox = findViewById(R.id.gerarReservaDist);
        ratBox = findViewById(R.id.gerarReservaRating);
        dayBox = findViewById(R.id.dia_reserva);
        monthBox = findViewById(R.id.mes_reserva);
        yearBox = findViewById(R.id.ano_reserva);
        minuteBox = findViewById(R.id.minutos_reserva);
        hourBox = findViewById(R.id.horas_reserva);
        peopleBox = findViewById(R.id.num_pessoas);
        imageReserv = findViewById(R.id.imageReserve);

        setValues(restaurant);
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                yearBox.setText(String.valueOf(year));
                monthBox.setText(String.valueOf(month));
                dayBox.setText(String.valueOf(day));
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }


    private void setValues(Restaurant restaurant) {

        restNameBox.setText(restaurant.getName());

        capBox.setText("Capacidade: " + restaurant.getCapacity());

        distBox.setText("Distância: " + GeoLocation.getDistance(restaurant.getLocation(), userLocation));

        ratBox.setText("Classificação: " + restaurant.getRating());

        imageReserv.setImageBitmap(BitmapFactory.decodeByteArray(restaurant.getImage(), 0, restaurant.getImage().length));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeReservation(View v) {
        try {
            try {
                int dayReserv = Integer.parseInt(dayBox.getText().toString());
                int monthReser = Integer.parseInt(monthBox.getText().toString());
                int yearReserv = Integer.parseInt(yearBox.getText().toString());
                this.dateReservation = LocalDate.of(yearReserv, monthReser, dayReserv);

            }catch (NumberFormatException e){
                if (this.dateReservation == null) {
                    throw new DateTimeException("");
                }
            }


            int hoursReserv = Integer.parseInt(hourBox.getText().toString());
            int minReserv = Integer.parseInt(minuteBox.getText().toString());

            if (hoursReserv >= 24 || hoursReserv < 0 || minReserv >= 60 || minReserv < 0) {
                Toast.makeText(this, "Horário inválido!", Toast.LENGTH_SHORT).show();
                return;
            }

            int numPeopleReserv = Integer.parseInt(peopleBox.getText().toString());

            if (numPeopleReserv > restaurant.getCapacity()) {
                Toast.makeText(this, "Número de pessoas excede a capacidade do restaurante!", Toast.LENGTH_SHORT).show();
                return;
            }

            String print = dateReservation.getDayOfMonth() + "/" + dateReservation.getMonthValue() + "/" + dateReservation.getYear() + ", " + hoursReserv + ":" + minReserv + " e para " + numPeopleReserv + " pessoas.";
            System.out.println("Reserva: " + print);
            Toast.makeText(CriarReserva.this, "Reserva feita no dia:\n " + print, Toast.LENGTH_SHORT).show();

        } catch (DateTimeException e) {
            Toast.makeText(CriarReserva.this, "Espaços inválidos!", Toast.LENGTH_SHORT).show();
            System.out.println("Catch1");

        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Complete os espaços!", Toast.LENGTH_SHORT).show();

        }
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}