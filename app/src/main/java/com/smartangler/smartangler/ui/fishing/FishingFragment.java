package com.smartangler.smartangler.ui.fishing;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smartangler.smartangler.SmartAnglerPhotoHelper;
import com.smartangler.smartangler.databinding.FragmentFishingBinding;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFishingBinding.inflate(inflater, container, false);

        binding.buttonTakePicture.setOnClickListener(v -> checkCameraPermission());

        fishEntries = new ArrayList<>();
        adapter = new FishEntryAdapter(fishEntries);
        binding.recyclerViewFish.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFish.setAdapter(adapter);

        loadFishEntries();

        return binding.getRoot();
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
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveFishEntry(imageBitmap);
        }
    }

    private void saveFishEntry(Bitmap imageBitmap) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String title = "Fish " + (fishEntries.size() + 1);
        String description = "Caught on " + currentDate;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        SmartAnglerPhotoHelper.addPhoto(requireContext(), title, description, byteArray, currentDate, "Unknown");

        FishEntry newEntry = new FishEntry(imageBitmap, title, description, currentDate);
        fishEntries.add(0, newEntry);
        adapter.notifyItemInserted(0);
        binding.recyclerViewFish.scrollToPosition(0);
    }

    private void loadFishEntries() {
        List<Object[]> photos = SmartAnglerPhotoHelper.loadAllPhotos(requireContext());
        for (Object[] photo : photos) {
            byte[] imageData = (byte[]) photo[3];
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            FishEntry entry = new FishEntry(bitmap, (String) photo[1], (String) photo[2], (String) photo[4]);
            fishEntries.add(entry);
        }
        adapter.notifyDataSetChanged();
    }
}