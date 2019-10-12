package com.example.aluminifinder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    private NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        notificationManagerCompat = NotificationManagerCompat.from(this);

             Intent i = new Intent(this,NotificationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,0);

                Notification notification = new NotificationCompat.Builder(this,"channel1")
                        .setContentTitle(messageTitle)
                        .setSmallIcon(R.drawable.logo_unicorn)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent)
                        .build();
                notificationManagerCompat.notify(1 ,notification);

    }
}