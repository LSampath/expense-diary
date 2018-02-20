package com.lahiru.cem.views.account;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.lahiru.cem.R;
import com.lahiru.cem.adapters.DatabaseHelper;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.models.Account;

public class StartActivity extends AppCompatActivity {

    private Account curAccount;
    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        changeFragment(R.layout.fragment_select_account);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeFragment(R.layout.fragment_select_account);
                }
            });
        }

        DatabaseHelper db = new DatabaseHelper(this);
        AccountController.testMethod(db);
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

    public void setCurAccount(Account acc) {
        this.curAccount = acc;
    }

    public Account getCurAccount() {
        return this.curAccount;
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
