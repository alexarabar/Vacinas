package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Alexandre on 06/01/2018.
 */

public class BootReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        System.out.println(">>> CONTEXT: " + context);
        System.out.println(">>> BOOT RECEIVER ON RECEIVER! <<<");
        */
        ServiceVacinas serviceVacinas = new ServiceVacinas(context);
        serviceVacinas.onBind(intent);
        // ServiceRemedios serviceRemedios = new ServiceRemedios(context);
        // serviceRemedios.onBind(intent);
    }
}
