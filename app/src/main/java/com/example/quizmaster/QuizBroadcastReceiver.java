package com.example.quizmaster;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.service.notification.NotificationListenerService;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class QuizBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            //Toast toast = Toast.makeText(context, "Data connected", Toast.LENGTH_LONG);
            //toast.show();
            context.startService(new Intent(context, NotificationService.class));
        }
        if(intent.getAction()==Intent.ACTION_AIRPLANE_MODE_CHANGED) {

            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);

            Toast.makeText(context, isAirplaneModeOn? "Airplane Mode ON" : "Airplane Mode Off", Toast.LENGTH_LONG).show();
        }
    }
}