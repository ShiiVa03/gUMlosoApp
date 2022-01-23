package com.example.gumloso;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.gumloso.consumer.MainPageActivity;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText user;
    private EditText pass;
    private EditText email;
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
                String usernameInput = user.getText().toString().trim();
                String passwordInput = pass.getText().toString().trim();
                String emailInput = email.getText().toString().trim();

                btn.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty()
                        && !emailInput.isEmpty());
            }
        };

        this.user = findViewById(R.id.editTextTextPersonName2);
        this.user.addTextChangedListener(textWatcher);
        this.email = findViewById(R.id.editTextTextEmailAddress);
        this.email.addTextChangedListener(textWatcher);
        this.pass = findViewById(R.id.editTextTextPassword2);
        this.pass.addTextChangedListener(textWatcher);

    }

    @SuppressLint("SetTextI18n")
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        if (parent.getItemAtPosition(pos).equals("Consumidor")) {
            btn.setText("Go");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(RegisterActivity.this, MainPageActivity.class));

                }
            });

        } else {
            btn.setText("Next");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(RegisterActivity.this, RegisterRestActivity.class));
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
}