package com.alabama.sweethome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.alabama.sweethome.CovidAPIService.DOLNOSLASKIE;
import static com.alabama.sweethome.CovidAPIService.KUJAWSKO_POMORSKIE;
import static com.alabama.sweethome.CovidAPIService.LODZKIE;
import static com.alabama.sweethome.CovidAPIService.LUBELSKIE;
import static com.alabama.sweethome.CovidAPIService.LUBUSKIE;
import static com.alabama.sweethome.CovidAPIService.MALOPOLSKIE;
import static com.alabama.sweethome.CovidAPIService.MAZOWIECKIE;
import static com.alabama.sweethome.CovidAPIService.OPOLSKIE;
import static com.alabama.sweethome.CovidAPIService.PODKARPACKIE;
import static com.alabama.sweethome.CovidAPIService.PODLASKIE;
import static com.alabama.sweethome.CovidAPIService.POLSKA;
import static com.alabama.sweethome.CovidAPIService.POMORSKIE;
import static com.alabama.sweethome.CovidAPIService.SLASKIE;
import static com.alabama.sweethome.CovidAPIService.SWIETOKRZYSKIE;
import static com.alabama.sweethome.CovidAPIService.WARMINSKOMAZURSKIE;
import static com.alabama.sweethome.CovidAPIService.WIELKOPOLSKIE;
import static com.alabama.sweethome.CovidAPIService.ZACHODNIOPOMORSKIE;
import static com.alabama.sweethome.Utils.capitalizeFirstLetter;

public class VoivodeshipAdapter extends ArrayAdapter<String> {
    private List<String> voivodeships;

    private VoivodeshipAdapter(@NonNull Context context, List<String> voivodeships) {
        super(context,0, voivodeships);
        this.voivodeships = voivodeships;
    }

    public static VoivodeshipAdapter getNew(Context context) {
        ArrayList<String> list = new ArrayList<>();
        list.add(capitalizeFirstLetter(POLSKA));
        list.add(capitalizeFirstLetter(DOLNOSLASKIE));
        list.add(capitalizeFirstLetter(KUJAWSKO_POMORSKIE));
        list.add(capitalizeFirstLetter(LUBELSKIE));
        list.add(capitalizeFirstLetter(LUBUSKIE));
        list.add(capitalizeFirstLetter(LODZKIE));
        list.add(capitalizeFirstLetter(MALOPOLSKIE));
        list.add(capitalizeFirstLetter(MAZOWIECKIE));
        list.add(capitalizeFirstLetter(OPOLSKIE));
        list.add(capitalizeFirstLetter(PODKARPACKIE));
        list.add(capitalizeFirstLetter(PODLASKIE));
        list.add(capitalizeFirstLetter(POMORSKIE));
        list.add(capitalizeFirstLetter(SLASKIE));
        list.add(capitalizeFirstLetter(SWIETOKRZYSKIE));
        list.add(capitalizeFirstLetter(WARMINSKOMAZURSKIE));
        list.add(capitalizeFirstLetter(WIELKOPOLSKIE));
        list.add(capitalizeFirstLetter(ZACHODNIOPOMORSKIE));

        return new VoivodeshipAdapter(context, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_element, parent, false);
        }

        String currentVoivo = getItem(position);
        TextView videoText = (TextView) listItemView.findViewById(R.id.voivo_text);
        videoText.setText(currentVoivo);

        return listItemView;
    }
}
