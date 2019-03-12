package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import br.com.alexandrebarboza.vacinas.Utility.Dates;
import br.com.alexandrebarboza.vacinas.Utility.Utility;

/**
 * Created by Alexandre on 05/01/2018.
 */

public class TaskVacinas extends AsyncTask <ArrayList<ArrayList<String>>, Integer, Long> {
    private Context context;
    private Intent intent;
    private int id;
    private boolean cancel;
    private final String LOADING = "Lendo vacinas... ";
    private final String LOADED  = "Vacinas notificadas!";
    private Toast message;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void callNotificationsVacinas(ArrayList<ArrayList<String>> content) {

        // System.out.println(">>> Call Notifications vacinas! <<<");

        for (int i = 0; i < content.size(); i++) {

            // System.out.println(">>> NOTIFICANDO VACINAS...");

            Date today = new Date();

            //System.out.println(">> DATA DE HOJE: " + today);

            long _id = Long.parseLong(content.get(i).get(0));
            String nome = content.get(i).get(1);
            String especie = content.get(i).get(2);
            String vacina = content.get(i).get(3);
            String tipo = content.get(i).get(4);

            //System.out.println(">> DATA DE APLICAÇÃO: " + Dates.getDateAndTimeNow(Dates.getShortDateForString(content.get(i).get(5))).getTime());

            if (Dates.puttTimeForLastDate(Dates.getShortDateForString(content.get(i).get(5))).getTime().compareTo(today) < 0) { // Data de aplicação menor que hoje!
                /*
                System.out.println(">> SIM HÁ DATA DE APLICAÇÃO MENOR QUE HOJE! <<<");
                System.out.println(">> DATA DE REVACINAÇÃO: " + Dates.getDateAndTimeNow(Dates.getShortDateForString(content.get(i).get(6))).getTime());
                */
                if (Dates.puttTimeForLastDate(Dates.getShortDateForString(content.get(i).get(6))).getTime().compareTo(today) >= 0) { // Data da revacinação maior ou igual hoje

                    //System.out.println(">> SIM HÁ DATA DE REVACINAÇÃO MAIOR OU IGUAL HOJE! <<<");

                    Notifications.addVacinaNotify(context, _id, Utility.getAnimalEspecie(especie), nome, tipo, vacina, content.get(i).get(6));
                } else {

                    //System.out.println(">> NÃO HÁ DATA DE REVACINAÇÃO MAIOR OU IGUAL HOJE! <<<");

                }

            } else {

                //System.out.println(">> NÃO HÁ DATA DE APLICAÇÃO MENOR QUE HOJE! <<<");

            }
            /*
            if (this.isCancelled()) {
                Toast.makeText(this.context, "TAREFA LER VACINAS INTERROMPIDA!", Toast.LENGTH_SHORT).show();
                break;
            }
            */
            /*
            try {
                 Thread.sleep(100);
                 publishProgress((int) ((i / (float) content.size()) * 100));
            } catch (Exception e) {
                 e.printStackTrace();
            }
            */
            publishProgress((int) ((i / (float) content.size()) * 100));
        }
    }

    private void callCancelNotificationsVacinas(ArrayList<ArrayList<String>> content) {
        for (int i = 0; i < content.size(); i++) {
            Date today = new Date();

            //System.out.println(">> DATA DE HOJE: " + today);

            long _id = Long.parseLong(content.get(i).get(0));
            /*
            System.out.println(">> DATA FIELD: " + content.get(i).get(5));
            System.out.println(">> DATA SHORT: " + Dates.getShortDateForString(content.get(i).get(5)));
            System.out.println(">> DATA DE APLICAÇÃO: " + Dates.getDateAndTimeNow(Dates.getShortDateForString(content.get(i).get(5))).getTime());
            */
            if (Dates.puttTimeForLastDate(Dates.getShortDateForString(content.get(i).get(5))).getTime().compareTo(today) < 0) { // Data de aplicação menor que hoje!
                /*
                System.out.println(">> SIM HÁ DATA DE APLICAÇÃO MENOR QUE HOJE!");
                System.out.println(">> DATA DE REVACINAÇÃO: " + Dates.getDateAndTimeNow(Dates.getShortDateForString(content.get(i).get(6))).getTime());
                */
                if (Dates.puttTimeForLastDate(Dates.getShortDateForString(content.get(i).get(6))).getTime().compareTo(today) >= 0) { // Data da revacinação maior ou igual hoje

                    //System.out.println(">> SIM HÁ DATA DE REVACINAÇÃO MAIOR OU IGUAL HOJE! <<<");

                } else {

                    //System.out.println(">> NÃO HÁ DATA DE REVACINAÇÃO MAIOR OU IGUAL HOJE! <<<");

                    Notifications.removeVacinaNotify(context, (int) _id);
                }

            } else {

                //System.out.println(">> NÃO HÁ DATA DE APLICAÇÃO MENOR QUE HOJE!");

            }
            // if (isCancelled()) break;
            // publishProgress((int) ((i / (float) content.size()) * 100));
        }
    }

    public TaskVacinas(Context context, Intent intent, int id, boolean cancel) {
        this.context = context;
        this.intent  = intent;
        this.id = id;
        this.cancel = cancel;
    }

    @Override
    protected void onPreExecute() {

        // Toast.makeText(this.context, "PRE EXECUTE VACINAS...", Toast.LENGTH_SHORT).show();

        if (!this.cancel) {
            message = Toast.makeText(this.context, LOADING, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, 0, 0);
            message.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected Long doInBackground(ArrayList<ArrayList<String>>... content) { // All fields of one table.
        if (this.cancel) {
            callCancelNotificationsVacinas(content[0]);
        } else {
            callNotificationsVacinas(content[0]);
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

        // Toast.makeText(this.context, "POST EXECUTE VACINAS...", Toast.LENGTH_SHORT).show();

        if (!this.cancel) {
            message.setText(LOADED);
            message.show();

            ServiceRemedios service = new ServiceRemedios(this.context);
            service.onBind(this.intent);

        }
    }

}
