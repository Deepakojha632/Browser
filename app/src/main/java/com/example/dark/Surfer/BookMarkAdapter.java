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
 * Created by Pushkar on 10-Apr-17.
 */

public class BookMarkAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public BookMarkAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        Log.v("Bookmark Adapter", "add ");
        list.add(object);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        Log.v("Bookmark Adapter", "getItem ");
        return list.get(position);
    }

    @Override
    public int getCount() {
        Log.v("Bookmark Adapter", "getCount ");
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
        BookMarkHolder bookMarkHolder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.bookmark_item_list, parent, false);
            bookMarkHolder = new BookMarkHolder();
            bookMarkHolder.bm_title = row.findViewById(R.id.bm_title);
            row.setTag(bookMarkHolder);
        } else {
            bookMarkHolder = (BookMarkHolder) row.getTag();
        }
        BookMarkItem bookMarkItem = (BookMarkItem) this.getItem(position);
        bookMarkHolder.bm_title.setText(bookMarkItem.getTitle());


        return row;
    }

    static class BookMarkHolder {
        TextView bm_title;
    }
}
