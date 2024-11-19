package com.smartangler.smartangler.ui.fishDB;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartangler.smartangler.Fish;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerOpenHelper;
import com.smartangler.smartangler.databinding.FragmentFishDbBinding;

import java.util.ArrayList;
import java.util.List;

public class FishDBFragment extends Fragment {

    private FishDBViewModel mViewModel;
    private FragmentFishDbBinding binding;
    private List<Fish> fishList;
    private TextView textFishDB;

    public static FishDBFragment newInstance() {
        return new FishDBFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFishDbBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SmartAnglerOpenHelper databaseOpenHelper = new SmartAnglerOpenHelper(this.getContext());

        fishList = new ArrayList<>();
        fishList = SmartAnglerOpenHelper.loadAllFish(this.getContext());
        String fishString = new String();

        for (Fish fish : fishList) {
            fishString = fishString.concat(fish.toString());
        }

        textFishDB = (TextView) root.findViewById(R.id.text_fishDB);
        textFishDB.setText(fishString);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FishDBViewModel.class);
        // TODO: Use the ViewModel
    }

}