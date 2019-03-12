package br.com.alexandrebarboza.vacinas.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Animal;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Remedio;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Tipo_Remedio;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Tipo_Vacina;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Vacina;
import br.com.alexandrebarboza.vacinas.Domain.Relation.AnimalXRemedio;
import br.com.alexandrebarboza.vacinas.Domain.Relation.AnimalXVacina;
import br.com.alexandrebarboza.vacinas.R;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Output;

/**
 * Created by Alexandre on 20/12/2017.
 */

public class Connector {

    private static int animalNameFound(Domain domain, String nome) {
        int flag = domain.hasAnimalName(nome);
        if (flag == 1)
            return -2;
        return flag;
    }

    private static int tipoVacinaDescricaoFound(Domain domain, String nome) {
        int flag = domain.hasTipoVacinaDescricao(nome);
        if (flag == 1)
            return -2;
        return flag;
    }

    private static int tipoRemedioDescricaoFound(Domain domain, String nome) {
        int flag = domain.hasTipoRemedioDescricao(nome);
        if (flag == 1)
            return -2;
        return flag;
    }

    private static int vacinaDescricaoFound(Domain domain, String nome) {
        int flag = domain.hasVacinaDescricao(nome);
        if (flag == 1)
            return -2;
        return flag;
    }

    private static int remedioDescricaoFound(Domain domain, String nome) {
        int flag = domain.hasRemedioDescricao(nome);
        if (flag == 1)
            return -2;
        return flag;
    }

    private static void errorMessageEntity(Context context, Domain domain, String data, String operation) {
        String msg, s = data.substring(0, data.length());
        msg = operation + " " + s + context.getResources().getString(R.string.str_msg_plus) + domain.getError();
        Output.Alert(context, context.getResources().getString(R.string.str_fail), msg);
    }

    public static boolean OpenDatabase(Resources resources, Context context, Database database, Domain domain, boolean flag) {
        String error = "";
        if (flag) {
            if (database.setWritable()) {
                // Banco de Dados aberto para escrita!
            } else {
                error = resources.getString(R.string.str_write);
            }
        } else {
            if (database.setReadable()) {
                // Banco de Dados aberto para leitura!
            } else {
                error = resources.getString(R.string.str_read);
            }
        }
        if (!error.isEmpty()) {
            Output.Alert(context, resources.getString(R.string.str_fail), resources.getString(R.string.str_err_sql_open) + " " + error + "!" + resources.getString(R.string.str_msg_plus) + database.getError());
            return false;
        }
        domain.setConnection(database.getConnection());
        domain.getConnection().execSQL("PRAGMA foreign_keys = 1;");
        return true;
    }

    public static boolean OpenDatabase(Database database, Domain domain, boolean flag) {
        String error = "";
        try {
            if (flag) {
                if (database.setWritable()) {
                    // Banco de Dados aberto para escrita!
                }
            } else {
                if (database.setReadable()) {
                    // Banco de Dados aberto para leitura!
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
        domain.setConnection(database.getConnection());
        domain.getConnection().execSQL("PRAGMA foreign_keys = 1;");
        return true;
    }

    public static int LoadAddAnimal(Domain domain, String nome) {
        int flag = animalNameFound(domain, nome);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static long SaveAddAnimal(Activity activity, Domain domain, Animal animal, String nome, String especie, String data) {
        animal.setNome(nome);
        animal.setEspecie(especie);
        animal.setNascimento(Dates.getSQLDate(data, true));
        long id_animal = domain.addAnimal(animal);
        if (id_animal < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animais), activity.getResources().getString(R.string.str_err_insert));
        }
        return id_animal;
    }

    public static int LoadUpdateAnimal(Domain domain, Animal animal, String nome, String especie, String data) {
        Date date = Dates.convertSQLFromDefaultDate(animal.getNascimento());
        String str = Dates.DateToString(date, DateFormat.LONG);
        boolean changed = (animal.getNome().compareTo(nome) != 0 || animal.getEspecie().compareTo(especie) != 0 || str.compareTo(data) != 0);
        if (!changed) {
            return 0;
        }
        int flag = 1;
        if (animal.getNome().compareTo(nome) != 0) {
            flag = animalNameFound(domain, nome);
            if (flag == 0) {
                return 1;
            }
        }
        return flag;
    }

    public static long SaveUpdateAnimal(Activity activity, Domain domain, Animal animal, String nome, String especie, String data) {
        animal.setNome(nome);
        animal.setEspecie(especie);
        animal.setNascimento(Dates.getSQLDate(data, true));
        long id_animal = domain.updateAnimal(animal);
        if (id_animal < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animais), activity.getResources().getString(R.string.str_err_update));
        }
        return id_animal;
    }

