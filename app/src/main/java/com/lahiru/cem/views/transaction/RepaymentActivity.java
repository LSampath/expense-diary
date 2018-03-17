package com.lahiru.cem.views.transaction;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.ListItem;
import com.lahiru.cem.views.adapters.RecycleAdapter;

import java.util.ArrayList;


public class RepaymentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private ArrayList<ListItem> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment);

        Log.i("TEST", "activity started");

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view_replayment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
        adapter = new RecycleAdapter(listItems, this);
        recyclerView.setAdapter(adapter);

        loadRepayments();
    }

    private void loadRepayments() {
        DatabaseHelper db = new DatabaseHelper(this);
        listItems = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        String inOut = bundle.getString("IN_OUT");

        ArrayList<String> dates = TransactionController.getLendingDates(db, inOut);
        for (String date: dates) {
            listItems.add(new ListItem(ListItem.DATE_ITEM, date));
            ArrayList<String> transList = TransactionController.getLendings(db, date, inOut);
            for (String tid: transList) {
                listItems.add(new ListItem(ListItem.REPAYMENT_ITEM, tid));
            }
        }
        adapter.setListItems(listItems);
    }


}
