package com.example.gumloso;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftsActivity extends AppCompatActivity {

    int[][] matrixIds = {
            {R.id.ah1, R.id.am1, R.id.fh1, R.id.fm1},
            {R.id.ah2, R.id.am2, R.id.fh2, R.id.fm2},
            {R.id.ah3, R.id.am3, R.id.fh3, R.id.fm3},
            {R.id.ah4, R.id.am4, R.id.fh4, R.id.fm4},
            {R.id.ah5, R.id.am5, R.id.fh5, R.id.fm5},
            {R.id.ah6, R.id.am6, R.id.fh6, R.id.fm6},
            {R.id.ah7, R.id.am7, R.id.fh7, R.id.fm7},
    };

    private List<Restaurant.DailySchedule> dailySchedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifts);

        dailySchedules = (List<Restaurant.DailySchedule>) getIntent().getExtras().get("DailySchedules");

        for (Restaurant.DailySchedule dS : dailySchedules) {
            int[] arrayIds = matrixIds[dS.day - 1];
            ((EditText) findViewById(arrayIds[0])).setText(String.valueOf(dS.timeOpen / 60));
            ((EditText) findViewById(arrayIds[1])).setText(String.valueOf(dS.timeOpen % 60));
            ((EditText) findViewById(arrayIds[2])).setText(String.valueOf(dS.timeClose / 60));
            ((EditText) findViewById(arrayIds[3])).setText(String.valueOf(dS.timeClose % 60));
        }

        findViewById(R.id.buttonSet).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                dailySchedules.clear();

                try {
                    for (int i = 0; i < 7; ++i) {
                        int[] arrayIds = matrixIds[i];

                        Restaurant.DailySchedule dS = createDailyScheduleFromIds(i + 1, arrayIds[0], arrayIds[1], arrayIds[2], arrayIds[3]);
                        if (dS != null)
                            dailySchedules.add(dS);
                    }

                    Intent intent = new Intent();
                    intent.putExtra("DailySchedules", (Serializable) dailySchedules);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(ShiftsActivity.this, "Invalid Shifts", Toast.LENGTH_SHORT).show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            public Restaurant.DailySchedule createDailyScheduleFromIds(int day, int openHourId, int openMinuteId, int closeHourId, int closeMinuteId) throws Exception {

                String openHourStr = ((EditText) findViewById(openHourId)).getText().toString();
                String openMinuteStr = ((EditText) findViewById(openMinuteId)).getText().toString();
                String closeHourStr = ((EditText) findViewById(closeHourId)).getText().toString();
                String closeMinuteStr = ((EditText) findViewById(closeMinuteId)).getText().toString();

                if (openHourStr.isEmpty() && openMinuteStr.isEmpty() && closeHourStr.isEmpty() && closeMinuteStr.isEmpty())
                    return null;


                int openHour = Integer.parseInt(openHourStr);
                int openMinute = Integer.parseInt(openMinuteStr);
                int closeHour = Integer.parseInt(closeHourStr);
                int closeMinute = Integer.parseInt(closeMinuteStr);

                if (openHour >= 24 || closeHour >= 24 || openMinute >= 60 || closeMinute >= 60 ||
                openHour < 0 || closeHour < 0 || openMinute < 0 || closeMinute < 0)
                    throw new Exception();

                LocalTime openTime = LocalTime.of(openHour, openMinute);
                LocalTime closeTime = LocalTime.of(closeHour, closeMinute);

                if (closeTime.isBefore(openTime))
                    throw new Exception();

                return new Restaurant.DailySchedule(day, openTime.getMinute() + openTime.getHour() * 60, closeTime.getMinute() + closeTime.getHour() * 60);

            }
        });
    }


}