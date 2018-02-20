package com.lahiru.cem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.ListItem;
import com.lahiru.cem.models.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Lahiru on 1/17/2018.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private DatabaseHelper db;

    public RecycleAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        this.db = new DatabaseHelper(context);
    }

    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutType = 0;
        switch (viewType) {
            case ListItem.DATE_ITEM:
                layoutType = R.layout.list_item_date;
                break;
            case ListItem.TRANSACTION_ITEM:
                layoutType = R.layout.list_item_transaction;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleAdapter.ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        if (listItem.getType() == ListItem.TRANSACTION_ITEM) {
            Transaction trans = TransactionController.getTransactionDetails(db, listItem.getValue());
            holder.headingText.setText("Rs. " + trans.getAmount());
            holder.descriptionText.setText(trans.getNote());

            if (trans.getCategory().equals("Loan") || trans.getCategory().equals("Debt")) {
                Date dueParsed = null;
                try {
                    dueParsed = new SimpleDateFormat("yyyy-MM-dd").parse(trans.getDueDate());
                    String dueDate = new SimpleDateFormat("dd MMM yyyy").format(dueParsed);
                    holder.factBottomText.setText("Due " + dueDate);
                    holder.factTopText.setText(trans.getPartner());
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            }

        } else if (listItem.getType() == ListItem.DATE_ITEM) {
            Date parsed = null;
            try {
                parsed = new SimpleDateFormat("yyyy-MM-dd").parse(listItem.getValue());
                String date = new SimpleDateFormat("EEE,  dd MMM yyyy").format(parsed);
                holder.headingText.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).getType();
    }

    public void setListItems(List<ListItem> itemList) {
        this.listItems = itemList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView headingText;
        public TextView descriptionText;
        public TextView factTopText;
        public TextView factBottomText;

        public ViewHolder(View itemView) {
            super(itemView);
            headingText = itemView.findViewById(R.id.headingTxt);
            descriptionText = itemView.findViewById(R.id.descriptionTxt);
            factTopText = itemView.findViewById(R.id.factTopTxt);
            factBottomText = itemView.findViewById(R.id.factBottomTxt);
        }
    }

}
