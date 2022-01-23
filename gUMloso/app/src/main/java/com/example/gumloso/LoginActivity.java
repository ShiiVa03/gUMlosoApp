package com.example.gumloso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.gumloso.consumer.MainPageActivity;
import com.example.gumloso.manager.GestaoReservas;

public class LoginActivity extends AppCompatActivity {

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        type = getIntent().getStringExtra("key");

        getSupportActionBar().setTitle("Login"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btn = findViewById(R.id.btn_login);

        if(type.equals("Consumidor")) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, MainPageActivity.class));
                }
            });
        }else{
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, GestaoReservas.class));
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
}