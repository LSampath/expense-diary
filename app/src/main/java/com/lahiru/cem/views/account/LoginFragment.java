package com.lahiru.cem.views.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.adapters.DatabaseHelper;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.models.Account;

public class LoginFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private boolean isRemember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_login, container, false);
        if (container != null) {
            container.removeAllViews();
        }

        dbHelper = new DatabaseHelper(getActivity());

        final StartActivity activity = (StartActivity) LoginFragment.this.getActivity();
        final Account curAccount = activity.getCurAccount();

        TextView accNameTxtView = fragment.findViewById(R.id.tv_pin);
        accNameTxtView.setText(curAccount.getAccountName());

        final EditText pinEdtTxt = fragment.findViewById(R.id.edt_pin);
        pinEdtTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        pinEdtTxt.setHint("");
                        pinEdtTxt.setLetterSpacing(1.2f);
                    } else {
                        if (pinEdtTxt.getText().toString().equals("")) {
                            pinEdtTxt.setLetterSpacing(0f);
                            pinEdtTxt.setHint("PIN");
                        }
                    }
                }
            }
        });

        Button loginBtn = fragment.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = pinEdtTxt.getText().toString();
                if (pin.length() == 5) {
                    Account account = AccountController.authenticateAccount(dbHelper, curAccount.getAid(), pin);

                    if (account != null) {
                        activity.setCurAccount(account);

                        SharedPreferences accPreferences = activity.getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = accPreferences.edit();
                        if (isRemember) {
                            editor.putString("ACC_NAME", curAccount.getAccountName());
                            editor.putString("AID", curAccount.getAid());
                        } else {
                            editor.remove("ACC_NAME");
                            editor.remove("AID");
                        }
                        editor.apply();

                        Intent intent = new Intent("com.lahiru.cem.views.home.HomeActivity");
                        intent.putExtra("ACC_NAME", curAccount.getAccountName());
                        intent.putExtra("AID", curAccount.getAid());
                        startActivity(intent);

                        activity.finish();
                    } else {
                        Toast.makeText(getActivity(), "Wrong PIN", Toast.LENGTH_SHORT).show();
                        pinEdtTxt.setText("");
                    }
                } else {
                    Toast.makeText(getActivity(), "PIN should be five digit long", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CheckBox rememberCheck = fragment.findViewById(R.id.cb_remember);
        rememberCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRemember = b;
            }
        });
        return fragment;
    }
}
