package com.example.gumloso.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileUser extends AppCompatActivity {
    private String email;
    private int minimumSizePassword = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);

        LinearLayout restaurantes = findViewById(R.id.scrollViewLayout);
        for (int i = 0; i < 10; ++i) {
            TextView v = (TextView) LayoutInflater.from(this).inflate(R.layout.item_scroll, null);
            v.setText("Restaurante");
            restaurantes.addView(v);
        }

        EditText pass1Box = findViewById(R.id.insert_pass);
        EditText pass2Box = findViewById(R.id.confirm_pass);
        EditText userBox = findViewById(R.id.usernameEdit);


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestQueue queue = Volley.newRequestQueue(EditProfileUser.this);

                String username = userBox.getText().toString();
                String url = MainActivity.urlC;

                JSONObject postData = new JSONObject();

                if (!username.isEmpty()) {
                    try {
                        postData.put("username",username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String pass1 = pass1Box.getText().toString();
                String pass2 = pass2Box.getText().toString();

                if (!pass1.isEmpty()) {
                    if (pass1.compareTo(pass2) == 0) {
                        if (pass1.length() < minimumSizePassword) {
                            Toast.makeText(EditProfileUser.this, "Passe demasiado curta", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            postData.put("password",pass1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(EditProfileUser.this, "Passes não coincidem", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else {
                    if (!pass2.isEmpty()) {
                        Toast.makeText(EditProfileUser.this, "Falta campo de password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(!username.isEmpty() || !pass1.isEmpty()){
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,url,
                            postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(EditProfileUser.this, "Dados alterados com sucesso",Toast.LENGTH_SHORT).show();
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
}