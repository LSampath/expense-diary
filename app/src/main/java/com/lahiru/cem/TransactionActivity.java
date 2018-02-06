package com.lahiru.cem;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private Calendar dateCalendar = Calendar.getInstance();
    private EditText dateText;
    private DatePickerDialog.OnDateSetListener date;
    private Spinner categorySpin;
    private EditText dueDateTxt;
    private DatePickerDialog.OnDateSetListener dueDate;
    private Calendar dueDateCalendar = Calendar.getInstance();
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // initializing date picker elements----------------------------------------------------
        dateText = (EditText) findViewById(R.id.dateTxt);
        dateText.setFocusable(false);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, month);
                dateCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateText();
            }
        };

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TransactionActivity.this, date,
                        dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // initializing category spinner and radio buttons ---------------------------------------
        categorySpin = (Spinner) findViewById(R.id.categorySpin);

        @SuppressLint("ResourceType")
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.drawable.spinner_list_item);
        categorySpin.setAdapter(spinnerAdapter);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Spinner categorySpin = (Spinner) findViewById(R.id.categorySpin);
                ArrayAdapter<String> adapter = (ArrayAdapter) categorySpin.getAdapter();

                String[] categoryList = new String[]{};

                if (i == R.id.expenseRadioBtn) {
                    categoryList = new String[] {
                        "Food & Beverage", "Bills", "Transport", "Shopping", "Lover", "Health", "Fun", "Gift", "Family", "Education", "Donations", "Other",
                        "Loan", "Debt Repayment"
                    };
                }else if (i == R.id.incomeRadioBtn) {
                    categoryList = new String[] {
                        "Salary", "Gift", "Award", "Interest", "Selling",
                        "Debt", "Loan Repayment"
                    };
                }
                adapter.clear();
                adapter.addAll(categoryList);
            }
        });
        radioGroup.check(R.id.expenseRadioBtn);

        // set listener for category spinner -----------------------------------------------------
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = adapterView.getItemAtPosition(i).toString();

                EditText dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
                EditText partnerTxt = (EditText) findViewById(R.id.partnerTxt);

                if (category == "Loan" || category == "Debt") {
                    dueDateTxt.setVisibility(View.VISIBLE);
                    partnerTxt.setVisibility(View.VISIBLE);
                } else {
                    dueDateTxt.setVisibility(View.INVISIBLE);
                    partnerTxt.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //initialize due date element -----------------------------------------------------------
        dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
        dueDateTxt.setFocusable(false);

        dueDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dueDateCalendar.set(Calendar.YEAR, year);
                dueDateCalendar.set(Calendar.MONTH, month);
                dueDateCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDueDateText();
            }
        };

        dueDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TransactionActivity.this, dueDate,
                        dueDateCalendar.get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH), dueDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // initialize back button on toolbar -----------------------------------------------------
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transaction, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void updateDateText() {
        String myFormat = "MMM, dd, yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        dateText.setText(dateFormat.format(dateCalendar.getTime()));
    }

    private void updateDueDateText() {
        String myFormat = "MMM, dd, yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        dueDateTxt.setText(dateFormat.format(dueDateCalendar.getTime()));
    }

}
