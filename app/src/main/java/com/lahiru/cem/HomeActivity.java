package com.lahiru.cem;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.lahiru.cem.adapters.DatabaseHelper;
import com.lahiru.cem.adapters.SectionPagerAdapter;
import com.lahiru.cem.adapters.TransactionAdapter;
import com.lahiru.cem.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;

//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
//
//    private List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DatabaseHelper db = new DatabaseHelper(this);

//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        transactions = new ArrayList<>();
//
//        for(int i=0; i<10; i++) {
//            Transaction tran = new Transaction("Heading " + i, "description of the item " + i);
//            transactions.add(tran);
//        }
//
//        adapter = new TransactionAdapter(transactions, this);
//        recyclerView.setAdapter(adapter);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "HOME");
        adapter.addFragment(new TransactionsFragment(), "TRANSACTIONS");
        adapter.addFragment(new LendingsFragment(), "LENDINGS");
        viewPager.setAdapter(adapter);
    }

}

