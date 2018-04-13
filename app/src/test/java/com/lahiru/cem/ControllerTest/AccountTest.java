package com.lahiru.cem.ControllerTest;

import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.Account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

/**
 * Created by Lahiru on 3/17/2018.
 */
@RunWith(org.robolectric.RobolectricTestRunner.class)
public class AccountTest {

    public DatabaseHelper dbHelper;

    @Before
    public void setup() {
        dbHelper = new DatabaseHelper(RuntimeEnvironment.application);
    }

    @Test
    public void getAccounts() {     // get all accounts - not implemented properly
        ArrayList<String[]> accounts = AccountController.getAccounts(dbHelper);
        for (String[] account: accounts) {
            Assert.assertNotNull(account);
        }
    }

    @Test
    public void insertAndTestAccount() {        // insert and check new account
//        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        long index = AccountController.insertAccount(dbHelper, account);

        Assert.assertTrue(index != -1);

        Account checkedAccount = AccountController.checkAccount(dbHelper, index+"", account.getAccountName());

        Assert.assertNotNull(checkedAccount);
        Assert.assertEquals(index+"", checkedAccount.getAid());
        Assert.assertEquals(account.getAccountName(), checkedAccount.getAccountName());
        Assert.assertEquals(account.getEmail(), checkedAccount.getEmail());

        Account authAccount = AccountController.authenticateAccount(dbHelper, index+"", account.getPin());

        Assert.assertNotNull(authAccount);
        Assert.assertEquals(index+"", authAccount.getAid());
        Assert.assertEquals(account.getAccountName(), authAccount.getAccountName());
        Assert.assertEquals(account.getEmail(), authAccount.getEmail());

        int i = AccountController.deleteAccount(dbHelper, index + "");

        Assert.assertEquals(i, index);
    }


}
