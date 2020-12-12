package com.alabama.sweethome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.alabama.sweethome.CovidAPIService.LODZKIE;

public class SecondFragment extends Fragment {
    private CovidAPIService covidAPIService;
    private String region = LODZKIE;

    private TextView dataDate;
    private TextView casesWoj;
    private TextView voivodeship;

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

        CAPIData stinkyData = covidAPIService.getDataForRegion(region, true);
        dataDate.setText(String.format("%s %s", getString(R.string.stats_placeholder), stinkyData.getDataDate()));

        String cases = getString(R.string.cases_placeholder)
                .replaceFirst("X", Integer.toString(stinkyData.getNewCases()))
                .replaceFirst("Y", Integer.toString(stinkyData.getNewDeaths()));
        casesWoj.setText(cases);

        String regionStr = region.substring(0, 1).toUpperCase() + region.substring(1);
        voivodeship.setText(String.format("%s %s", getString(R.string.voivodeship), regionStr));
    }
}