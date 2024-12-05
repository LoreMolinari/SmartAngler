package com.smartangler.smartangler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
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
            holder.fishPictureImageView.setContentDescription("A picture of a perch");
        } else if (item.getName().equals("Zander")) {
            holder.fishPictureImageView.setImageResource(R.drawable.zander);
            holder.fishPictureImageView.setContentDescription("A picture of a zander");
        } else {
            holder.fishPictureImageView.setImageResource(R.drawable.generic_fish);
        }
        holder.fishNameTextView.setText(item.getName());
        holder.fishDescriptionTextView.setText(item.getDescription());

        String techniquesText = "Suggested techniques: ";
        techniquesText = techniquesText.concat(String.join(", ", item.getTechniques()));
        holder.fishTechniquesTextView.setText(techniquesText);

        String baitsAndLuresText = "Suggested baits and lures: ";
        baitsAndLuresText = baitsAndLuresText.concat(String.join(", ", item.getBaitsAndLures()));
        holder.fishBaitsAndLuresTextView.setText(baitsAndLuresText);

        // I don't love this at all
        String seasonsText = "Active seasons: ";
        List<String> seasonList = new ArrayList<>();
        for (Fish.Season season : item.getSeasons()) {
            String seasonString = season.toString();
            seasonString = seasonString.substring(0, 1).toUpperCase() +
                    seasonString.substring(1).toLowerCase();
            seasonList.add(seasonString);
        }
        seasonsText = seasonsText.concat(String.join(", ", seasonList));
        holder.fishSeasonsTextView.setText(seasonsText);

        String timesOfDayText = "Active times of day: ";
        List<String> timesOfDayList = new ArrayList<>();
        for (Fish.TimeOfDay timeOfDay : item.getTimesOfDay()) {
            String timeOfDayString = timeOfDay.toString();
            timeOfDayString = timeOfDayString.substring(0, 1).toUpperCase() +
                    timeOfDayString.substring(1).toLowerCase();
            timesOfDayList.add(timeOfDayString);
        }
        timesOfDayText = timesOfDayText.concat(String.join(", ", timesOfDayList));
        holder.fishTimesOfDayTextView.setText(timesOfDayText);
    }

    @Override
    public int getItemCount() {
        if (fishList == null) {
            return 0;
        } else {
            return fishList.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fishPictureImageView;
        TextView fishNameTextView;
        TextView fishDescriptionTextView;
        TextView fishTechniquesTextView;
        TextView fishBaitsAndLuresTextView;
        TextView fishSeasonsTextView;
        TextView fishTimesOfDayTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            fishPictureImageView = itemView.findViewById(R.id.picture_fish);
            fishNameTextView = itemView.findViewById(R.id.text_fish_name);
            fishDescriptionTextView = itemView.findViewById(R.id.text_fish_description);
            fishTechniquesTextView = itemView.findViewById(R.id.text_techniques);
            fishBaitsAndLuresTextView = itemView.findViewById(R.id.text_baits_and_lures);
            fishSeasonsTextView = itemView.findViewById(R.id.text_seasons);
            fishTimesOfDayTextView = itemView.findViewById(R.id.text_times_of_day);
        }
    }
}
