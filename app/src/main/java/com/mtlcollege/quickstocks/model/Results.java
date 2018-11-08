package com.mtlcollege.quickstocks.model;

import android.os.Parcel;
import android.os.Parcelable;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getExDividend() {
        return exDividend;
    }

    public void setExDividend(double exDividend) {
        this.exDividend = exDividend;
    }

    public double getSplitRatio() {
        return splitRatio;
    }

    public void setSplitRatio(double splitRatio) {
        this.splitRatio = splitRatio;
    }

    public double getAdjFactor() {
        return adjFactor;
    }

    public void setAdjFactor(double adjFactor) {
        this.adjFactor = adjFactor;
    }

    public double getAdjOpen() {
        return adjOpen;
    }

    public void setAdjOpen(double adjOpen) {
        this.adjOpen = adjOpen;
    }

    public double getAdjHigh() {
        return adjHigh;
    }

    public void setAdjHigh(double adjHigh) {
        this.adjHigh = adjHigh;
    }

    public double getAdjLow() {
        return adjLow;
    }

    public void setAdjLow(double adjLow) {
        this.adjLow = adjLow;
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }

    public long getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(long adjVolume) {
        this.adjVolume = adjVolume;
    }

    public Results() {
    }
}
