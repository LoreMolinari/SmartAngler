package com.smartangler.smartangler.ui.fishing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartangler.smartangler.databinding.FragmentFishingBinding;

public class FishingFragment extends Fragment {

    private FragmentFishingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FishingViewModel slideshowViewModel =
                new ViewModelProvider(this).get(FishingViewModel.class);

        binding = FragmentFishingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFishing;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}