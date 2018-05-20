package com.lahiru.cem.views.adapters;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.Transaction;
import com.lahiru.cem.views.home.TodayFragment;
import com.lahiru.cem.views.start.StartActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lahiru on 4/12/2018.
 */

public class NotificationReceiver extends BroadcastReceiver{

    private DatabaseHelper dbHelper;
    private String aid;

    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra("TIME");
        boolean due = intent.getBooleanExtra("DUE_CHECK", false);
        boolean predict = intent.getBooleanExtra("PREDICT", false);

        dbHelper = new DatabaseHelper(context);
        SharedPreferences accPreferences = context.getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
        aid = accPreferences.getString("AID", "");
        if (aid.equals("")) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);


        if (due) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Expense Diary - Due Repayments" + time)
                    .setAutoCancel(true);

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            ArrayList<String> transList = TransactionController.getLendingFromDuedate(dbHelper, aid, today);
            builder.setContentText("number " + transList.size());
            if (transList.size() == 1) {
                String tid = transList.get(0);
                Transaction tran = TransactionController.getTransactionDetails(dbHelper, tid);
                if (tran.getCategory().equals("Loan")) {
                    if (tran.getPartner().equals("")) {
                        builder.setContentText("Loan of Rs." + tran.getAmount() + ", due to collect today");
                    } else {
                        builder.setContentText("Loan to " + tran.getPartner() + ", due to collect today");
                    }
                } else if (tran.getCategory().equals("Debt")) {
                    if (tran.getPartner().equals("")) {
                        builder.setContentText("Debt of Rs." + tran.getAmount() + ", due to repay today");
                    } else {
                        builder.setContentText("Debt from " + tran.getPartner() + ", due to repay today");
                    }
                }
                notificationManager.notify(0, builder.build());
            } else if (transList.size() > 1) {
                builder.setContentText("You have multiple collections/repayments due today");
                notificationManager.notify(0, builder.build());
            }
        }

        if (predict) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Expense Diary - Today predictions" + time)
                    .setAutoCancel(true);

            String[] val = forecastToday();
            if (val[0].equals("-1") && val[1].equals("-1")) {
                return;
            } else if (!val[0].equals("-1") && val[1].equals("-1")) {
                builder.setContentText("Predicted inflow Rs." + val[0]);
                notificationManager.notify(0, builder.build());
            } else if (val[0].equals("-1") && !val[1].equals("-1")) {
                builder.setContentText("Predicted outflow Rs." + val[1]);
                notificationManager.notify(0, builder.build());
            } else {
                builder.setContentText("Predicted inflow Rs." + val[0] + " & " + "Predicted outflow Rs." + val[1]);
                notificationManager.notify(0, builder.build());
            }
        }
    }

    private String[] forecastToday() {
        ArrayList<String[]> inflowData = SummaryController.getTotalsByDate(dbHelper, aid, "ALL", "inflow");
        ArrayList<String[]> outflowData = SummaryController.getTotalsByDate(dbHelper, aid, "ALL", "outflow");

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        String startDate;
        String valI;
        String valO;

        if (inflowData.size() > 3) {
            startDate = inflowData.get(0)[0];
            double[][] inflowValues = new double[inflowData.size()][];
            for (int i = 0; i < inflowData.size(); i++) {
                int x = daysBetween(startDate, inflowData.get(i)[0]) + 1;
                double y = Double.parseDouble(inflowData.get(i)[1]);
                inflowValues[i] = new double[]{x, y};
            }
            double[] Bi = findVariables(inflowValues);
            double Xi = daysBetween(startDate, today);
            valI = String.format("%.2f", (Bi[0] + Bi[1] * Xi));
        } else {
            valI = "-1";
        }
        if (outflowData.size() > 3) {
            startDate = outflowData.get(0)[0];
            double[][] outflowValues = new double[outflowData.size()][];
            for (int i = 0; i < outflowData.size(); i++) {
                int x = daysBetween(startDate, outflowData.get(i)[0]) + 1;
                double y = Double.parseDouble(outflowData.get(i)[1]);
                outflowValues[i] = new double[]{x, y};
            }
            double[] Bo = findVariables(outflowValues);
            double Xo = daysBetween(startDate, today);
            valO = String.format("%.2f", (Bo[0] + Bo[1] * Xo));
        } else {
            valO = "-1";
        }
        return new String[]{valI, valO};
    }

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
}
