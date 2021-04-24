package com.example.quizmaster;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.service.notification.NotificationListenerService;
import android.widget.Toast;

public class quizReceiver extends BroadcastReceiver {

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

            if (isAirplaneModeOn) {
                Toast toast = Toast.makeText(context, "Airplane Mode ON", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(context, "Airplane Mode Off", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
        /*if(intent.getAction()==Intent.ACTION_AIRPLANE_MODE_CHANGED)
        {
            context.startService(new Intent(context, NotificationService.class));
        }
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                Boolean airplane = android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
                context.startService(new Intent(context, NotificationService.class));
                break;
        }
        if(intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
            Toast toast =Toast.makeText(context,"Airplane Mode ON",Toast.LENGTH_LONG);
            toast.show();
        }
*/
}