package com.alabama.sweethome;

import android.app.Activity;
import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

public  class Utils {

    static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    static String decapitalizeFirtLetter(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }



    static void changeColorTheme(Activity context){
        ConstraintLayout x =  context.findViewById(R.id.main_view_constraint);
    }
}
