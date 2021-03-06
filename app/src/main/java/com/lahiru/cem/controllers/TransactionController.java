package com.lahiru.cem.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.Transaction;

import java.util.ArrayList;

/**
 * Created by Lahiru on 2/15/2018.
 */

public class TransactionController {

    public static long insertTransaction(DatabaseHelper dbHelper, Transaction tran) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String aid = AppData.getInstance().getAccount().getAid();

        ContentValues values = new ContentValues();
        values.put("aid", aid);
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
            values.put("duedate", tran.getDueDate());
            values.put("partner", tran.getPartner());
            result = db.insert(DatabaseHelper.LENDING_TABLE, null, values);
        }
        if (tid != -1 && (tran.getCategory().equals("Loan Collection") || tran.getCategory().equals("Debt Repayment"))) {
            values = new ContentValues();
            values.put("tid", tid);
            values.put("lend_tid", tran.getLendTID());
            result = db.insert(DatabaseHelper.REPAYMENT_TABLE, null, values);
        }
        return result;
    }

    public static long updateTransaction(DatabaseHelper dbHelper, Transaction tran) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (tran.getCategory().equals("Loan") || tran.getCategory().equals("Debt")) {
            db.delete(DatabaseHelper.LENDING_TABLE, "tid=?", new String[]{tran.getTID()});
        }
        if (tran.getCategory().equals("Loan Collection") || tran.getCategory().equals("Debt Repayment")) {
           db.delete(DatabaseHelper.REPAYMENT_TABLE, "tid=?", new String[]{tran.getTID()});
        }
        ContentValues values = new ContentValues();
        values.put("amount", tran.getAmount());
        values.put("date", tran.getDate());
        values.put("day", tran.getDay());
        values.put("type", tran.getInOut());
        values.put("category", tran.getCategory());
        values.put("note", tran.getNote());
        long result = db.update(DatabaseHelper.TRANSACTION_TABLE, values, "tid=?", new String[]{tran.getTID()});

        if (result == 1 && (tran.getCategory().equals("Loan") || tran.getCategory().equals("Debt"))) {
            values = new ContentValues();
            values.put("tid", tran.getTID());
            values.put("duedate", tran.getDueDate());
            values.put("partner", tran.getPartner());
            result = db.insert(DatabaseHelper.LENDING_TABLE, null, values);
        }
        if (result == 1 && (tran.getCategory().equals("Loan Collection") || tran.getCategory().equals("Debt Repayment"))) {
            values = new ContentValues();
            values.put("tid", tran.getTID());
            values.put("lend_tid", tran.getLendTID());
            result = db.insert(DatabaseHelper.REPAYMENT_TABLE, null, values);
        }
        return result;
    }

    public static Transaction getTransactionDetails(DatabaseHelper dbHelper, String tid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String aid = AppData.getInstance().getAccount().getAid();

        Cursor res = db.rawQuery("select * from " + DatabaseHelper.TRANSACTION_TABLE + " where tid='" + tid + "' " +
                "and aid='" + aid + "'", null);
        Transaction tran = null;
        if (res.moveToNext()) {
            tran = new Transaction(
                    res.getString(0),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    res.getString(6),
                    res.getString(7)
            );
            if (tran.getCategory().equals("Loan") || tran.getCategory().equals("Debt")) {
                res = db.rawQuery("select partner, duedate from " + DatabaseHelper.LENDING_TABLE + " where tid='" + tran.getTID() + "'" ,
                        null);
                if (res.moveToNext()) {
                    tran.setLendingDetails(res.getString(0), res.getString(1));
                }
            }
            if (tran.getCategory().equals("Loan Collection") || tran.getCategory().equals("Debt Repayment")) {
                res = db.rawQuery("select lend_tid from " + DatabaseHelper.REPAYMENT_TABLE + " where tid='" + tran.getTID() + "'" ,
                        null);
                if (res.moveToNext()) {
                    tran.setRepaymentDetails(res.getString(0));
                    Log.i("TEST", "controller lend_tid=" + tran.getLendTID() + " tid=" + tran.getTID());
                }
            }
        }
        return tran;
    }

    public static ArrayList<String> getTransactionDates(DatabaseHelper dbHelper, String aid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select distinct date from " + DatabaseHelper.TRANSACTION_TABLE +
                " where aid='" + aid + "' order by date desc", null);
        ArrayList<String> dateList = new ArrayList<>();
        while (res.moveToNext()) {
            dateList.add(res.getString(0));
        }
        return dateList;
    }

    public static ArrayList<String> getLendingDates(DatabaseHelper dbHelper, String aid, String inOut) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select distinct date, category from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "type='" + inOut + "' and aid=" + aid + " and (category='Loan' or category='Debt') order by date", null);
        ArrayList<String> dateList = new ArrayList<>();
        while (res.moveToNext()) {
            dateList.add(res.getString(0));
        }
        return dateList;
    }

    public static ArrayList<String> getTransactions(DatabaseHelper dbHelper, String aid, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select tid from " + DatabaseHelper.TRANSACTION_TABLE + " where " +
                "date='" + date + "' and aid=" + aid, null);
        ArrayList<String> transList = new ArrayList<>();
        while (res.moveToNext()) {
            transList.add(res.getString(0));
        }
        return transList;
    }

    public static int deleteTransaction(DatabaseHelper dbHelper, String tid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select tid from " + DatabaseHelper.TRANSACTION_TABLE +
                " natural join " + DatabaseHelper.REPAYMENT_TABLE + " where lend_tid=" + tid, null);
        while (res.moveToNext()) {
            db.delete(DatabaseHelper.TRANSACTION_TABLE, "tid=?", new String[]{res.getString(0)});
        }
        return db.delete(DatabaseHelper.TRANSACTION_TABLE, "tid=?", new String[]{tid});
    }

    public static ArrayList<String> getLendings(DatabaseHelper dbHelper, String aid, String date, String inOut) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res;
        if (inOut.equals("ALL")) {
            res = db.rawQuery("select tid from " + DatabaseHelper.TRANSACTION_TABLE + " where date='" + date + "' " +
                    "and aid=" + aid + " and (category='Loan' or category='Debt') order by date", null);
        } else {
            res = db.rawQuery("select tid from " + DatabaseHelper.TRANSACTION_TABLE + " where date='" + date + "' " +
                    "and aid=" + aid + " and type='" + inOut + "' and (category='Loan' or category='Debt') order by date", null);
        }
        ArrayList<String> transList = new ArrayList<>();
        while (res.moveToNext()) {
            transList.add(res.getString(0));
        }
        return transList;
    }

    public static double getRepayedAmount(DatabaseHelper dbHelper, String aid, String lend_tid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(amount) from " + DatabaseHelper.TRANSACTION_TABLE + " natural join " +
               DatabaseHelper.REPAYMENT_TABLE + " where aid=" + aid + " and lend_tid=" + lend_tid , null);
        double amount = 0;
        if (res.moveToNext()) {
            amount += res.getDouble(0);
        }
        return amount;
    }

    public static ArrayList<String> getDueRepaymentDates(DatabaseHelper dbHelper, String aid, String today) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select distinct date from " + DatabaseHelper.TRANSACTION_TABLE +
                " natural join " + DatabaseHelper.LENDING_TABLE +
                " where aid='" + aid + "' and duedate>='" + today + "' order by date", null);
        ArrayList<String> dateList = new ArrayList<>();
        while (res.moveToNext()) {
            dateList.add(res.getString(0));
        }
        return dateList;
    }

    public static ArrayList<String> getLendingFromDuedate(DatabaseHelper dbHelper, String aid, String duedate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select tid from " + DatabaseHelper.LENDING_TABLE + " natural join " + DatabaseHelper.TRANSACTION_TABLE +
                " where duedate='" + duedate + "' " +
                "and aid=" + aid + " order by date", null);
        ArrayList<String> transList = new ArrayList<>();
        while (res.moveToNext()) {
            transList.add(res.getString(0));
        }
        return transList;
    }
}
