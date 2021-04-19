package com.example.quizmaster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        final Toast toast = Toast.makeText(this, "Your quiz is processing!!", Toast.LENGTH_LONG);

        final Timer timer = new Timer(true);
        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification notification = new Notification.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_notification_quiz)
                                        .setContentTitle(getString(R.string.app_name))
                                        .setContentText(getString(R.string.notifiation_message))
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .build();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                toast.show();
                manager.notify(1, notification);
                timer.cancel();
                stopSelf();
            }
        }, 5000);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}