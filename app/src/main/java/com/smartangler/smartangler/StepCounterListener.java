package com.smartangler.smartangler;

import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class StepCounterListener implements SensorEventListener {

    private long initialStepCount = -1;
    public static int stepCount = 0;
    private TextView stepCountsView;
    private SQLiteDatabase database;

    public StepCounterListener(TextView stepCountsView, TextView castsView, SQLiteDatabase database) {
        this.stepCountsView = stepCountsView;
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
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateUI() {
        stepCountsView.setText("Steps: " + stepCount);
    }
}