    public static boolean DeleteAnimal(Activity activity, Domain domain, Animal animal, String[] v) {
        boolean flag;
        String str = "";
        if (domain.deleteAnimal(animal.get_id()) > 0) {
            flag = true;
        } else {
            str = activity.getResources().getString(R.string.str_animais);
            str = str.substring(0, str.length() - 1) + " ";
            flag = false;
        }
        v[0] = str;
        return flag;
    }

    public static long SaveAddTipoVacina(Activity activity, Domain domain, Tipo_Vacina tipo, String descricao) {
        tipo.setDescricao(descricao);
        long id_tipo = domain.addTipoVacina(tipo);
        if (id_tipo < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_tipos_vacina), activity.getResources().getString(R.string.str_err_insert));
        }
        return id_tipo;
    }

    public static long SaveAddTipoRemedio(Activity activity, Domain domain, Tipo_Remedio tipo, String descricao) {
        tipo.setDescricao(descricao);
        long id_tipo = domain.addTipoRemedio(tipo);
        if (id_tipo < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_tipos_remedio), activity.getResources().getString(R.string.str_err_insert));
        }
        return id_tipo;
    }

    public static long SaveUpdateTipoVacina(Activity activity, Domain domain, Tipo_Vacina tipo, long _id, String descricao) {
        tipo.set_id(_id);
        tipo.setDescricao(descricao);
        long id_tipo = domain.updateTipoVacina(tipo);
        if (id_tipo < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_tipos_vacina), activity.getResources().getString(R.string.str_err_update));
        }
        return id_tipo;
    }

    public static long SaveUpdateTipoRemedio(Activity activity, Domain domain, Tipo_Remedio tipo, long _id, String descricao) {
        tipo.set_id(_id);
        tipo.setDescricao(descricao);
        long id_tipo = domain.updateTipoRemedio(tipo);
        if (id_tipo < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_tipos_remedio), activity.getResources().getString(R.string.str_err_update));
        }
        return id_tipo;
    }

    public static int LoadAddTipoVacina(Domain domain, String descricao) {
        int flag = tipoVacinaDescricaoFound(domain, descricao);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static int LoadAddTipoRemedio(Domain domain, String descricao) {
        int flag = tipoRemedioDescricaoFound(domain, descricao);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static int LoadUpdateTipoVacina(Domain domain, String old, String descricao) {
        int changed = old.toLowerCase().compareTo(descricao.toLowerCase());
        if (changed == 0) {
            return 0;
        }
        int flag = tipoVacinaDescricaoFound(domain, descricao);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static int LoadUpdateTipoRemedio(Domain domain, String old, String descricao) {
        int changed = old.toLowerCase().compareTo(descricao.toLowerCase());
        if (changed == 0) {
            return 0;
        }
        int flag = tipoRemedioDescricaoFound(domain, descricao);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static boolean DeleteTipoVacina(Domain domain, long _id) {
        if (domain.deleteTipoVacina(_id) > 0) {
            return true;
        }
        return false;
    }

    public static boolean DeleteTipoRemedio(Domain domain, long _id) {
        if (domain.deleteTipoRemedio(_id) > 0) {
            return true;
        }
        return false;
    }

    public static boolean DeleteVacina(Domain domain, long _id) {
        if (domain.deleteVacina(_id) > 0) {
            return true;
        }
        return false;
    }

    public static boolean DeleteRemedio(Domain domain, long _id) {
        if (domain.deleteRemedio(_id) > 0) {
            return true;
        }
        return false;
    }

    public static int LoadAddVacina(Domain domain, String descricao) {
        int flag = vacinaDescricaoFound(domain, descricao);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static int LoadAddRemedio(Domain domain, String descricao) {
        int flag = remedioDescricaoFound(domain, descricao);
        if (flag == 0) {
            return 1;
        }
        return flag;
    }

    public static int LoadUpdateVacina(Domain domain, Vacina vacina, long id_tipo, String descricao) {
        String old_des = vacina.getDescricao().toUpperCase();
        String new_des = descricao.toUpperCase();
        boolean cmp_1 = vacina.get_tipo() != id_tipo;
        boolean cmp_2 = old_des.compareTo(new_des) != 0;
        boolean changed = (cmp_1 || cmp_2);
        if (!changed) {
            return 0;
        }
        int flag = 1;
        if (cmp_2) {
            flag = vacinaDescricaoFound(domain, new_des);
            if (flag == 0) {
                return 1;
            }
        }
        return flag;
    }

    public static int LoadUpdateRemedio(Domain domain, Remedio remedio, long id_tipo, String descricao) {
        String old_des = remedio.getDescricao().toUpperCase();
        String new_des = descricao.toUpperCase();
        boolean cmp_1 = remedio.get_tipo() != id_tipo;
        boolean cmp_2 = old_des.compareTo(new_des) != 0;
        boolean changed = (cmp_1 || cmp_2);
        if (!changed) {
            return 0;
        }
        int flag = 1;
        if (cmp_2) {
            flag = remedioDescricaoFound(domain, new_des);
            if (flag == 0) {
                return 1;
            }
        }
        return flag;
    }

    public static long SaveAddVacina(Activity activity, Domain domain, Vacina vacina, long tipo_id, String descricao) {
        vacina.set_tipo(tipo_id);
        vacina.setDescricao(descricao);
        long id_vacina = domain.addVacina(vacina);
        if (id_vacina < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_vacinas), activity.getResources().getString(R.string.str_err_insert));
        }
        return id_vacina;
    }

    public static long SaveAddRemedio(Activity activity, Domain domain, Remedio remedio, long tipo_id, String descricao) {
        remedio.set_tipo(tipo_id);
        remedio.setDescricao(descricao);
        long id_remedio = domain.addRemedio(remedio);
        if (id_remedio < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_remedios), activity.getResources().getString(R.string.str_err_insert));
        }
        return id_remedio;
    }

    public static long SaveUpdateVacina(Activity activity, Domain domain, Vacina vacina, long _id, long tipo, String descricao) {
        vacina.set_id(_id);
        vacina.set_tipo(tipo);
        vacina.setDescricao(descricao);
        long id_vacina = domain.updateVacina(vacina);
        if (id_vacina < 1) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_vacina), activity.getResources().getString(R.string.str_err_update));
            } else {
                Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.str_update_disabled), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        return id_vacina;
    }

    public static long SaveUpdateRemedio(Activity activity, Domain domain, Remedio remedio, long _id, long tipo, String descricao) {
        remedio.set_id(_id);
        remedio.set_tipo(tipo);
        remedio.setDescricao(descricao);
        long id_vacina = domain.updateRemedio(remedio);
        if (id_vacina < 1) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_remedio), activity.getResources().getString(R.string.str_err_update));
            } else {
                Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.str_update_disabled), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        return id_vacina;
    }

    public static void getAllVacinas(Activity activity, Domain domain, List<Long> ls_vac_id, List<String> ls_vac_des, List<Long> ls_tip_id, List<String> ls_tip_des) {
        if (!domain.getAllVacinas(ls_vac_id, ls_vac_des, ls_tip_id, ls_tip_des)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_vacinas), activity.getResources().getString(R.string.str_err_find));

            }
        }
    }

    public static void getAllRemedios(Activity activity, Domain domain, List<Long> ls_rem_id, List<String> ls_rem_des, List<Long> ls_tip_id, List<String> ls_tip_des) {
        if (!domain.getAllRemedios(ls_rem_id, ls_rem_des, ls_tip_id, ls_tip_des)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_remedios), activity.getResources().getString(R.string.str_err_find));

            }
        }
    }

    public static boolean getAllTiposVacina(Activity activity, Domain domain, ArrayAdapter adapter, List<Long> list) {
        if (!domain.getAllTiposVacina(adapter, list)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_tipos_vacina), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getAllTiposRemedio(Activity activity, Domain domain, ArrayAdapter adapter, List<Long> list) {
        if (!domain.getAllTiposRemedio(adapter, list)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_tipos_remedio), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getTiposVacinaAnimal(Activity activity, Domain domain, ArrayAdapter adapter, long animal_id) {
        if (!domain.getTiposVacinaAnimal(adapter, animal_id)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getTiposRemedioAnimal(Activity activity, Domain domain, ArrayAdapter adapter, long animal_id) {
        if (!domain.getTiposRemedioAnimal(adapter, animal_id)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getVacinasAnimalByTipo(Activity activity, Domain domain, ArrayAdapter adapter, long animal_id, String str) {
        if (!domain.getVacinasAnimalByTipo(adapter, animal_id, str)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getRemediosAnimalByTipo(Activity activity, Domain domain, ArrayAdapter adapter, long animal_id, String str) {
        if (!domain.getRemediosAnimalByTipo(adapter, animal_id, str)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getMinMaxDatesForAnimalXVacina(Activity activity, Domain domain, String[] dates, long animal_id) {
        if (!domain.MinMaxDatesForAnimalXVacina(dates, animal_id)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getMinMaxDatesForAnimalXRemedio(Activity activity, Domain domain, String[] dates, long animal_id) {
        if (!domain.MinMaxDatesForAnimalXRemedio(dates, animal_id)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getAllVacinasByAnimal(Activity activity, Domain domain, ArrayAdapter<String> ar_descricao, long animal_id) {
        if (!domain.DescricaoVacinaByAnimal(ar_descricao, animal_id)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static boolean getAllRemediosByAnimal(Activity activity, Domain domain, ArrayAdapter<String> ar_descricao, long animal_id) {
        if (!domain.DescricaoRemedioByAnimal(ar_descricao, animal_id)) {
            if (domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_find));
                return false;
            }
        }
        return true;
    }

    public static int LoadAddVacina(Domain domain, long animal_id, long vacina_id, String d_vac, String d_rev) {
        Date dtv = Dates.StringToDate(d_vac, "en", "us", false);
        Date dtr = Dates.StringToDate(d_rev, "en", "us", false);
        if (dtv.after(dtr) && dtr.before(dtv)) { // Verificar se data de revacinação maior que data da aplicação.
            return -3;
        }
        int flag = domain.findAnimalXVacina(animal_id, vacina_id, dtv, dtr);
        if (flag < 0) {
            return flag - 3;
        }
        if (flag == 0) {
            return -1;
        }
        return flag;
    }

    public static int LoadAddRemedio(Domain domain, long animal_id, long remedio_id, String dt_ini, String tx_de, String tx_de_un, String tx_durante, String tx_durante_un, String tx_dosagem, String tx_dosagem_un, int conta_dose) {
        Date dti = Dates.StringToDate(dt_ini, "en", "us", false);
        int flag = domain.findAnimalXRemedio(animal_id, remedio_id, dti, conta_dose);
        if (flag == 0) {
            return -1;
        }
        if (flag == -1) {
            return -2;
        }
        return flag;
    }

    public static long SaveAddAnimalXVacina(Activity activity, Domain domain, long animal_id, long vacina_id, java.sql.Date sql_vac, java.sql.Date sql_rev) {
        AnimalXVacina animal_x_vacina = new AnimalXVacina();
        animal_x_vacina.set_animal(animal_id);
        animal_x_vacina.set_vacina(vacina_id);
        animal_x_vacina.setAplicadaEm(sql_vac);
        animal_x_vacina.setRevacinarEm(sql_rev);
        long result = domain.addAnimalXVacina(animal_x_vacina);
        if (result < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_insert));
        }
        return result;
    }

    public static void setDateAndTimeForRemedio(AnimalXRemedio animal_x_remedio, Domain domain, Activity activity) {
        Date dt_1 = new Date();
        Date dt_2 = Dates.convertSQLFromDefaultDate(animal_x_remedio.getInicio_em());
        Date dt_3;
        if (domain == null) {
            long res = Dates.getDifferenceBetween(dt_1, dt_2);
            java.sql.Date sql_date;

            // System.out.println(">>> RESULT IN MILISECS: " + res);

            if (res > 0) {
                sql_date = Dates.getSQLDateFromUtilDate(dt_1);
            } else {
                sql_date = Dates.getSQLDateFromUtilDate(dt_2);
            }
            dt_3 = getDoseDataAnimalXRemedio(sql_date, animal_x_remedio.getRepetir_de(), animal_x_remedio.getUnidade_repetir());
            /*
            System.out.println(">>> NOW: " + dt_1);
            System.out.println(">>> DOSE: " + dt_2);
            System.out.println(">>> RESULT: " + dt_3);
            */
        } else {
            dt_3 = domain.getDateTimeFromAnimalXRemedio(animal_x_remedio);
            if (dt_3 == null && domain.getError() != null && !domain.getError().isEmpty()) {
                errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_find));
            }
        }

        java.sql.Date sql_date = Dates.getSQLDateFromUtilDate(dt_3);
        java.sql.Time sql_time = new java.sql.Time(dt_3.getTime());
        animal_x_remedio.setDose_data(sql_date);
        animal_x_remedio.setDose_hora(sql_time);
        /*
        System.out.println(">>> SAVE SQL DATE :" + sql_date.toString());
        System.out.println(">>> SAVE SQL TIME :" + sql_time.toString());
        */
    }

    public static long SaveAddAnimalXRemedio(Activity activity, Domain domain, long animal_id, long remedio_id, java.sql.Date sql_rem, int in_de, String st_de, int in_durante, String st_durante, int in_dosagem, String st_dosagem, int conta, String[] dose) {
        AnimalXRemedio animal_x_remedio = new AnimalXRemedio();
        animal_x_remedio.set_animal(animal_id);
        animal_x_remedio.set_remedio(remedio_id);
        animal_x_remedio.setInicio_em(sql_rem);
        animal_x_remedio.setRepetir_de(in_de);
        animal_x_remedio.setUnidade_repetir(st_de);
        animal_x_remedio.setDurante(in_durante);
        animal_x_remedio.setUnidade_durante(st_durante);
        animal_x_remedio.setDosagem(in_dosagem);
        animal_x_remedio.setUnidade_dosagem(st_dosagem);
        animal_x_remedio.setConta_dose(conta);

        setDateAndTimeForRemedio(animal_x_remedio, null, null);
        dose[0] = Dates.getShortDateForString(Dates.convertSQLFromDefaultDate(animal_x_remedio.getDose_data()).toString(), "en", "US");
        dose[1] = Dates.getTimeStringForSql(animal_x_remedio.getDose_hora().toString());

        /*
        System.out.println(">>> Repetir_de: " + animal_x_remedio.getRepetir_de());
        System.out.println(">>> Unidade repetir: " + animal_x_remedio.getUnidade_repetir());
        System.out.println(">>> Durante: " + animal_x_remedio.getDurante());
        System.out.println(">>> Unidade durante: " + animal_x_remedio.getUnidade_durante());
        System.out.println(">>> Dosagem: " + animal_x_remedio.getDosagem());
        System.out.println(">>> Unidade dosagem: " + animal_x_remedio.getUnidade_dosagem());
        System.out.println(">>> Conta dose: " + animal_x_remedio.getConta_dose());
        */

        long result = domain.addAnimalXRemedio(animal_x_remedio);
        if (result < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_insert));
        }
        return result;
    }

    public static long SaveUpdateAnimalXRemedio(Activity activity, Domain domain, long animal_id, long remedio_id, java.sql.Date sql_rem, int in_de, String st_de, int in_durante, String st_durante, int in_dosagem, String st_dosagem) {
        AnimalXRemedio animal_x_remedio = new AnimalXRemedio();
        animal_x_remedio.set_animal(animal_id);
        animal_x_remedio.set_remedio(remedio_id);
        animal_x_remedio.setInicio_em(sql_rem);
        animal_x_remedio.setRepetir_de(in_de);
        animal_x_remedio.setUnidade_repetir(st_de);
        animal_x_remedio.setDurante(in_durante);
        animal_x_remedio.setUnidade_durante(st_durante);
        animal_x_remedio.setDosagem(in_dosagem);
        animal_x_remedio.setUnidade_dosagem(st_dosagem);
        long result = domain.updateAnimalXRemedio(animal_x_remedio);
        if (result < 1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_update));
        }
        return result;
    }

    public static boolean ListAnimalXVacina(Activity activity, Domain domain, ArrayList<ArrayList<String>> content, String[] cols, String tables, String where, String order) {
        if (!domain.getListAnimalXVacina(content, cols, tables, where, order)) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_vacina), activity.getResources().getString(R.string.str_err_find));
            return false;
        }
        return true;
    }

    public static boolean ListAnimalXRemedio(Activity activity, Domain domain, ArrayList<ArrayList<String>> content, String[] cols, String tables, String where, String order) {
        if (!domain.getListAnimalXRemedio(content, cols, tables, where, order)) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_find));
            return false;
        }
        return true;
    }

    public static boolean getLastAnimalXRemedio(Activity activity, Domain domain, long animal_id, long remedio_id, String[] array) {
        if (!domain.getLastAnimalXRemedio(animal_id, remedio_id, array)) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_animal_x_remedio), activity.getResources().getString(R.string.str_err_find));
            return false;
        }
        return true;
    }

    public static boolean DeleteHistoryVacinas(Domain domain, String[] ids) {
        if (domain.deleteAllAnimalXVacinas(ids)) {
            return true;
        }
        return false;
    }

    public static boolean DeleteHistoryRemedios(Domain domain, String[] ids) {
        if (domain.deleteAllAnimalXRemedios(ids)) {
            return true;
        }
        return false;
    }

    public static void LoadVacinasForNotifications(Context context, Domain domain, ArrayList<ArrayList<String>> content) {
        if (!domain.getFullVacinas(content) && context.getResources() != null) {
            errorMessageEntity(context, domain, context.getResources().getString(R.string.str_notif_vac), context.getResources().getString(R.string.str_err_find));
        }
    }

    public static void LoadRemediosForNotifications(Context context, Domain domain, ArrayList<ArrayList<String>> content) {
        if (!domain.getFullRemedios(content) && context.getResources() != null) {
            errorMessageEntity(context, domain, context.getResources().getString(R.string.str_notif_rem), context.getResources().getString(R.string.str_err_find));
        }
    }

    public static long getLastDoseOfRemedio(Activity activity, Domain domain, long animal_id, long remedio_id, java.sql.Date sql_rem, int last_dose) {
        long result = domain.getIdForLastDoseOfAnimalXRemedio(animal_id, remedio_id, sql_rem, last_dose);
        // System.out.println("RESULT: " + result);
        if (result == -1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_rem), activity.getResources().getString(R.string.str_err_find));
        }
        return result;
    }

    public static long getPreviousAnimalXRemedio(Activity activity, Domain domain, long animal_id, long vacina_id, String d_vac) {
        long result = domain.getPreviousByDateAnimalXRemedio(animal_id, vacina_id, d_vac);
        if (result == -1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_rem), activity.getResources().getString(R.string.str_err_find));
        }
        return result;
    }

    public static int getLastContaDoseForAnimalXRemedio(Activity activity, Domain domain, long animal_id, long remedio_id, java.sql.Date sqlDate, ArrayList<String> fields) {
        int result = domain.getMaxContaDoseForAnimalXRemedio(animal_id, remedio_id, sqlDate, fields);
        if (result == -1) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_rem), activity.getResources().getString(R.string.str_err_find));
        }
        return result;
    }

    public static Date getDoseDataAnimalXRemedio(java.sql.Date sql_rem, int in_de, String st_de) {

        // System.out.println(">>> GET DOSE DATA ANIMAL X REMEDIO! <<<");

        Date date = Dates.convertSQLFromDefaultDate(sql_rem);

        //Calendar cal = Dates.puttTimeForLastDate(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        /*
        System.out.println(">>> DATE: " + date);
        System.out.println(">>> CALENDAR: " + cal.getTime().toString());
        */
        if (st_de.toUpperCase().compareTo("HORAS") == 0) {
            cal.add(Calendar.HOUR_OF_DAY, in_de);
        } else if (st_de.toUpperCase().compareTo("DIAS") == 0) {
            cal.add(Calendar.DAY_OF_MONTH, in_de);
        } else if (st_de.toUpperCase().compareTo("SEMANAS") == 0) {
            cal.add(Calendar.WEEK_OF_MONTH, in_de);
        } else if (st_de.toUpperCase().compareTo("MESES") == 0) {
            cal.add(Calendar.MONTH, in_de);
        } else if (st_de.toUpperCase().compareTo("ANOS") == 0) {
            cal.add(Calendar.YEAR, in_de);
        }

        // System.out.println(">>> DATE NEXT DOSE: " + cal.getTime().toString());

        return cal.getTime();
    }

    public static ArrayList<Long> geListIdForAnimalXRemedioByTipo(Activity activity, Domain domain, String tipo_desc) {
        ArrayList<Long> aList = domain.geListIdForAnimalXRemedioByTipo(tipo_desc);
        if (aList == null && domain.getError() != null && !domain.getError().isEmpty()) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_rem), activity.getResources().getString(R.string.str_err_find));
            return null;
        }
        return aList;
    }

    public static ArrayList<Long> geListIdForAnimalXRemedioByDescricao(Activity activity, Domain domain, String descricao) {
        ArrayList<Long> aList = domain.geListIdForAnimalXRemedioByDescricao(descricao);
        if (aList == null && domain.getError() != null && !domain.getError().isEmpty()) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_rem), activity.getResources().getString(R.string.str_err_find));
            return null;
        }
        return aList;
    }

    public static ArrayList<Long> geListIdForAnimalXVacinaByTipo(Activity activity, Domain domain, String tipo_desc) {
        ArrayList<Long> aList = domain.geListIdForAnimalXVacinaByTipo(tipo_desc);
        if (aList == null && domain.getError() != null && !domain.getError().isEmpty()) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_vac), activity.getResources().getString(R.string.str_err_find));
            return null;
        }
        return aList;
    }

    public static ArrayList<Long> geListIdForAnimalXVacinaByDescricao(Activity activity, Domain domain, String descricao) {
        ArrayList<Long> aList = domain.geListIdForAnimalXVacinaByDescricao(descricao);
        if (aList == null && domain.getError() != null && !domain.getError().isEmpty()) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_vac), activity.getResources().getString(R.string.str_err_find));
            return null;
        }
        return aList;
    }

    public static ArrayList<Long> geListIdForAnimalXRemedioByAnimalId(Activity activity, Domain domain, long animal) {
        ArrayList<Long> aList = domain.geListIdForAnimalXRemedioByAnimalId(animal);
        if (aList == null && domain.getError() != null && !domain.getError().isEmpty()) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_rem), activity.getResources().getString(R.string.str_err_find));
            return null;
        }
        return aList;
    }

    public static ArrayList<Long> geListIdForAnimalXVacinaByAnimalId(Activity activity, Domain domain, long animal) {
        ArrayList<Long> aList = domain.geListIdForAnimalXVacinaByAnimalId(animal);
        if (aList == null && domain.getError() != null && !domain.getError().isEmpty()) {
            errorMessageEntity(activity, domain, activity.getResources().getString(R.string.str_notif_vac), activity.getResources().getString(R.string.str_err_find));
            return null;
        }
        return aList;
    }
}
