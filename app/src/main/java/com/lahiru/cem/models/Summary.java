package com.lahiru.cem.models;

/**
 * Created by Lahiru on 3/13/2018.
 */

public class Summary {

    private String aid;
    private String toDate;
    private String fromDate;
    private String category;
    private String inOut;
    private int count;
    private double total;
    private double average;

    public Summary(String aid, String toDate, String fromDate, String category, String inOut) {
        this.aid = aid;
        this.toDate = toDate;
        this.fromDate = fromDate;
        this.category = category;
        this.inOut = inOut;
    }

    public void setValues(int count, double total, double average) {
        this.count = count;
        this.total = total;
        this.average = average;
    }

    public String getAID() {
        return aid;
    }
    public String getToDate() {
        return toDate;
    }
    public String getFromDate() {
        return fromDate;
    }
    public String getCategory() {
        return category;
    }
    public String getInOut() {
        return inOut;
    }
    public int getCount() {
        return count;
    }
    public double getTotal() {
        return total;
    }
    public double getAverage() {
        return average;
    }

    public void setAID(String aid) {
        this.aid = aid;
    }
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setInOut(String inOut) {
        this.inOut = inOut;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public void setAverage(double average) {
        this.average = average;
    }
}
