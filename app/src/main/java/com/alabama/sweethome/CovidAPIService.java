package com.alabama.sweethome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.alabama.sweethome.data.DBService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class CovidAPIService {
    private URL csvURL;
    private Context context;
    private List<CAPIData> allData;
    private DBService dbService;
    private Date dataDate;

    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    public CovidAPIService(Context context) {
        try {
            this.csvURL = new URL("https://arcgis.com/sharing/rest/content/items/829ec9ff36bc45a88e1245a82fff4ee0/data");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.context = context;
        this.dataDate = Calendar.getInstance().getTime();
        this.dbService = DBService.getInstance(context);
        this.allData = dbService.getAllData();
        if (allData != null) {
            try {
                dataDate = format.parse(allData.get(0).getDataDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    public List<CAPIData> getAllData() {
        final AtomicReference<Boolean> tasksDone = new AtomicReference<>(false);

        AsyncTask.execute(() -> {
            BufferedReader br = null;
            List<String[]> list = null;

            try {
                br = new BufferedReader(new InputStreamReader(csvURL.openStream(), "Cp1250"));
            } catch (IOException e) {
                showAlertDialog("Web error - check your internet connection!", "An web error occurred!\nMessage:" + e.getMessage());
            }

            try {
                CSVReader reader = new CSVReader(br);
                list = reader.readAll();
                br.close();
                reader.close();
            } catch (CsvValidationException | IOException e) {
                showAlertDialog("CSV validation error!", "Message:" + e.getMessage());
            } catch (CsvException e) {
                showAlertDialog("CSV error!", "Message: " + e.getMessage());
            }

            allData = toDataList(list);
            tasksDone.set(true);
        });

        while(!tasksDone.get()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        dbService.addData(allData);
        return allData;
    }

    public CAPIData getDataForRegion(String region) {
        if (!isDataUpToDate()) {
            getAllData();
        }
        return allData.stream().filter(x -> x.getRegion().equals(region)).findFirst().orElse(new CAPIData());
    }

    private boolean isDataUpToDate() {
        return format.format(Calendar.getInstance().getTime()).equals(format.format(dataDate));
    }

    private List<CAPIData> toDataList(List<String[]> list) {
        List<CAPIData> dataList = new ArrayList<>();

        for (int i = 1; i < list.size(); i++) {
            String tmp = list.get(i)[0];
            String[] dataArray = separateSemicolons(tmp);

            CAPIData data = new CAPIData();
            data.setRegion(dataArray[0]);
            data.setNewCases(Integer.parseInt(dataArray[1]));
            data.setNewDeaths(Integer.parseInt(dataArray[3]));
            data.setDataDate(format.format(Calendar.getInstance().getTime()));
            dataList.add(data);
        }
        return dataList;
    }

    private String[] separateSemicolons(String string) {
        return string.split(";");
    }

    private void showAlertDialog(String title, String message) {
        ((Activity)context).runOnUiThread(() -> new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                .create().show());
    }

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

}
