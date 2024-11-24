package com.smartangler.smartangler.ui.sessions;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smartangler.smartangler.SmartAnglerSessionHelper;
import com.smartangler.smartangler.databinding.FragmentSessionsBinding;
import android.util.Log;

import java.util.List;

public class SessionFragment extends Fragment {

    private FragmentSessionsBinding binding;
    private SessionAdapter sessionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSessionsBinding.inflate(inflater, container, false);

        setupRecyclerView();
        loadStatistics();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewSessions.setLayoutManager(new LinearLayoutManager(requireContext()));
        sessionAdapter = new SessionAdapter();
        binding.recyclerViewSessions.setAdapter(sessionAdapter);
    }

    private void loadStatistics() {
        List<Object[]> sessions = SmartAnglerSessionHelper.loadAllSessions(requireContext());

        int totalFishCaught = 0;
        int totalDuration = 0;
        int totalStep =0;

        for (Object[] session : sessions) {
            if (session.length >= 5) {
                totalFishCaught += (int) session[4];
                totalDuration += (int) session[3];
                totalStep += (int) session[5];
            }
        }

        binding.totalSessions.setText("Total Sessions: " + sessions.size());
        binding.totalFishCaught.setText("Total Fish Caught: " + totalFishCaught);
        binding.totalTime.setText("Total Duration: " + formatTime(totalDuration));
        binding.totalSteps.setText("Total Steps: " + totalStep);

        sessionAdapter.setSessions(sessions);
    }

    private String formatTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
