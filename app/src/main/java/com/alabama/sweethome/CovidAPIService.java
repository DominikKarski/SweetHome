package com.alabama.sweethome;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CovidAPIService {
    private URL endpoint;
    private Context context;

    public CovidAPIService(Context context) {
        this.context = context;

        try {
            endpoint = new URL("https://www.gov.pl/web/koronawirus/wykaz-zarazen-koronawirusem-sars-cov-2");
        } catch (MalformedURLException e) {
            // This case should never happen - there is no use of handling it.
            e.printStackTrace();
        }
    }

    public CAPIData getDataForRegion(String region) {
        CAPIData data = new CAPIData();
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
                    br.close();
                } else {
                    throw new IOException("Connection could'nt be established.");
                }
            } catch (IOException e) {
                new AlertDialog.Builder(context)
                        .setTitle("Web error - check your internet connection!")
                        .setMessage("An web error ocurred!\nMessage:" + e.getMessage())
                        .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        });
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
