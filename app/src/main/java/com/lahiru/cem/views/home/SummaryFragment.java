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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.CharSummary;
import com.lahiru.cem.models.Summary;
import com.lahiru.cem.views.adapters.CustomSpinAdapter;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SummaryFragment extends Fragment {

    private HomeActivity activity;
    private DatabaseHelper db;
    private AppData appData;

    private PieChart pieChart;
    private LineChart lineChart;
    private CharSummary charSummary;

    private EditText fromDateText;
    private EditText toDateText;
    private DatePickerDialog.OnDateSetListener fromDate;
    private DatePickerDialog.OnDateSetListener toDate;
    private Calendar fromDateCalendar = Calendar.getInstance();
    private Calendar toDateCalendar = Calendar.getInstance();
    private RadioGroup radioGroupPie;
    private RadioGroup radioGroupList;
    private CustomSpinAdapter categoryAdapter;
    private Spinner categorySpin;

    private TextView totalInflowView;
    private TextView totalOutflowView;
    private TextView expensiveView;
    private TextView profitableView;
    private TextView balanceView;

    private TextView lineChartMsg;
    private TextView pieChartMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_summary, container, false);

        activity = (HomeActivity) getActivity();
        db = new DatabaseHelper(activity);
        appData = AppData.getInstance();

        // when no data available for charts -------------------------------------------------------
        pieChartMsg = fragment.findViewById(R.id.tv_pie_chart_msg);
        lineChartMsg = fragment.findViewById(R.id.tv_line_chart_msg);

        //initialize text views for total values----------------------------------------------------
        totalInflowView = fragment.findViewById(R.id.tv_total_inflow);
        totalOutflowView = fragment.findViewById(R.id.tv_total_outflow);
        expensiveView = fragment.findViewById(R.id.tv_most_expensive);
        profitableView = fragment.findViewById(R.id.tv_most_profit);
        balanceView = fragment.findViewById(R.id.tv_balance);

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
                loadPieChart();
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
                loadPieChart();
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

        // initialize radio group for pie chart ----------------------------------------------------
        radioGroupPie = fragment.findViewById(R.id.radio_group_pie);
        radioGroupPie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                loadPieChart();
            }
        });
        radioGroupPie.check(R.id.rb_outflow_pie);

        //initialize radio group and category spinner for list chart -------------------------------
        categorySpin = (Spinner) fragment.findViewById(R.id.spin_category);
        radioGroupList = fragment.findViewById(R.id.radio_group_list);
        categoryAdapter = new CustomSpinAdapter(activity,
                AppData.getInstance().getOutflowIconList(), AppData.getInstance().getOutflowNameList());
        categorySpin.setAdapter(categoryAdapter);
        radioGroupList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String[] nameList;
                int[] iconList;
                if (i == R.id.rb_outflow_list) {
                    nameList = AppData.getInstance().getOutflowNameList();
                    iconList = AppData.getInstance().getOutflowIconList();
                } else {
                    nameList = AppData.getInstance().getInflowNameList();
                    iconList = AppData.getInstance().getInflowIconList();
                }
                categoryAdapter.setItems(iconList, nameList);
                categorySpin.setSelection(0);
                loadLineChart();
            }
        });
        radioGroupList.check(R.id.rb_outflow_list);
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadLineChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // initialize and set a listener to pieChart------------------------------------------------
        pieChart = fragment.findViewById(R.id.pie_chart);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ArrayList<PieEntry> dataList = charSummary.getDataList();
                ArrayList<String> categoryList = charSummary.getCategoryList();
                int index = dataList.indexOf(e);
                String category = categoryList.get(index);
                PieEntry pieEntry = dataList.get(index);

                int selected = radioGroupList.getCheckedRadioButtonId();
                int icon;
                int color;
                if (selected == R.id.rb_outflow_list) {
                    icon = AppData.getInstance().getOutflowIcon(category);
                    color = activity.getResources().getColor(R.color.outflow_color);
                } else {
                    icon = AppData.getInstance().getInflowIcon(category);
                    color = activity.getResources().getColor(R.color.inflow_color);
                }

                new StyleableToast
                        .Builder(activity)
                        .text(category + ", Total amount : Rs." + pieEntry.getY())
                        .textColor(Color.BLACK)
                        .iconStart(icon)
                        .iconEnd(icon)
                        .backgroundColor(activity.getResources().getColor(R.color.light_gray))
                        .cornerRadius(3)
                        .show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);
        Description dsPie = new Description();
        dsPie.setText("");
        pieChart.setDescription(dsPie);
        pieChart.getLegend().setEnabled(false);

        // initialize and list chart - listener ?? -------------------------------------------------
        lineChart = fragment.findViewById(R.id.list_chart);
        Description dsLine = new Description();
        dsLine.setText("");
        lineChart.setDescription(dsLine);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.fitScreen();
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1f);
        xAxis.setAxisLineColor(this.getResources().getColor(R.color.light_gray));

        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setDrawZeroLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);

        return fragment;
    }

    private void loadTotals() {
        String[] mostCategory = SummaryController.getMostCategories(db, appData.getAccount().getAid());
        double[] totals = SummaryController.getTotals(db, appData.getAccount().getAid());

        totalInflowView.setText("Rs. " + totals[0]);
        totalOutflowView.setText("Rs. " + totals[1]);
        expensiveView.setText(mostCategory[1]);
        profitableView.setText(mostCategory[0]);
        balanceView.setText("Rs. " + (totals[0] - totals[1]));
    }

    private void loadPieChart() {
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

        int selected = radioGroupPie.getCheckedRadioButtonId();
        String inOut = "inflow";
        if (selected == R.id.rb_outflow_pie) {
            inOut = "outflow";
        }
        String aid = appData.getAccount().getAid();
        charSummary = SummaryController.getCategoryWiseDetails(db, new Summary(aid, toDate, fromDate, null, inOut));

        if (charSummary == null) {
            pieChart.setVisibility(View.INVISIBLE);
            pieChartMsg.setVisibility(View.VISIBLE);
            return;
        } else {
            pieChart.setVisibility(View.VISIBLE);
            pieChartMsg.setVisibility(View.INVISIBLE);
        }

        PieDataSet pieDataSet = new PieDataSet(charSummary.getDataList(), "");
        pieDataSet.setSliceSpace(3);

        ArrayList<Integer> colors = new ArrayList<>();
        int[] colorList = AppData.getInstance().getColorList();
        for (int color : colorList) {
            colors.add(ContextCompat.getColor(activity, color));
        }
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void loadLineChart() {
        int selected = radioGroupList.getCheckedRadioButtonId();
        String inOut = "inflow";
        if (selected == R.id.rb_outflow_list) {
            inOut = "outflow";
        }
        String category = (String) categorySpin.getAdapter().getItem(categorySpin.getSelectedItemPosition());

        String aid = appData.getAccount().getAid();
        ArrayList<String[]> totals = SummaryController.getTotalsByDate(db, aid, category, inOut);

        if (totals.size() < 2) {
            lineChart.setVisibility(View.INVISIBLE);
            lineChartMsg.setVisibility(View.VISIBLE);
            return;
        } else {
            lineChart.setVisibility(View.VISIBLE);
            lineChartMsg.setVisibility(View.INVISIBLE);
        }
        // 1 = total 0 = date
        float min = Float.parseFloat(totals.get(0)[1]);
        float max = min;
        ArrayList<Entry> data = new ArrayList<>();

        for (int i = 0; i < totals.size(); i++) {
            float val = Float.parseFloat(totals.get(i)[1]);
            data.add(new Entry(i, val));
            if (val > max) {
                max = val;
            } else if (val < min) {
                min = val;
            }
        }

        LineDataSet lineDataSet = new LineDataSet(data, null);
        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);
        lineChart.animateXY(1000, 1000);
        lineChart.invalidate();

        lineDataSet.setColor(this.getResources().getColor(R.color.c6));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(this.getResources().getColor(R.color.c11));
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTotals();
        loadPieChart();
        loadLineChart();
    }

}
