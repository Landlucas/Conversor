package com.example.conversor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater inflater;
    List<HistoryItem> historyItemList;

    public HistoryListAdapter(Context ctx, List<HistoryItem> historyItemList) {
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.historyItemList = historyItemList;
    }

    @Override
    public int getCount() {
        return historyItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return historyItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return historyItemList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.currency_history_item, viewGroup, false);
        TextView date = v.findViewById(R.id.date);
        TextView rate = v.findViewById(R.id.rate);
        HistoryItem item = historyItemList.get(i);
        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(item.getDate()));
        rate.setText(item.getRate().toString());
        return v;
    }

}
