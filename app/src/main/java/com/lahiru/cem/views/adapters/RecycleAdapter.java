package com.lahiru.cem.views.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lahiru.cem.R;
import com.lahiru.cem.controllers.DatabaseHelper;
import com.lahiru.cem.controllers.SummaryController;
import com.lahiru.cem.controllers.TransactionController;
import com.lahiru.cem.models.AppData;
import com.lahiru.cem.models.ListItem;
import com.lahiru.cem.models.Transaction;
import com.lahiru.cem.views.home.HomeActivity;

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
            case ListItem.REPAYMENT_ITEM:
                layoutType = R.layout.list_item_transaction;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleAdapter.ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);

        //------if type == TRANSACTION_ITEM then bind details and set listener----------------------
        if (listItem.getType() == ListItem.TRANSACTION_ITEM) {

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            holder.itemView.setBackgroundResource(backgroundResource);

            final Transaction trans = TransactionController.getTransactionDetails(db, listItem.getValue());
            int icon = 0;
            if (trans.getInOut().equals("inflow")) {
                icon = AppData.getInstance().getInflowIcon(trans.getCategory());
                holder.headingText.setTextColor(context.getResources().getColor(R.color.inflow_color));
            } else {
                icon = AppData.getInstance().getOutflowIcon(trans.getCategory());
                holder.headingText.setTextColor(context.getResources().getColor(R.color.outflow_color));
            }
            holder.categoryIcon.setImageResource(icon);

            holder.headingText.setText("Rs. " + trans.getAmount());
            holder.descriptionText.setText(trans.getNote());
            if (trans.getCategory().equals("Loan") || trans.getCategory().equals("Debt")) {
                Date dueParsed = null;
                try {
                    dueParsed = new SimpleDateFormat("yyyy-MM-dd").parse(trans.getDueDate());
                    String dueDate = new SimpleDateFormat("dd MMM yyyy").format(dueParsed);
                    holder.factBottomText.setText("Due " + dueDate);
                    if (trans.getPartner().equals("")) {
                        holder.factTopText.setText("");
                    } else {
                        holder.factTopText.setText("With " + trans.getPartner());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                holder.factBottomText.setText("");
                holder.factTopText.setText("");
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) RecycleAdapter.this.context;
                    Intent intent = new Intent("com.lahiru.cem.views.transaction.TransactionActivity");
                    intent.putExtra("TID", trans.getTID());
                    activity.startActivity(intent);
                }
            });

        //--------if type == DATE_ITEM then bind date-----------------------------------------------
        } else if (listItem.getType() == ListItem.DATE_ITEM) {
            Date parsed = null;
            try {
                parsed = new SimpleDateFormat("yyyy-MM-dd").parse(listItem.getValue());
                String date = new SimpleDateFormat("EEE,  dd MMM yyyy").format(parsed);
                holder.headingText.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        //--------if type == REPAYMENT_ITEM then bind details---------------------------------------
        } else if (listItem.getType() == ListItem.REPAYMENT_ITEM) {

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            holder.itemView.setBackgroundResource(backgroundResource);

            final Transaction trans = TransactionController.getTransactionDetails(db, listItem.getValue());
            int icon = 0;
            if (trans.getInOut().equals("inflow")) {
                icon = AppData.getInstance().getInflowIcon(trans.getCategory());
                holder.headingText.setTextColor(context.getResources().getColor(R.color.inflow_color));
            } else {
                icon = AppData.getInstance().getOutflowIcon(trans.getCategory());
                holder.headingText.setTextColor(context.getResources().getColor(R.color.outflow_color));
            }
            holder.categoryIcon.setImageResource(icon);
            holder.headingText.setText("Rs. " + trans.getAmount());
            holder.descriptionText.setText(trans.getNote());
            try {
                Log.i("TEST", " due date "+trans.getDueDate());
                Date dueParsed = new SimpleDateFormat("yyyy-MM-dd").parse(trans.getDueDate());
                String dueDate = new SimpleDateFormat("dd MMM yyyy").format(dueParsed);
                holder.factBottomText.setText("Due " + dueDate);
                if (trans.getPartner().equals("")) {
                    holder.factTopText.setText("");
                } else {
                    holder.factTopText.setText("With " + trans.getPartner());
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) context;
                    Intent intent = new Intent();
                    intent.putExtra("LEND_TID", trans.getTID());
                    intent.putExtra("CATEGORY", trans.getCategory());
                    intent.putExtra("AMOUNT", trans.getAmount());
                    intent.putExtra("PARTNER", trans.getPartner());
                    activity.setResult(activity.RESULT_OK, intent);
                    activity.finish();
                }
            });

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
        public ImageView categoryIcon;

        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            headingText = itemView.findViewById(R.id.headingTxt);
            descriptionText = itemView.findViewById(R.id.descriptionTxt);
            factTopText = itemView.findViewById(R.id.factTopTxt);
            factBottomText = itemView.findViewById(R.id.factBottomTxt);
            categoryIcon = itemView.findViewById(R.id.img_litm_icn);

            this.itemView = itemView;
        }
    }
}
