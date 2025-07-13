package com.example.b07_project21;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderScheduler {
    @SuppressLint("ScheduleExactAlarm")
    public static void schedule(Context ctx, Reminder r) {
        Intent i = new Intent(ctx, ReminderReceiver.class)
                .putExtra("id", r.getId());
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, r.getId().hashCode(), i, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, r.getTriggerAt(), pi);
    }
}
