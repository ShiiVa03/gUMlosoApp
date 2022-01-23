package com.example.gumloso;

import static android.graphics.Bitmap.createScaledBitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gumloso.manager.GestaoReservas;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RegisterRestActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    private TextView restaurantText;
    private TextView locationText;
    private TextView contactText;
    private TextView typeText;
    private List<Restaurant.DailySchedule> dailySchedules = new ArrayList<>();
    private TextView capacityText;
    private Button btn;
    private ImageButton imageUpload;
    private boolean hasImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_rest);

        restaurantText = findViewById(R.id.restaurantText);
        locationText = findViewById(R.id.locationText);
        contactText = findViewById(R.id.contactText);
        typeText = findViewById(R.id.typeText);
        capacityText = findViewById(R.id.capacityText);
        btn = findViewById(R.id.btn2);
        imageUpload = findViewById(R.id.imageUpload);

        btn.setEnabled(false);

        getSupportActionBar().setTitle("Register"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btn.setEnabled(validate());
            }
        };

        restaurantText.addTextChangedListener(textWatcher);
        locationText.addTextChangedListener(textWatcher);
        typeText.addTextChangedListener(textWatcher);

        imageUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
        Button btnShift = findViewById(R.id.btnDefinir);
        btnShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterRestActivity.this, ShiftsActivity.class);
                intent.putExtra("DailySchedules", (Serializable) dailySchedules);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterRestActivity.this, GestaoReservas.class));
            }
        });
    }

    private boolean validate() {
        String restaurantInput = restaurantText.getText().toString().trim();
        String locationInput = locationText.getText().toString().trim();
        String typeInput = typeText.getText().toString().trim();
        String capcityInput = capacityText.getText().toString().trim();


        return !restaurantInput.isEmpty() && !locationInput.isEmpty()
                && !typeInput.isEmpty() && !capcityInput.isEmpty() && hasImage && dailySchedules.size() > 0;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == SECOND_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                dailySchedules.clear();
                dailySchedules.addAll((List<Restaurant.DailySchedule>) data.getExtras().get("DailySchedules"));
                btn.setEnabled(validate());
            }
        } else {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageUpload.setImageBitmap(createScaledBitmap(selectedImage, 240, 100, true));
                    hasImage = true;
                    btn.setEnabled(validate());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
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