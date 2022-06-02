package com.example.gumloso;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.consumer.MainPageActivity;
import com.example.gumloso.manager.GestaoReservas;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String type;
    private EditText email;
    private EditText password;


    private EditText emailText;
    private EditText passText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        type = getIntent().getStringExtra("key");

        getSupportActionBar().setTitle("Login"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailText = findViewById(R.id.editTextTextPersonName);
        passText = findViewById(R.id.editTextTextPassword);

        Button btn = findViewById(R.id.btn_login);

        if(type.equals("Consumidor")) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postDataUsingVolley(type);
                }
            });
        }else{
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postDataUsingVolley(type);
                }
            });
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


    private void postDataUsingVolley(String type) {
        String url;
        if(type.equals("Consumidor")) {
             url = MainActivity.urlC + "login";
        }else{
            url = MainActivity.urlG + "login";
        }

        JSONObject postData = new JSONObject();
        String email = emailText.getText().toString();
        String password = passText.getText().toString();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);


        JsonObjectRequest req = new JsonObjectRequest(url,
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MainActivity.apiKey = response.getString("token");
                            System.out.println("------------------------------- " + response.getString("token"));
                            if(type.equals("Consumidor")) {
                                Intent j = new Intent(LoginActivity.this, MainPageActivity.class);
                                startActivity(j);
                            }else{
                                Intent i = new Intent(LoginActivity.this, GestaoReservas.class);
                                startActivity(i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        if(error.networkResponse.data!=null) {
                            Toast.makeText(LoginActivity.this,statusCode,Toast.LENGTH_SHORT).show();
                        }
                    }

                });
        queue.add(req);}}