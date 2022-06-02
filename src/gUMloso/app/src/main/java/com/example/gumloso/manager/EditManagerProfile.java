package com.example.gumloso.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.MainActivity;
import com.example.gumloso.R;
import com.example.gumloso.RegisterRestActivity;
import com.example.gumloso.Restaurant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditManagerProfile extends AppCompatActivity implements MyRecyclerViewAdapterManager.ItemClickListener {
    private int minimumSizePassword = 3;
    private String newUsername = null;
    private String newPassword = null;
    private List<Restaurant> restaurants = new ArrayList<>();
    MyRecyclerViewAdapterManager adapter;
    RequestQueue queue;
    List<Restaurant.DailySchedule> sc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_manager_profile);

        queue = Volley.newRequestQueue(this);

        EditText pass1Box = findViewById(R.id.insert_passManager);
        EditText pass2Box = findViewById(R.id.confirm_passManager);
        EditText emailBox = findViewById(R.id.email);
        EditText userBox = findViewById(R.id.usernameEditManager);

        setRestaurantsButtons();

        findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditManagerProfile.this, RegisterRestActivity.class);
                startActivity(i);
            }
        });


        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(EditManagerProfile.this);

                String username = userBox.getText().toString();
                String url = MainActivity.urlG;

                JSONObject postData = new JSONObject();

                if (!username.isEmpty()) {
                    try {
                        postData.put("username",username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }                }

                String pass1 = pass1Box.getText().toString();
                String pass2 = pass2Box.getText().toString();

                if (!pass1.isEmpty()) {
                    if (pass1.compareTo(pass2) == 0) {
                        if (pass1.length() < minimumSizePassword) {
                            Toast.makeText(EditManagerProfile.this, "Passe demasiado curta", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            postData.put("password",pass1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(EditManagerProfile.this, "Passes não coincidem", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else {
                    if (!pass2.isEmpty()) {
                        Toast.makeText(EditManagerProfile.this, "Falta campo de password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(!username.isEmpty() || !pass1.isEmpty()){
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,url,
                            postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(EditManagerProfile.this, "Dados alterados com sucesso",Toast.LENGTH_SHORT).show();
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
                }
            }
        });

    }

    public void onItemClick(View view, int position) {
        Context context = view.getContext();
        Intent intent = new Intent(context, EditRestaurant.class);
        adapter.getItem(position).setImage(null);
        System.out.println("-----------" + sc);
        System.out.println(restaurants.get(0).toString());

        intent.putExtra("Restaurant", (Serializable) adapter.getItem(position));
        intent.putExtra("Position", position);
        startActivity(intent);
    }



    private void setRestaurantsButtons() {

        String url = MainActivity.urlG + "restaurant";

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<ArrayList<Restaurant>>(){}.getType();
                        restaurants = new Gson().fromJson(response, listType);
                        sc = restaurants.get(0).getSchedule();

                        adapter = new MyRecyclerViewAdapterManager(EditManagerProfile.this, restaurants);
                        adapter.setClickListener(EditManagerProfile.this);

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setAdapter(adapter);
                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(EditManagerProfile.this, DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(itemDecoration);
                        recyclerView.setLayoutManager(new LinearLayoutManager(EditManagerProfile.this));
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

}