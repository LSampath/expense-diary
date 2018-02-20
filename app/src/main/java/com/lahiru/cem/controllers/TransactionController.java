package com.lahiru.cem.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lahiru.cem.adapters.DatabaseHelper;
import com.lahiru.cem.models.Transaction;

import java.util.ArrayList;

/**
 * Created by Lahiru on 2/15/2018.
 */

public class TransactionController {

    public static long insertTransaction(DatabaseHelper dbHelper, Transaction tran) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", tran.getAmount());
        values.put("date", tran.getDate());
        values.put("day", tran.getDay());
        values.put("type", tran.getInOut());
        values.put("category", tran.getCategory());
        values.put("note", tran.getNote());
        long result = db.insert(DatabaseHelper.TRANSACTION_TABLE, null, values);

        long tid = result;
        if (tid != -1 && (tran.getCategory().equals("Loan") || tran.getCategory().equals("Debt"))) {
            values = new ContentValues();
            values.put("tid", tid);
            values.put("category", tran.getCategory());
            values.put("duedate", tran.getDueDate());
            values.put("partner", tran.getPartner());
            result = db.insert(DatabaseHelper.LENDING_TABLE, null, values);
        }
        return result;
    }

    public static Transaction getTransactionDetails(DatabaseHelper dbHelper, String tid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseHelper.TRANSACTION_TABLE + " where tid='" + tid + "'", null);
        Transaction tran = null;
        if (res.moveToNext()) {
            tran = new Transaction(
                    res.getString(0),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    res.getString(6)
            );
            if (tran.getCategory().equals("Loan") || tran.getCategory().equals("Debt")) {
                res = db.rawQuery("select duedate, partner from " + DatabaseHelper.LENDING_TABLE + " where tid='" + tran.getTID() + "'" ,
                        null);
                if (res.moveToNext()) {
                    tran.setLendingDetails(res.getString(0), res.getString(1));
                }
            }
        }
        return tran;
    }

    public static ArrayList<String> getTransactionDates(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select distinct date from " + DatabaseHelper.TRANSACTION_TABLE + " order by date", null);

        ArrayList<String> dateList = new ArrayList<>();
        while (res.moveToNext()) {
            dateList.add(res.getString(0));
        }
        return dateList;
    }

    public static ArrayList<String> getTransactions(DatabaseHelper dbHelper, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select tid from " + DatabaseHelper.TRANSACTION_TABLE + " where date='" + date + "'", null);

        ArrayList<String> transList = new ArrayList<>();
        while (res.moveToNext()) {
            transList.add(res.getString(0));
        }
        return transList;
    }

}
