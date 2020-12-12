package com.alabama.sweethome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import static com.alabama.sweethome.CovidAPIService.POLSKA;

public class FirstFragment extends Fragment {
    private CovidAPIService covidAPIService;

    private TextView dataDate;
    private TextView casesView;
    private Button statsButton;
    private Button homeButton;

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
        statsButton = view.findViewById(R.id.stats_button);
        homeButton = view.findViewById(R.id.home_button);

        CAPIData stinkyData = covidAPIService.getDataForRegion(POLSKA, true);
        dataDate.setText(String.format("%s %s", getString(R.string.stats_placeholder), stinkyData.getDataDate()));
        String cases = getString(R.string.cases_placeholder)
                .replaceFirst("X", Integer.toString(stinkyData.getNewCases()))
                .replaceFirst("Y", Integer.toString(stinkyData.getNewDeaths()));
        casesView.setText(cases);

        statsButton.setOnClickListener(x -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

    }

}