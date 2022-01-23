package com.example.gumloso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChooseTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

        getSupportActionBar().setTitle("Choose type"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button btn = findViewById(R.id.buttonConsu);
        Button btnG = findViewById(R.id.buttonGest);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseTypeActivity.this, LoginActivity.class);
                i.putExtra("key","Consumidor");
                startActivity(i);
            }
        });

        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChooseTypeActivity.this, LoginActivity.class);
                i.putExtra("key","Gestor");
                startActivity(i);
            }
        });
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

