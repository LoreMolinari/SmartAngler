package com.smartangler.smartangler.ui.fishing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartangler.smartangler.R;

import java.util.List;

public class FishEntryAdapter extends RecyclerView.Adapter<FishEntryAdapter.ViewHolder> {

    private final List<FishEntry> fishEntries;

    public FishEntryAdapter(List<FishEntry> fishEntries) {
        this.fishEntries = fishEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_photo_fish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FishEntry entry = fishEntries.get(position);
        holder.picture.setImageBitmap(entry.getImage());
        holder.name.setText(entry.getName());
        holder.description.setText(entry.getDescription());
        holder.date.setText(entry.getDate());
    }

    @Override
    public int getItemCount() {
        return fishEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name, description, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picture_fish);
            name = itemView.findViewById(R.id.text_fish_name);
            description = itemView.findViewById(R.id.text_fish_description);
            date = itemView.findViewById(R.id.text_times_of_day);
        }
    }
}