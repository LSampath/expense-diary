package com.lahiru.cem.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lahiru.cem.models.Account;

import java.util.ArrayList;

/**
 * Created by Lahiru on 2/18/2018.
 */

public class AccountController {


    public static long insertAccount(DatabaseHelper dbHelper, Account acc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accname", acc.getAccountName());
        values.put("email", acc.getEmail());
        values.put("pin", acc.getPin());
        long result = db.insert(DatabaseHelper.ACCOUNT_TABLE, null, values);

        return  result;
    }

    public static ArrayList<String[]> getAccounts(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select aid, accname from " + DatabaseHelper.ACCOUNT_TABLE, null);

        ArrayList<String[]> accountList = new ArrayList<>();
        while (res.moveToNext()) {
            accountList.add(new String[]{res.getString(0), res.getString(1)});
        }
        return accountList;
    }

    public static Account authenticateAccount(DatabaseHelper dbHelper, String aid, String pin) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select aid, accname, email from " + DatabaseHelper.ACCOUNT_TABLE + " where aid='" +
                aid + "' and pin='" + pin + "'", null);

        if (res.moveToNext()) {
            return new Account(res.getString(0), res.getString(1), res.getString(2), null);
        } else {
            return null;
        }
    }

    public static Account checkAccount(DatabaseHelper dbHelper, String aid, String accname) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select aid, accname, email from " + DatabaseHelper.ACCOUNT_TABLE + " where aid='" + aid + "' " +
                "and accname='" + accname + "'", null);
        if (res.moveToNext()) {
            return new Account(res.getString(0), res.getString(1), res.getString(2), null);
        } else {
            return null;
        }
    }

//    public static long testMethod(DatabaseHelper dbHelper) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        return db.delete(DatabaseHelper.ACCOUNT_TABLE, "email!=?", new String[]{"lahirusampath.15@cse.mrt.ac.lk"} );
//    }
}
