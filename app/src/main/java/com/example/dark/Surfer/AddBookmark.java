package com.example.dark.Surfer;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddBookmark extends AppCompatActivity implements View.OnClickListener {
    EditText titleBox, urlBox;
    Button save;
    String title, url;
    Bundle bm;
    SQLiteDatabase db;
    ListView bm_list;
    BookMarkAdapter bookMarkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bookmark);
        // getSupportActionBar().setTitle("Add Bookmark");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
        bm_list = findViewById(R.id.bm_list);
        bm = getIntent().getExtras();
        title = bm.getString("TITLE");
        url = bm.getString("URL");
        titleBox = findViewById(R.id.titleBox);
        urlBox = findViewById(R.id.urlBox);
        titleBox.setText(title);
        urlBox.setText(url);
        save = findViewById(R.id.saveBookmark);
        db = openOrCreateDatabase("Web.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS BOOKMARK(title varchar,url varchar,PRIMARY KEY(url));");
        save.setOnClickListener(this);
        bookMarkAdapter = new BookMarkAdapter(this, R.layout.activity_add_bookmark);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveBookmark) {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                db.execSQL("INSERT INTO BOOKMARK VALUES('" + titleBox.getText() + "','" + urlBox.getText() + "');");
                bookmarkList();

            } catch (SQLException e) {
                String tempLink = null;
                Cursor c = db.rawQuery("SELECT * from BOOKMARK WHERE url='" + urlBox.getText() + "';", null);
                c.moveToLast();
                tempLink = c.getString(1);
                if (tempLink.equals(urlBox.getText().toString()) && !(titleBox.getText().toString().equals(c.getString(0)))) {
                    c.close();
                    Toast.makeText(getApplicationContext(), "Tez bantae hiya re NULLA", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bookmark Already added", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error occured while saving", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bookmarkList();
    }

    public void bookmarkList() {
        String query = null;
        query = "SELECT * FROM BOOKMARK;";
        if (query == null) {
            bm_list.removeViews(0, bm_list.getChildCount());
        } else {
            final String innerQuery = query;
            bm_list.removeViews(0, bm_list.getChildCount());
            bookMarkAdapter.clear();
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                BookMarkItem bookMarkItem = new BookMarkItem(cursor.getString(0), cursor.getString(1));
                bookMarkAdapter.add(bookMarkItem);
                cursor.moveToPrevious();
            }
            cursor.close();
            bm_list.setAdapter(bookMarkAdapter);
            bm_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final Cursor c2 = db.rawQuery(innerQuery, null);
                    String title = null;
                    String link = null;
                    if (c2.moveToPosition(position)) {
                        title = c2.getString(0);
                        link = c2.getString(1);
                    }
                    Toast.makeText(getApplicationContext(), "Clicked on " + title, Toast.LENGTH_SHORT).show();
                    try {
                        if (link != null) {
                            Toast.makeText(getApplicationContext(), "opening " + title, Toast.LENGTH_SHORT).show();
//                            WebViewActivity.wv.loadUrl(link);
//                            WebViewActivity.urlAdd.setText(link);
                        }
                    } catch (Exception e) {
                        Log.v("exception", e.getMessage());

                    }
                }
            });
            bm_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    final Cursor c2 = db.rawQuery(innerQuery, null);
                    String link = null;
                    if (c2.moveToPosition(position)) {
                        link = c2.getString(1);
                        db.execSQL("DELETE FROM BOOKMARK WHERE url='" + link + "';");
                        bookmarkList();
                    }
                    return false;
                }
            });

        }
    }
}
