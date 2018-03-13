package com.lahiru.cem.models;

/**
 * Created by Lahiru on 2/9/2018.
 */

public class Transaction {

    private String tid;
    private String amount;
    private String date;
    private String day;
    private String inOut;
    private String category;
    private String note;
    private String dueDate;
    private String partner;
    private String lend_tid;

    public Transaction(String tid, String amount, String date, String day, String inOut, String category, String note) {
        this.amount = amount;
        this.date = date;
        this.day = day;
        this.inOut = inOut;
        this.category = category;
        this.note = note;
        this.tid = tid;
    }

    public void setLendingDetails(String partner, String dueDate) {
        this.partner = partner;
        this.dueDate = dueDate;
    }

    public void setRepaymentDetails(String lend_tid) {
        this.lend_tid = lend_tid;
    }

    public String getTID() {
        return tid;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getInOut() {
        return inOut;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPartner() {
        return partner;
    }

    public String getLendTID() {
        return lend_tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
