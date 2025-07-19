package com.example.b07_project21;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CH_ID = "reminders";

    @Override public void onReceive(Context ctx, Intent in) {
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm.getNotificationChannel(CH_ID) == null) {
            nm.createNotificationChannel(new NotificationChannel(
                    CH_ID, "Reminders", NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent tapIntent = new Intent(ctx, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent tap = PendingIntent.getActivity(
                ctx, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx, CH_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(ctx.getString(R.string.notif_generic_title))
                .setContentText(ctx.getString(R.string.notif_generic_body))
                .setContentIntent(tap)
                .setAutoCancel(true);

        // there is some issue here, yet it still runs
        NotificationManagerCompat.from(ctx)
                .notify(in.getStringExtra("id").hashCode(), nb.build());
    }
}
