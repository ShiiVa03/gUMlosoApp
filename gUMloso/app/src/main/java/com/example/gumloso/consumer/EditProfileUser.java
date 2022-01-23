package com.example.gumloso.consumer;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gumloso.R;

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
        EditText emailBox = findViewById(R.id.editTextTextEmailAddress2);
        EditText userBox = findViewById(R.id.usernameEdit);


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userBox.getText().toString();

                if (!username.isEmpty()) {
                    // TODO: API CALL
                    Toast.makeText(EditProfileUser.this, "Username alterado com sucesso", Toast.LENGTH_SHORT).show();
                }

                String email = emailBox.getText().toString();

                if (!email.isEmpty()) {
                    // TODO: API CALL
                    Toast.makeText(EditProfileUser.this, "Email alterado com sucesso", Toast.LENGTH_SHORT).show();
                }


                String pass1 = pass1Box.getText().toString();
                String pass2 = pass2Box.getText().toString();

                if (!pass1.isEmpty()) {
                    if (pass1.compareTo(pass2) == 0) {
                        if (pass1.length() < minimumSizePassword) {
                            Toast.makeText(EditProfileUser.this, "Passe demasiado curta", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(EditProfileUser.this, "Password alterada com sucesso", Toast.LENGTH_SHORT).show();
                            // TODO: API CALL
                        }
                    } else
                        Toast.makeText(EditProfileUser.this, "Passes não coincidem", Toast.LENGTH_SHORT).show();

                } else {
                    if (!pass2.isEmpty())
                        Toast.makeText(EditProfileUser.this, "Falta campo de password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}