package com.example.aluminifinder;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class notification extends Application {

    public static final String CHANNEL_1 = "channel1";
    public static final String CHANNEL_2 = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotification();
    }

    private void createNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.enableVibration(true);

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.enableVibration(true);

            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
