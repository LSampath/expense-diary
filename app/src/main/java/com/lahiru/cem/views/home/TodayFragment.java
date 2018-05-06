package com.lahiru.cem.views.home;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.ListItem;
import com.lahiru.cem.views.adapters.RecycleAdapter;
import com.tomer.fadingtextview.FadingTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TodayFragment extends Fragment {

    private HomeActivity activity;
    private DatabaseHelper db;
    private AppData appData;

    private TextView inflowForecastTxt;
    private TextView outflowForecastTxt;
    private TextView inflowRateTxt;
    private TextView outflowRateTxt;

    private FadingTextView todayFadingTxt;

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private ArrayList<ListItem> listItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_today, container, false);

        activity = (HomeActivity) getActivity();
        db = new DatabaseHelper(activity);
        appData = AppData.getInstance();

        inflowForecastTxt = fragment.findViewById(R.id.tv_forecast_inflow);
        outflowForecastTxt = fragment.findViewById(R.id.tv_forecast_outflow);
        inflowRateTxt = fragment.findViewById(R.id.tv_rate_inflow);
        outflowRateTxt = fragment.findViewById(R.id.tv_rate_outflow);

        // initialize today fading text view -------------------------------------------------------
        todayFadingTxt = fragment.findViewById(R.id.tv_today);
        String val1 = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(new Date());
        String val2 = new SimpleDateFormat("dd", Locale.ENGLISH).format(new Date());
        String val3 = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
        if (val2.equals("01")) {
            val2 = "1st of " + val1;
        } else if (val2.equals("02")) {
            val2 = "2nd of " + val1;
        } else if (val2.equals("03")) {
            val2 = "3rd of " + val1;
        } else  {
            val2 = val2 + "th of " + val1;
        }
        todayFadingTxt.setTexts(new String[]{val3, val2});
//        todayFadingTxt.setTimeout(5, TimeUnit.SECONDS);
//        todayFadingTxt.restart();

        // initialize due dates layout -------------------------------------------------------------
        recyclerView = fragment.findViewById(R.id.recycle_view_due);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        listItems = new ArrayList<>();
        adapter = new RecycleAdapter(listItems, activity);
        recyclerView.setAdapter(adapter);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        forecastToday();
        loadDueTransactions();
    }

    private void forecastToday() {
        ArrayList<String[]> inflowData = SummaryController.getTotalsByDate(db, appData.getAccount().getAid(), "ALL", "inflow");
        ArrayList<String[]> outflowData = SummaryController.getTotalsByDate(db, appData.getAccount().getAid(), "ALL", "outflow");

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        String startDate;
        String val;
        double inflowTotal = 0;
        double outflowTotal = 0;

        if (inflowData.size() > 3) {
            startDate = inflowData.get(0)[0];
            double[][] inflowValues = new double[inflowData.size()][];
            for (int i = 0; i < inflowData.size(); i++) {
                int x = daysBetween(startDate, inflowData.get(i)[0]) + 1;
                double y = Double.parseDouble(inflowData.get(i)[1]);
                inflowValues[i] = new double[]{x, y};
                inflowTotal += y;
            }
            double[] Bi = findVariables(inflowValues);
            double Xi = daysBetween(startDate, today);
            val = String.format("%.2f", (Bi[0] + Bi[1] * Xi));
            inflowForecastTxt.setText("Rs. " + val);
            String amount = String.format("%.2f", (inflowTotal / Xi));
            inflowRateTxt.setText("Rs. " + amount + " /day");
        } else {
            inflowForecastTxt.setText("UNABLE");
            inflowRateTxt.setText("UNABLE");
        }
        if (outflowData.size() > 3) {
            startDate = outflowData.get(0)[0];
            double[][] outflowValues = new double[outflowData.size()][];
            for (int i = 0; i < outflowData.size(); i++) {
                int x = daysBetween(startDate, outflowData.get(i)[0]) + 1;
                double y = Double.parseDouble(outflowData.get(i)[1]);
                outflowValues[i] = new double[]{x, y};
                outflowTotal += y;
            }
            double[] Bo = findVariables(outflowValues);
            double Xo = daysBetween(startDate, today);
            val = String.format("%.2f", (Bo[0] + Bo[1] * Xo));
            outflowForecastTxt.setText("Rs. " + val);
            String amount = String.format("%.2f", (outflowTotal / Xo));
            outflowRateTxt.setText("Rs. " + amount + " /day");
        } else {
            outflowForecastTxt.setText("UNABLE");
            outflowRateTxt.setText("UNABLE");
        }
    }

    private int daysBetween(String start, String end) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            startDate.setTime(sdf.parse(start));
            endDate.setTime(sdf.parse(end));
        } catch (ParseException ex) {
            Logger.getLogger(TodayFragment.class.getName()).log(Level.SEVERE, null, ex);
        }
        //get difference between dates
        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
        long endInstant = endDate.getTimeInMillis();
        int presumedDays = (int) ((endInstant - startDate.getTimeInMillis()) / MILLIS_IN_DAY);
        Calendar cursor = (Calendar) startDate.clone();
        cursor.add(Calendar.DAY_OF_YEAR, presumedDays);
        long instant = cursor.getTimeInMillis();
        if (instant == endInstant) {
            return presumedDays;
        }
        final int step = instant < endInstant ? 1 : -1;
        do {
            cursor.add(Calendar.DAY_OF_MONTH, step);
            presumedDays += step;
        } while (cursor.getTimeInMillis() != endInstant);
        return presumedDays;
    }

    // find b0 and b1 co-efficients for Linear Regression equation ( Y = B0 + B1 * X ) -------------
    private double[] findVariables(double[][] data) {
        double meanX = 0;
        double meanY = 0;
        for (double[] d : data) {
            meanX += d[0];
            meanY += d[1];
        }
        meanX = meanX / data.length;
        meanY = meanY / data.length;

        double b1_up = 0;
        double b1_down = 0;
        for (double[] d : data) {
            b1_up += (d[0] - meanX) * (d[1] - meanY);
            b1_down += (d[0] - meanX) * (d[0] - meanX);
        }
        double b1 = b1_up / b1_down;
        double b0 = meanY - b1 * meanX;

        return new double[]{b0, b1};
    }

    public void loadDueTransactions() {
        DatabaseHelper db = new DatabaseHelper(activity);
        listItems = new ArrayList<>();
        String aid = AppData.getInstance().getAccount().getAid();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        ArrayList<String> dates = TransactionController.getDueRepaymentDates(db, aid, today);
        for (String date: dates) {
            listItems.add(new ListItem(ListItem.DATE_ITEM, date));
            ArrayList<String> transList = TransactionController.getLendings(db, aid, date, "ALL");
            for (String tid: transList) {
                listItems.add(new ListItem(ListItem.TRANSACTION_ITEM, tid));
            }
        }
        adapter.setListItems(listItems);
    }
}
