package com.smartangler.smartangler.ui.fishing;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.smartangler.smartangler.CastDetectorListener;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerOpenHelper;
import com.smartangler.smartangler.SmartAnglerSessionHelper;
import com.smartangler.smartangler.StepCounterListener;
import com.smartangler.smartangler.databinding.FragmentFishingBinding;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FishingFragment extends Fragment {
    private FragmentFishingBinding binding;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private List<FishEntry> fishEntries;
    private FishEntryAdapter adapter;
    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    private String currentSessionId;
    private boolean isSessionActive = false;
    private Integer fish_caught = 0;
    private int totalMinutes = 0;
    private TextView stepCountsView;
    private TextView counterPB;
    private TextView castsView;
    private CircularProgressIndicator progressBar;
    private Sensor stepCounter;
    private Sensor accSensor;
    private SensorManager sensorManager;
    private StepCounterListener sensorListener;
    private CastDetectorListener castDetectorListener;
    private static final int PHYSICAL_ACTIVITY_PERMISSION_REQUEST_CODE = 1001;
    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint currentLocation;
    private boolean isStartMarkerAdded = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFishingBinding.inflate(inflater, container, false);
        checkAndRequestPhysicalActivityPermission();

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.buttonTakePicture.setOnClickListener(v -> checkCameraPermission());
        fishEntries = new ArrayList<>();
        adapter = new FishEntryAdapter(fishEntries);
        binding.recyclerViewFish.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFish.setAdapter(adapter);

        setupToggleButtonGroup();

        View root = binding.getRoot();
        stepCountsView = root.findViewById(R.id.steps_text);
        stepCountsView.setText(getString(R.string.steps_counter, 0));
        counterPB = root.findViewById(R.id.counter);
        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setMax(50);
        progressBar.setProgress(0);
        castsView = root.findViewById(R.id.casts_text);
        castsView.setText(getString(R.string.casts_counter, 0));

        try {
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        } catch (NullPointerException e) {
            Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_SHORT).show();
        }

        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getCurrentLocation();

        return binding.getRoot();
    }

    private void setupToggleButtonGroup() {
        binding.startButton.setOnClickListener(v -> startFishingSession());
        binding.stopButton.setOnClickListener(v -> stopFishingSession());
    }

    private void startFishingSession() {
        if (!isSessionActive) {
            isSessionActive = true;
            currentSessionId = generateSessionId();
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(updateTimerThread, 0);
            fishEntries.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "Fishing session started", Toast.LENGTH_SHORT).show();

            SmartAnglerOpenHelper databaseOpenHelper = new SmartAnglerOpenHelper(this.getContext());
            SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

            progressBar.setProgress(0);
            counterPB.setText("0");
            castsView.setText(getString(R.string.casts_counter, 0));
            stepCountsView.setText(getString(R.string.steps_counter, 0));

            if (stepCounter != null) {
                sensorListener = new StepCounterListener(stepCountsView, counterPB, progressBar, castsView, database);
                sensorManager.registerListener(sensorListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.sensor_not_available, Toast.LENGTH_SHORT).show();
            }

            if (accSensor != null) {
                castDetectorListener = new CastDetectorListener(castsView);
                sensorManager.registerListener(castDetectorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                CastDetectorListener.resetCounter();
                Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_SHORT).show();
            }

            getCurrentLocation();
            map.invalidate();
        }
    }

    private void stopFishingSession() {
        int stepCount = StepCounterListener.stepCount;
        if (isSessionActive) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            SmartAnglerSessionHelper.addSession(
                    requireContext(),
                    currentSessionId,
                    currentDate,
                    "Unknown Location",
                    totalMinutes,
                    fish_caught,
                    stepCount,
                    CastDetectorListener.castsCounter
            );

            fish_caught = 0;
            isSessionActive = false;
            timerHandler.removeCallbacks(updateTimerThread);
            binding.timerText.setText("00:00:00");
            Toast.makeText(requireContext(), "Fishing session ended", Toast.LENGTH_SHORT).show();
            sensorManager.unregisterListener(sensorListener);
            Toast.makeText(getContext(), R.string.stop_text, Toast.LENGTH_SHORT).show();

            isStartMarkerAdded = false;
            map.getOverlays().clear();
            getCurrentLocation();
        }
    }

    private String generateSessionId() {
        return new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date());
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            long timeInMilliseconds = System.currentTimeMillis() - startTime;
            int seconds = (int) (timeInMilliseconds / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            totalMinutes = (int) (timeInMilliseconds / (60 * 1000));

            binding.timerText.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    private void checkAndRequestPhysicalActivityPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PHYSICAL_ACTIVITY_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        getCurrentLocation();
        updateMap();
        if (isSessionActive) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(requireContext(), "Start a fishing session first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            fish_caught = fish_caught + 1;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveFishEntry(imageBitmap);
        }
    }

    private void saveFishEntry(Bitmap imageBitmap) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String title = "Fish " + (fishEntries.size() + 1);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        SmartAnglerSessionHelper.addPhoto(
                requireContext(),
                title,
                byteArray,
                currentDate,
                String.valueOf(currentLocation),
                currentSessionId
        );


        if (currentLocation != null) {
            Marker fishMarker = new Marker(map);
            fishMarker.setPosition(currentLocation);
            fishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            fishMarker.setTextIcon(title);

            Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.bait);

            int markerSize = 100;
            Bitmap fishIcon = getResizedBitmap(drawable, markerSize, markerSize);

            fishMarker.setIcon(new BitmapDrawable(requireContext().getResources(), fishIcon));
            fishMarker.setTitle(title + "\n" + currentDate);

            map.getOverlays().add(fishMarker);

            FishEntry newEntry = new FishEntry(imageBitmap, title, currentDate);
            fishEntries.add(0, newEntry);
            binding.recyclerViewFish.scrollToPosition(0);

            fishMarker.setOnMarkerClickListener((marker, mapView) -> {
                String markerTitle = marker.getTitle();
                String markerTitleOnly = markerTitle.split("\n")[0];
                Log.d("FishingFragment", "Marker clicked: " + markerTitleOnly);
                int index = findFishEntryIndex(markerTitleOnly);
                if (index != -1) {
                    Log.d("FishingFragment", "Found index: " + index);
                    showSelectedFishEntry(index);
                } else {
                    Log.e("FishingFragment", "Marker title not found: " + markerTitleOnly);
                }
                return true; // Consuma il click
            });

            updateMap();
        }
    }

    // Metodo per trovare l'indice della FishEntry corrispondente
    private int findFishEntryIndex(String title) {
        for (int i = 0; i < fishEntries.size(); i++) {
            if (fishEntries.get(i).getName().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    // Metodo per mostrare la foto corrispondente nella RecyclerView
    private void showSelectedFishEntry(int index) {
        if (index >= 0 && index < fishEntries.size()) {
            adapter.setSelectedIndex(index);
            binding.recyclerViewFish.scrollToPosition(0);
        }
    }

    private void loadFishEntries(String sessionId) {
        List<Object[]> photos = SmartAnglerSessionHelper.loadPhotosForSession(requireContext(), sessionId);
        fishEntries.clear();
        for (Object[] photo : photos) {
            byte[] imageData = (byte[]) photo[3];
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            FishEntry entry = new FishEntry(bitmap, (String) photo[1], (String) photo[2]);
            fishEntries.add(entry);
        }
        adapter.notifyDataSetChanged();
    }

    private void updateMap() {
        if (currentLocation != null) {
            if (!isStartMarkerAdded) {
                Marker startMarker = new Marker(map);
                startMarker.setPosition(currentLocation);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                startMarker.setTitle("Session position");

                Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.position);
                int markerSize = 100;
                Bitmap fishIcon = getResizedBitmap(drawable, markerSize, markerSize);

                startMarker.setIcon(new BitmapDrawable(requireContext().getResources(), fishIcon));
                map.getOverlays().add(startMarker);
                isStartMarkerAdded = true;
            }

            map.getController().setCenter(currentLocation);
            map.getController().setZoom(15.0);

            map.invalidate();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                                Log.d("PositionOSM", String.valueOf(currentLocation));
                                updateMap();
                            }
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    private Bitmap getResizedBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}