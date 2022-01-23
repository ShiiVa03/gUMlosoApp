package com.example.gumloso.consumer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gumloso.Booking;
import com.example.gumloso.R;

import java.time.format.DateTimeFormatter;

public class ShowReservation extends AppCompatActivity {

    private Booking book;
    private Button favBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reservation);

        Intent intent = getIntent();
        book = (Booking) intent.getExtras().get("Book");
        favBtn = findViewById(R.id.button7);

        fillInformation();

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book.getRestaurant().setFavorite(!book.getRestaurant().isFavorite());
                updateFavBtnText();
                // TODO: API CALL WITH BOOLEAN RETURNED
            }
        });

    }

    public boolean updateFavBtnText() {
        if (book.getRestaurant().isFavorite()) {
            favBtn.setText("Remover dos Favoritos");
            return false;
        }

        favBtn.setText("Adicionar aos Favoritos");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillInformation() {
        TextView txtReservation = findViewById(R.id.nrPessoas_reserva2);
        TextView horaReservation = findViewById(R.id.hora_reserva2);
        TextView dateReservation = findViewById(R.id.data_reserva2);
        TextView nameRestReservation = findViewById(R.id.nome_restaurante_res2);

        String pessoas_reserva = "Nr. pessoas " + book.getNumberOfPeople();
        txtReservation.setText(pessoas_reserva);
        String hora = "Hora "+book.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
        horaReservation.setText(hora);
        String data = "Data "+book.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        dateReservation.setText(data);
        nameRestReservation.setText(book.getRestaurant().getName());

        updateFavBtnText();
    }

}