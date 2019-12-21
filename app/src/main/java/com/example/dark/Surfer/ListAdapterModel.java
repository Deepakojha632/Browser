package com.example.dark.Surfer;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DARK on 01-01-2002.
 */

public class ListAdapterModel extends ArrayAdapter<String> {
    int groupId;
    ArrayList<String> titles;
    ArrayList<String> urls;
    ArrayList<Bitmap> bitmaps;
    Context context;

    public ListAdapterModel(Context content, int vg, int id, ArrayList<String> titles, ArrayList<String> urls, ArrayList<Bitmap> bitmaps) {
        super(content, vg, id, titles);
        this.context = content;
        this.titles = titles;
        this.urls = urls;
        this.bitmaps = bitmaps;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listlayout, parent);
        ImageView iv = (ImageView) itemView.findViewById(R.id.icon);
        iv.setImageBitmap(bitmaps.get(position));
        TextView textTitle = (TextView) itemView.findViewById(R.id.tvtitle);
        String title = titles.get(position);
        textTitle.setText(title);
        TextView textUrl = (TextView) itemView.findViewById(R.id.tvurl);
        String url = urls.get(position);
        textUrl.setText(url);
        Linkify.addLinks(textUrl, Linkify.ALL);
        return itemView;
    }
}
