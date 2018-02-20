package com.lahiru.cem.models;

/**
 * Created by Lahiru on 2/18/2018.
 */

public class Account {

    private String aid;
    private String accountName;
    private String email;
    private String pin;

    public Account(String aid, String accountName, String email, String pin) {
        this.aid = aid;
        this.accountName = accountName;
        this.email = email;
        this.pin = pin;
    }

    public String getAid() {
        return aid;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getEmail() {
        return email;
    }

    public String getPin() {
        return pin;
    }
}
