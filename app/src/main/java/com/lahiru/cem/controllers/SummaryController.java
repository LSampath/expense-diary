package com.lahiru.cem.controllers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.CharSummary;
import com.lahiru.cem.models.Summary;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Lahiru on 3/13/2018.
 */

public class SummaryController {

    // get smallest and largest date from transactions of current account---------------------------
    public static String[] getMaxMinDates(DatabaseHelper dbHelper, String aid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select max(date), min(date) from " + DatabaseHelper.TRANSACTION_TABLE +
                " where aid='" + aid + "'", null);
        if (res.moveToNext()) {
            if (res.getString(0) == null || res.getString(1) == null) {
                return null;
            }
            return new String[] {res.getString(0), res.getString(1)};
        } else {
            return null;
        }
    }

    // get summary values for a given details of current account------------------------------------
    // given details=aid,fromDate,toDate,category,inOut | output=total,count,average----------------
    public static Summary getSummaryDetails(DatabaseHelper dbHelper, Summary summary) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount), avg(amount), count(amount) from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "aid='" + summary.getAID() + "' " +
                "and category='" + summary.getCategory() + "' and type='" + summary.getInOut() + "' and " +
                "(date<='" + summary.getToDate() + "' and date>='" + summary.getFromDate() + "')", null);
        if (res.moveToNext()) {
            summary.setValues(res.getInt(2), res.getDouble(0), res.getDouble(1));
        }
        return summary;
    }

    // get most expensive and most profitable category for current account--------------------------
    public static String[] getMostCategories(DatabaseHelper dbHelper, String aid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select category from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "aid='" + aid + "' and type='inflow' " +
                "group by category order by sum(amount) desc limit 1", null);
        String[] result = new String[2];
        if (res.moveToNext()) {
            result[0] = res.getString(0);
        }
        res = db.rawQuery("select category from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "aid='" + aid + "' and type='outflow' " +
                "group by category order by sum(amount) desc limit 1", null);
        if (res.moveToNext()) {
            result[1] = res.getString(0);
        }
        return result;
    }

    // get total inflow and total outflow amounts for current account-------------------------------
    public static double[] getTotals(DatabaseHelper dbHelper, String aid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "aid='" + aid + "' and type='inflow'", null);
        double[] result = new double[2];
        if (res.moveToNext()) {
            result[0] = res.getDouble(0);
        }
        res = db.rawQuery("select sum(amount) from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "aid='" + aid + "' and type='outflow'", null);
        if (res.moveToNext()) {
            result[1] = res.getDouble(0);
        }
        return result;
    }

    // get total amounts for selected inOut and account, category wise -----------------------------
    public static CharSummary getCategoryWiseDetails(DatabaseHelper dbHelper, Summary summary) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount), category from " + DatabaseHelper.TRANSACTION_TABLE +
                " where aid='" +  summary.getAID() + "' and type='" + summary.getInOut() + "'" +
                " and (date<='" + summary.getToDate() + "' and date>='" + summary.getFromDate() + "')" +
                " group by category", null);

        CharSummary charSummary = new CharSummary();
        while (res.moveToNext()) {
            charSummary.setData(res.getString(1), res.getFloat(0));
        }
        if (res.getCount() == 0) {
            return null;
        }
        return charSummary;
    }

    // get totals amount by date for selected category ---------------------------------------------
    public static ArrayList<String[]> getTotalsByDate(DatabaseHelper dbHelper, String aid, String category, String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "select date, sum(amount) from " + DatabaseHelper.TRANSACTION_TABLE +
                " where aid=" + aid + " and type='" + type + "' and category='" + category + "' " +
                "group by date order by date asc";
        if (category.equals("ALL")) {
            query = "select date, sum(amount) from " + DatabaseHelper.TRANSACTION_TABLE +
                    " where aid=" + aid + " and type='" + type + "' group by date order by date asc";
        }
        Cursor res = db.rawQuery(query, null);

        ArrayList<String[]> result = new ArrayList<>();
        while(res.moveToNext()) {
            result.add(new String[]{res.getString(0), res.getString(1)});
        }
        return  result;
    }
}
