package com.example.dark.Surfer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class History extends AppCompatActivity {
    Cursor c;
    SQLiteDatabase db;
    ListView his_list;
    Button clearHistory;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        his_list = (ListView) findViewById(R.id.history_list);
        clearHistory = (Button) findViewById(R.id.clearHistory);
        Context context = getApplicationContext();
        db = openOrCreateDatabase("Web.db", context.MODE_PRIVATE, null);
        Toast.makeText(getApplicationContext(), "database found", Toast.LENGTH_SHORT).show();
        historyAdapter = new HistoryAdapter(this, R.layout.activity_history);
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.wv.clearHistory();
                String query = null;
                query = "Drop table HISTORY;";
                try {
                    db.execSQL(query);
                    Toast.makeText(getApplicationContext(), "All history cleared", Toast.LENGTH_SHORT).show();
                    his_list.removeViews(0, his_list.getChildCount());
                    historyAdapter.clear();
                    //historyList();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //historyList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(getApplicationContext(), "Retrieving History from database", Toast.LENGTH_SHORT).show();
        db = openOrCreateDatabase("Web.db", Context.MODE_PRIVATE, null);
        try{
            String q="SELECT * FROM HISTORY;";
            Cursor c=db.rawQuery(q,null);
            c.close();
            historyList();
        }
        catch (Exception e){
        db.execSQL("CREATE TABLE IF NOT EXISTS HISTORY(title varchar ,url varchar,time VARCHAR,PRIMARY KEY(url,time));");
        //historyList();
        }

       // Toast.makeText(getApplicationContext(), "History retrieved", Toast.LENGTH_SHORT).show();
    }

    public void historyList() {
        String query = null;
        query = "SELECT * FROM HISTORY ORDER BY time DESC;";
        if (query == null) {
            his_list.removeViews(0, his_list.getChildCount());
        } else {
            final String innerQuery = query;
            his_list.removeViews(0, his_list.getChildCount());
            historyAdapter.clear();
            c = db.rawQuery(query, null);
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                HistoryItem histItem = new HistoryItem(c.getString(0), c.getString(1));
                historyAdapter.add(histItem);
                c.moveToNext();
            }
            c.close();
            //Toast.makeText(getApplicationContext(), "Closing cursor", Toast.LENGTH_SHORT).show();
            his_list.setAdapter(historyAdapter);
            his_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    final Cursor cr = db.rawQuery(innerQuery, null);
                    String title = null;
                    String url = null;
                    if (cr.moveToPosition(position)) {
                        title = cr.getString(0);
                        url = cr.getString(1);
                    }
                    Toast.makeText(getApplicationContext(), "Clicked on" + title, Toast.LENGTH_SHORT).show();
                    try {
                        if (url != null) {
                            Toast.makeText(getApplicationContext(), "opening" + title, Toast.LENGTH_SHORT).show();
                            WebViewActivity.wv.loadUrl(url);
                            WebViewActivity.urlAdd.setText(url);
                        }
                    } catch (Exception e) {
                        Log.v("exception", e.getMessage());
                    }
                }
            });
            his_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    final Cursor cr = db.rawQuery(innerQuery, null);
                    String title = null;
                    String url = null;
                    String time = null;
                    if (cr.moveToPosition(position)) {
                        title = cr.getString(0);
                        url = cr.getString(1);
                        time = cr.getString(2);
                        // id = cr.getInt(2);
                        db.execSQL("DELETE FROM HISTORY WHERE time='" + time + "';");
                        Toast.makeText(History.this, title + "deleted from the history", Toast.LENGTH_SHORT).show();
                        historyList();
                    }
                    return false;
                }
            });
        }
    }
}
