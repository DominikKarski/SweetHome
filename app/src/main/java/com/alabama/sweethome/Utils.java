package com.alabama.sweethome;

public class Utils {
    static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    static String decapitalizeFirtLetter(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
