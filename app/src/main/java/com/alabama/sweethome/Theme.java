package com.alabama.sweethome;

import android.content.Context;
import android.graphics.Color;

import com.alabama.sweethome.data.DBService;

public class Theme {

    private DBService dbService;
    private String theme;



    public Theme(Context context) {
        this.dbService = DBService.getInstance(context);
        int holder = dbService.getTheme();
        switch (holder){
            case 0:
                theme = "LightTheme";
                break;
            case 1:
                theme = "DarkTheme";
                break;
        }

    }

    public String getTheme() {
        int holder = dbService.getTheme();
        switch (holder){
            case 0:
                theme = "LightTheme";
                break;
            case 1:
                theme = "DarkTheme";
                break;
        }

        return theme;
    }

    public  void setTheme(String theme) {
        switch (theme){
            case "LightTheme":
                dbService.saveTheme(0);
                break;
            case "DarkTheme":
                dbService.saveTheme(1);
                break;
        }
    }
}
