package com.smartangler.smartangler.ui.fishing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartangler.smartangler.R;

import java.util.ArrayList;
import java.util.List;

public class FishEntryAdapter extends RecyclerView.Adapter<FishEntryAdapter.FishEntryViewHolder> {

    private final List<FishEntry> fishEntries;
    private List<FishEntry> filteredFishEntries;
    private int selectedIndex = -1;

    public FishEntryAdapter(List<FishEntry> fishEntries) {
        this.fishEntries = fishEntries;
        this.filteredFishEntries = new ArrayList<>(fishEntries);
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
        filterEntries(index);
    }

    public void filterEntries(int index) {
        if (index == -1) {
            filteredFishEntries = new ArrayList<>(fishEntries);
        } else {
            filteredFishEntries.clear();
            filteredFishEntries.add(fishEntries.get(index));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FishEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_photo_fish, parent, false);
        return new FishEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FishEntryViewHolder holder, int position) {
        FishEntry fishEntry = filteredFishEntries.get(position);
        holder.title.setText(fishEntry.getName());
        holder.date.setText(fishEntry.getDate());
        holder.imageView.setImageBitmap(fishEntry.getImage());
    }

    @Override
    public int getItemCount() {
        return filteredFishEntries.size();
    }

    public static class FishEntryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView date;

        public FishEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
        }
    }
}