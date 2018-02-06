package com.lahiru.cem.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lahiru on 1/20/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "expense_db";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table STUDENT (name varchar(10) primary key)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
