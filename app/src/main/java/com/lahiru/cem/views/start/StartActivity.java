package com.lahiru.cem.views.start;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.views.home.HomeActivity;

import java.util.Calendar;

public class StartActivity extends AppCompatActivity {

    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //--------check for previous login ---------------------------------------------------------
        DatabaseHelper db = new DatabaseHelper(this);

        SharedPreferences accPreferences = getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
        String aid = accPreferences.getString("AID", "");
        String accname = accPreferences.getString("ACC_NAME", "");
        Account account = AccountController.checkAccount(db, aid, accname);
        if (account != null) {
            AppData.getInstance().setAccount(account);
            Intent intent = new Intent("com.lahiru.cem.views.home.HomeActivity");
            startActivity(intent);
            finish();
        } else {
            Log.i("TEST", "account not found for AID=" + aid + " ACC_NAME=" + accname);
        }

        //----load first fragment and setting back btn listener-------------------------------------
        changeFragment(R.layout.fragment_select_account);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeFragment(R.layout.fragment_select_account);
                }
            });
        }
    }

    public void changeFragment(int fragmentId) {
        if (fragmentId == R.layout.fragment_login) {
            curFragment =  new LoginFragment();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }else if (fragmentId == R.layout.fragment_select_account) {
            curFragment =  new SelectAccountFragment();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        } else {
            curFragment = new NewAccountFragment();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.framgent_holder, curFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (curFragment instanceof SelectAccountFragment) {
            super.onBackPressed();
        } else {
            changeFragment(R.layout.fragment_select_account);
        }
    }
}
