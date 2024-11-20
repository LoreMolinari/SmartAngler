package com.smartangler.smartangler.ui.fishDB;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        SmartAnglerOpenHelper databaseOpenHelper = new SmartAnglerOpenHelper(this.getContext());

        fishList = new ArrayList<>();
        fishList = SmartAnglerOpenHelper.loadAllFish(this.getContext());
        String fishString = new String();

        for (Fish fish : fishList) {
            fishString = fishString.concat(fish.toString());
        }

        ItemAdapter adapter = new ItemAdapter(fishList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FishDBViewModel.class);
        // TODO: Use the ViewModel
    }

}

class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    private List<Fish> fishList;

    public ItemAdapter(List<Fish> fishList) {
        this.fishList = fishList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_fish_db, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fish item = fishList.get(position);
        if (item.getName().equals("Perch")) {
            holder.fishPictureImageView.setImageResource(R.drawable.perch);
        } else if (item.getName().equals("Zander")) {
            holder.fishPictureImageView.setImageResource(R.drawable.zander);
        }
        holder.fishNameTextView.setText(item.getName());
        holder.fishDescriptionTextView.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fishPictureImageView;
        TextView fishNameTextView;
        TextView fishDescriptionTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            fishPictureImageView = itemView.findViewById(R.id.picture_fish);
            fishNameTextView = itemView.findViewById(R.id.text_fish_name);
            fishDescriptionTextView = itemView.findViewById(R.id.text_fish_description);
        }
    }
}