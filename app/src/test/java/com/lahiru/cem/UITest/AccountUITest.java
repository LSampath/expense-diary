package com.lahiru.cem.UITest;

/**
 * Created by Lahiru on 5/13/2018.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.lahiru.cem.BuildConfig;
import com.lahiru.cem.R;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.views.start.LoginFragment;
import com.lahiru.cem.views.start.SelectAccountFragment;
import com.lahiru.cem.views.start.StartActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.robolectric.util.FragmentTestUtil.startFragment;

@RunWith(org.robolectric.RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AccountUITest {

    private DatabaseHelper dbHelper;
    private StartActivity startActivity;

    @Before
    public void setup() {
        dbHelper = new DatabaseHelper(RuntimeEnvironment.application);

        Account account = new Account(null, "TestAccount", "testing.15@cse.mrt.ac.lk", "12345");
        long index = AccountController.insertAccount(dbHelper, account);

        if (index != -1) {
            AppData.getInstance().setAccount(account);
        }

        startActivity = Robolectric.buildActivity(StartActivity.class).create().start().resume().visible().get();
    }

    @Test
    public void selectAccountFragment() {
        Fragment fragment = new SelectAccountFragment();
        startFragment(fragment);

        Assert.assertNotNull(fragment);

        ListView accList = (ListView) startActivity.findViewById(R.id.list_acc_names);
        String account = (String) accList.getItemAtPosition(0);
        Assert.assertEquals("TestAccount", account);
    }

    @Test
    public void loginToAccountFragment() {
        Fragment fragment = new LoginFragment();
        startFragment(fragment, StartActivity.class);

        EditText pinTxt = fragment.getView().findViewById(R.id.edt_pin);
        pinTxt.setText("12345");

        CheckBox rememberCheck = fragment.getView().findViewById(R.id.cb_remember);
        rememberCheck.setChecked(true);

        Button loginBtn = fragment.getView().findViewById(R.id.btn_login);
        loginBtn.performClick();

        SharedPreferences accPreferences = fragment.getActivity().getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
        Assert.assertEquals("TestAccount", accPreferences.getString("ACC_NAME", ""));
    }
}
