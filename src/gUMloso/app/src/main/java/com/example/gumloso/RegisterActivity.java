package com.example.gumloso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gumloso.consumer.MainPageActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText userText;
    private EditText passText;
    private EditText emailText;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getSupportActionBar().setTitle("Register"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Spinner spin = findViewById(R.id.register_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.register_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        btn = findViewById(R.id.buttonRegisterNext);
        btn.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String usernameInput = userText.getText().toString().trim();
                String passwordInput = passText.getText().toString().trim();
                String emailInput = emailText.getText().toString().trim();

                btn.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty()
                        && !emailInput.isEmpty());
            }
        };

        this.userText = findViewById(R.id.editTextTextPersonName2);
        this.userText.addTextChangedListener(textWatcher);
        this.emailText = findViewById(R.id.editTextTextEmailAddress);
        this.emailText.addTextChangedListener(textWatcher);
        this.passText = findViewById(R.id.editTextTextPassword2);
        this.passText.addTextChangedListener(textWatcher);

    }

    @SuppressLint("SetTextI18n")
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        if (parent.getItemAtPosition(pos).equals("Consumidor")) {
            btn.setText("Go");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postRegister();
                }
            });

        } else {
            btn.setText("Next");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(RegisterActivity.this, RegisterRestActivity.class);
                    i.putExtra("Register", true);
                    i.putExtra("email",emailText.getText().toString());
                    i.putExtra("username",userText.getText().toString());
                    i.putExtra("password",userText.getText().toString());
                    startActivity(i);
                }
            });
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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


    private void postRegister() {
        String url = MainActivity.urlC + "register";

        JSONObject postData = new JSONObject();
        String email = emailText.getText().toString();
        String username = userText.getText().toString();
        String password = passText.getText().toString();

        try {
            postData.put("email", email);
            postData.put("password", password);
            postData.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);


        JsonObjectRequest req = new JsonObjectRequest(url,
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MainActivity.apiKey = response.getString("token");
                            System.out.println("------------------------------- " + response.getString("token"));
                                Intent j = new Intent(RegisterActivity.this, MainPageActivity.class);
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
                            Toast.makeText(RegisterActivity.this, statusCode, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        queue.add(req);
    }

}