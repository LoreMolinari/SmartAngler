package com.smartangler.smartangler.ui.statistics;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.HoverMode;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerSessionHelper;
import com.smartangler.smartangler.databinding.FragmentStatisticsBinding;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private Cartesian cartesian;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupChartToggleGroup();

        binding.stepsButton.setChecked(true);
        updateChart(R.id.stepsButton);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                updateChartBackground();
            }
        });
    }

    private void setupChartToggleGroup() {
        binding.chartToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateChart(checkedId);
            }
        });
    }

    private void updateChart(int checkedId) {
        binding.loadingBar.setVisibility(View.VISIBLE);
        binding.anyChartView.setVisibility(View.GONE);
        binding.noDataText.setVisibility(View.GONE);

        List<DataEntry> data;
        String title, xAxisTitle, yAxisTitle;

        if (checkedId == R.id.stepsButton) {
            data = createStepsData();
            title = "Steps";
            xAxisTitle = "Session";
            yAxisTitle = "Number of steps";
        } else if (checkedId == R.id.fishCaughtButton) {
            data = createFishCaughtData();
            title = "Fish Caught";
            xAxisTitle = "Session";
            yAxisTitle = "Number of fish";
        } else if (checkedId == R.id.castingButton) {
            data = createCastingData();
            title = "Casts";
            xAxisTitle = "Session";
            yAxisTitle = "Number of casts";
        } else {
            binding.loadingBar.setVisibility(View.GONE);
            return;
        }

        if (data.isEmpty()) {
            binding.noDataText.setVisibility(View.VISIBLE);
            binding.loadingBar.setVisibility(View.GONE);
            return;
        }

        if (cartesian == null) {
            cartesian = AnyChart.column();
            cartesian.background().fill(getBackgroundColor());
            binding.anyChartView.setChart(cartesian);
        }

        updateColumnChart(data, title, xAxisTitle, yAxisTitle);
        updateChartBackground();

        binding.anyChartView.setVisibility(View.VISIBLE);
        binding.loadingBar.setVisibility(View.GONE);
    }

    private List<DataEntry> createStepsData() {
        List<DataEntry> data = new ArrayList<>();
        List<Object[]> sessions = SmartAnglerSessionHelper.loadAllSessions(requireContext());

        for (int i = sessions.size() - 1; i >= 0; i--) {
            Object[] session = sessions.get(i);
            String sessionId = (String) session[0];
            int steps = (int) session[5];
            data.add(new ValueDataEntry(sessionId, steps));
        }

        return data;
    }

    private List<DataEntry> createFishCaughtData() {
        List<DataEntry> data = new ArrayList<>();
        List<Object[]> sessions = SmartAnglerSessionHelper.loadAllSessions(requireContext());

        for (int i = sessions.size() - 1; i >= 0; i--) {
            Object[] session = sessions.get(i);
            String sessionId = (String) session[0];
            int fishCaught = (int) session[4];
            data.add(new ValueDataEntry(sessionId, fishCaught));
        }

        return data;
    }

    private List<DataEntry> createCastingData() {
        List<DataEntry> data = new ArrayList<>();
        List<Object[]> sessions = SmartAnglerSessionHelper.loadAllSessions(requireContext());

        for (int i = sessions.size() - 1; i >= 0; i--) {
            Object[] session = sessions.get(i);
            String sessionId = (String) session[0];
            int casts = (int) session[6];
            data.add(new ValueDataEntry(sessionId, casts));
        }

        return data;
    }

    private void updateColumnChart(List<DataEntry> data, String title, String xAxisTitle, String yAxisTitle) {
        cartesian.data(data);

        cartesian.title()
                .text(title)
                .fontSize(18)
                .fontWeight("500");

        cartesian.xAxis(0)
                .title(xAxisTitle)
                .labels()
                .rotation(90)
                .padding(5d, 5d, 5d, 5d);

        cartesian.yAxis(0)
                .title(yAxisTitle)
                .labels()
                .format("{%Value}");

        cartesian.animation(true);
        cartesian.yScale().minimum(0);

        cartesian.tooltip()
                .titleFormat("{%X}")
                .format("{%Value}")
                .background().fill("rgba(0,0,0,0)");

        cartesian.interactivity().hoverMode(HoverMode.BY_X);
    }

    private String getBackgroundColor() {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        return String.format("#%06X", (0xFFFFFF & typedValue.data));
    }

    private void updateChartBackground() {
        if (cartesian != null) {
            cartesian.background().fill(getBackgroundColor());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}