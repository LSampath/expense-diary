package com.lahiru.cem.views;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.views.start.StartActivity;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private AppData appData;
    private SharedPreferences notifyPreferences;

    private TextView timeTxt;
    private CheckBox dueTransCheckbox;
    private CheckBox predictCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper = new DatabaseHelper(this);
        appData = AppData.getInstance();

        timeTxt = (TextView) findViewById(R.id.tv_notify_time);
        dueTransCheckbox = (CheckBox) findViewById(R.id.cb_due_trans);
        predictCheckbox = (CheckBox) findViewById(R.id.cb_predict);

        notifyPreferences = SettingsActivity.this.getSharedPreferences("NOTIFICATION_PREFERENCES", Context.MODE_PRIVATE);

        // account link ----------------------------------------------------------------------------
        findViewById(R.id.account_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent("com.lahiru.cem.views.account.AccountActivity");
                startActivityForResult(intent, AppData.ACCOUNT_RESULT_CODE);
                return false;
            }
        });

        // logout link -----------------------------------------------------------------------------
        findViewById(R.id.logout_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SharedPreferences accPreferences = SettingsActivity.this.getSharedPreferences("ACCOUNT_PREFERENCES", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = accPreferences.edit();
                editor.remove("AID");
                editor.remove("ACC_NAME");
                editor.apply();

                Intent intent = new Intent();
                intent.putExtra("TYPE", "DROP_LOGOUT");
                SettingsActivity.this.setResult(Activity.RESULT_OK, intent);
                SettingsActivity.this.finish();
                return false;
            }
        });

        // init checkboxes -------------------------------------------------------------------------
        dueTransCheckbox.setChecked(notifyPreferences.getBoolean("DUE", false));
        dueTransCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = notifyPreferences.edit();
                editor.putBoolean("DUE", isChecked);
                editor.apply();

                setNotifications();
            }
        });

        predictCheckbox.setChecked(notifyPreferences.getBoolean("PREDICT", false));
        predictCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = notifyPreferences.edit();
                editor.putBoolean("PREDICT", isChecked);
                editor.apply();

                setNotifications();
            }
        });

        // init time picker ------------------------------------------------------------------------
        String timeString = notifyPreferences.getString("TIME", "12:01 PM");
        timeTxt.setText( timeString );

        findViewById(R.id.notification_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(
                        SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar time = Calendar.getInstance();
                        Log.i("TEST", "hour=" + selectedHour + " minute=" + selectedMinute);
                        time.set(Calendar.HOUR_OF_DAY, selectedHour);
                        time.set(Calendar.MINUTE, selectedMinute);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                        String timeString = timeFormat.format(time.getTime());

                        TextView timeTxt = (TextView) findViewById(R.id.tv_notify_time);
                        timeTxt.setText( timeString );

                        SharedPreferences.Editor editor = notifyPreferences.edit();
                        editor.putString("TIME", timeString);
                        editor.apply();

                        setNotifications();
                    }
                }, hour, minute, false);
                timePicker.setTitle("Select Time");
                timePicker.show();
                return false;
            }
        });

        // init about text view --------------------------------------------------------------------
        findViewById(R.id.tv_about_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.lahiru.cem.views.AboutActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppData.ACCOUNT_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            String type =  data.getStringExtra("TYPE");
            if (type.equals("DROP_LOGOUT")) {
                Intent intent = new Intent();
                intent.putExtra("TYPE", "DROP_LOGOUT");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    private void setNotifications() {
        boolean dueChecked = notifyPreferences.getBoolean("DUE", false);
        boolean predictChecked = notifyPreferences.getBoolean("PREDICT", false);
        String timeString = notifyPreferences.getString("TIME", "12:01 PM");

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        long time = Calendar.getInstance().getTimeInMillis();
        try {
            time = timeFormat.parse(timeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView timeTxt = (TextView) findViewById(R.id.tv_notify_time);
        timeTxt.setText( timeString );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        Bundle bundle = new Bundle();
        bundle.putBoolean("DUE_CHECK", dueChecked);
        bundle.putBoolean("PREDICT", predictChecked);
        bundle.putString("TIME", timeString);
        notificationIntent.putExtras(bundle);

        PendingIntent broadcast =
                PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, broadcast);
        }
    }

}
