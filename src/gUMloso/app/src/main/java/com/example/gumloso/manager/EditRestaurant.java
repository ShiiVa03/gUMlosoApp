package com.example.gumloso.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;
import com.example.gumloso.ShiftsActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditRestaurant extends AppCompatActivity {

    private List<Restaurant.DailySchedule> dailySchedules;
    private static final int SHIFTS_REQUEST_CODE = 0;
    private static final int RESULT_LOAD_IMG = 1;
    private boolean hasImage = false;
    private Restaurant restaurant;

    private TextView restTypeBox;
    private TextView capacityET;
    private Button confirmBtn;

    class EditInfo {
        @SerializedName("food_type")
        String foodType;
        int capacity;
        @SerializedName("timetable")
        List<Restaurant.DailySchedule> dailySchedules;
        byte[] image;

        public EditInfo(String foodType, int capacity, List<Restaurant.DailySchedule> dailySchedules, byte[] image) {
            this.foodType = foodType;
            this.capacity = capacity;
            this.dailySchedules = dailySchedules;
            this.image = image;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getExtras().get("Restaurant");
        int position = intent.getIntExtra("Position", -1);
        dailySchedules = restaurant.getSchedule();
        TextView restNameBox = findViewById(R.id.rest_name);
        restNameBox.setText(restaurant.getName());

        restTypeBox = findViewById(R.id.textView20);
        restTypeBox.setText(restaurant.getType());

        capacityET = findViewById(R.id.NumCapacidade);
        capacityET.setText(String.valueOf(restaurant.getCapacity()));

        Button btnShift = findViewById(R.id.defineShceduleBtn);
        btnShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRestaurant.this, ShiftsActivity.class);
                intent.putExtra("DailySchedules", (Serializable) dailySchedules);
                startActivityForResult(intent, SHIFTS_REQUEST_CODE);
            }
        });


        confirmBtn = findViewById(R.id.button5);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {

                    RequestQueue queue = Volley.newRequestQueue(EditRestaurant.this);

                    String url = MainActivity.urlG + "restaurant/" + restaurant.getId();
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();

                    String str = gson.toJson(new EditInfo(restTypeBox.getText().toString().trim(),
                            Integer.parseInt(capacityET.getText().toString().trim()),
                            dailySchedules,
                            hasImage ? restaurant.getImage() : null));

                    JSONObject data = null;
                    try {
                        data = new JSONObject(str);

                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,url,
                                data,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(EditRestaurant.this, "Restaurante alterado com sucesso",Toast.LENGTH_SHORT).show();
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

                        queue.add(req);

                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button definirBtn = findViewById(R.id.buttonDefinir);
        definirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == SHIFTS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                dailySchedules = (List<Restaurant.DailySchedule>) data.getExtras().get("DailySchedules");
                restaurant.setSchedule(dailySchedules);
                confirmBtn.setEnabled(validate());
            }
        } else {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 0, stream);

                    hasImage = true;
                    restaurant.setImage(stream.toByteArray());
                    confirmBtn.setEnabled(validate());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }

    }

    public boolean validate() {
        String typeInput = restTypeBox.getText().toString().trim();
        String capcityInput = capacityET.getText().toString().trim();

        return !typeInput.isEmpty() && !capcityInput.isEmpty() && dailySchedules.size() > 0;
    }

}