package com.smartangler.smartangler;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class CastDetectorListener implements SensorEventListener {
    private static final int CAST_THRESHOLD = 30;

    private final TextView castsView;
    public static int castsCounter = 0;
    private final ArrayList<Integer> accSeries = new ArrayList<>();
    private int lastAddedIndex = 1;
    private TextView counterPB;
    private CircularProgressIndicator progressBar;

    public CastDetectorListener(TextView castsView, TextView counterPB, CircularProgressIndicator pb) {
        this.castsView = castsView;
        this.counterPB = counterPB;
        this.progressBar = pb;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        double accMag = Math.sqrt(x * x + y * y + z * z);

        accSeries.add((int) accMag);

        int windowSize = 20;
        int currentSize = accSeries.size();
        if (currentSize - lastAddedIndex < windowSize) {
            return;
        }

        List<Integer> valuesInWindow = accSeries.subList(lastAddedIndex, currentSize);
        lastAddedIndex = currentSize;

        for (int i = 1; i < valuesInWindow.size() - 1; i++) {
            int forwardSlope = valuesInWindow.get(i + 1) - valuesInWindow.get(i);
            int downwardSlope = valuesInWindow.get(i) - valuesInWindow.get(i - 1);

            // Peak due to cast
            if (forwardSlope < 0 && downwardSlope > 0 && valuesInWindow.get(i) > CAST_THRESHOLD) {
                castsCounter += 1;
                String castsText = "Casts: " + castsCounter;
                castsView.setText(castsText);
                counterPB.setText(String.format("%d", castsCounter));
                progressBar.setProgress(castsCounter);
                Log.d("Cast detection", "Cast detected with peak at " + valuesInWindow.get(i));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void resetCounter() {
        castsCounter = 0;
    }
}
