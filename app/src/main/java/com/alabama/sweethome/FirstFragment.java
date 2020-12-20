package com.alabama.sweethome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import static com.alabama.sweethome.CovidAPIService.POLSKA;

public class FirstFragment extends Fragment {
    private CovidAPIService covidAPIService;
    private GPSController gpsController;

    private TextView dataDate;
    private TextView casesView;
    private Button statsButton;
    private Button homeButton;
    private ImageView housePic;

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
        housePic = view.findViewById(R.id.house_pic);
        gpsController = new GPSController(view.getContext());

        handleHomeImageColor();

        CAPIData stinkyData = covidAPIService.getDataForRegion(POLSKA);
        dataDate.setText(String.format("%s %s", getString(R.string.stats_placeholder), stinkyData.getDataDate()));
        String cases = getString(R.string.cases_placeholder)
                .replaceFirst("X", Integer.toString(stinkyData.getNewCases()))
                .replaceFirst("Y", Integer.toString(stinkyData.getNewDeaths()));
        casesView.setText(cases);

        statsButton.setOnClickListener(x -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gpsController.isHomeSet()) {
                    if(!gpsController.setHome()) {
                        Toast.makeText(v.getContext(), "Nie udało się odczytać lokalizacji. Spróbuj jeszcze raz!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Confirm your action")
                            .setMessage("Do you want to remove home?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    gpsController.removeHome();
                                    handleHomeImageColor();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
                handleHomeImageColor();
            }
        });

    }

    private void handleHomeImageColor() {
        if (gpsController.isHomeSet()) {
            housePic.setColorFilter(getContext().getResources().getColor(R.color.green));
        } else {
            housePic.setColorFilter(getContext().getResources().getColor(R.color.house_icon));
        }

    }

}