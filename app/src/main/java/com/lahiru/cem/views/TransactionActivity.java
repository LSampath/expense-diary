package com.lahiru.cem.views;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.adapters.CustomSpinAdapter;
import com.lahiru.cem.adapters.DatabaseHelper;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private Calendar dateCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private Spinner categorySpin;
    private DatePickerDialog.OnDateSetListener dueDate;
    private Calendar dueDateCalendar = Calendar.getInstance();
    private android.support.v7.widget.Toolbar toolbar;

    private String[] outflowNameList = {
            "Food & Beverage", "Bills & Fees", "Shopping", "Fun & Love", "Travel", "Health", "Family", "Education", "Business",
            "Other", "Loan", "Debt Repayment"
    };
    private int[] outflowIconList = {
            R.drawable.icn_food, R.drawable.icn_bills,R.drawable.icn_shopping, R.drawable.icn_love, R.drawable.icn_travel,
            R.drawable.icn_health, R.drawable.icn_family, R.drawable.icn_education, R.drawable.icn_business,
            R.drawable.icn_other, R.drawable.icn_loan, R.drawable.icn_replay
    };
    private String[] inflowNameList = {
            "Salary", "Business", "Interest", "Other", "Debt", "Loan Collection"
    };
    private int[] inflowIconList = {
            R.drawable.icn_salary, R.drawable.icn_business, R.drawable.icn_interest, R.drawable.icn_other,
            R.drawable.icn_debt, R.drawable.icn_replay
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseHelper db = new DatabaseHelper(this);

        // initializing date picker elements--------------------------------------------------------
         EditText dateText = (EditText) findViewById(R.id.dateTxt);
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

        // initializing category spinner and radio buttons -----------------------------------------
        categorySpin = (Spinner) findViewById(R.id.categorySpin);

        CustomSpinAdapter customAdapter=new CustomSpinAdapter(getApplicationContext(), inflowIconList, inflowNameList);
        categorySpin.setAdapter(customAdapter);

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Spinner categorySpin = (Spinner) findViewById(R.id.categorySpin);
                CustomSpinAdapter adapter = (CustomSpinAdapter) categorySpin.getAdapter();

                String[] nameList = new String[]{};
                int[] iconList = new int[]{};

                if (i == R.id.outflowRadioBtn) {
                    nameList = outflowNameList;
                    iconList = outflowIconList;
                }else if (i == R.id.inflowRadioBtn) {
                    nameList = inflowNameList;
                    iconList = inflowIconList;
                }
                adapter.setItems(iconList, nameList);
            }
        });
        radioGroup.check(R.id.outflowRadioBtn);

        // set listener for category spinner -------------------------------------------------------
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = null;
                if (radioGroup.getCheckedRadioButtonId() == R.id.inflowRadioBtn) {
                    selectedCategory = inflowNameList[i];
                }else {
                    selectedCategory = outflowNameList[i];
                }

                EditText dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
                EditText partnerTxt = (EditText) findViewById(R.id.partnerTxt);
                ImageView dueDateView = (ImageView) findViewById(R.id.dueDateView);
                ImageView partnerView = (ImageView) findViewById(R.id.partnerView);

                RelativeLayout sourceSpinLayout = (RelativeLayout) findViewById(R.id.sourceSpinnerLayout);
                ImageView sourceView = (ImageView) findViewById(R.id.sourceView);

                if (selectedCategory.equals("Loan") || selectedCategory.equals("Debt")) {
                    dueDateTxt.setVisibility(View.VISIBLE);
                    partnerTxt.setVisibility(View.VISIBLE);
                    dueDateView.setVisibility(View.VISIBLE);
                    partnerView.setVisibility(View.VISIBLE);
                } else {
                    dueDateTxt.setVisibility(View.INVISIBLE);
                    partnerTxt.setVisibility(View.INVISIBLE);
                    dueDateView.setVisibility(View.INVISIBLE);
                    partnerView.setVisibility(View.INVISIBLE);
                }
                if (selectedCategory.equals("Loan Collection") || selectedCategory.equals("Debt Repayment")) {
                    sourceSpinLayout.setVisibility(View.VISIBLE);
                    sourceView.setVisibility(View.VISIBLE);
                } else {
                    sourceSpinLayout.setVisibility(View.INVISIBLE);
                    sourceView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        //initialize due date element --------------------------------------------------------------
        EditText dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
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

        // initialize back button on toolbar -------------------------------------------------------
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionSave) {
            try {
                insertTransactionToDB();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    private void updateDateText() {
        String myFormat = "EEE,  dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        EditText dateText = (EditText) findViewById(R.id.dateTxt);
        dateText.setText(dateFormat.format(dateCalendar.getTime()));
    }

    private void updateDueDateText() {
        String myFormat = "EEE,  dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        EditText dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
        dueDateTxt.setText(dateFormat.format(dueDateCalendar.getTime()));
    }


    private void insertTransactionToDB() throws ParseException {
        EditText amountText = (EditText) findViewById(R.id.amountTxt);
        String amount = amountText.getText().toString();
        if (amount.equals("")) {
            Toast.makeText(this, "Amount should be provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        amount = String.format("%.2f", Double.parseDouble(amount));

        EditText dateText = (EditText) findViewById(R.id.dateTxt);
        String date = dateText.getText().toString();
        if (date.equals("")) {
            Toast.makeText(this, "DayDate should be provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        Date parsed = new SimpleDateFormat("EEE,  dd MMM yyyy").parse(date);
        String day = new SimpleDateFormat("EEE").format(parsed);
        date = new SimpleDateFormat("yyyy-MM-dd").format(parsed);

        RadioGroup inOutRadio = (RadioGroup) findViewById(R.id.radioGroup);
        int selected = inOutRadio.getCheckedRadioButtonId();
        String inOut = "inflow";
        if (selected == R.id.outflowRadioBtn) {
            inOut = "outflow";
        }

        Spinner categorySpin = (Spinner) findViewById(R.id.categorySpin);
        String category = (String) categorySpin.getAdapter().getItem(categorySpin.getSelectedItemPosition());

        EditText noteText = (EditText) findViewById(R.id.noteTxt);
        String note = noteText.getText().toString();

        Transaction transaction = new Transaction(null, amount, date, day, inOut, category, note);

        if (category.equals("Loan") || category.equals("Debt")) {
            EditText dueDateText = (EditText) findViewById(R.id.dueDateTxt);
            String dueDate = dueDateText.getText().toString();
            if (date.equals("")) {
                Toast.makeText(this, "Due date should be provided.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Date dueParsed = new SimpleDateFormat("EEE,  dd MMM yyyy").parse(dueDate);
            dueDate = new SimpleDateFormat("yyyy-MM-dd").format(dueParsed);

            EditText partnerText = (EditText) findViewById(R.id.partnerTxt);
            String partner = partnerText.getText().toString();

            transaction.setLendingDetails(partner, dueDate);
        }
        if (category.equals("Debt Repayment") || category.equals("Loan Collection")) {
            // load list of loans
        }

        DatabaseHelper db = new DatabaseHelper(this);
        long i = TransactionController.insertTransaction(db, transaction);

        Toast.makeText(this, i+"", Toast.LENGTH_SHORT).show();
    }

}
