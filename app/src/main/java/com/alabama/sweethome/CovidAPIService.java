package com.alabama.sweethome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class CovidAPIService {
    private URL endpoint;
    private Context context;

    public static final String POLSKA = "Cały kraj";
    public static final String DOLNOSLASKIE = "dolnośląskie";
    public static final String KUJAWSKO_POMORSKIE = "kujawsko-pomorskie";
    public static final String LUBELSKIE = "lubelskie";
    public static final String LUBUSKIE = "lubuskie";
    public static final String LODZKIE = "łódzkie";
    public static final String MALOPOLSKIE = "małopolskie";
    public static final String MAZOWIECKIE = "mazowieckie";
    public static final String OPOLSKIE = "opolskie";
    public static final String PODKARPACKIE = "podkarpackie";
    public static final String PODLASKIE = "podlaskie";
    public static final String POMORSKIE = "pomorskie";
    public static final String SLASKIE = "śląskie";
    public static final String SWIETOKRZYSKIE = "świętokrzyskie";
    public static final String WARMINSKOMAZURSKIE = "warmińsko-mazurskie";
    public static final String WIELKOPOLSKIE = "wielkopolskie";
    public static final String ZACHODNIOPOMORSKIE = "zachodniopomorskie";

    public CovidAPIService(Context context) {
        this.context = context;

        try {
            endpoint = new URL("https://www.gov.pl/web/koronawirus/wykaz-zarazen-koronawirusem-sars-cov-2");
        } catch (MalformedURLException e) {
            // This case should never happen - there is no use of handling it.
            e.printStackTrace();
        }
    }

    public CAPIData getDataForRegion(String region, boolean wait) {
        CAPIData data = new CAPIData();
        AtomicReference<Boolean> done = new AtomicReference<>(false);
        AsyncTask.execute(() -> {
            try {
                HttpsURLConnection conn = (HttpsURLConnection) endpoint.openConnection();
                conn.setRequestProperty("User-Agent", "sweet-home-alabama-app");

                if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while((line = br.readLine()) != null) {
                        if(line.contains("aktualne na :")) {
                            data.setDataDate(line.substring(55, 65));
                        } else if(line.contains("id=\"registerData\"")) {
                            int pos = line.indexOf(region) + region.length() + 1;

                            String cases = followTheSemiColon(pos, line);
                            data.setNewCases(Integer.parseInt(cases));

                            int newPos = pos + cases.length() + 1;
                            String casesPerHundred = followTheSemiColon(newPos, line);

                            newPos += casesPerHundred.length() + 1;
                            String allDeaths = followTheSemiColon(newPos, line);

                            newPos += allDeaths.length() + 1;
                            String covidDeaths = followTheSemiColon(newPos, line);
                            data.setNewDeaths(Integer.parseInt(covidDeaths));
                        }
                    }
                    done.set(true);
                    br.close();
                } else {
                    throw new IOException("Connection could'nt be established.");
                }
            } catch (IOException e) {
                done.set(true);
                ((Activity)context).runOnUiThread(() -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Web error - check your internet connection!")
                            .setMessage("An web error ocurred!\nMessage:" + e.getMessage())
                            .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                            .create().show();
                });
            }
        });

        if(wait) {
            while(!done.get()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    /**
     * Returns the string between pos and first occurrence of ';' character.
     * @param pos starting position
     * @param line string with ';'
     * @return the string between pos and first occurrence of ';' character.
     */
    private String followTheSemiColon(int pos, String line) {
        StringBuilder casesAsStr = new StringBuilder();
        int temp_pos = pos;
        while(line.charAt(temp_pos) != ';') {
            casesAsStr.append(line.charAt(temp_pos));
            temp_pos++;
        }
        return casesAsStr.toString();
    }
}
