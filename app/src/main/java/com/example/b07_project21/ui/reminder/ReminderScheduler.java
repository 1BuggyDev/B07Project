package com.example.b07_project21.ui.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderScheduler {
    @SuppressLint("ScheduleExactAlarm")
    public static void schedule(Context ctx, Reminder r) {
        Intent i = new Intent(ctx, ReminderReceiver.class)
                .putExtra("id",  r.getId())
                .putExtra("msg", r.getMessage())
                .putExtra("frequency", r.getFrequency().name());
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, r.getId().hashCode(), i, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, r.getTriggerAt(), pi);

        switch (r.getFrequency()) {
            case ONCE:
                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, r.getTriggerAt(), pi);
                break;
            case DAILY:
                am.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        r.getTriggerAt(),
                        AlarmManager.INTERVAL_DAY,
                        pi);
                break;
            case WEEKLY:
                am.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        r.getTriggerAt(),
                        AlarmManager.INTERVAL_DAY * 7,
                        pi);
                break;
            case MONTHLY:
                am.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        r.getTriggerAt(),
                        AlarmManager.INTERVAL_DAY * 30, // approx.
                        pi);
                break;
        }
    }

    public static void cancel(Context ctx, Reminder r) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        Intent i = new Intent(ctx, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, r.getId().hashCode(), i, PendingIntent.FLAG_IMMUTABLE
        );
        am.cancel(pi);
    }
}