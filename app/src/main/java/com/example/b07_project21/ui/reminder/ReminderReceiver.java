package com.example.b07_project21.ui.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.b07_project21.MainActivity;
import com.example.b07_project21.R;

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

        String body = in.getStringExtra("msg");
        if (body == null || body.isEmpty()) {
            body = ctx.getString(R.string.notif_generic_body); // fallback
        }
        NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx, CH_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(ctx.getString(R.string.notif_generic_title))
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body)) // for longer notes
                .setContentIntent(tap)
                .setAutoCancel(true);

        // there is some issue here, yet it still runs
        NotificationManagerCompat.from(ctx)
                .notify(in.getStringExtra("id").hashCode(), nb.build());

        String fStr = in.getStringExtra("frequency");
        if ("MONTHLY".equals(fStr)) {
            long next = System.currentTimeMillis();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(next);
            cal.add(java.util.Calendar.MONTH, 1);

            Reminder tmp = new Reminder(
                    in.getStringExtra("id"),
                    cal.getTimeInMillis(),
                    in.getStringExtra("msg"),
                    Reminder.Frequency.MONTHLY);
            ReminderScheduler.schedule(ctx, tmp);
        }
    }
}