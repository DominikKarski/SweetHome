package com.alabama.sweethome;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.alabama.sweethome.CovidAPIService.LODZKIE;
import static com.alabama.sweethome.CovidAPIService.POLSKA;

public class SecondFragment extends Fragment {
    private CovidAPIService covidAPIService;
    private String region = LODZKIE;

    private TextView dataDate;
    private TextView casesWoj;
    private TextView voivodeship;
    private PieChart chart;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        covidAPIService = new CovidAPIService(getContext());
        dataDate = view.findViewById(R.id.statistics_date2);
        casesWoj = view.findViewById(R.id.cases_view_voivo);
        voivodeship = view.findViewById(R.id.voivodeship);

        CAPIData voivoData = covidAPIService.getDataForRegion(region, true);
        dataDate.setText(String.format("%s %s", getString(R.string.stats_placeholder), voivoData.getDataDate()));

        String cases = getString(R.string.cases_placeholder)
                .replaceFirst("X", Integer.toString(voivoData.getNewCases()))
                .replaceFirst("Y", Integer.toString(voivoData.getNewDeaths()));
        casesWoj.setText(cases);

        String regionStr = region.substring(0, 1).toUpperCase() + region.substring(1);
        voivodeship.setText(String.format("%s %s", getString(R.string.voivodeship), regionStr));

        chartInit(view);
        chartAddData(covidAPIService.getDataForRegion(POLSKA, true), voivoData);
    }

    private void chartInit(View view) {
        chart = view.findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setCenterText("\uD83C\uDDF5\uD83C\uDDF1");
        chart.setCenterTextSize(50f);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
    }

    private void chartAddData(CAPIData countryData, CAPIData voivoData) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(countryData.getNewCases() - voivoData.getNewCases(), "Polska"));
        String regionStr = region.substring(0, 1).toUpperCase() + region.substring(1);
        entries.add(new PieEntry(voivoData.getNewCases(), regionStr));

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);


        PieDataSet dataSet = new PieDataSet(entries, "Stosunek zarażeń");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
    }
}