package com.example.gumloso;

import static android.graphics.Bitmap.createScaledBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.manager.GestaoReservas;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterRestActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    private TextView restaurantText;
    private TextView locationText;
    private TextView contactText;
    private TextView typeText;
    private List<Restaurant.DailySchedule> dailySchedules = new ArrayList<>();
    private TextView capacityText;
    private Button btn;
    private ImageButton imageUpload;
    private boolean hasImage = false;
    private byte[] imagem;

    private boolean toRegister;
    private String email;
    private String pass;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_rest);

        restaurantText = findViewById(R.id.restaurantText);
        locationText = findViewById(R.id.locationText);
        contactText = findViewById(R.id.contactText);
        typeText = findViewById(R.id.typeText);
        capacityText = findViewById(R.id.capacityText);
        btn = findViewById(R.id.btn2);
        imageUpload = findViewById(R.id.imageUpload);

        Intent i = getIntent();

        toRegister = i.getBooleanExtra("Register", false);
        if (toRegister) {
            email = i.getStringExtra("email");
            pass = i.getStringExtra("password");
            user = i.getStringExtra("username");
        }


        btn.setEnabled(false);

        getSupportActionBar().setTitle("Register"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btn.setEnabled(validate());
            }
        };

        restaurantText.addTextChangedListener(textWatcher);
        locationText.addTextChangedListener(textWatcher);
        typeText.addTextChangedListener(textWatcher);

        imageUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
        Button btnShift = findViewById(R.id.btnDefinir);
        btnShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterRestActivity.this, ShiftsActivity.class);
                intent.putExtra("DailySchedules", (Serializable) dailySchedules);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               postRegister();
            }
        });
    }

    private boolean validate() {
        String restaurantInput = restaurantText.getText().toString().trim();
        String locationInput = locationText.getText().toString().trim();
        String typeInput = typeText.getText().toString().trim();
        String capcityInput = capacityText.getText().toString().trim();


        return !restaurantInput.isEmpty() && !locationInput.isEmpty()
                && !typeInput.isEmpty() && !capcityInput.isEmpty() && hasImage && dailySchedules.size() > 0;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == SECOND_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                dailySchedules.clear();
                dailySchedules.addAll((List<Restaurant.DailySchedule>) data.getExtras().get("DailySchedules"));
                btn.setEnabled(validate());
            }
        } else {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageUpload.setImageBitmap(createScaledBitmap(selectedImage, 240, 100, true));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    this.imagem = stream.toByteArray();
                    selectedImage.recycle();

                    hasImage = true;
                    btn.setEnabled(validate());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void postRegister() {
        String url;
        String restName = restaurantText.getText().toString();
        String contact = contactText.getText().toString();
        String address = locationText.getText().toString();
        String type = typeText.getText().toString();
        int capa = Integer.parseInt(capacityText.getText().toString());
        byte[] image = imagem;
        List<Restaurant.DailySchedule> list = dailySchedules;

        Restaurant restaurant = new Restaurant(null,restName,contact,address,type,capa,null,null,image,list,null);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();


        JSONObject j = null;
        JSONObject postData;

        try {
            RequestQueue queue = Volley.newRequestQueue(RegisterRestActivity.this);
            j = new JSONObject(gson.toJson(restaurant));
            JsonObjectRequest req;

            if (toRegister) {

                url = MainActivity.urlG + "register";
                JSONObject postDataI = new JSONObject();

                postData = new JSONObject();
                postDataI.put("email", email);
                postDataI.put("password", pass);
                postDataI.put("username", user);
                postData.put("manager", postDataI);

                postData.put("restaurant", j);

                req = new JsonObjectRequest(url,
                        postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    MainActivity.apiKey = response.getString("token");
                                    System.out.println("------------------------------- " + response.getString("token"));
                                    Intent j = new Intent(RegisterRestActivity.this, GestaoReservas.class);
                                    startActivity(j);
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                                String body;
                                //get status code here
                                String statusCode = String.valueOf(error.networkResponse.statusCode);
                                //get response body and parse with appropriate encoding
                                if (error.networkResponse.data != null) {
                                    Toast.makeText(RegisterRestActivity.this, statusCode, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RegisterRestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                url = MainActivity.urlG + "restaurant";
                postData = j;

                req = new JsonObjectRequest(url,
                        postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(RegisterRestActivity.this, "Restaurante adicionado com sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                                String body;
                                //get status code here
                                String statusCode = String.valueOf(error.networkResponse.statusCode);
                                //get response body and parse with appropriate encoding
                                if (error.networkResponse.data != null) {
                                    Toast.makeText(RegisterRestActivity.this, statusCode, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RegisterRestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> params = new HashMap<>();

                        params.put("Authorization", "Bearer " + MainActivity.apiKey);

                        return params;
                    }
                };
            }

            queue.add(req);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }




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