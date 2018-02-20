package com.lahiru.cem.views.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.adapters.DatabaseHelper;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.models.Account;

import java.util.ArrayList;

public class SelectAccountFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private ArrayList<String[]> accounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_select_account, container, false);
        if (container != null) {
            container.removeAllViews();
        }
        dbHelper = new DatabaseHelper(getActivity());

        //---------------make new account button listener
        Button newAccountBtn = fragment.findViewById(R.id.btn_new_acc);
        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartActivity activity = (StartActivity) SelectAccountFragment.this.getActivity();
                activity.changeFragment(R.layout.fragment_new_account);
            }
        });


        //------------------load account name list--------------------------------------------------
        accounts = AccountController.getAccounts(dbHelper);
        ArrayList<String> accNames = new ArrayList<>();
        for (String[] acc: accounts) {
            accNames.add(acc[1]);
        }

        ListView accNameList = fragment.findViewById(R.id.list_acc_names);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_account, R.id.txt_list_item_acc, accNames);
        accNameList.setAdapter(adapter);
        accNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String accountName = accounts.get(i)[1];
                String aid = accounts.get(1)[0];

                StartActivity activity = (StartActivity) SelectAccountFragment.this.getActivity();
                activity.setCurAccount(new Account(aid, accountName, null, null));

                activity.changeFragment(R.layout.fragment_login);
            }
        });

        return fragment;
    }

}
