package com.example.gumloso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String urlC = "http://192.168.1.167:9000/customer/";
    public static final String urlG = "http://192.168.1.167:9000/manager/";

    public static String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("gUMloso"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        Button btnLogin = (Button)findViewById(R.id.LoginButton);
        Button btnRegister = (Button)findViewById(R.id.RegisterButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChooseTypeActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }

}