package com.lahiru.cem.views.account;


import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.AppData;

public class AccountActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private AppData appData;

    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        db = new DatabaseHelper(this);
        appData = AppData.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curFragment instanceof CurrentAccountFragment) {
                        finish();
                    } else {
                        changeFragment(R.layout.fragment_current_account);
                    }
                }
            });
        }

        changeFragment(R.layout.fragment_current_account);
    }

    public void changeFragment(int fragmentId) {
        if (fragmentId == R.layout.fragment_current_account) {
            curFragment =  new CurrentAccountFragment();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else if (fragmentId == R.layout.fragment_edit_account) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            curFragment =  new EditAccountFragment();
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_holder, curFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (curFragment instanceof CurrentAccountFragment) {
            super.onBackPressed();
        } else {
            changeFragment(R.layout.fragment_current_account);
        }
    }
}
