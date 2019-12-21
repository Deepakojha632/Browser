package com.example.dark.Surfer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import static com.example.dark.Surfer.WebViewActivity.urlAdd;
import static com.example.dark.Surfer.WebViewActivity.wv;

public class ConnectionBroadReceiver extends BroadcastReceiver {
    String statusOfNetwork;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                statusOfNetwork = "Internet Connected";
                if (urlAdd.getText().toString().contains("file:///android_asset/Web/index.html")) {
                    Toast.makeText(context, "url matched", Toast.LENGTH_SHORT).show();
                    wv.loadUrl("javascript:history.go(-1)");
                } else
                    wv.reload();
            } else {
                statusOfNetwork = "Internet Disconnected";
                WebViewActivity.wv.loadUrl("file:///android_asset/Web/index.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectionNotifier(context, statusOfNetwork);
    }

    public void connectionNotifier(Context context, String message) {
        // Set Notification Title
        String strtitle = context.getString(R.string.app_name);
        // notification click krne pe jha bhejna hai wha kaa
        //Intent intent = new Intent(context, null);

        // Open WebViewActivity.class
        //PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Ticker Message
                .setTicker(message)
                // Set Title
                .setContentTitle(context.getString(R.string.app_name))
                // Set Text
                .setContentText(message)
                // Add an Action Button below Notification
                //.addAction(R.drawable.home, "Open Browser", pIntent)
                // Set PendingIntent into Notification
                //.setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
