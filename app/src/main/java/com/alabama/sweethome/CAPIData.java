package com.alabama.sweethome;

public class CAPIData {
    private String region;
    private String dataDate;
    private int newCases;
    private int newDeaths;

    public CAPIData(CAPIData capiData) {
        this.region = capiData.getRegion();
        this.dataDate = capiData.getDataDate();
        this.newCases = capiData.getNewCases();
        this.newDeaths = capiData.getNewDeaths();
    }

    public CAPIData() {
    }

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

    public synchronized String getRegion() {
        return region;
    }

    public synchronized void setRegion(String region) {
        this.region = region;
    }

}
