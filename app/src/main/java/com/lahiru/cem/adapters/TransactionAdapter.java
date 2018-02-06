package com.lahiru.cem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lahiru.cem.R;
import com.lahiru.cem.models.Transaction;

import java.util.List;

/**
 * Created by Lahiru on 1/17/2018.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> listItems;
    private Context context;

    public TransactionAdapter(List<Transaction> listItems, Context context) {

        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, int position) {
        Transaction listItem = listItems.get(position);
//        holder.headingText.setText(listItem.getHeading());
//        holder.descriptionText.setText(listItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView headingText;
        public TextView descriptionText;

        public ViewHolder(View itemView) {
            super(itemView);
//            headingText = itemView.findViewById(R.id.listItemHead);
//            descriptionText = itemView.findViewById(R.id.listItemDesc);
        }
    }
}
