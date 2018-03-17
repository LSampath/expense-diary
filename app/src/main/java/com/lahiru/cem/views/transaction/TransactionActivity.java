package com.lahiru.cem.views.transaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.views.adapters.CustomSpinAdapter;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.Transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private Calendar dateCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private DatePickerDialog.OnDateSetListener dueDate;
    private Calendar dueDateCalendar = Calendar.getInstance();
    private android.support.v7.widget.Toolbar toolbar;
    private CustomSpinAdapter categoryAdapter;

    private EditText amountText;
    private Spinner categorySpin;
    private EditText dateText;
    private RadioGroup radioGroup;
    private EditText partnerTxt;
    private EditText dueDateTxt;
    private EditText noteText;
    private TextView sourceTxt;

    private boolean newTrans;
    private String lend_tid;
    private DatabaseHelper db;
    public final int REPAYMENT_RESULT_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);

        newTrans = true;

        amountText = (EditText) findViewById(R.id.amountTxt);
        dateText = (EditText) findViewById(R.id.dateTxt);
        categorySpin = (Spinner) findViewById(R.id.categorySpin);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_type);
        dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
        partnerTxt = (EditText) findViewById(R.id.partnerTxt);
        categorySpin = (Spinner) findViewById(R.id.categorySpin);
        noteText = (EditText) findViewById(R.id.noteTxt);
        sourceTxt = (TextView) findViewById(R.id.sourceTxt);


        //-initializing date picker elements--------------------------------------------------------
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
        categoryAdapter=new CustomSpinAdapter(this,
                AppData.getInstance().getInflowIconList(), AppData.getInstance().getInflowNameList());
        categorySpin.setAdapter(categoryAdapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String[] nameList = new String[]{};
                int[] iconList = new int[]{};

                if (i == R.id.outflowRadioBtn) {
                    nameList = AppData.getInstance().getOutflowNameList();
                    iconList = AppData.getInstance().getOutflowIconList();
                }else if (i == R.id.inflowRadioBtn) {
                    nameList = AppData.getInstance().getInflowNameList();
                    iconList = AppData.getInstance().getInflowIconList();
                }
                categoryAdapter.setItems(iconList, nameList);
            }
        });
        radioGroup.check(R.id.outflowRadioBtn);

        // set listener for category spinner -------------------------------------------------------
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory;
                if (radioGroup.getCheckedRadioButtonId() == R.id.inflowRadioBtn) {
                    selectedCategory = AppData.getInstance().getInflowNameList()[i];
                }else {
                    selectedCategory = AppData.getInstance().getOutflowNameList()[i];
                }

                ImageView dueDateView = (ImageView) findViewById(R.id.dueDateView);
                ImageView partnerView = (ImageView) findViewById(R.id.partnerView);
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
                    sourceTxt.setVisibility(View.VISIBLE);
                    sourceView.setVisibility(View.VISIBLE);
                } else {
                    sourceTxt.setVisibility(View.INVISIBLE);
                    sourceView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //initialize due date element --------------------------------------------------------------
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

        //---initialize source element--------------------------------------------------------------
        sourceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.lahiru.cem.views.transaction.RepaymentActivity");
                String inOut = "outflow";
                if (radioGroup.getCheckedRadioButtonId() == R.id.outflowRadioBtn) {
                    inOut = "inflow";
                }
                // this is a special case where source transactions are negative inOut of the repayment transaction
                intent.putExtra("IN_OUT", inOut);
                startActivityForResult(intent, REPAYMENT_RESULT_CODE);
                Log.i("TEST", "activity started");
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

        //if this is to edit details-----------------------------------------------------------------
        String aid = getIntent().getStringExtra("TID");
        if (aid != null) {
            newTrans = false;
            Transaction tran = TransactionController.getTransactionDetails(db, aid);

            DateFormat to   = new SimpleDateFormat("EEE,  dd MMM yyyy");
            DateFormat from = new SimpleDateFormat("yyyy-MM-dd");

            amountText.setText(tran.getAmount());
            noteText.setText(tran.getNote());
            try {
                dateText.setText(to.format(from.parse(tran.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (tran.getInOut().equals("inflow")) {
                ((RadioButton) findViewById(R.id.inflowRadioBtn)).setChecked(true);
                categoryAdapter.setItems(AppData.getInstance().getInflowIconList(), AppData.getInstance().getInflowNameList());
            } else {
                ((RadioButton) findViewById(R.id.outflowRadioBtn)).setChecked(true);
                categoryAdapter.setItems(AppData.getInstance().getOutflowIconList(), AppData.getInstance().getOutflowNameList());
            }
            categorySpin.setSelection(categoryAdapter.getItemId(tran.getCategory()));

            if (tran.getCategory().equals("Loan") || tran.getCategory().equals("Debt")) {
                partnerTxt.setText(tran.getPartner());
                try {
                    dueDateTxt.setText(to.format(from.parse(tran.getDueDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (tran.getCategory().equals("Loan Collection") || tran.getCategory().equals("Debt Repayment")) {
                Transaction sourceLending = TransactionController.getTransactionDetails(db, tran.getLendTID());
                lend_tid =  tran.getLendTID();
                String text = "Rs. " + sourceLending.getAmount() + " (" + sourceLending.getCategory() + ")";
                if (! sourceLending.getPartner().equals("")) {
                    text += " With, " + sourceLending.getPartner();
                }
                sourceTxt.setText(text);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transaction, menu);

        MenuItem removeItem = menu.findItem(R.id.action_remove);
        if (!newTrans) {
            removeItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            try {
                saveTransaction();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == R.id.action_remove) {
            removeTransaction();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REPAYMENT_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            lend_tid =  data.getStringExtra("LEND_TID");
            Transaction sourceLending = TransactionController.getTransactionDetails(db, lend_tid);
            String text = "Rs. " + sourceLending.getAmount() + " (" + sourceLending.getCategory() + ")";
            if (! sourceLending.getPartner().equals("")) {
                text += " With, " + sourceLending.getPartner();
            }
            sourceTxt.setText(text);
        }
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


    private void saveTransaction() throws ParseException {
        String amount = amountText.getText().toString();
        if (amount.equals("")) {
            Toast.makeText(this, "Amount should be provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        amount = String.format("%.2f", Double.parseDouble(amount));

        String date = dateText.getText().toString();
        if (date.equals("")) {
            Toast.makeText(this, "DayDate should be provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        Date parsed = new SimpleDateFormat("EEE,  dd MMM yyyy").parse(date);

        String day = new SimpleDateFormat("EEE").format(parsed);
        date = new SimpleDateFormat("yyyy-MM-dd").format(parsed);

        int selected = radioGroup.getCheckedRadioButtonId();
        String inOut = "inflow";
        if (selected == R.id.outflowRadioBtn) {
            inOut = "outflow";
        }
        String category = (String) categorySpin.getAdapter().getItem(categorySpin.getSelectedItemPosition());
        String note = noteText.getText().toString();

        Transaction transaction = new Transaction(null, amount, date, day, inOut, category, note);

        //for Loans and Debts, taking due date and partner------------------------------------------
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

        //for repayments and collections, selecting source transactions-----------------------------
        if (category.equals("Debt Repayment") || category.equals("Loan Collection")) {
            transaction.setRepaymentDetails(lend_tid);
        }

        long result = -1;
        if (newTrans) {
            result = insertTransaction(transaction);
        } else {
            result = updateTransaction(transaction);
        }
        if (result != -1) {
            finish();
            Toast.makeText(this, "Transaction recorded. + " + result, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Something is wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private long insertTransaction(Transaction transaction) {
        DatabaseHelper db = new DatabaseHelper(this);
        return TransactionController.insertTransaction(db, transaction);
    }

    private long updateTransaction(Transaction transaction) {
        DatabaseHelper db = new DatabaseHelper(this);
        String tid = getIntent().getStringExtra("TID");
        transaction.setTid(tid);
        Toast.makeText(this, "Going to update", Toast.LENGTH_SHORT).show();
        return TransactionController.updateTransaction(db, transaction);
    }

    public void removeTransaction() {
        String tid = getIntent().getStringExtra("TID");
        DatabaseHelper db = new DatabaseHelper(this);
        int result = TransactionController.deleteTransaction(db, tid);
        if (result == 1) {
            finish();
            Toast.makeText(this, "Transaction removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something is wrong.", Toast.LENGTH_SHORT).show();
        }
    }


}
