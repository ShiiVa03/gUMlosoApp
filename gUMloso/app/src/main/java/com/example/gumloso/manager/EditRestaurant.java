package com.example.gumloso.manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gumloso.R;
import com.example.gumloso.Restaurant;
import com.example.gumloso.ShiftsActivity;
import com.google.android.gms.common.util.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class EditRestaurant extends AppCompatActivity {

    private List<Restaurant.DailySchedule> dailySchedules;
    private static final int SHIFTS_REQUEST_CODE = 0;
    private static final int RESULT_LOAD_IMG = 1;
    private boolean hasImage = false;
    private Restaurant restaurant;

    private TextView restTypeBox;
    private TextView capacityET;
    private Button confirmBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

        Intent intent = getIntent();
        restaurant = (Restaurant) intent.getExtras().get("Restaurant");
        int position = intent.getIntExtra("Position", -1);
        dailySchedules = restaurant.getSchedule();
        TextView restNameBox = findViewById(R.id.rest_name);
        restNameBox.setText(restaurant.getName());

        restTypeBox = findViewById(R.id.textView20);
        restTypeBox.setText(restaurant.getType());

        capacityET = findViewById(R.id.NumCapacidade);
        capacityET.setText(String.valueOf(restaurant.getCapacity()));

        Button btnShift = findViewById(R.id.defineShceduleBtn);
        btnShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRestaurant.this, ShiftsActivity.class);
                intent.putExtra("DailySchedules", (Serializable) dailySchedules);
                startActivityForResult(intent, SHIFTS_REQUEST_CODE);
            }
        });


        confirmBtn = findViewById(R.id.button5);
        confirmBtn.setText("Definir");
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {
                    restaurant.setType(restTypeBox.getText().toString().trim());
                    restaurant.setCapacity(Integer.parseInt(capacityET.getText().toString().trim()));
                    Intent intent = new Intent();
                    intent.putExtra("Restaurant", (Serializable) restaurant);
                    intent.putExtra("Position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        Button definirBtn = findViewById(R.id.buttonDefinir);
        definirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == SHIFTS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                dailySchedules = (List<Restaurant.DailySchedule>) data.getExtras().get("DailySchedules");
                restaurant.setSchedule(dailySchedules);
                confirmBtn.setEnabled(validate());
            }
        } else {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    hasImage = true;
                    restaurant.setImage(IOUtils.toByteArray(imageStream));
                    confirmBtn.setEnabled(validate());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }

    }

    public boolean validate() {
        String typeInput = restTypeBox.getText().toString().trim();
        String capcityInput = capacityET.getText().toString().trim();

        return !typeInput.isEmpty() && !capcityInput.isEmpty() && hasImage && dailySchedules.size() > 0;
    }

}