package com.example.quizmaster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        final Toast toast = Toast.makeText(this, "Your quiz is processing!!", Toast.LENGTH_LONG);

        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        final Timer timer = new Timer(true);
        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (networkInfo != null && networkInfo.isConnected()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        final Notification notification = new Notification.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText("Data connected")
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();

                        manager.notify(1, notification);
                    }
                    finally {
                        timer.cancel();
                        stopSelf();
                    }
                }
            }, 5000);
        }
        else {
            stopSelf();
        }

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}