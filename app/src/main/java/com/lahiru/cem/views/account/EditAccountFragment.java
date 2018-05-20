package com.lahiru.cem.views.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.AccountController;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.Account;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.views.adapters.TextValidator;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class EditAccountFragment extends Fragment {

    private DatabaseHelper db;
    private AppData appData;

    private EditText accNameTxt;
    private EditText emailTxt;
    private EditText newPinTxt;
    private EditText confirmPinTxt;
    private EditText currentPinTxt;
    private Button saveBtn;
    private TextView accountTitleTxt;

    private String accNameValid;
    private String emailValid;
    private String newPinValid;
    private String confirmPinValid;
    private String currentPinValid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_edit_account, container, false);

        db = new DatabaseHelper(fragment.getContext());
        appData = AppData.getInstance();

        accNameTxt = fragment.findViewById(R.id.edt_acc_name);
        emailTxt = fragment.findViewById(R.id.edt_email);
        newPinTxt = fragment.findViewById(R.id.edt_new_pin);
        confirmPinTxt = fragment.findViewById(R.id.edt_confirm_pin);
        currentPinTxt = fragment.findViewById(R.id.edt_current_pin);
        saveBtn = fragment.findViewById(R.id.btn_save);
        accountTitleTxt = fragment.findViewById(R.id.tv_account_title);

        // initialize account name text ------------------------------------------------------------
//        accNameTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if (b) {
//                        accNameTxt.setHint("");
//                    } else {
//                        if (accNameTxt.getText().toString().equals("")) {
//                            accNameTxt.setHint("account name");
//                        }
//                    }
//                }
//            }
//        });
        accNameValid = "Please provide a name.";
        accNameTxt.addTextChangedListener(new TextValidator(accNameTxt) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0) {
                    accNameValid = "Please provide a name.";
                } else if (text.length() > 20) {
                    accNameValid = "Account name is too long.";
                } else {
                    accNameValid = "GOOD";
                }
                if (accNameValid.equals("GOOD")) {
                    accNameTxt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    accNameTxt.setTextColor(getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initialize email text -------------------------------------------------------------------
//        emailTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if (b) {
//                        emailTxt.setHint("");
//                    } else {
//                        if (emailTxt.getText().toString().equals("")) {
//                            emailTxt.setHint("email");
//                        }
//                    }
//                }
//            }
//        });
        emailValid = "Please provide an email.";
        emailTxt.addTextChangedListener(new TextValidator(emailTxt) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0) {
                    emailValid = "Please provide an email.";
                } else if (text.length() > 100) {
                    emailValid = "email address is too long.";
                } else {
                    emailValid = "GOOD";
                }
                if (emailValid.equals("GOOD")) {
                    emailTxt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    emailTxt.setTextColor(getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initialize new pin ----------------------------------------------------------------------
        newPinTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        newPinTxt.setHint("");
                        newPinTxt.setLetterSpacing(1.2f);
                    } else {
                        if (newPinTxt.getText().toString().equals("")) {
                            newPinTxt.setLetterSpacing(0f);
                            newPinTxt.setHint("new PIN");
                        }
                    }
                }
            }
        });
        newPinValid = "GOOD";
        newPinTxt.addTextChangedListener(new TextValidator(newPinTxt) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() != 0 && text.length() != 5) {
                    newPinValid = "PIN must be 5 character long.";
                } else {
                    newPinValid = "GOOD";
                }
                if (newPinValid.equals("GOOD")) {
                    newPinTxt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    newPinTxt.setTextColor(getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initialize confirm pin text -------------------------------------------------------------
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
        confirmPinValid = "GOOD";
        confirmPinTxt.addTextChangedListener(new TextValidator(confirmPinTxt) {
            @Override
            public void validate(TextView textView, String text) {
                String pin = newPinTxt.getText().toString();
                if (text.length() != 0 && text.length() != 5) {
                    confirmPinValid = "PIN must be 5 character long.";
                } else if (!text.equals(pin)) {
                    confirmPinValid = "PIN does not match.";
                } else {
                    confirmPinValid = "GOOD";
                }
                if (confirmPinValid.equals("GOOD")) {
                    confirmPinTxt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    confirmPinTxt.setTextColor(getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initialize current pin ------------------------------------------------------------------
        currentPinTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (b) {
                        currentPinTxt.setHint("");
                        currentPinTxt.setLetterSpacing(1.2f);
                    } else {
                        if (currentPinTxt.getText().toString().equals("")) {
                            currentPinTxt.setLetterSpacing(0f);
                            currentPinTxt.setHint("current PIN");
                        }
                    }
                }
            }
        });
        currentPinValid = "Please provide your current PIN.";
        currentPinTxt.addTextChangedListener(new TextValidator(currentPinTxt) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0) {
                    currentPinValid = "Please provide your current PIN.";
                } else if (text.length() != 5) {
                    currentPinValid = "PIN must be 5 character long.";
                } else {
                    currentPinValid = "GOOD";
                }
                if (currentPinValid.equals("GOOD")) {
                    currentPinTxt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    currentPinTxt.setTextColor(getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initialize save and drop buttons --------------------------------------------------------
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });

        // load account details to fields ----------------------------------------------------------
        accNameTxt.setText(appData.getAccount().getAccountName());
        emailTxt.setText(appData.getAccount().getEmail());

        // on touch event listener to hide softkeyboard --------------------------------------------
        fragment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });

        return fragment;
    }

    private void saveDetails() {
        // validation
        if (!accNameValid.equals("GOOD")) {
            Toast.makeText(getActivity(), accNameValid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!emailValid.equals("GOOD")) {
            Toast.makeText(getActivity(), emailValid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPinValid.equals("GOOD")) {
            Toast.makeText(getActivity(), newPinValid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmPinValid.equals("GOOD")) {
            Toast.makeText(getActivity(), confirmPinValid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!currentPinValid.equals("GOOD")) {
            Toast.makeText(getActivity(), currentPinValid, Toast.LENGTH_SHORT).show();
            return;
        }
        // get details
        String aid = appData.getAccount().getAid();
        String accName = accNameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String pin = confirmPinTxt.getText().toString();
        String currentPin = currentPinTxt.getText().toString();
        if (pin.equals("")) {
            pin = currentPin;
        }
        Account account = new Account(aid, accName, email, pin);

        // then save
        long result = AccountController.updateAccount(db, account, currentPin);
        if (result == -99) {
            Toast.makeText(getActivity(), "Sorry, Account name already in use." + result, Toast.LENGTH_SHORT).show();
        } else if (result <= 0 ) {
            Toast.makeText(getActivity(), "Sorry, something went wrong." + result, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Successfully updated." + result, Toast.LENGTH_LONG).show();

            appData.setAccount(account);
            SharedPreferences accPreferences = getActivity().getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = accPreferences.edit();
            editor.putString("ACC_NAME", account.getAccountName());
            editor.apply();

            Intent intent = new Intent();
            intent.putExtra("TYPE", "UPDATE");
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

}
