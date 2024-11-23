package com.smartangler.smartangler.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerOpenHelper;
import com.smartangler.smartangler.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView stepCountsView;
    private CircularProgressIndicator progressBar;
    private MaterialButtonToggleGroup toggleButtonGroup;
    private Sensor accSensor;
    private SensorManager sensorManager;
    private StepCounterListener sensorListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        stepCountsView = root.findViewById(R.id.counter);
        stepCountsView.setText("0");

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setMax(50);
        progressBar.setProgress(0);

        try {
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        } catch (NullPointerException e) {
            Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_LONG).show();
        }
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        SmartAnglerOpenHelper databaseOpenHelper = new SmartAnglerOpenHelper(this.getContext());
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        toggleButtonGroup = root.findViewById(R.id.toggleButtonGroup);
        toggleButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (group.getCheckedButtonId() == R.id.start_button) {
                    if (accSensor != null) {
                        sensorListener = new StepCounterListener(stepCountsView, progressBar, database);
                        sensorManager.registerListener(sensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_LONG).show();
                    }

                } else {
                    sensorManager.unregisterListener(sensorListener);
                    Toast.makeText(getContext(), R.string.stop_text, Toast.LENGTH_LONG).show();
                }
            }
        });

        root.findViewById(R.id.statistic_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

class StepCounterListener implements SensorEventListener {

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
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
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
                accStepCounter += 1;
                Log.d("ACC STEPS: ", String.valueOf(accStepCounter));
                stepCountsView.setText(String.valueOf(accStepCounter));
                progressBar.setProgress(accStepCounter);

                ContentValues databaseEntry = new ContentValues();
                databaseEntry.put(SmartAnglerOpenHelper.KEY_TIMESTAMP, timePointList.get(i));

                databaseEntry.put(SmartAnglerOpenHelper.KEY_DAY, this.day);
                databaseEntry.put(SmartAnglerOpenHelper.KEY_HOUR, this.hour);

                database.insert(SmartAnglerOpenHelper.TABLE_NAME, null, databaseEntry);
            }
        }
    }
}
