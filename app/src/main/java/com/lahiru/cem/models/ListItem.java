package com.lahiru.cem.models;

/**
 * Created by Lahiru on 2/9/2018.
 */

public class ListItem {
    public static final int TRANSACTION_ITEM = 0;
    public static final int DATE_ITEM = 1;

    private int type;
    private String value;

    public ListItem(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return this.type;
    }

    public String getValue() {
        return value;
    }
}
