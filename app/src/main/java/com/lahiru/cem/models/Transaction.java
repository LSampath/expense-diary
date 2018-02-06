package com.lahiru.cem.models;

/**
 * Created by Lahiru on 1/17/2018.
 */

public class Transaction {

    private String heading;
    private String description;

    public Transaction(String heading, String description) {
        this.heading = heading;
        this.description = description;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }
}
