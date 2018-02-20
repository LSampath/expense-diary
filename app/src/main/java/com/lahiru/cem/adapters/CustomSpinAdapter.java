package com.lahiru.cem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.lahiru.cem.R;

/**
 * Created by Lahiru on 2/7/2018.
 */

public class CustomSpinAdapter extends BaseAdapter {

    private Context context;
    private int iconList[];
    private String[] nameList;
    private LayoutInflater inflter;

    public CustomSpinAdapter(Context applicationContext, int[] iconList, String[] nameList) {
        this.context = applicationContext;
        this.iconList = iconList;
        this.nameList = nameList;
        this.inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return iconList.length;
    }

    @Override
    public Object getItem(int i) {
        return nameList[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_item, null);
        ImageView icon = view.findViewById(R.id.icon_image);
        TextView names = view.findViewById(R.id.text_item);
        icon.setImageResource(iconList[i]);
        names.setText(nameList[i]);
        return view;
    }

    public void setItems(int[] iconList, String[] nameList) {
        this.iconList = iconList;
        this.nameList = nameList;
        notifyDataSetChanged();
    }
}
