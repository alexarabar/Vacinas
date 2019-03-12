package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.alexandrebarboza.vacinas.Utility.Dates;
import br.com.alexandrebarboza.vacinas.Utility.Utility;

/**
 * Created by Alexandre on 05/01/2018.
 */

public class TaskRemedios extends AsyncTask<ArrayList<ArrayList<String>>, Integer, Long> {
    private Context context;
    private int id;
    private boolean cancel;
    private final String LOADING = "Lendo remédios... ";
    private final String LOADED  = "Remédios notificados!";
    private Toast message;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void callNotificationsRemedios(ArrayList<ArrayList<String>> content) throws NullPointerException {

        // System.out.println(">>> Call Notifications remédios! <<<");

        for (int i = 0; i < content.size(); i++) {

            // System.out.println(">>> NOTIFICANDO REMÉDIOS...");

            Date today = new Date();
            long _id = Long.parseLong(content.get(i).get(0));
            String nome    = content.get(i).get(1);
            String especie = content.get(i).get(2);
            String remedio = content.get(i).get(3);
            String tipo    = content.get(i).get(4);
            String inicio    = content.get(i).get(5);
            String dose_hora  = content.get(i).get(6);
            int    repetir = Integer.parseInt(content.get(i).get(7));
            String un_repetir = content.get(i).get(8);
            int durante       = Integer.parseInt(content.get(i).get(9));
            String un_durante = content.get(i).get(10);
            int dose = Integer.parseInt(content.get(i).get(11)) + 1;
            String dose_data  = content.get(i).get(12);
            Calendar cal_ini  = Dates.joinDateAndTimeSql(inicio, dose_hora);
            Calendar cal_last = Dates.joinDateAndTimeSql(dose_data, dose_hora);

            /*
            System.out.println(">>> DOSE DATA: " + dose_data);
            System.out.println(">>> DOSE HORA: " + dose_hora);
            System.out.println(">>> INICIO: " + inicio);
            System.out.println(">>> DATA INICIAL: " + cal_ini.getTime());
            System.out.println(">>> DATA ULTIMA: "  + cal_last.getTime());
            System.out.println(">>> DATA LIMITE: " + Dates.getDateLimitFor(cal_ini.getTime(), un_durante, durante));
            */

            if (Dates.getDateLimitFor(cal_ini.getTime(), un_durante, durante).compareTo(today) > 0) { // A data limite é maior que a data de hoje.

                // System.out.println("A data limite é maior que a data de hoje <SERÁ INCLUIDO>");

                Notifications.addRemedioNotify(context, _id, Utility.getAnimalEspecie(especie), nome, tipo, remedio, cal_last.getTime(), repetir, un_repetir, dose);
            } else { // Já venceu!

                // System.out.println("A data limite é menor que a data de hoje <FOI REMOVIDO>");

            }
            /*
            if (isCancelled()) {
                Toast.makeText(this.context, "TAREFA LER REMÉDIOS INTERROMPIDA!", Toast.LENGTH_SHORT).show();
                break;
            }
            */
            /*
            try {
                 Thread.sleep(1000);
                 publishProgress((int) ((i / (float) content.size()) * 100));
            } catch (Exception e) {
                 e.printStackTrace();
            }
            */
            publishProgress((int) ((i / (float) content.size()) * 100));
        }
    }


    private void callCancelNotificationsRemedios(ArrayList<ArrayList<String>> content) throws NullPointerException {
        for (int i = 0; i < content.size(); i++) {
            /*
            for (int j = 0; j < content.get(i).size(); j++) {
                System.out.println(">>> FIELD [" + i + "] [" + j + "] = " + content.get(i).get(j));
            }
            */
            Date today = new Date();
            long _id = Long.parseLong(content.get(i).get(0));
            String inicio   = content.get(i).get(5);
            String ini_hora = content.get(i).get(6);
            int durante       = Integer.parseInt(content.get(i).get(9));
            String un_durante = content.get(i).get(10);
            Calendar cal = Dates.joinDateAndTimeSql(inicio, ini_hora);
            /*
            System.out.println("DATA INICIO: " + inicio);
            System.out.println("DATA SHORT: " + Dates.getShortDateForString(inicio));
            System.out.println("DATA CALENDARIO: " + cal.getTime());
            System.out.println("DATA LIMITE: " + Dates.getDateLimitFor(cal.getTime(), un_durante, durante));
            */
            if (Dates.getDateLimitFor(cal.getTime(), un_durante, durante).compareTo(today) > 0 ) { // A data limite é maior que a data de hoje.

                // System.out.println("A data limite é maior que a data de hoje <FOI INCLUÍDO>");

            } else { // Já venceu!

                // System.out.println("A data limite é menor ou igual a data de hoje <SERÁ REMOVIDO>");

                Notifications.removeRemedioNotify(context, (int) _id);
            }
            // if (isCancelled()) break;
            // publishProgress((int) ((i / (float) content.size()) * 100));
        }
    }

    public TaskRemedios(Context context, int id, boolean cancel) {
        this.context = context;
        this.id = id;
        this.cancel = cancel;
    }

    @Override
    protected void onPreExecute() {

        // Toast.makeText(this.context, "PRE EXECUTE REMEDIOS...", Toast.LENGTH_SHORT).show();

        if (!this.cancel) {
            message = Toast.makeText(this.context, LOADING, Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, 0, 0);
            message.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected Long doInBackground(ArrayList<ArrayList<String>>... content) { // All fields of one table.
        if (this.cancel) {
            callCancelNotificationsRemedios(content[0]);
        } else {
            callNotificationsRemedios(content[0]);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (!this.cancel) {
            message.setText(LOADING + progress[0] + "%");
            message.show();
        }
    }

    @Override
    protected void onPostExecute(Long result) {

        // Toast.makeText(this.context, "POST EXECUTE REMÉDIOS...", Toast.LENGTH_SHORT).show();

        if (!this.cancel) {
            message.setText(LOADED);
            message.show();
        }

    }

}
