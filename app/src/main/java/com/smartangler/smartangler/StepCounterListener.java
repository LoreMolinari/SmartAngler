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
import java.util.List;
import java.util.TimeZone;

public class StepCounterListener implements SensorEventListener {

    private long lastSensorUpdate = 0;
    public static int accStepCounter = 0;
    ArrayList<Integer> accSeries = new ArrayList<>();
    ArrayList<String> timestampsSeries = new ArrayList<>();
    private double accMag = 0;
    private int lastAddedIndex = 1;
    int stepThreshold = 6;

    TextView stepCountsView;
    CircularProgressIndicator progressBar;
    private final SQLiteDatabase database;

    private String timestamp;
    private String day;
    private String hour;

    public StepCounterListener(TextView stepCountsView, CircularProgressIndicator progressBar, SQLiteDatabase database) {
        this.stepCountsView = stepCountsView;
        this.database = database;
        this.progressBar = progressBar;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                long currentTimeInMilliSecond = System.currentTimeMillis();

                long timeInMillis = currentTimeInMilliSecond + (sensorEvent.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000;

                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String sensorEventDate = jdf.format(timeInMillis);

                if ((currentTimeInMilliSecond - lastSensorUpdate) > 1000) {
                    lastSensorUpdate = currentTimeInMilliSecond;
                    String sensorRawValues = "  x = " + x + "  y = " + y + "  z = " + z;
                    Log.d("Acc. Event", "last sensor update at " + sensorEventDate + sensorRawValues);
                }

                accMag = Math.sqrt(x * x + y * y + z * z);

                accSeries.add((int) accMag);

                timestamp = sensorEventDate;
                day = sensorEventDate.substring(0, 10);
                hour = sensorEventDate.substring(11, 13);

                Log.d("SensorEventTimestampInMilliSecond", timestamp);

                timestampsSeries.add(timestamp);
                peakDetection();
                break;

            case Sensor.TYPE_STEP_DETECTOR:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void peakDetection() {

        int windowSize = 20;
        int currentSize = accSeries.size();
        if (currentSize - lastAddedIndex < windowSize) {
            return;
        }

        List<Integer> valuesInWindow = accSeries.subList(lastAddedIndex, currentSize);
        List<String> timePointList = timestampsSeries.subList(lastAddedIndex, currentSize);
        lastAddedIndex = currentSize;

        for (int i = 1; i < valuesInWindow.size() - 1; i++) {
            int forwardSlope = valuesInWindow.get(i + 1) - valuesInWindow.get(i);
            int downwardSlope = valuesInWindow.get(i) - valuesInWindow.get(i - 1);

            if (forwardSlope < 0 && downwardSlope > 0 && valuesInWindow.get(i) > stepThreshold) {
                countStep(timePointList.get(i));
            }
        }
    }

    private void countStep(String timestamp) {
        accStepCounter += 1;
        Log.d("ACC STEPS: ", String.valueOf(accStepCounter));
        stepCountsView.setText(String.valueOf(accStepCounter));
        progressBar.setProgress(accStepCounter);

        ContentValues databaseEntry = new ContentValues();
        databaseEntry.put(SmartAnglerOpenHelper.KEY_TIMESTAMP, timestamp);

        databaseEntry.put(SmartAnglerOpenHelper.KEY_DAY, this.day);
        databaseEntry.put(SmartAnglerOpenHelper.KEY_HOUR, this.hour);

        database.insert(SmartAnglerOpenHelper.TABLE_NAME, null, databaseEntry);
    }
}
