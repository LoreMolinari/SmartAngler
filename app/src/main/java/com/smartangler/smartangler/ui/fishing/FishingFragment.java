package com.smartangler.smartangler.ui.fishing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartangler.smartangler.SmartAnglerPhotoHelper;
import com.smartangler.smartangler.databinding.FragmentFishingBinding;

import java.util.List;

public class FishingFragment extends Fragment {

    private FragmentFishingBinding binding;
    private SmartAnglerPhotoHelper databaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        databaseHelper = new SmartAnglerPhotoHelper(getActivity());

        binding = FragmentFishingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}