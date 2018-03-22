package com.lahiru.cem.views.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.Summary;
import com.lahiru.cem.views.adapters.CustomSpinAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SummaryFragment extends Fragment {

    private HomeActivity activity;
    private DatabaseHelper db;
    private AppData appData;

    private EditText fromDateText;
    private EditText toDateText;
    private DatePickerDialog.OnDateSetListener fromDate;
    private DatePickerDialog.OnDateSetListener toDate;
    private Calendar fromDateCalendar = Calendar.getInstance();
    private Calendar toDateCalendar = Calendar.getInstance();
    private CustomSpinAdapter categoryAdapter;
    private Spinner categorySpin;
    private RadioGroup radioGroup;

    private TextView totalInflowView;
    private TextView totalOutflowView;
    private TextView expensiveView;
    private TextView profitableView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_summary, container, false);

        activity = (HomeActivity) getActivity();
        db = new DatabaseHelper(activity);
        appData = AppData.getInstance();

        //initialize text views for total values----------------------------------------------------
        totalInflowView = (TextView) fragment.findViewById(R.id.tv_total_inflow);
        totalOutflowView = (TextView) fragment.findViewById(R.id.tv_total_outflow);
        expensiveView = (TextView) fragment.findViewById(R.id.tv_most_expensive);
        profitableView = (TextView) fragment.findViewById(R.id.tv_most_profit);

        //initialize date picker and listener----FOR DATE-------------------------------------------
        fromDateText = fragment.findViewById(R.id.et_from_date);
        fromDateText.setFocusable(false);
        fromDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                fromDateCalendar.set(Calendar.YEAR, year);
                fromDateCalendar.set(Calendar.MONTH, month);
                fromDateCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "EEE,  dd MMM yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                fromDateText.setText(dateFormat.format(fromDateCalendar.getTime()));
                loadSummery();
            }
        };
        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, fromDate,
                        fromDateCalendar.get(Calendar.YEAR),
                        fromDateCalendar.get(Calendar.MONTH),
                        fromDateCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        //initialize date picker and listener----TO DATE--------------------------------------------
        toDateText = fragment.findViewById(R.id.et_to_date);
        toDateText.setFocusable(false);
        toDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toDateCalendar.set(Calendar.YEAR, year);
                toDateCalendar.set(Calendar.MONTH, month);
                toDateCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "EEE,  dd MMM yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                toDateText.setText(dateFormat.format(toDateCalendar.getTime()));
                loadSummery();
            }
        };
        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, toDate,
                        toDateCalendar.get(Calendar.YEAR),
                        toDateCalendar.get(Calendar.MONTH),
                        toDateCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        //set initial max and min dates-------------------------------------------------------------
        String[] dates = SummaryController.getMaxMinDates(db, appData.getAccount().getAid());

        DateFormat to   = new SimpleDateFormat("EEE,  dd MMM yyyy");
        DateFormat from = new SimpleDateFormat("yyyy-MM-dd");
        if (dates == null) {
            dates = new String[] {to.format(new Date()), to.format(new Date())};
        }
        try {
            fromDateText.setText(to.format(from.parse(dates[1])));
            toDateText.setText(to.format(from.parse(dates[0])));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //initialize radio group and category spinner-----------------------------------------------
        categoryAdapter=new CustomSpinAdapter(activity, appData.getOutflowIconList(), appData.getOutflowNameList());
        categorySpin = fragment.findViewById(R.id.spin_category);
        categorySpin.setAdapter(categoryAdapter);

        radioGroup = fragment.findViewById(R.id.radio_group_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String[] nameList = new String[]{};
                int[] iconList = new int[]{};

                if (i == R.id.rb_outflow) {
                    nameList = appData.getOutflowNameList();
                    iconList = appData.getOutflowIconList();
                }else if (i == R.id.rb_inflow) {
                    nameList = appData.getInflowNameList();
                    iconList = appData.getInflowIconList();
                }
                categoryAdapter.setItems(iconList, nameList);
                categorySpin.setSelection(0);
                loadSummery();
            }
        });
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadSummery();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        radioGroup.check(R.id.rb_outflow);

        loadTotals();

        return fragment;
    }

    private void loadTotals() {
        String[] mostCategory = SummaryController.getMostCategories(db, appData.getAccount().getAid());
        double[] totals = SummaryController.getTotals(db, appData.getAccount().getAid());

        totalInflowView.setText("Rs. " + totals[0]);
        totalOutflowView.setText("Rs. " + totals[1]);
        expensiveView.setText(mostCategory[1]);
        profitableView.setText(mostCategory[0]);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSummery();
        loadTotals();
    }

    private void loadSummery() {
        String fromDate = fromDateText.getText().toString();
        String toDate = toDateText.getText().toString();
        DateFormat from   = new SimpleDateFormat("EEE,  dd MMM yyyy");
        DateFormat to = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fromDate = to.format(from.parse(fromDate));
            toDate = to.format(from.parse(toDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int selected = radioGroup.getCheckedRadioButtonId();
        String inOut = "inflow";
        if (selected == R.id.rb_outflow) {
            inOut = "outflow";
        }

        String category = (String) categorySpin.getAdapter().getItem(categorySpin.getSelectedItemPosition());
        Summary summary = new Summary(appData.getAccount().getAid(), toDate, fromDate, category, inOut);
        summary = SummaryController.getSummaryDetails(db, summary);

        TextView transCountView = (TextView) activity.findViewById(R.id.tv_trans_count);
        TextView totalAmountView = (TextView) activity.findViewById(R.id.tv_total_amount);
        TextView avgAmountView = (TextView) activity.findViewById(R.id.tv_avg_amount);

        transCountView.setText(String.valueOf(summary.getCount()));
        totalAmountView.setText("Rs. " + summary.getTotal());
        avgAmountView.setText("Rs. " + summary.getAverage());
    }

}
