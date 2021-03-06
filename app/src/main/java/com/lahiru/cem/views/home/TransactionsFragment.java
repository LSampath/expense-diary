package com.lahiru.cem.views.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.Transaction;
import com.lahiru.cem.views.adapters.RecycleAdapter;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.ListItem;
import com.lahiru.cem.views.transaction.TransactionActivity;

import java.util.ArrayList;


public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private ArrayList<ListItem> listItems;
    private FloatingActionButton newTransactionBtn;

    private HomeActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_transactions, container, false);
        activity = (HomeActivity) getActivity();

        recyclerView = fragment.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        listItems = new ArrayList<>();
        adapter = new RecycleAdapter(listItems, activity);
        recyclerView.setAdapter(adapter);

        newTransactionBtn = fragment.findViewById(R.id.btn_new_transaction);
//        newTransactionBtn.setButtonColor(fragment.getResources().getColor(R.color.outflow_color));
        newTransactionBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.lahiru.cem.views.transaction.TransactionActivity");
                startActivity(intent);
            }
        });

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }

    public void loadTransactions() {
        DatabaseHelper db = new DatabaseHelper(activity);
        listItems = new ArrayList<>();
        String aid = AppData.getInstance().getAccount().getAid();

        ArrayList<String> dates = TransactionController.getTransactionDates(db, aid);
        for (String date: dates) {
            listItems.add(new ListItem(ListItem.DATE_ITEM, date));
            ArrayList<String> transList = TransactionController.getTransactions(db, aid, date);
            for (String tid: transList) {
                listItems.add(new ListItem(ListItem.TRANSACTION_ITEM, tid));
            }
        }
        adapter.setListItems(listItems);
    }
}
