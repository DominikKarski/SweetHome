package com.alabama.sweethome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.alabama.sweethome.data.DBService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class CovidAPIService {
    private URL endpoint;
    private Context context;
    private List<CAPIData> allData;
    private DBService dbService;

    private final AtomicReference<Boolean> tasksDone = new AtomicReference<>(false);

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

    private static final List<String> regions = new ArrayList<>(
            Arrays.asList(
                    POLSKA, DOLNOSLASKIE, KUJAWSKO_POMORSKIE, LUBELSKIE, LUBUSKIE, LODZKIE, LODZKIE,
                    MALOPOLSKIE, MAZOWIECKIE, OPOLSKIE, PODKARPACKIE, PODLASKIE, POMORSKIE, SLASKIE,
                    SWIETOKRZYSKIE, WARMINSKOMAZURSKIE, WIELKOPOLSKIE, ZACHODNIOPOMORSKIE));

    public CovidAPIService(Context context) {
        this.context = context;
        this.dbService = DBService.getInstance(context);

        this.allData = dbService.getAllData();

        try {
            endpoint = new URL("https://www.gov.pl/web/koronawirus/wykaz-zarazen-koronawirusem-sars-cov-2");
        } catch (MalformedURLException e) {
            // This case should never happen - there is no use of handling it.
            e.printStackTrace();
        }
    }

    public List<CAPIData> getAllData() {
        List<CAPIData> data = new ArrayList<>(regions.size());
        regions.forEach(region -> {
            CAPIData cdata = new CAPIData();
            cdata.setRegion(region);
            data.add(cdata);
        });

        tasksDone.set(false);
        AsyncTask.execute(() -> {
            try {
                HttpsURLConnection conn = (HttpsURLConnection) endpoint.openConnection();
                conn.setRequestProperty("User-Agent", "sweet-home-alabama-app");

                if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while((line = br.readLine()) != null) {
                        if(line.contains("aktualne na :")) {
                            String date = line.substring(55, 65);
                            data.forEach(x -> x.setDataDate(date));
                        } else if(line.contains("id=\"registerData\"")) {
                            for (CAPIData d : data) {
                                String region = d.getRegion();
                                int pos = line.indexOf(region) + region.length() + 1;

                                String cases = followTheSemiColon(pos, line);
                                d.setNewCases(Integer.parseInt(cases));

                                int newPos = pos + cases.length() + 1;
                                String casesPerHundred = followTheSemiColon(newPos, line);

                                newPos += casesPerHundred.length() + 1;
                                String allDeaths = followTheSemiColon(newPos, line);

                                newPos += allDeaths.length() + 1;
                                String covidDeaths = followTheSemiColon(newPos, line);
                                d.setNewDeaths(Integer.parseInt(covidDeaths));
                            }
                        }
                    }
                    tasksDone.set(true);
                    br.close();
                } else {
                    throw new IOException("Connection could'nt be established.");
                }
            } catch (IOException e) {
                tasksDone.set(true);
                ((Activity)context).runOnUiThread(() -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Web error - check your internet connection!")
                            .setMessage("An web error ocurred!\nMessage:" + e.getMessage())
                            .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                            .create().show();
                });
            }
        });

        while(!tasksDone.get()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        allData = new ArrayList<>();
        data.forEach(x -> allData.add(new CAPIData(x)));
        dbService.addData(data);
        return data;
    }

    public CAPIData getDataForRegion(String region) {
        List<CAPIData> data;
        if(!isDataUpToDate()) {
            data = getAllData();
        } else {
            data = new ArrayList<>();
            allData.forEach(x -> data.add(new CAPIData(x)));
        }

        return data.stream().filter(x -> x.getRegion().equals(region)).findFirst().orElse(new CAPIData());
    }

    private boolean isDataUpToDate() {
        if(allData.size() > 0) {
            try {
                Date date = new SimpleDateFormat("dd.MM.yyyy").parse(allData.get(0).getDataDate());
                Date now = new Date();
                if (date.getYear() == now.getYear() && date.getMonth() == now.getMonth() && date.getDay() == now.getDay()) {
                    return true;
                }
            } catch (ParseException ignored) { }
        }
        return false;
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
