package com.smartangler.smartangler;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class StepCounterListener implements SensorEventListener {

    private long initialStepCount = -1;
    public static int stepCount = 0;
    private TextView stepCountsView;
    private TextView counterPB;
    private CircularProgressIndicator progressBar;
    private SQLiteDatabase database;

    public StepCounterListener(TextView stepCountsView, TextView counterPB, CircularProgressIndicator progressBar, TextView castsView, SQLiteDatabase database) {
        this.stepCountsView = stepCountsView;
        this.counterPB = counterPB;
        this.progressBar = progressBar;
        this.database = database;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SensorSC", "Update +1");
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount < 0) {
                initialStepCount = (long) event.values[0];
            }

            long currentSteps = (long) event.values[0];
            stepCount = (int) (currentSteps - initialStepCount);

            updateUI();
            saveStepData(event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateUI() {
        stepCountsView.setText("Steps: " + stepCount);
        counterPB.setText(String.valueOf(stepCount));
        progressBar.setProgress(stepCount);
    }

    private void saveStepData(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String currentTime = sdf.format(new Date(System.currentTimeMillis()));

        ContentValues values = new ContentValues();
        values.put(SmartAnglerOpenHelper.KEY_TIMESTAMP, currentTime);
        values.put(SmartAnglerOpenHelper.KEY_DAY, currentTime.substring(0, 10));
        values.put(SmartAnglerOpenHelper.KEY_HOUR, currentTime.substring(11, 13));

        database.insert(SmartAnglerOpenHelper.TABLE_NAME, null, values);
    }
}