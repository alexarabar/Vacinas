package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Utility.Connector;

/**
 * Created by Alexandre on 06/01/2018.
 */

public class ServiceVacinas extends Service {
    private ArrayList<ArrayList<String>> content;
    private Context context;
    private Database database;
    private Domain domain;

    private void loadVacinas(Intent intent) {

        // System.out.println(">>> LOAD SERVICE VACINAS <<<");

        Connector.LoadVacinasForNotifications(this.context, domain, content);
        TaskVacinas notif = new TaskVacinas(context, intent, 1, false);
        notif.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, content);
    }

    public ServiceVacinas() {
        this.content  = null;
        this.context = null;
        this.database = null;
        this.domain   = null;
    }

    public ServiceVacinas(Context context) {

        // System.out.println(">>> CREATE SERVICE VACINAS <<<");

        this.content = new ArrayList<ArrayList<String>>();
        this.context = context;
        this.database = Database.getInstance(context);
        this.domain   = Domain.getInstance();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        // System.out.println(">>> SERVICE VACINAS STARTED!");

        return super.onStartCommand(intent, flags, startid);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (Connector.OpenDatabase(database, domain, false)) {
            loadVacinas(intent);
            database.Close();
        }
        return null;
    }
}
