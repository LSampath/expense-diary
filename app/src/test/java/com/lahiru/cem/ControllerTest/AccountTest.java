package com.lahiru.cem.ControllerTest;

import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;

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

        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        long index = AccountController.insertAccount(dbHelper, account);

        if (index != -1) {
            AppData appData = AppData.getInstance();
            appData.setAccount(new Account(index+"", account.getAccountName(), account.getEmail(), account.getPin()));
        }
    }

    @Test
    public void getAccounts() {     // get all accounts
        ArrayList<String[]> accounts = AccountController.getAccounts(dbHelper);
        for (String[] account: accounts) {
            String accName = account[1];
            String aid = account[0];
            Account checkedAcc = AccountController.checkAccount(dbHelper, aid, accName);
            Assert.assertEquals(accName, checkedAcc.getAccountName());
            Assert.assertEquals(aid, checkedAcc.getAid());
        }
    }

    @Test
    public void insertAndTestAccount() {        // insert and check new account
//        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        long index = AccountController.insertAccount(dbHelper, account);

        Assert.assertNotEquals(-1, index);

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

    @Test       // delete account
    public void deleteAccount() {
        int result = AccountController.deleteAccount(dbHelper, "5");
        Assert.assertEquals(0, result);
    }

    @Test
    public void updateAccount() {
        ArrayList<Account> aList = new ArrayList<>();
        aList.add(new Account("1", "updatedName", "ls@gmail.com", "33333"));
//        aList.add(new Account("1", "updatedName", "", "33333"));         correct
//        aList.add(new Account("1", "updatedName", null, "12345"));       wrong
//        aList.add(new Account("1", "updatedName", "ls@gmail.com", "1234564"));    wrong
//        aList.add(new Account("1", "updatedName", "ls@gmail.com", "333.44"));     wrong
//        aList.add(new Account("1", "updatedName", "ls@gmail.com", null));         wrong
//        aList.add(new Account("1", "updatedName", "ls@gmail.com", ""));
//        aList.add(new Account("1", "", "ls@gmail.com", "333.44"));
//        aList.add(new Account("1", null, "ls@gmail.com", "333.44"));

        for (Account a: aList) {
            long result = AccountController.updateAccount(dbHelper, a, "12345");
            Assert.assertEquals(1, result);
        }

    }

}
