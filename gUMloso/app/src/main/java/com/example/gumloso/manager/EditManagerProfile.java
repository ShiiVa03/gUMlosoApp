package com.example.gumloso.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gumloso.GeoLocation;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditManagerProfile extends AppCompatActivity implements MyRecyclerViewAdapterManager.ItemClickListener {
    private int minimumSizePassword = 3;
    private String newUsername = null;
    private String newPassword = null;
    private List<Restaurant> restaurants = new ArrayList<>();
    private Map<Integer, Restaurant> outputRestaurants = new HashMap<>();
    MyRecyclerViewAdapterManager adapter;
    private static final int RESTAURANT_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_manager_profile);


        restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), false));
        restaurants.add(new Restaurant(1, "R", "C", "A", "T", 4, 12, new GeoLocation(123, 321), new byte[0], new ArrayList<>(), true));

        adapter = new MyRecyclerViewAdapterManager(this, restaurants);
        adapter.setClickListener(this);

        EditText pass1Box = findViewById(R.id.insert_passManager);
        EditText pass2Box = findViewById(R.id.confirm_passManager);
        EditText emailBox = findViewById(R.id.email);
        EditText userBox = findViewById(R.id.usernameEditManager);

        setRestaurantsButtons();


        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userBox.getText().toString();

                if (!username.isEmpty()) {
                    // TODO: API CALL
                    Toast.makeText(EditManagerProfile.this, "Username alterado com sucesso", Toast.LENGTH_SHORT).show();
                }

                String email = emailBox.getText().toString();

                if (!email.isEmpty()) {
                    // TODO: API CALL
                    Toast.makeText(EditManagerProfile.this, "Email alterado com sucesso", Toast.LENGTH_SHORT).show();
                }


                String pass1 = pass1Box.getText().toString();
                String pass2 = pass2Box.getText().toString();

                if (!pass1.isEmpty()) {
                    if (pass1.compareTo(pass2) == 0) {
                        if (pass1.length() < minimumSizePassword) {
                            Toast.makeText(EditManagerProfile.this, "Passe demasiado curta", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(EditManagerProfile.this, "Password alterada com sucesso", Toast.LENGTH_SHORT).show();
                            // TODO: API CALL
                        }
                    } else
                        Toast.makeText(EditManagerProfile.this, "Passes não coincidem", Toast.LENGTH_SHORT).show();

                } else {
                    if (!pass2.isEmpty())
                        Toast.makeText(EditManagerProfile.this, "Falta campo de password", Toast.LENGTH_SHORT).show();
                }

                if (!outputRestaurants.isEmpty()) {
                    // TODO: API CALL Dos values de outputRestaurants
                    Toast.makeText(EditManagerProfile.this, "Alteração nos restaurantes efetuada com sucesso", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void onItemClick(View view, int position) {

        Context context = view.getContext();
        Intent intent = new Intent(context, EditRestaurant.class);
        intent.putExtra("Restaurant", (Serializable) adapter.getItem(position));
        intent.putExtra("Position", position);
        startActivityForResult(intent, RESTAURANT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RESTAURANT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                 outputRestaurants.put(data.getIntExtra("Position", -1), (Restaurant) data.getExtras().get("Restaurant"));
            }
        }
    }



    private void setRestaurantsButtons() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}