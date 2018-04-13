package com.lahiru.cem.models;

import android.util.Log;

import com.lahiru.cem.R;

/**
 * Created by Lahiru on 2/28/2018.
 */

public class AppData {

    private String[] outflowNameList = {
            "Food & Beverage", "Bills & Fees", "Shopping", "Fun & Love", "Travel", "Health", "Family", "Education", "Business",
            "Other", "Loan", "Debt Repayment"
    };
    private int[] outflowIconList = {
            R.drawable.icn_food, R.drawable.icn_bills,R.drawable.icn_shopping, R.drawable.icn_love, R.drawable.icn_travel,
            R.drawable.icn_health, R.drawable.icn_family, R.drawable.icn_education, R.drawable.icn_business,
            R.drawable.icn_other, R.drawable.icn_loan, R.drawable.icn_replay
    };
    private String[] inflowNameList = {
            "Salary", "Business", "Interest", "Other", "Debt", "Loan Collection"
    };
    private int[] inflowIconList = {
            R.drawable.icn_salary, R.drawable.icn_business, R.drawable.icn_interest, R.drawable.icn_other,
            R.drawable.icn_debt, R.drawable.icn_replay
    };
    private int[] colors = {
            R.color.c1, R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6,
            R.color.c7, R.color.c8, R.color.c9, R.color.c10, R.color.c11, R.color.c12
    };

    private static AppData instance;
    private Account account;

    public static synchronized AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public void setAccount(Account acc) {
        this.account = acc;
    }

    public Account getAccount() {
        return account;
    }

    public String[] getOutflowNameList() {
        return outflowNameList;
    }

    public int[] getOutflowIconList() {
        return outflowIconList;
    }

    public String[] getInflowNameList() {
        return inflowNameList;
    }

    public int[] getInflowIconList() {
        return inflowIconList;
    }

    public int[] getColorList() {
        return colors;
    }

    public int getInflowIcon(String category) {
        for (int i=0; i<inflowNameList.length; i++) {
            if (category.equals(inflowNameList[i])) {
                return inflowIconList[i];
            }
        }
        return -1;
    }

    public int getOutflowIcon(String category) {
        for (int i=0; i<outflowNameList.length; i++) {
            if (category.equals(outflowNameList[i])) {
                return outflowIconList[i];
            }
        }
        return -1;
    }
}
