package com.alabama.sweethome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.alabama.sweethome.CovidAPIService.POLSKA;

public class FirstFragment extends Fragment {
    private CovidAPIService covidAPIService;

    private TextView dataDate;
    private TextView casesView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        covidAPIService = new CovidAPIService(this.getContext());
        dataDate = view.findViewById(R.id.statistics_date);
        casesView = view.findViewById(R.id.cases_view);

        CAPIData stinkyData = covidAPIService.getDataForRegion(POLSKA, true);
        dataDate.setText(String.format("%s %s", getString(R.string.stats_placeholder), stinkyData.getDataDate()));
        String cases = getString(R.string.cases_placeholder)
                .replaceFirst("X", Integer.toString(stinkyData.getNewCases()))
                .replaceFirst("Y", Integer.toString(stinkyData.getNewDeaths()));
        casesView.setText(cases);

    }

}