package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

public class ServiceRemedios extends Service {
    private Context context;
    private ArrayList<ArrayList<String>> content;
    private Database database;
    private Domain domain;

    private void loadRemedios() {

        // System.out.println(">>> LOAD SERVICE REMEDIOS <<<");

        Connector.LoadRemediosForNotifications(this.context, domain, content);
        TaskRemedios notif = new TaskRemedios(context, 2, false);
        notif.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, content);
    }

    public ServiceRemedios() {
        this.context = null;
        this.content = null;
        this.database = null;
        this.domain = null;
    }

    public ServiceRemedios(Context context) {

        //System.out.println(">>> CREATE SERVICE REMEDIOS <<<");

        this.context = context;
        this.content = new ArrayList<ArrayList<String>>();
        this.database = Database.getInstance(context);
        this.domain = Domain.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        // System.out.println(">>> SERVICE REMEDIOS STARTED!");

        return super.onStartCommand(intent, flags, startid);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (Connector.OpenDatabase(database, domain, false)) {
            loadRemedios();
            database.Close();
        }
        return null;
    }
}
