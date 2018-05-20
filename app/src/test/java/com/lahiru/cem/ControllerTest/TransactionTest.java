package com.lahiru.cem.ControllerTest;

import android.database.sqlite.SQLiteDatabase;

import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.Transaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

/**
 * Created by Lahiru on 4/3/2018.
 */
@RunWith(org.robolectric.RobolectricTestRunner.class)
public class TransactionTest {

    public DatabaseHelper dbHelper;

    @Before
    public void setup() {
        dbHelper = new DatabaseHelper(RuntimeEnvironment.application);

        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        long index = AccountController.insertAccount(dbHelper, account);

        if (index != -1) {
            AppData appData = AppData.getInstance();
            appData.setAccount(new Account(index+"", account.getAccountName(), account.getEmail(), account.getPin()));
        }

        Transaction tran1 = new Transaction(null, "00.00", "2001-01-01", "Monday",
                "inflow", AppData.getInstance().getInflowNameList()[4], null);
        TransactionController.insertTransaction(dbHelper, tran1);

        Transaction tran2 = new Transaction(null, "00.00", "2001-01-01", "Monday",
                "inflow", AppData.getInstance().getOutflowNameList()[7], "outflow baby");
        TransactionController.insertTransaction(dbHelper, tran2);

        Transaction tran3 = new Transaction(null, "00.00", "2001-01-01", "Monday",
                "inflow", AppData.getInstance().getOutflowNameList()[3], "outflow baby");
        TransactionController.insertTransaction(dbHelper, tran3);
    }

    @Test
    public void insertAndTestTransaction() {        // insert and check new transaction
//        Transaction tran = new Transaction(null, "00.00", "2000-12-12", "Monday",
//                "inflow", "Food & Beverage", null);
        Transaction tran = new Transaction(null, "00.00", "2000-12-12", "Monday",
                "inflow", "Food Category", null);
        long tid = TransactionController.insertTransaction(dbHelper, tran);

        Assert.assertEquals(2, tid);

        Transaction checkTran = TransactionController.getTransactionDetails(dbHelper, tid + "");

        Assert.assertEquals(tran.getInOut(), checkTran.getInOut());
    }

    @Test
    public void getTransactionDates() {
        ArrayList<String> transactionDates = TransactionController.getTransactionDates(dbHelper, "1");

        Assert.assertEquals("2001-01-01", transactionDates.get(0));
//        Assert.assertEquals("2000-12-12", transactionDates.get(1));

    }

    @Test
    public void getLendingDates() {
        ArrayList<String> lendingDates = TransactionController.getLendingDates(dbHelper, "1", "inflow");
        for (String date: lendingDates) {
            ArrayList<String> tids = TransactionController.getLendings(dbHelper, "1", date, "inflow");
            for (String tid: tids) {
                Transaction tran = TransactionController.getTransactionDetails(dbHelper, tid);
                Assert.assertEquals("inflow", tran.getInOut());
            }
        }
    }

}
