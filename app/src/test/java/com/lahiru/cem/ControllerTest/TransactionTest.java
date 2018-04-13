package com.lahiru.cem.ControllerTest;

import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.Transaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

/**
 * Created by Lahiru on 4/3/2018.
 */

public class TransactionTest {

    public DatabaseHelper dbHelper;

    @Before
    public void setup() {
        dbHelper = new DatabaseHelper(RuntimeEnvironment.application);

        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        long index = AccountController.insertAccount(dbHelper, account);

        AppData appData = AppData.getInstance();
        appData.setAccount(new Account(index+"", account.getAccountName(), account.getEmail(), account.getPin()));
    }

    @Test
    public void insertAndTestTransaction() {        // insert and check new account
//        Transaction tran = new Transaction(null, "00.00", "2000-12-12", "Monday",
//                "inflow", "Food & Beverage", null);
//
//        long tid = TransactionController.insertTransaction(dbHelper, tran);
//
//        Assert.assertFalse(tid != -1);

        ArrayList<String[]> accounts = AccountController.getAccounts(dbHelper);
        for (String[] account: accounts) {
            Assert.assertNotNull(account);
        }
    }

}
