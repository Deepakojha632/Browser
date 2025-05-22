package com.example.dark.Surfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class TabActivity extends android.app.TabActivity {
    private static int tabIndex = 1;
    Button btn;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        tabHost = getTabHost();
        btn = findViewById(R.id.add_tab);
        btn.setOnClickListener(view -> {
            ++tabIndex;
            add();
        });
        add();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void add() {
        LayoutInflater li = LayoutInflater.from(TabActivity.this);
        btn = (Button) li.inflate(R.layout.tab_event, null);
        btn.setText("Tab" + tabIndex);
        Intent tabIntent = new Intent(TabActivity.this, WebViewActivity.class);
        setupTab(btn, tabIntent, "don" + tabIndex);
    }

    protected void setupTab(View tabBtn, Intent setClass, String tag) {
        TabHost.TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabBtn).setContent(setClass);
        tabHost.addTab(setContent);
    }
}
