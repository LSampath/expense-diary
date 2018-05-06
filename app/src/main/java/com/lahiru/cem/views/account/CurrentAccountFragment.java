package com.lahiru.cem.views.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.views.start.StartActivity;

public class CurrentAccountFragment extends Fragment {

    private DatabaseHelper db;
    private AppData appData;

    private TextView accNameTxt;
    private TextView emailTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_current_account, container, false);

        db = new DatabaseHelper(fragment.getContext());
        appData = AppData.getInstance();

        // initialize acc name and email -----------------------------------------------------------
        accNameTxt = fragment.findViewById(R.id.tv_acc_name);
        emailTxt = fragment.findViewById(R.id.tv_email);

        accNameTxt.setText(appData.getAccount().getAccountName());
        emailTxt.setText(appData.getAccount().getEmail());

        // init edit btn ---------------------------------------------------------------------------
        fragment.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountActivity activity = (AccountActivity) getActivity();
                activity.changeFragment(R.layout.fragment_edit_account);
            }
        });

        // init logout btn -------------------------------------------------------------------------
        fragment.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences accPreferences = getActivity().getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = accPreferences.edit();
                editor.remove("AID");
                editor.remove("ACC_NAME");
                editor.apply();
                Intent intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // init remove btn -------------------------------------------------------------------------
        fragment.findViewById(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropAccount();
            }
        });

        return fragment;
    }

    private void dropAccount() {
        // alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Confirm remove !");
        dialog.setMessage("Sure about removing this account ?");
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String aid = appData.getAccount().getAid();
                int result = AccountController.deleteAccount(db, aid);
                if (result <= 0) {
                    Toast.makeText(getActivity(), "Sorry, something went wrong." + result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Account successfully removed." + result, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("TYPE", "DROP");
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
            }
        });
        dialog.show();
    }

}
