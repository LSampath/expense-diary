package com.lahiru.cem.views.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lahiru.cem.R;
import com.lahiru.cem.views.account.StartActivity;
import com.lahiru.cem.views.adapters.SectionPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TransactionsFragment(), "TRANSACTIONS");
        adapter.addFragment(new SummaryFragment(), "SUMMARY");
        adapter.addFragment(new ChartsFragment(), "CHART");
        adapter.addFragment(new ForecastFragment(), "FORECAST");
        viewPager.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent("com.lahiru.cem.views.AboutActivity");
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_settings) {
//            Intent intent = new Intent("com.lahiru.cem.views.transaction.TransactionActivity");
//            startActivity(intent);
        } else if (item.getItemId() == R.id.action_logout) {
            SharedPreferences accPreferences = this.getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = accPreferences.edit();
            editor.remove("AID");
            editor.remove("ACC_NAME");
            editor.apply();
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
        }
        return true;



    }

}

