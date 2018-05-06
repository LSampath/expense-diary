package com.lahiru.cem.views.adapters;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.Transaction;
import com.lahiru.cem.views.start.StartActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Lahiru on 4/12/2018.
 */

public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, StartActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setContentTitle("Expense Diary - Due Repayments")
                .setAutoCancel(true);

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SharedPreferences accPreferences = context.getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
        String aid = accPreferences.getString("AID", "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

        ArrayList<String> dates = TransactionController.getDueRepaymentDates(dbHelper, aid, today);
        if (dates.size() != 0) {
            String date = today;
            DateFormat to   = new SimpleDateFormat("EEE,  dd MMM yyyy");
            DateFormat from = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = to.format(from.parse(dates.get(0)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ArrayList<String> transList = TransactionController.getLendings(dbHelper, aid, dates.get(0), "ALL");
            if (transList.size() == 1) {
                String tid = transList.get(0);
                Transaction tran = TransactionController.getTransactionDetails(dbHelper, tid);
                if (tran.getCategory().equals("Loan")) {
                    builder.setContentText("Loan with " + tran.getPartner() + " pending to collect on " + date);
                } else if (tran.getCategory().equals("Debt")) {
                    builder.setContentText("Debt with " + tran.getPartner() + " pending to repay on " + date);
                }
            } else if (transList.size() > 1) {
                int count = transList.size();
                builder.setContentText("You have " + count + " pending repayments on " + date);
            }
        }
        notificationManager.notify(0, builder.build());
    }
}
