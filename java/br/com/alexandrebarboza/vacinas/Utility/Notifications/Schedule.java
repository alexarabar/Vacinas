package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;

import br.com.alexandrebarboza.vacinas.MainActivity;
import br.com.alexandrebarboza.vacinas.R;
import br.com.alexandrebarboza.vacinas.Utility.Dates;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Alexandre on 02/01/2018.
 */

public class Schedule {
    private int id;
    private Context context;
    private String title;
    private String text;
    private Calendar date;
    PendingIntent pendingIntent;

    private void setRequestCode(int p1, int p2) {
        String target = String.valueOf(p1) + String.valueOf(p2);
        try {
            this.id = Integer.parseInt(target);
        } catch (NumberFormatException e) {
            // e.printStackTrace();
        }
    }

    private void setPendingIntent(int flag, Notification notification) {
        Intent notificationIntent = new Intent(context, Receiver.class);
        notificationIntent.setAction(String.valueOf(id));
        notificationIntent.putExtra(Receiver.NOTIFICATION_ID, id);
        notificationIntent.putExtra(Receiver.NOTIFICATION, notification);
        this.pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, flag);
    }

    /*
    private void setPendingIntent(int flag, Notification notification) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction("android.intent.action.MAIN");
        notificationIntent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, notificationIntent, flag);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Receiver.NOTIFICATION, notification);
        bundle.putInt(Receiver.NOTIFICATION_ID, id);
        BroadcastReceiver call_method = new Receiver(this.context, bundle);
        context.registerReceiver(call_method, new IntentFilter("call_method"));
        this.pendingIntent = contentIntent;
    }
    */

    private void scheduleNotification(Notification notification) {
        setPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT, notification);
        //setPendingIntent(notification);

        this.date.set(Calendar.HOUR_OF_DAY, 0);
        this.date.set(Calendar.MINUTE, 0);
        this.date.set(Calendar.SECOND, 0);

        //System.out.println(">>> Notification at Date: " + this.date.getTime());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.date.getTimeInMillis(), pendingIntent);
    }

    private void scheduleNotification(Notification notification, long delay) {
        setPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT, notification);
        //setPendingIntent(notification);

        /*
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        Date target = new Date(this.date.getTimeInMillis()
                - SystemClock.elapsedRealtime()
                + futureInMillis);
        */
        /*
        System.out.println(">>> Notification at Time Repeat");
        System.out.println(">>> THIS TIME: " + this.date.getTime());
        System.out.println(">>> NOW: "  + new Date());
        System.out.println(">>> Delay: "  + delay);
        */
        long start = this.date.getTimeInMillis(); // - SystemClock.elapsedRealtime() + futureInMillis;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, start, delay, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification() {
        long[] pattern = {0, 300, 0};
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, null);
        Notification notification = builder.setSmallIcon(R.drawable.ic_update)
                .setTicker(title)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentTitle(this.title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(this.text))
                .setContentIntent(this.pendingIntent)
                .setVibrate(pattern)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(this.text)
                .build();
        return notification;
    }

    public Schedule(Context context, int p1, int p2) {
        setRequestCode(p1, p2);

        // System.out.println("Create Schedule Notification at: " + this.id);

        this.context = context;
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(System.currentTimeMillis());
    }

    public void loadNotification(Date date, String title, String text, boolean repeat) {
        /*
        System.out.println(">>> Load Notification <<<");
        System.out.println("GET DATE: " + date.toString());
        */
        if (!repeat) {
            this.date = Dates.puttTimeForLastDate(date);
        } else {
            this.date.setTime(date);
        }
        this.title = title;
        this.text = text;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInSeconds(long amount) {
        scheduleNotification(getNotification(), (amount * 1000));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInMinutes(long amount) {
        scheduleNotification(getNotification(), (amount * 1000 * 60));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInHours(long amount) {
        scheduleNotification(getNotification(), (amount * 1000 * 60 * 60));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInDays(long amount) {
        scheduleNotification(getNotification(), (amount * 1000 * 60 * 60 * 24));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInWeeks(long amount) {
        scheduleNotification(getNotification(), (amount * 1000 * 60 * 60 * 24 * 7));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInMonths(long amount) {
        scheduleNotification(getNotification(), (amount * 1000 * 60 * 60 * 24 * 31));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyInYears(long amount) {
        scheduleNotification(getNotification(), (amount * 1000 * 60 * 60 * 24 * 31 * 12));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyByDate() {
        scheduleNotification(getNotification());
    }

    public void cancelNotification() {
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // System.out.println("Cancel Notification at: " + this.id);

        setPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT, null);
        // setPendingIntent(null);

        notifManager.cancel(this.id);
        alarmManager.cancel(this.pendingIntent);
        this.pendingIntent.cancel();
    }

    public boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
}
