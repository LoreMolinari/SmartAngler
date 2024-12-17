package com.smartangler.smartangler.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.smartangler.smartangler.Fish;
import com.smartangler.smartangler.FishingLocation.FishingLocation;
import com.smartangler.smartangler.FishingLocation.Vertex;
import com.smartangler.smartangler.ItemAdapter;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerOpenHelper;
import com.smartangler.smartangler.databinding.FragmentHomeBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    private Vertex currentVertex;
    private FishingLocation currentFishingLocation;

    private Button refreshButton, askAIButton;
    private TextView seasonText, timeOfDayText, locationText, locationNameText, noFishLikelyText;
    private RecyclerView recyclerView;
    private FragmentHomeBinding binding;

    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_UPLOAD = 2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.home_fish_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshButton = root.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> refreshConditions());

        askAIButton = root.findViewById(R.id.ask_ai_button);
        askAIButton.setOnClickListener(v -> askAI());

        seasonText = root.findViewById(R.id.current_season_text);
        seasonText.setText(getString(R.string.current_season,
                Fish.getCurrentSeason().toString().charAt(0) + Fish.getCurrentSeason().toString().substring(1).toLowerCase()));

        timeOfDayText = root.findViewById(R.id.current_time_of_day_text);
        timeOfDayText.setText(getString(R.string.current_time_of_day,
                Fish.getCurrentTimeOfDay().toString().charAt(0) + Fish.getCurrentTimeOfDay().toString().substring(1).toLowerCase()));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationText = root.findViewById(R.id.current_location_text);
        locationText.setText(getString(R.string.current_location_unknown));

        locationNameText = root.findViewById(R.id.location_name_text);
        locationNameText.setText(getString(R.string.location_name_unknown));

        noFishLikelyText = root.findViewById(R.id.no_fish_likely_text);

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshConditions();
            }
        });

        binding.buttonTakePhoto.setOnClickListener(v -> checkCameraPermission());
        binding.buttonUploadPhoto.setOnClickListener(v -> checkStoragePermission());


        return root;
    }

    private void refreshConditions() {
        Log.d("Conditions refresh", "Conditions refreshed");

        seasonText.setText(getString(R.string.current_season,
                Fish.getCurrentSeason().toString().charAt(0) + Fish.getCurrentSeason().toString().substring(1).toLowerCase()));
        timeOfDayText.setText(getString(R.string.current_time_of_day,
                Fish.getCurrentTimeOfDay().toString().charAt(0) + Fish.getCurrentTimeOfDay().toString().substring(1).toLowerCase()));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
            ).addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("Location service", location.toString());

                        currentVertex = new Vertex(location.getLatitude(), location.getLongitude());

                        locationText.setText(getString(R.string.current_location,
                                currentVertex.getLatitude(),
                                currentVertex.getLongitude()));

                        currentFishingLocation = SmartAnglerOpenHelper.getCurrentFishingLocation(getContext(), currentVertex);
                        if (currentFishingLocation != null) {
                            locationNameText.setText(getString(R.string.location_name,
                                    currentFishingLocation.getName()));
                        } else {
                            locationNameText.setText(getString(R.string.location_name_unknown));
                        }

                        makeFishCards();
                    } else {
                        Toast.makeText(getContext(), "Location unavailable", Toast.LENGTH_SHORT).show();
                        Log.d("Location service", "Location unavailable");
                        currentVertex = null;
                        locationText.setText(getString(R.string.current_location_unknown));
                        currentFishingLocation = null;
                        locationNameText.setText(getString(R.string.location_name_unknown));

                        makeFishCards();
                    }
                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void askAI() {

    }

    private void makeFishCards() {
        Log.d("Home cards", "Making home cards");
        List<Fish> fishList = SmartAnglerOpenHelper.getFishByConditions(this.getContext(), Fish.getCurrentSeason(), Fish.getCurrentTimeOfDay(), currentVertex);

        if (fishList == null || fishList.isEmpty()) {
            noFishLikelyText.setVisibility(View.VISIBLE);
        } else {
            noFishLikelyText.setVisibility(View.GONE);
        }

        ItemAdapter adapter = new ItemAdapter(fishList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location service", "Location access not granted, asking for permission");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d("Location service", "Location access granted");
        } else {
            Log.d("Location service", "Location access already granted");
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d("Location service", location.toString());

                                currentVertex = new Vertex(location.getLatitude(), location.getLongitude());

                                locationText.setText(getString(R.string.current_location,
                                        currentVertex.getLatitude(),
                                        currentVertex.getLongitude()));

                                currentFishingLocation = SmartAnglerOpenHelper.getCurrentFishingLocation(getContext(), currentVertex);
                                if (currentFishingLocation != null) {
                                    locationNameText.setText(getString(R.string.location_name,
                                            currentFishingLocation.getName()));
                                } else {
                                    locationNameText.setText(getString(R.string.location_name_unknown));
                                }

                                makeFishCards();
                            } else {
                                Toast.makeText(getContext(), "Location unavailable", Toast.LENGTH_SHORT).show();
                                Log.d("Location service", "Location unavailable");
                                locationText.setText(getString(R.string.current_location_unknown));
                                locationNameText.setText(getString(R.string.location_name_unknown));
                            }
                        }
                    });
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            openCamera();
        }
    }

    public void checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_IMAGE_UPLOAD);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_UPLOAD);
            } else {
                openGallery();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    openCamera();
                    break;
                case REQUEST_IMAGE_UPLOAD:
                    openGallery();
                    break;
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_UPLOAD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Bitmap imageBitmap;

            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imageBitmap = (Bitmap) extras.get("data");
                        ImageToCache(imageBitmap);
                    }
                    break;

                case REQUEST_IMAGE_UPLOAD:
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        UriToCache(selectedImageUri);
                    }
                    break;
            }
        }
    }

    private void ImageToCache(Bitmap bitmap) {
        try {
            File cacheDir = requireContext().getCacheDir();
            File tempFile = new File(cacheDir, "uploaded_image.jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            Intent intent = new Intent(getContext(), FishRecognitionActivity.class);
            intent.putExtra("imagePath", tempFile.getAbsolutePath());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private void UriToCache(Uri imageUri) {
        try {
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().getContentResolver(), imageUri);

            File cacheDir = requireContext().getCacheDir();
            File tempFile = new File(cacheDir, "uploaded_image.jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            Intent intent = new Intent(getContext(), FishRecognitionActivity.class);
            intent.putExtra("imagePath", tempFile.getAbsolutePath());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
