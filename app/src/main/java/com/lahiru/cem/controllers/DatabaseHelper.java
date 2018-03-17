package com.lahiru.cem.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lahiru on 1/20/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "expense_diary_db";
    public static final String TRANSACTION_TABLE = "tran";
    public static final String LENDING_TABLE = "lend";
    public static final String REPAYMENT_TABLE = "repay";
    public static final String ACCOUNT_TABLE = "account";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 8);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table account(" +
                        "aid integer primary key AUTOINCREMENT, " +
                        "accname varchar(20) not null unique, " +
                        "email varchar(100) not null, " +
                        "pin numeric(5,0) not null" +
                        ");"
        );

        sqLiteDatabase.execSQL(
                "create table " + TRANSACTION_TABLE + "(" +
                        "tid integer primary key AUTOINCREMENT," +
                        "aid integer, " +
                        "amount numeric(12,2) not null," +
                        "date date not null," +
                        "day varchar(10) not null," +
                        "type varchar(7) not null," +
                        "category varchar(20) not null," +
                        "note varchar(100)," +
                        "check(type IN ('inflow', 'outflow')), " +
                        "foreign key (aid) references " + ACCOUNT_TABLE + " (aid) " +
                        "on delete cascade" +
                        ");"
        );
        sqLiteDatabase.execSQL(
                "create table " + LENDING_TABLE + "(" +
                        "tid integer," +
                        "duedate varchar(10)," +
                        "partner varchar(50)," +
                        "primary key (tid), " +
                        "foreign key (tid) references " + TRANSACTION_TABLE + " (tid) " +
                        "on delete cascade" +
                        ");"
        );
        sqLiteDatabase.execSQL(
                "create table " + REPAYMENT_TABLE + "(" +
                        "tid integer," +
                        "lend_tid integer, " +
                        "primary key (tid, lend_tid), " +
                        "foreign key (tid) references " + TRANSACTION_TABLE + " (tid) " +
                        "on delete cascade, " +
                        "foreign key (lend_tid) references " + LENDING_TABLE + " (tid) " +
                        "on delete cascade" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL("drop table if exists " + LENDING_TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + REPAYMENT_TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + TRANSACTION_TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + ACCOUNT_TABLE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

}
