package com.lahiru.cem.views.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.ListItem;
import com.lahiru.cem.views.adapters.CustomSpinAdapter;
import com.lahiru.cem.views.adapters.NotificationReceiver;
import com.lahiru.cem.views.adapters.RecycleAdapter;

import java.util.ArrayList;
import java.util.Calendar;


public class ForecastFragment extends Fragment {

    private HomeActivity activity;
    private DatabaseHelper db;
    private AppData appData;

    private CustomSpinAdapter categoryAdapter;
    private Spinner categorySpin;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_forecast, container, false);

        activity = (HomeActivity) getActivity();
        db = new DatabaseHelper(activity);
        appData = AppData.getInstance();

        // initialize radio buttons and category spin ----------------------------------------------
        categorySpin = (Spinner) fragment.findViewById(R.id.spin_category);
        radioGroup = (RadioGroup) fragment.findViewById(R.id.radio_group_type);
        categoryAdapter=new CustomSpinAdapter(activity,
                AppData.getInstance().getOutflowIconList(), AppData.getInstance().getOutflowNameList());
        categorySpin.setAdapter(categoryAdapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String[] nameList = new String[]{};
                int[] iconList = new int[]{};
                String category;
                String inOut;
                if (i == R.id.rb_outflow) {
                    nameList = AppData.getInstance().getOutflowNameList();
                    iconList = AppData.getInstance().getOutflowIconList();
                    inOut = "outflow";
                    category = AppData.getInstance().getOutflowNameList()[0];
                }else {
                    nameList = AppData.getInstance().getInflowNameList();
                    iconList = AppData.getInstance().getInflowIconList();
                    inOut = "inflow";
                    category = AppData.getInstance().getInflowNameList()[0];
                }
                categoryAdapter.setItems(iconList, nameList);
                categorySpin.setSelection(0);
                forecastNextValue(category, inOut);
            }
        });
        radioGroup.check(R.id.rb_outflow);

        // set listener for category spinner -------------------------------------------------------
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category;
                String inOut;
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_inflow) {
                    category = AppData.getInstance().getInflowNameList()[i];
                    inOut = "inflow";
                }else {
                    category = AppData.getInstance().getOutflowNameList()[i];
                    inOut = "outflow";
                }
                forecastNextValue(category, inOut);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return fragment;
    }

    private void forecastNextValue(String category, String inOut) {
        ArrayList<String[]> totals = SummaryController.getTotalsByDate(db, category, inOut);
        if ()
        for (String[] total: totals) {
            Log.i("TEST", "date = " + total[0] + " | total = " + total[1]);
        }
    }

}
