package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Alexandre on 02/01/2018.
 */

public class Receiver extends BroadcastReceiver  {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    /*
    public Receiver(Context context, Bundle bundle) {
        if (bundle.getParcelable(NOTIFICATION) != null) {

            System.out.println(">>> NOTIFICATION RECEIVED! <<<");
            System.out.println(">>> BUNDLE: " + bundle.toString());
            System.out.println(">>> NOTIFICATION: " + bundle.getParcelable(NOTIFICATION).toString());
            System.out.println(">>> ID: " + bundle.getInt(NOTIFICATION_ID));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = bundle.getParcelable(NOTIFICATION);
            int id = bundle.getInt(NOTIFICATION_ID, 0);
            notificationManager.notify(id, notification);

            System.out.println(">>> NOTIFICATION ID:" + id);
        }
    }
    */

    @Override
    public void onReceive(Context context, Intent intent) {

        // System.out.println(">>> NOTIFICATION RECEIVED! <<<");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);


        // System.out.println(">>> NOTIFICATION ID:" + id);

        /*
        String action_name = intent.getAction();
        if (action_name.equals("call_method")) {

            // call your method here and do what ever you want.
            System.out.println(">>> RECEIVE ACTION CALL!");

        }
        */
    }
}
