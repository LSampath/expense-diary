package com.lahiru.cem.views.home;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.CharSummary;
import com.lahiru.cem.models.Summary;
import com.lahiru.cem.views.adapters.CustomSpinAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChartsFragment extends Fragment {

    private HomeActivity activity;
    private DatabaseHelper db;
    private AppData appData;

    private PieChart pieChart;
    private CharSummary charSummary;

    private EditText fromDateText;
    private EditText toDateText;
    private DatePickerDialog.OnDateSetListener fromDate;
    private DatePickerDialog.OnDateSetListener toDate;
    private Calendar fromDateCalendar = Calendar.getInstance();
    private Calendar toDateCalendar = Calendar.getInstance();
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_chart, container, false);

        activity = (HomeActivity) getActivity();
        db = new DatabaseHelper(activity);
        appData = AppData.getInstance();

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
                loadChart();
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
                loadChart();
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

        DateFormat to = new SimpleDateFormat("EEE,  dd MMM yyyy");
        DateFormat from = new SimpleDateFormat("yyyy-MM-dd");
        if (dates == null) {
            dates = new String[]{to.format(new Date()), to.format(new Date())};
        }
        try {
            fromDateText.setText(to.format(from.parse(dates[1])));
            toDateText.setText(to.format(from.parse(dates[0])));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //initialize radio group and category spinner-----------------------------------------------
        radioGroup = fragment.findViewById(R.id.radio_group_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                loadChart();
            }
        });
        radioGroup.check(R.id.rb_outflow);

        //initialize and set a listener to pieChart-------------------------------------------------
        pieChart = fragment.findViewById(R.id.pie_chart);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ArrayList<PieEntry> dataList = charSummary.getDataList();
                ArrayList<String> categoryList = charSummary.getCategoryList();
                int index = dataList.indexOf(e);
                String category = categoryList.get(index);
                PieEntry pieEntry = dataList.get(index);
                Toast.makeText(activity, category + ", Total amount : Rs." + pieEntry.getY(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {}
        });


        loadChart();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChart();
    }

    private void loadChart() {
        String fromDate = fromDateText.getText().toString();
        String toDate = toDateText.getText().toString();
        DateFormat from = new SimpleDateFormat("EEE,  dd MMM yyyy");
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
        String aid = appData.getAccount().getAid();
        charSummary = SummaryController.getCategoryWiseDetails(db, new Summary(aid, toDate, fromDate, null, inOut));

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);
        Description ds = new Description();
        ds.setText("");
        pieChart.setDescription(ds);

        PieDataSet pieDataSet = new PieDataSet(charSummary.getDataList(), "Total amounts");
        pieDataSet.setSliceSpace(3);

        ArrayList<Integer> colors = new ArrayList<>();
        int[] colorList = AppData.getInstance().getColorList();
        for (int color: colorList) {
            colors.add(ContextCompat.getColor(activity, color));
        }
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
