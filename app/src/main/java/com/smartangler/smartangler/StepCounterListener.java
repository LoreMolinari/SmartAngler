package com.smartangler.smartangler;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class StepCounterListener implements SensorEventListener {

    private long initialStepCount = -1;
    public static int stepCount = 0;
    private final TextView stepCountsView;
    private final CircularProgressIndicator progressBar;

    public StepCounterListener(TextView stepCountsView, CircularProgressIndicator pb) {
        this.stepCountsView = stepCountsView;
        this.progressBar = pb;
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
        String update = "Steps: " + stepCount;
        stepCountsView.setText(update);
        progressBar.setProgress(stepCount);
    }
}