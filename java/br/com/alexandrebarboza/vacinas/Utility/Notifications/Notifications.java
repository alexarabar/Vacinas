package br.com.alexandrebarboza.vacinas.Utility.Notifications;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

import br.com.alexandrebarboza.vacinas.Utility.Dates;

/**
 * Created by Alexandre on 02/01/2018.
 */

public class Notifications {
    private static Schedule schedule;

    private static boolean createNotification(Context context, int p1, int p2, Date date, String title, String text, boolean flag) {
        schedule = new Schedule(context, p1, p2);
        if (schedule.areNotificationsEnabled() == true) {
            if (date != null && title != null && text != null) {
                /*
                System.out.println(">>> Create Notification <<<");
                System.out.println("GET DATE: " + date.toString());
                */
                schedule.loadNotification(date, title, text, flag);
            }
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void addVacinaNotify(Context context, long id, String especie, String nome, String tipo, String vacina, String data) {
        String title = "Dia de vacina!";
        String text  = "Seu " + especie + " " + nome + " precisa tomar a vacina de " + tipo + " ("  + vacina + ") hoje.";
        /*
        System.out.println(">>> Add Vacina Notify! <<<");
        System.out.println(">>> NOTIFY VACINA DATA: " + data);
        */
        Date vence = Dates.getShortDateForString(data);

        //System.out.println(">>> NOTIFY VACINA NEW DATA: " + vence.toString());

        if (createNotification(context, 1, (int) id, vence, title, text, false)) {

            //System.out.println(">>> NOTIFY BY DATA! <<<");

            schedule.notifyByDate();
        }
    }

    public static void removeVacinaNotify(Context context, int id) {
        if (createNotification(context, 1, id, null, null, null, false)) {
            schedule.cancelNotification();
        }
    }

    public static void removeAllVacinasNotify(Context context, int[] id) {
        for (int i = 0; i < id.length; i++) {
            removeVacinaNotify(context, id[i]);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void addRemedioNotify(Context context, long id, String especie, String nome, String tipo, String remedio, Date full, int repetir, String un_repetir, int dose) {
        /*
        System.out.println(">>> ADD REMEDIO NOTIFY! <<<");
        System.out.println(">>> REPETIR: (" + repetir + ") " + un_repetir.toUpperCase());
        System.out.println(">>> FULL DATE: " + full.toString());
        */
        String title = "Hora do remédio!";
        String text  = "Seu " + especie + " " + nome + " precisa tomar a dose (" + dose + ") do remédio de " + tipo + " ("  + remedio + ") agora.";
        if (!createNotification(context, 2, (int) id, full, title, text, true)) {
            return;
        }
        if (un_repetir.toUpperCase().compareTo("HORAS") == 0) {
            schedule.notifyInHours(repetir);
        } else if (un_repetir.toUpperCase().compareTo("DIAS") == 0) {
            schedule.notifyInDays(repetir);
        } else if (un_repetir.toUpperCase().compareTo("SEMANAS") == 0) {
            schedule.notifyInWeeks(repetir);
        } else if (un_repetir.toUpperCase().compareTo("MESES") == 0) {
            schedule.notifyInMonths(repetir);
        } else if (un_repetir.toUpperCase().compareTo("ANOS") == 0) {
            schedule.notifyInYears(repetir);
        }
    }

    public static void removeRemedioNotify(Context context, int id) {
        if (createNotification(context, 2, id, null, null, null, false)) {
            schedule.cancelNotification();
        }
    }

    public static void removeAllRemediosNotify(Context context, int[] id) {
        for (int i = 0; i < id.length; i++) {
             removeRemedioNotify(context, id[i]);
        }
    }
}
