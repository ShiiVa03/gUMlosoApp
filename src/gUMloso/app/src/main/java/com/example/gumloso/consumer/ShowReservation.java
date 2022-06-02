package com.example.gumloso.consumer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.Booking;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ShowReservation extends AppCompatActivity {

    private Booking book;
    private Button favBtn;
    RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reservation);

        getSupportActionBar().setTitle("Main Page"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        book = (Booking) intent.getExtras().get("Book");
        favBtn = findViewById(R.id.button7);
        queue = Volley.newRequestQueue(ShowReservation.this);
        fillInformation();

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText ratingText = findViewById(R.id.ratingText);

        ((Button) findViewById(R.id.reviewButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject postData = new JSONObject();
                String url = MainActivity.urlC + "review";

                try {
                    postData.put("score", Math.round(ratingBar.getRating()));
                    postData.put("description", ratingText.getText().toString());
                    postData.put("restaurant_id", book.getRestaurant().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest req = new JsonObjectRequest(url,
                        postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(ShowReservation.this, "Review enviada com sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
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
                queue.add(req);
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book.getRestaurant().setFavorite(!book.getRestaurant().isFavorite());
                updateFavBtnText();

                JSONObject postData = new JSONObject();

                try {
                    postData.put("restaurant_id", book.getRestaurant().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = MainActivity.urlC + "favorite";
                JsonObjectRequest req = new JsonObjectRequest(url,
                        postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.error.VolleyError error) {
                        book.getRestaurant().setFavorite(!book.getRestaurant().isFavorite());
                        updateFavBtnText();
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> params = new HashMap<>();

                        params.put("Authorization", "Bearer " + MainActivity.apiKey);

                        return params;
                    }
                };
                queue.add(req);
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
        String hora = "Hora " + book.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
        horaReservation.setText(hora);
        String data = "Data " + book.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        dateReservation.setText(data);
        nameRestReservation.setText(book.getRestaurant().getName());

        updateFavBtnText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}