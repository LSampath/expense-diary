package com.lahiru.cem.models;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * Created by Lahiru on 3/18/2018.
 */

public class CharSummary {

    private ArrayList<String> categoryList;
    private ArrayList<PieEntry> dataList;

    public CharSummary() {
        this.categoryList = new ArrayList<>();
        this.dataList = new ArrayList<>();
    }

    public void setData(String category, float amount) {
        this.categoryList.add(category);
        this.dataList.add(new PieEntry(amount));
    }

    public ArrayList<String> getCategoryList() {
        return categoryList;
    }
    public ArrayList<PieEntry> getDataList() {
        return dataList;
    }
}
