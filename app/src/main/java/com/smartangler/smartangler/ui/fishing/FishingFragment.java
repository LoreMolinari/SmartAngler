package com.smartangler.smartangler.ui.fishing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smartangler.smartangler.databinding.FragmentFishingBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FishingFragment extends Fragment {

    private FragmentFishingBinding binding;
    private final List<FishEntry> fishEntries = new ArrayList<>();
    private FishEntryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFishingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configura RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FishEntryAdapter(fishEntries);
        binding.recyclerView.setAdapter(adapter);

        // Configura pulsanti
        binding.buttonTakePicture.setOnClickListener(v -> openCamera());

        return root;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");

                    addNewFishEntry(photo, "Nome Pesce", "Descrizione Pesce");
                }
            });

    private void addNewFishEntry(Bitmap image, String name, String description) {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        fishEntries.add(new FishEntry(image, name, description, date));
        adapter.notifyItemInserted(fishEntries.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
