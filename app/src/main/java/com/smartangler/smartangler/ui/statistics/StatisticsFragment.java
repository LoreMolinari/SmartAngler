package com.smartangler.smartangler.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipPositionMode;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerOpenHelper;
import com.smartangler.smartangler.SmartAnglerSessionHelper;
import com.smartangler.smartangler.databinding.FragmentStatisticsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private Date cDate = new Date();
    private String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
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
        setupInitialChart();
    }

    private void setupChartToggleGroup() {
        binding.chartToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateChart(checkedId);
            }
        });
    }

    private void setupInitialChart() {
        binding.chartToggleGroup.check(R.id.stepsButton);
        updateChart(R.id.stepsButton);
    }

    private void updateChart(int checkedId) {
        binding.loadingBar.setVisibility(View.VISIBLE);
        binding.anyChartView.setVisibility(View.GONE);

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
            title = "Castings";
            xAxisTitle = "Session";
            yAxisTitle = "Number of castings";
        } else {
            binding.loadingBar.setVisibility(View.GONE);
            return;
        }

        if (cartesian == null) {
            cartesian = AnyChart.column();
            binding.anyChartView.setChart(cartesian);
        }

        updateColumnChart(data, title, xAxisTitle, yAxisTitle);

        binding.anyChartView.setVisibility(View.VISIBLE);
        binding.loadingBar.setVisibility(View.GONE);
    }

    private List<DataEntry> createStepsData() {
        Map<Integer, Integer> stepsByHour = SmartAnglerOpenHelper.loadStepsByHour(getContext(), current_time);
        List<DataEntry> data = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            int steps = stepsByHour.containsKey(i) ? stepsByHour.get(i) : 0;
            data.add(new ValueDataEntry(i, steps));
        }

        return data;
    }

    private List<DataEntry> createFishCaughtData() {
        List<DataEntry> data = new ArrayList<>();
        List<Object[]> sessions = SmartAnglerSessionHelper.loadAllSessions(requireContext());

        int totalFishCaught = 0;
        int i=0;

        for (Object[] session : sessions) {
            totalFishCaught = (int) session[4];
            data.add(new ValueDataEntry(i, totalFishCaught));
            i++;
        }

        return data;
    }

    private List<DataEntry> createCastingData() {
        //TODO Now random import from new database
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            data.add(new ValueDataEntry(i, (int) (Math.random() * 20)));
        }
        return data;
    }

    private void updateColumnChart(List<DataEntry> data, String title, String xAxisTitle, String yAxisTitle) {
        cartesian.data(data);
        cartesian.title(title);
        cartesian.xAxis(0).title(xAxisTitle);
        cartesian.yAxis(0).title(yAxisTitle);

        cartesian.animation(true);
        cartesian.yScale().minimum(0);
        cartesian.yAxis(0).labels().format("{%Value}");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.background().fill(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(requireContext(), R.color.light_md_theme_background))));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}