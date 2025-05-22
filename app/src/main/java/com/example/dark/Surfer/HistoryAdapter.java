package com.example.dark.Surfer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pushkar on 14-Apr-17.
 */

public class HistoryAdapter extends ArrayAdapter {
    List list = new ArrayList();


    public HistoryAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);

    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        Log.v("History Adapter", "add");
        list.add(object);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        Log.v("History Adapter", "getItem ");
        return list.get(position);
    }

    @Override
    public int getCount() {
        Log.v("History Adapter", "getCount ");
        return list.size();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        row = convertView;
        HistoryHolder historyHolder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.history_item_list, parent, false);
            historyHolder = new HistoryHolder();
            historyHolder.his_title = row.findViewById(R.id.his_title);
            row.setTag(historyHolder);
        } else {
            historyHolder = (HistoryHolder) row.getTag();
        }
        HistoryItem historyItem = (HistoryItem) this.getItem(position);
        historyHolder.his_title.setText(historyItem.getTitle());

        return row;
    }

    static class HistoryHolder {
        TextView his_title;
    }

}
