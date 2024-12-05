package com.smartangler.smartangler.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    private Vertex currentVertex;
    private FishingLocation currentFishingLocation;

    private Button refreshButton;
    private TextView seasonText, timeOfDayText, locationText, locationNameText, noFishLikelyText;
    private RecyclerView recyclerView;
    private FragmentHomeBinding binding;

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

        seasonText = root.findViewById(R.id.current_season_text);
        seasonText.setText(getString(R.string.current_season, Fish.getCurrentSeason()));

        timeOfDayText = root.findViewById(R.id.current_time_of_day_text);
        timeOfDayText.setText(getString(R.string.current_time_of_day, Fish.getCurrentTimeOfDay()));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationText = root.findViewById(R.id.current_location_text);
        locationText.setText(getString(R.string.current_location_unknown));

        locationNameText = root.findViewById(R.id.location_name_text);
        locationNameText.setText(getString(R.string.location_name_unknown));

        noFishLikelyText = root.findViewById(R.id.no_fish_likely_text);

        makeFishCards();

        return root;
    }

    private void refreshConditions() {
        Log.d("Conditions refresh", "Conditions refreshed");

        seasonText.setText(getString(R.string.current_season, Fish.getCurrentSeason()));
        timeOfDayText.setText(getString(R.string.current_time_of_day, Fish.getCurrentTimeOfDay()));

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

                        currentFishingLocation =  SmartAnglerOpenHelper.getCurrentFishingLocation(getContext(), currentVertex);
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

        // Check location permission and request if not granted
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

                                currentFishingLocation =  SmartAnglerOpenHelper.getCurrentFishingLocation(getContext(), currentVertex);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
