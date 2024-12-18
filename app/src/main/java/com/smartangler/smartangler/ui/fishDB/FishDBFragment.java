package com.smartangler.smartangler.ui.fishDB;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartangler.smartangler.Fish;
import com.smartangler.smartangler.ItemAdapter;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerOpenHelper;
import com.smartangler.smartangler.databinding.FragmentFishDbBinding;

import java.util.ArrayList;
import java.util.List;

public class FishDBFragment extends Fragment {

    private FishDBViewModel mViewModel;
    private FragmentFishDbBinding binding;
    private List<Fish> fishList;

    public static FishDBFragment newInstance() {
        return new FishDBFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFishDbBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fishList = new ArrayList<>();
        fishList = SmartAnglerOpenHelper.loadAllFish(this.getContext());

        ItemAdapter adapter = new ItemAdapter(fishList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FishDBViewModel.class);
    }

}

