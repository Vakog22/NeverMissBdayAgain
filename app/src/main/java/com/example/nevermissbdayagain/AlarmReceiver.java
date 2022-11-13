package com.example.nevermissbdayagain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String event = intent.getStringExtra("event");
        String pName = intent.getStringExtra("person_name");
        int notId = intent.getIntExtra("id", 0);
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,activityIntent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = "channel_id";
        CharSequence name = "channel_name";
        String description = "description";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            NotificationChannel notificationChannel = new NotificationChannel(channelId,name, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(event)
                .setContentText(pName)
                .setDeleteIntent(pendingIntent)
                .setGroup("Group_birthdays")
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notId,notification);
    }
}
