package com.hospitality.fooddoor.helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.hospitality.fooddoor.R;

public class NotificationHelper extends ContextWrapper {

    public static final String FOODDOOR_CHANNEL_ID = "com.hospitality.fooddoor.FoodDoor";
    public static final String FOODDOOR_CHANNEL_NAME = "Food@Door - DINE|SNACK|CHEER";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)       //only works if API level >= 26
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel fooddoor_channel = new NotificationChannel(FOODDOOR_CHANNEL_ID,
                FOODDOOR_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        fooddoor_channel.enableLights(false);
        fooddoor_channel.enableVibration(true);
        fooddoor_channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);


        getManager().createNotificationChannel(fooddoor_channel);
    }

    public NotificationManager getManager() {
        if(manager == null)
        {
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getFoodDoorChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(), FOODDOOR_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.applogo_round)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
