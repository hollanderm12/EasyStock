package com.mtlcollege.quickstocks.model;

public class Results {

    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private double exDividend;
    private double splitRatio;
    private double adjFactor;
    private double adjOpen;
    private double adjHigh;
    private double adjLow;
    private double adjClose;
    private long adjVolume;
    private boolean[] flags;

    public String getDate() {
        return date;
    }

    public void setDate(String date) { this.date = date; }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
        flags[0] = true;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
        flags[1] = true;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
        flags[2] = true;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
        flags[3] = true;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
        flags[4] = true;
    }

    public double getExDividend() {
        return exDividend;
    }

    public void setExDividend(double exDividend) {
        this.exDividend = exDividend;
        flags[5] = true;
    }

    public double getSplitRatio() {
        return splitRatio;
    }

    public void setSplitRatio(double splitRatio) {
        this.splitRatio = splitRatio;
        flags[6] = true;
    }

    public double getAdjFactor() {
        return adjFactor;
    }

    public void setAdjFactor(double adjFactor) {
        this.adjFactor = adjFactor;
        flags[7] = true;
    }

    public double getAdjOpen() {
        return adjOpen;
    }

    public void setAdjOpen(double adjOpen) {
        this.adjOpen = adjOpen;
        flags[8] = true;
    }

    public double getAdjHigh() {
        return adjHigh;
    }

    public void setAdjHigh(double adjHigh) {
        this.adjHigh = adjHigh;
        flags[9] = true;
    }

    public double getAdjLow() {
        return adjLow;
    }

    public void setAdjLow(double adjLow) {
        this.adjLow = adjLow;
        flags[10] = true;
    }

    public double getAdjClose() { return adjClose; }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
        flags[11] = true;
    }

    public long getAdjVolume() { return adjVolume; }

    public void setAdjVolume(long adjVolume) {
        this.adjVolume = adjVolume;
        flags[12] = true;
    }

    public boolean[] getFlags() { return flags; }

    public Results() {
        flags = new boolean[13];
    }
}
