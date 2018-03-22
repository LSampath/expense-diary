package com.lahiru.cem.ControllerTest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.views.account.StartActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.ArrayList;

/**
 * Created by Lahiru on 3/17/2018.
 */
@RunWith(org.robolectric.RobolectricTestRunner.class)
public class AccountTest {

    @Test
    public void getAccounts() {
        DatabaseHelper dbHelper = new DatabaseHelper(Robolectric.setupActivity(StartActivity.class));
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select accname, aid from " + DatabaseHelper.ACCOUNT_TABLE, null);

        res.moveToNext();

        Assert.assertEquals("lahiruAcc", res.getString(0));
    }

}
