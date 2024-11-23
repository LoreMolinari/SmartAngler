package com.smartangler.smartangler.ui.sessions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.smartangler.smartangler.R;

import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private List<Object[]> sessions = new ArrayList<>();

    public void setSessions(List<Object[]> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Object[] session = sessions.get(position);

        String sessionId = (String) session[0];
        String date = (String) session[1];
        String location = (String) session[2];
        Integer duration = (Integer) session[3];
        Integer fishCaught = (Integer) session[4];

        holder.sessionIdTextView.setText("Session ID: " + sessionId);
        holder.dateTextView.setText("Date: " + date);
        holder.locationTextView.setText("Location: " + location);
        holder.durationTextView.setText("Duration: " + duration);
        holder.fishCaughtTextView.setText("Fish Caught: " + fishCaught);

        holder.itemView.setOnClickListener(v -> {
            // eventually click for photos
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    private String formatDuration(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView sessionIdTextView, dateTextView, detailsTextView, locationTextView, durationTextView, fishCaughtTextView;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionIdTextView = itemView.findViewById(R.id.session_id_text);
            dateTextView = itemView.findViewById(R.id.date_text);
            detailsTextView = itemView.findViewById(R.id.details_text);
            locationTextView = itemView.findViewById(R.id.location_text);
            durationTextView = itemView.findViewById(R.id.duration_text);
            fishCaughtTextView = itemView.findViewById(R.id.fishCaught_text);
        }
    }
}
