package com.lahiru.cem.views.start;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewAccountFragment extends Fragment {

    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_new_account, container, false);
        if (container != null) {
            container.removeAllViews();
        }
        dbHelper = new DatabaseHelper(getActivity());

        //------------initialize view components----------------------------------------------------
        Button newAccBtn = fragment.findViewById(R.id.btn_submit);
        newAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertNewAccountToDB(fragment);
            }
        });

        final EditText pinTxt = fragment.findViewById(R.id.edt_pin);
        pinTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        pinTxt.setHint("");
                        pinTxt.setLetterSpacing(1.2f);
                    } else {
                        if (pinTxt.getText().toString().equals("")) {
                            pinTxt.setLetterSpacing(0f);
                            pinTxt.setHint("PIN");
                        }
                    }
                }
            }
        });
        final EditText confirmPinTxt = fragment.findViewById(R.id.edt_confirm_pin);
        confirmPinTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        confirmPinTxt.setHint("");
                        confirmPinTxt.setLetterSpacing(1.2f);
                    } else {
                        if (confirmPinTxt.getText().toString().equals("")) {
                            confirmPinTxt.setLetterSpacing(0f);
                            confirmPinTxt.setHint("confirm PIN");
                        }
                    }
                }
            }
        });
        final EditText emailTxt = fragment.findViewById(R.id.edt_email);
        emailTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        emailTxt.setHint("");
                    } else {
                        if (emailTxt.getText().toString().equals("")) {
                            emailTxt.setHint("email");
                        }
                    }
                }
            }
        });
        final EditText accNameTxt = fragment.findViewById(R.id.edt_acc_name);
        accNameTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        accNameTxt.setHint("");
                    } else {
                        if (accNameTxt.getText().toString().equals("")) {
                            accNameTxt.setHint("account name");
                        }
                    }
                }
            }
        });
        return fragment;
    }

    private void insertNewAccountToDB(View fragment) {
        DatabaseHelper db = new DatabaseHelper(getActivity());

        EditText accNameTxt = fragment.findViewById(R.id.edt_acc_name);
        String accName = accNameTxt.getText().toString();
        if (accName.equals("")) {
            Toast.makeText(getActivity(), "Account name should be provided.", Toast.LENGTH_SHORT).show();
            return;
        }else if (accName.length() >= 20) {
            Toast.makeText(getActivity(), "Account name is too long.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText emailTxt = fragment.findViewById(R.id.edt_email);
        String email = emailTxt.getText().toString();
        if (email.equals("")) {
            Toast.makeText(getActivity(), "Email address need to be provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        String regEx = "[[a-z|A-Z|0-9][a-z|A-Z|0-9|.]?[a-z|A-Z|0-9]]+[a-z]+[.[a-z]]*";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        boolean isValidEmail = matcher.find();
        if (! isValidEmail) {
            Toast.makeText(getActivity(), "Email address is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }else if (email.length() >= 100) {
            Toast.makeText(getActivity(), "email address is too long.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText pinTxt = fragment.findViewById(R.id.edt_pin);
        String pin = pinTxt.getText().toString();
        if (pin.equals("")) {
            Toast.makeText(getActivity(), "PIN number need to be provided.", Toast.LENGTH_SHORT).show();
            return;
        }else if (pin.length() > 5) {
            Toast.makeText(getActivity(), "PIN need to be 5 digits long.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText confirmPinTxt = fragment.findViewById(R.id.edt_confirm_pin);
        String confirmPin = confirmPinTxt.getText().toString();
        if (confirmPin.equals("")) {
            Toast.makeText(getActivity(), "PIN number need to be confirmed.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (confirmPin.length() > 5) {
            Toast.makeText(getActivity(), "PIN need to be 5 digits long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (! confirmPin.equals(pin)) {
            Toast.makeText(getActivity(), "PIN confirmation failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = AccountController.insertAccount(dbHelper, new Account(null, accName, email, pin));
        if (result == -1) {
            Toast.makeText(getActivity(), "Account name already exists.", Toast.LENGTH_SHORT).show();
            return;
        }else {
//            Toast.makeText(getActivity(), "Account created with AID:"+ result, Toast.LENGTH_SHORT).show();
            AppData.getInstance().setAccount(new Account(String.valueOf(result), accName, email, null));

            Intent intent = new Intent("com.lahiru.cem.views.home.HomeActivity");
            startActivity(intent);
            getActivity().finish();
        }
    }

}
