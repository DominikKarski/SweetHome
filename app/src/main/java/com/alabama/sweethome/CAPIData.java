package com.alabama.sweethome;

public class CAPIData {
    private String dataDate;
    private int newCases;
    private int newDeaths;

    public synchronized String getDataDate() {
        return dataDate;
    }

    public synchronized void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public synchronized int getNewCases() {
        return newCases;
    }

    public synchronized void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    public synchronized int getNewDeaths() {
        return newDeaths;
    }

    public synchronized void setNewDeaths(int newDeaths) {
        this.newDeaths = newDeaths;
    }
}
