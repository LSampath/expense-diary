package com.lahiru.cem.views.transaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.lahiru.cem.views.adapters.RecycleAdapter;
import com.lahiru.cem.views.adapters.TextValidator;
import com.lahiru.cem.views.home.HomeActivity;
import com.lahiru.cem.views.home.TransactionsFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private Calendar dateCalendar = Calendar.getInstance();
    private Calendar dueDateCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private DatePickerDialog.OnDateSetListener dueDate;
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

    private TextView payedView;
    private Button repaymentBtn;

    private boolean newTrans;
    private String lend_tid;
    private DatabaseHelper db;
    private AppData appData;

    private String amountValid;
    private String dateValid;
    private String noteValid;
    private String dueDateValid;
    private String partnerValid;
    private String sourceValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = new DatabaseHelper(this);
        appData = AppData.getInstance();
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

        payedView = (TextView) findViewById(R.id.tv_payed);
        repaymentBtn = (Button) findViewById(R.id.btn_repayment);

        // initialize amount edit text -------------------------------------------------------------
        amountText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {       // focus next input
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    radioGroup.requestFocus();      // illusion - to release focus
                    hideSoftKeyboard();
                }
                return false;
            }
        });
        amountValid = "Please provide an amount.";
        amountText.addTextChangedListener(new TextValidator(amountText) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0) {
                    amountValid = "Please provide an amount.";
                } else if ((Double.parseDouble(text) > 9999999999.99) || (Double.parseDouble(text) < 0)) {
                    amountValid = "Amount value is much larger.";
                } else {
                    amountValid = "GOOD";
                }
                if (amountValid.equals("GOOD")) {
                    amountText.setTextColor(TransactionActivity.this.getResources().getColor(R.color.black));
                } else {
                    amountText.setTextColor(TransactionActivity.this.getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initializing date picker elements--------------------------------------------------------
        dateValid = "Please select transaction date.";
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, month);
                dateCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateText();
                radioGroup.requestFocus();
            }
        };
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                new DatePickerDialog(TransactionActivity.this, date,
                        dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                categorySpin.setSelection(0);
                radioGroup.requestFocus();      // illusion
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
                // show hide elements for lendings and repayments
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
                radioGroup.requestFocus();  // illusion
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // initialize note text --------------------------------------------------------------------
        noteText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {       // focus next input
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    radioGroup.requestFocus();      // illusion - to release focus
                    hideSoftKeyboard();
                }
                return false;
            }
        });
        noteValid = "GOOD";
        noteText.addTextChangedListener(new TextValidator(noteText) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() > 100) {
                    noteValid = "Note is too long to save.";
                } else {
                    noteValid = "GOOD";
                }
                if (noteValid.equals("GOOD")) {
                    noteText.setTextColor(TransactionActivity.this.getResources().getColor(R.color.black));
                } else {
                    noteText.setTextColor(TransactionActivity.this.getResources().getColor(R.color.outflow_color));
                }
            }
        });

        // initialize due date element -------------------------------------------------------------
        dueDateValid = "Please select due date for repayment.";
        dueDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dueDateCalendar.set(Calendar.YEAR, year);
                dueDateCalendar.set(Calendar.MONTH, month);
                dueDateCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDueDateText();
                radioGroup.requestFocus();      // illusion - to release focus
            }
        };
        dueDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TransactionActivity.this, dueDate,
                        dueDateCalendar.get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH), dueDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // initialize partner text -----------------------------------------------------------------
        partnerValid = "GOOD";
        partnerTxt.addTextChangedListener(new TextValidator(partnerTxt) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() > 50) {
                    partnerValid = "Partner name is too long to save.";
                } else {
                    partnerValid = "GOOD";
                }
                if (partnerValid.equals("GOOD")) {
                    partnerTxt.setTextColor(TransactionActivity.this.getResources().getColor(R.color.black));
                } else {
                    partnerTxt.setTextColor(TransactionActivity.this.getResources().getColor(R.color.outflow_color));
                }
            }
        });

        //---initialize source element--------------------------------------------------------------
        sourceValid = "Please select source lending transaction.";
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
                startActivityForResult(intent, AppData.REPAYMENT_RESULT_CODE);
            }
        });

        // repayment btn and payed amount text view ------------------------------------------------
        repaymentBtn.setVisibility(View.INVISIBLE);
        payedView.setVisibility(View.INVISIBLE);

        // initialize back button on toolbar -------------------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        // this is to edit details -----------------------------------------------------------------
        final String tid = getIntent().getStringExtra("TID");
        if (tid != null) {
            newTrans = false;
            amountValid = "GOOD";
            dateValid = "GOOD";
            Transaction tran = TransactionController.getTransactionDetails(db, tid);

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
                partnerValid = "GOOD";
                dueDateValid = "GOOD";
                partnerTxt.setText(tran.getPartner());
                try {
                    dueDateTxt.setText(to.format(from.parse(tran.getDueDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // load repayment button
                double repayedAmount = TransactionController.getRepayedAmount(db, appData.getAccount().getAid(), tid);
                payedView.setVisibility(View.VISIBLE);
                repaymentBtn.setVisibility(View.VISIBLE);
                final String category;
                final String type;
                if (tran.getCategory().equals("Loan")) {
                    repaymentBtn.setText("Add Collection");
                    payedView.setText("Rs." + repayedAmount + " collected out of Rs." + tran.getAmount());
                    category = "Loan Collection";
                    type = "inflow";
                } else {
                    repaymentBtn.setText("Add Repayment");
                    payedView.setText("Rs." + repayedAmount + " repayed out of Rs." + tran.getAmount());
                    category = "Debt Repayment";
                    type = "outflow";
                }
                repaymentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.lahiru.cem.views.transaction.TransactionActivity");
                        intent.putExtra("LEND_TID", tid);
                        intent.putExtra("CATEGORY", category);
                        intent.putExtra("TYPE", type);
                        startActivity(intent);
                        TransactionActivity.this.finish();
                    }
                });

            } else if (tran.getCategory().equals("Loan Collection") || tran.getCategory().equals("Debt Repayment")) {
                sourceValid = "GOOD";
                Transaction sourceLending = TransactionController.getTransactionDetails(db, tran.getLendTID());
                lend_tid =  tran.getLendTID();
                String text = "Rs. " + sourceLending.getAmount() + " (" + sourceLending.getCategory() + ")";
                if (! sourceLending.getPartner().equals("")) {
                    text += " With, " + sourceLending.getPartner();
                }
                sourceTxt.setText(text);
            }
            categorySpin.setClickable(false);
            findViewById(R.id.category_imgView).setVisibility(View.INVISIBLE);
            findViewById(R.id.inflowRadioBtn).setClickable(false);
            findViewById(R.id.outflowRadioBtn).setClickable(false);
            findViewById(R.id.spin_layout_category).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Cannot change the transaction category, once assigned.", Snackbar.LENGTH_SHORT).show();
                }
            });
            findViewById(R.id.radio_group_type).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "annot change the transaction type, once assigned.", Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        // this is to add repayment details --------------------------------------------------------
        final String lend_tid = getIntent().getStringExtra("LEND_TID");
        if (lend_tid != null) {
            String category = getIntent().getStringExtra("CATEGORY");
            String type = getIntent().getStringExtra("TYPE");

            sourceValid = "GOOD";
            this.lend_tid = lend_tid;
            Transaction sourceLending = TransactionController.getTransactionDetails(db, lend_tid);
            String text = "Rs. " + sourceLending.getAmount() + " (" + sourceLending.getCategory() + ")";
            if (! sourceLending.getPartner().equals("")) {
                text += " With, " + sourceLending.getPartner();
            }
            sourceTxt.setText(text);
            if (type.equals("inflow")) {
                ((RadioButton) findViewById(R.id.inflowRadioBtn)).setChecked(true);
                categoryAdapter.setItems(AppData.getInstance().getInflowIconList(), AppData.getInstance().getInflowNameList());
            } else {
                ((RadioButton) findViewById(R.id.outflowRadioBtn)).setChecked(true);
                categoryAdapter.setItems(AppData.getInstance().getOutflowIconList(), AppData.getInstance().getOutflowNameList());
            }
            categorySpin.setSelection(categoryAdapter.getItemId(category));

            sourceTxt.setClickable(false);
            categorySpin.setClickable(false);
            findViewById(R.id.category_imgView).setVisibility(View.INVISIBLE);
            findViewById(R.id.inflowRadioBtn).setClickable(false);
            findViewById(R.id.outflowRadioBtn).setClickable(false);
            findViewById(R.id.spin_layout_category).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Cannot change the transaction category, once assigned.", Snackbar.LENGTH_SHORT).show();
                }
            });
            findViewById(R.id.radio_group_type).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Cannot change the transaction type, once assigned.", Snackbar.LENGTH_SHORT).show();
                }
            });
            findViewById(R.id.sourceTxt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Cannot change source transaction at this stage.", Snackbar.LENGTH_SHORT).show();
                }
            });
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
        if (requestCode == AppData.REPAYMENT_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            lend_tid =  data.getStringExtra("LEND_TID");
            Transaction sourceLending = TransactionController.getTransactionDetails(db, lend_tid);
            String text = "Rs. " + sourceLending.getAmount() + " (" + sourceLending.getCategory() + ")";
            if (! sourceLending.getPartner().equals("")) {
                text += " With, " + sourceLending.getPartner();
            }
            sourceTxt.setText(text);
            sourceValid = "GOOD";
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard();
        return super.onTouchEvent(event);
    }

    private void updateDateText() {
        String myFormat = "EEE,  dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        EditText dateText = (EditText) findViewById(R.id.dateTxt);
        dateText.setText(dateFormat.format(dateCalendar.getTime()));
        dateValid = "GOOD";
    }

    private void updateDueDateText() {
        String myFormat = "EEE,  dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        EditText dueDateTxt = (EditText) findViewById(R.id.dueDateTxt);
        dueDateTxt.setText(dateFormat.format(dueDateCalendar.getTime()));
        dueDateValid = "GOOD";
    }

    private void saveTransaction() throws ParseException {
        // validation
        if (!amountValid.equals("GOOD")) {
            Toast.makeText(this, amountValid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dateValid.equals("GOOD")) {
            Toast.makeText(this, dateValid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!noteValid.equals("GOOD")) {
            Toast.makeText(this, noteValid, Toast.LENGTH_SHORT).show();
            return;
        }

        // retrieve data
        String amount = amountText.getText().toString();
        amount = String.format("%.2f", Double.parseDouble(amount));

        String date = dateText.getText().toString();
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
            // validation
            if (!dueDateValid.equals("GOOD")) {
                Toast.makeText(this, dueDateValid, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!partnerValid.equals("GOOD")) {
                Toast.makeText(this, partnerValid, Toast.LENGTH_SHORT).show();
                return;
            }

            // retrieve data
            String dueDate = dueDateTxt.getText().toString();
            Date dueParsed = new SimpleDateFormat("EEE,  dd MMM yyyy").parse(dueDate);
            dueDate = new SimpleDateFormat("yyyy-MM-dd").format(dueParsed);
            String partner = partnerTxt.getText().toString();

            transaction.setLendingDetails(partner, dueDate);
        }

        //for repayments and collections, selecting source transactions-----------------------------
        if (category.equals("Debt Repayment") || category.equals("Loan Collection")) {
            // validation
            if (!sourceValid.equals("GOOD")) {
                Toast.makeText(this, sourceValid, Toast.LENGTH_SHORT).show();
                return;
            }
            // retrieve data
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
//            Toast.makeText(this, "Transaction recorded. " + result, Toast.LENGTH_SHORT).show();
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
        return TransactionController.updateTransaction(db, transaction);
    }

    private void removeTransaction() {
        // alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirm remove !");
        dialog.setMessage("Sure about removing this transaction ?");
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String tid = getIntent().getStringExtra("TID");
                DatabaseHelper db = new DatabaseHelper(TransactionActivity.this);
                int result = TransactionController.deleteTransaction(db, tid);

                // alert TransactionFragment when transaction is deleted
                if (result == 1) {
                    Intent intent = new Intent();
                    TransactionActivity.this.setResult(HomeActivity.RESULT_OK, intent);
                    TransactionActivity.this.finish();
                } else {
                    Toast.makeText(TransactionActivity.this, "Something is wrong.", Toast.LENGTH_SHORT).show();
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

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        radioGroup.requestFocus();
        super.onResume();
    }
}
