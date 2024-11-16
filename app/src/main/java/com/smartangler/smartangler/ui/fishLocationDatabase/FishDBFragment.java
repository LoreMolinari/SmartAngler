package com.smartangler.smartangler.ui.fishLocationDatabase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartangler.smartangler.databinding.FragmentFishDatabaseBinding;

public class FishDBFragment extends Fragment {

    private FragmentFishDatabaseBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FishDBViewModel slideshowViewModel =
                new ViewModelProvider(this).get(FishDBViewModel.class);

        binding = FragmentFishDatabaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFishingDB;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}