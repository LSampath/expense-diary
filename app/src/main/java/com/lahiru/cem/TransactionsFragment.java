package com.lahiru.cem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lahiru.cem.adapters.TransactionAdapter;
import com.lahiru.cem.models.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Transaction> transactions;
    private FloatingActionButton newTransactionBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_transactions, container, false);

        recyclerView = fragment.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        transactions = new ArrayList<>();

        for(int i=0; i<10; i++) {
            Transaction tran = new Transaction("Heading " + i, "description of the item " + i);
            transactions.add(tran);
            //------------------------------------------------------------------------------------------------------------------------------------
        }

        adapter = new TransactionAdapter(transactions, getActivity());
        recyclerView.setAdapter(adapter);

        newTransactionBtn = (FloatingActionButton) fragment.findViewById(R.id.newTransactionBtn);
        newTransactionBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.lahiru.cem.TransactionActivity");
                startActivity(intent);
            }
        });

        return fragment;
    }
}
