package br.com.alexandrebarboza.vacinas.Domain;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.alexandrebarboza.vacinas.Domain.Entity.Animal;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Remedio;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Tipo_Remedio;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Tipo_Vacina;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Vacina;
import br.com.alexandrebarboza.vacinas.Domain.Relation.AnimalXRemedio;
import br.com.alexandrebarboza.vacinas.Domain.Relation.AnimalXVacina;
import br.com.alexandrebarboza.vacinas.R;
import br.com.alexandrebarboza.vacinas.Utility.Adapters.AnimalAdapter;
import br.com.alexandrebarboza.vacinas.Utility.Dates;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class Domain {
    private static Domain instance = null;
    private SQLiteDatabase connection = null;
    private String error;

    private Domain() { // Singleton
    }
    private String makePlaceholders(int len) {
        if (len < 1) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    private Cursor getRecords(String tabela, String columns[], String where, String[] args, String order, String GroupBy) {
        try {
            Cursor c = connection.query(true, tabela, columns, where, args, GroupBy, null, order, null);
            return c;
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }

    private ContentValues putAnimal(Animal animal) {
        ContentValues values = new ContentValues();
        values.put(Animal.NOME, animal.getNome().toString());
        values.put(Animal.ESPECIE, animal.getEspecie().toString());
        values.put(Animal.NASCIMENTO, animal.getNascimento().toString());
        return values;
    }

    private ContentValues putVacina(Vacina vacina) {
        ContentValues values = new ContentValues();
        values.put(Vacina._TIPO, vacina.get_tipo());
        values.put(Vacina.DESCRICAO, vacina.getDescricao().toString());
        return values;
    }

    private ContentValues putRemedio(Remedio remedio) {
        ContentValues values = new ContentValues();
        values.put(Remedio._TIPO, remedio.get_tipo());
        values.put(Remedio.DESCRICAO, remedio.getDescricao().toString());
        return values;
    }

    private ContentValues putTipoVacina(Tipo_Vacina tipo) {
        ContentValues values = new ContentValues();
        values.put(Tipo_Vacina.DESCRICAO, tipo.getDescricao().toString());
        return values;
    }

    private ContentValues putTipoRemedio(Tipo_Remedio tipo) {
        ContentValues values = new ContentValues();
        values.put(Tipo_Remedio.DESCRICAO, tipo.getDescricao().toString());
        return values;
    }

    private ContentValues putAnimalXVacina(AnimalXVacina animal_x_vacina) {
        ContentValues values = new ContentValues();
        values.put(AnimalXVacina._ANIMAL, animal_x_vacina.get_animal());
        values.put(AnimalXVacina._VACINA, animal_x_vacina.get_vacina());
        values.put(AnimalXVacina.APLICADA_EM, animal_x_vacina.getAplicada_em().toString());
        values.put(AnimalXVacina.REVACINAR_EM, animal_x_vacina.getRevacinar_em().toString());
        return values;
    }

    private ContentValues putAnimalXRemedio(AnimalXRemedio animal_x_remedio, boolean flag) {
        ContentValues values = new ContentValues();
        values.put(AnimalXRemedio._ANIMAL, animal_x_remedio.get_animal());
        values.put(AnimalXRemedio._REMEDIO, animal_x_remedio.get_remedio());
        values.put(AnimalXRemedio.INICIO_EM, animal_x_remedio.getInicio_em().toString());
        values.put(AnimalXRemedio.REPETIR_DE, animal_x_remedio.getRepetir_de());
        values.put(AnimalXRemedio.UNIDADE_REPETIR, animal_x_remedio.getUnidade_repetir());
        values.put(AnimalXRemedio.DURANTE, animal_x_remedio.getDurante());
        values.put(AnimalXRemedio.UNIDADE_DURANTE, animal_x_remedio.getUnidade_durante());
        values.put(AnimalXRemedio.DOSAGEM, animal_x_remedio.getDosagem());
        values.put(AnimalXRemedio.UNIDADE_DOSAGEM, animal_x_remedio.getUnidade_dosagem());
        if (flag == true) {
            values.put(AnimalXRemedio.CONTA_DOSE, animal_x_remedio.getConta_dose());
            values.put(AnimalXRemedio.DOSE_DATA, animal_x_remedio.getDose_data().toString());
            values.put(AnimalXRemedio.DOSE_HORA, animal_x_remedio.getDose_hora().toString());
        }
        return values;
    }

    private long insertRecord(String tabela, ContentValues values) {
        long result = -1;
        try {
            result = connection.insertOrThrow(tabela, null, values);
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
        //System.out.println("Resultado da Inclusão na tabela: " + tabela + " foi: " + result);
        return result;
    }

    private long updateRecord(String tabela, ContentValues values, long id) {
        long result = -1;
        try {
            result = connection.update(tabela, values, "_id = ?", new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
        //System.out.println("Resultado da Atualização na tabela: " + tabela + " foi: " + result);
        return result;
    }

    private long updateRecord(String tabela, ContentValues values, String where, String[] args) {
        long result = -1;
        try {
            result = connection.update(tabela, values, where, args);
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
        //System.out.println("Resultado da Atualização na tabela: " + tabela + " foi: " + result);
        return result;
    }

    private long deleteRecord(String tabela, long id) {
        int result = -1;
        try {
            result = connection.delete(tabela, "_id = ?", new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
        //System.out.println("Resultado da Exclusão na tabela: " + tabela + " foi: " + result);
        return result;
    }

    private long deleteAllRecords(String tabela, String[] array) {
        int result = -1;
        try {
            result = connection.delete(tabela, "_id IN (" + makePlaceholders(array.length) + ")", array);
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
        //System.out.println("Resultado da Exclusão na tabela: " + tabela + " foi: " + result);
        return result;
    }
    private int hasDescricao(String descricao, String tabela) {
        String where = "descricao = '" + descricao + "'";
        String col[] = {"descricao"};
        Cursor cursor = null;
        try {
            cursor = getRecords(tabela, col, where, null, null, null);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return 1;
            }
            cursor.close();
        } else {
            return -1;
        }
        return 0;
    }

    public static Domain getInstance() {
        if (instance == null) {
            instance = new Domain();
        }
        return instance;
    }

    public SQLiteDatabase getConnection() {
        return connection;
    }

    public void setConnection(SQLiteDatabase connection) {
        this.connection = connection;
    }

    public String getError() {
        return error;
    }

    public int hasAnimalName(String nome) {
        String where = "nome = '" + nome + "'";
        String col[] = {"nome"};
        Cursor cursor = getRecords(Animal.TABELA, col, where, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String compare = cursor.getString(cursor.getColumnIndex("nome"));
                        boolean result = (nome.compareTo(compare) == 0);
                        if (result) {
                            cursor.close();
                            return 1;
                        }
                    } while (cursor.moveToNext());
                } else {
                    return -1;
                }
            }
            cursor.close();
        } else {
            return -1;
        }
        return 0;
    }

    public int hasTipoVacinaDescricao(String descricao) {
        return hasDescricao(descricao, Tipo_Vacina.TABELA);
    }

    public int hasTipoRemedioDescricao(String descricao) {
        return hasDescricao(descricao, Tipo_Remedio.TABELA);
    }

    public int hasVacinaDescricao(String descricao) {
        return hasDescricao(descricao, Vacina.TABELA);
    }

    public int hasRemedioDescricao(String descricao) {
        return hasDescricao(descricao, Remedio.TABELA);
    }

    public long addAnimal(Animal animal) {
        ContentValues values = putAnimal(animal);
        return insertRecord(Animal.TABELA, values);
    }

    public long updateAnimal(Animal animal) {
        ContentValues values = putAnimal(animal);
        return updateRecord(Animal.TABELA, values, animal.get_id());
    }

    public AnimalAdapter findAnimais(Context context) {
        AnimalAdapter adapter = new AnimalAdapter(context, R.layout.list_animais);
        Cursor cursor = connection.query(Animal.TABELA, null, null, null, null, null, "nome COLLATE NOCASE ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Animal animal = new Animal();
                animal.set_id(cursor.getLong(cursor.getColumnIndex(Animal._ID)));
                animal.setNome(cursor.getString(cursor.getColumnIndex(Animal.NOME)));
                animal.setEspecie(cursor.getString(cursor.getColumnIndex(Animal.ESPECIE)));
                String dt = cursor.getString(cursor.getColumnIndex(Animal.NASCIMENTO));
                animal.setNascimento(Dates.getSQLDate(dt, false));
                adapter.add(animal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return adapter;
    }

    public long deleteAnimal(long id) {
        return deleteRecord(Animal.TABELA, id);
    }

    public long addTipoVacina(Tipo_Vacina tipo) {
        ContentValues values = putTipoVacina(tipo);
        return insertRecord(tipo.TABELA, values);
    }

    public long addTipoRemedio(Tipo_Remedio tipo) {
        ContentValues values = putTipoRemedio(tipo);
        return insertRecord(tipo.TABELA, values);
    }

    public String getDescricaoForId(long _id, String tabela) {
        String where = "_id = '" + _id + "'";
        String col[] = {"descricao"};
        String result = "";
        Cursor cursor = getRecords(tabela, col, where, null, "descricao ASC", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getString(0);
            }
            cursor.close();
        }
        return result;
    }

    public long updateTipoVacina(Tipo_Vacina tipo) {
        ContentValues values = putTipoVacina(tipo);
        return updateRecord(Tipo_Vacina.TABELA, values, tipo.get_id());
    }

    public long updateTipoRemedio(Tipo_Remedio tipo) {
        ContentValues values = putTipoRemedio(tipo);
        return updateRecord(Tipo_Remedio.TABELA, values, tipo.get_id());
    }

    public long deleteTipoVacina(long id) {
        return deleteRecord(Tipo_Vacina.TABELA, id);
    }

    public long deleteTipoRemedio(long id) {
        return deleteRecord(Tipo_Remedio.TABELA, id);
    }

    public long addVacina(Vacina vacina) {
        ContentValues values = putVacina(vacina);
        return insertRecord(Vacina.TABELA, values);
    }

    public long addRemedio(Remedio remedio) {
        ContentValues values = putRemedio(remedio);
        return insertRecord(Remedio.TABELA, values);
    }

    public long updateVacina(Vacina vacina) {
        ContentValues values = putVacina(vacina);
        return updateRecord(Vacina.TABELA, values, vacina.get_id());
    }

    public long updateRemedio(Remedio remedio) {
        ContentValues values = putRemedio(remedio);
        return updateRecord(Remedio.TABELA, values, remedio.get_id());
    }

    public long deleteVacina(long id) {
        return deleteRecord(Vacina.TABELA, id);
    }

    public long deleteRemedio(long id) {
        return deleteRecord(Remedio.TABELA, id);
    }

    public boolean getAllVacinas(List<Long> ls_vac_id, List<String> ls_vac_des, List<Long> ls_tip_id, List<String> ls_tip_des) {
        String col[] = {"_id", "_tipo", "descricao"};
        Cursor cursor = getRecords(Vacina.TABELA, col, null, null, "descricao ASC", null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    ls_vac_id.add(cursor.getLong(0));
                    ls_tip_id.add(cursor.getLong(1));
                    ls_vac_des.add(cursor.getString(2));
                    ls_tip_des.add(getDescricaoForId(ls_tip_id.get(ls_tip_id.size() - 1), Tipo_Vacina.TABELA));
                    if (ls_tip_des.get(ls_tip_des.size() - 1).isEmpty()) {
                        cursor.close();
                        return false;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getAllRemedios(List<Long> ls_rem_id, List<String> ls_rem_des, List<Long> ls_tip_id, List<String> ls_tip_des) {
        String col[] = {"_id", "_tipo", "descricao"};
        Cursor cursor = getRecords(Remedio.TABELA, col, null, null, "descricao ASC", null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    ls_rem_id.add(cursor.getLong(0));
                    ls_tip_id.add(cursor.getLong(1));
                    ls_rem_des.add(cursor.getString(2));
                    ls_tip_des.add(getDescricaoForId(ls_tip_id.get(ls_tip_id.size() - 1), Tipo_Remedio.TABELA));
                    if (ls_tip_des.get(ls_tip_des.size() - 1).isEmpty()) {
                        cursor.close();
                        return false;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getAllTiposVacina(ArrayAdapter adapter, List<Long> list) {
        String col[] = {"_id", "descricao"};
        Cursor cursor = getRecords(Tipo_Vacina.TABELA, col, null, null, "descricao ASC", null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        adapter.add(cursor.getString(1));
                        list.add(cursor.getLong(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getAllTiposRemedio(ArrayAdapter adapter, List<Long> list) {
        String col[] = {"_id", "descricao"};
        Cursor cursor = getRecords(Tipo_Remedio.TABELA, col, null, null, "descricao ASC", null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        adapter.add(cursor.getString(1));
                        list.add(cursor.getLong(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getTiposVacinaAnimal(ArrayAdapter adapter, long animal_id) {
        String tables = "AnimalXVacina AS X, Tipo_Vacina AS T, Vacinas AS V";
        String where  = "X._animal = " + animal_id + " AND T._id =  V._tipo AND V._id == X._vacina";
        String order  = "T.descricao ASC";
        String cols[] = {"T.descricao"};
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        adapter.add(cursor.getString(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getTiposRemedioAnimal(ArrayAdapter adapter, long animal_id) {
        String tables = "AnimalXRemedio AS X, Tipo_Remedio AS T, Remedios AS R";
        String where  = "X._animal = " + animal_id + " AND T._id =  R._tipo AND R._id = X._remedio";
        String order  = "T.descricao ASC";
        String cols[] = {"T.descricao"};
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        adapter.add(cursor.getString(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getVacinasAnimalByTipo(ArrayAdapter adapter, long animal_id, String src) {
        String tables = "AnimalXVacina AS X, Tipo_Vacina AS T, Vacinas AS V";
        String where  = "X._animal = " + animal_id + " AND T._id = V._tipo AND V._id = X._vacina AND T.descricao = '" + src + "'";
        String order  = "V.descricao ASC";
        String cols[] = {"V.descricao"};
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        adapter.add(cursor.getString(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getRemediosAnimalByTipo(ArrayAdapter adapter, long animal_id, String src) {
        String tables = "AnimalXRemedio AS X, Tipo_Remedio AS T, Remedios AS R";
        String where  = "X._animal = " + animal_id + " AND T._id = R._tipo AND R._id = X._remedio AND T.descricao = '" + src + "'";
        String order  = "R.descricao ASC";
        String cols[] = {"R.descricao"};
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        adapter.add(cursor.getString(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean MinMaxDatesForAnimalXVacina(String[] dates, long animal_id) {
        String where  = "_animal = " + animal_id;
        String cols[] = {"MIN(aplicada_em)", "MAX(aplicada_em)", "MIN(revacinar_em)", "MAX(revacinar_em)"};
        Cursor cursor = getRecords(AnimalXVacina.TABELA, cols, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                String tmp1 = cursor.getString(0);
                String tmp2 = cursor.getString(1);
                String tmp3 = cursor.getString(2);
                String tmp4 = cursor.getString(3);
                Date dt1 = Dates.getDateForSqlString(tmp1);
                Date dt2 = Dates.getDateForSqlString(tmp2);
                Date dt3 = Dates.getDateForSqlString(tmp3);
                Date dt4 = Dates.getDateForSqlString(tmp4);
                dates[0] = Dates.DateToString(dt1, DateFormat.SHORT);
                dates[1] = Dates.DateToString(dt2, DateFormat.SHORT);
                dates[2] = Dates.DateToString(dt3, DateFormat.SHORT);
                dates[3] = Dates.DateToString(dt4, DateFormat.SHORT);
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean MinMaxDatesForAnimalXRemedio(String[] dates, long animal_id) {
        String where  = "_animal = " + animal_id;
        String cols[] = {"MIN(inicio_em)", "MAX(inicio_em)"};
        Cursor cursor = getRecords(AnimalXRemedio.TABELA, cols, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                String tmp1 = cursor.getString(0);
                String tmp2 = cursor.getString(1);
                Date dt1 = Dates.getDateForSqlString(tmp1);
                Date dt2 = Dates.getDateForSqlString(tmp2);
                dates[0] = Dates.DateToString(dt1, DateFormat.SHORT);
                dates[1] = Dates.DateToString(dt2, DateFormat.SHORT);
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean DescricaoVacinaByAnimal(ArrayAdapter<String> ar_descricao, long animal_id) {
        String tables = "AnimalXVacina AS X, Vacinas AS V";
        String where  = "X._animal = " + animal_id + " AND V._id = X._vacina";
        String order  = "V.descricao ASC";
        String cols[] = {"V.descricao"};
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        ar_descricao.add(cursor.getString(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean DescricaoRemedioByAnimal(ArrayAdapter<String> ar_descricao, long animal_id) {
        String tables = "AnimalXRemedio AS X, Remedios AS R";
        String where  = "X._animal = " + animal_id + " AND R._id = X._remedio";
        String order  = "R.descricao ASC";
        String cols[] = {"R.descricao"};
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        ar_descricao.add(cursor.getString(0));
                    } catch (UnsupportedOperationException e) {
                        // e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public int findAnimalXVacina(long animal_id, long vacina_id, Date dt_v, Date dt_r) {
        String col[] = {"aplicada_em", "revacinar_em"};
        String where = "_animal = '" + animal_id + "' and _vacina = '" + vacina_id + "'";
        Cursor cursor = getRecords(AnimalXVacina.TABELA, col, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    String tmp_vac = cursor.getString(0);
                    String tmp_rev = cursor.getString(1);
                    Date dt1 = Dates.getDateForSqlString(tmp_vac);
                    Date dt2 = Dates.getDateForSqlString(tmp_rev);
                    if (dt1.equals(dt_v)) { // Verifica se existe na relação o mesmo animal, vacina e data de vacinação.
                        return -1;
                    }
                    int days = Dates.numberOfDays(dt2, dt_v);
                    //System.out.println(">>> DAYS: " + days);
                    if (days <= -10) { // Verifica se existe na relação o mesmo animal e vacina com uma data de revacinação menor que dez dias antes da data de aplicação inserida.
                        return -2;
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return 0;
        }
        return 1;
    }

    public int findAnimalXRemedio(long animal_id, long remedio_id, Date dt_ini, int conta_dose) {
        String col[] = {"inicio_em", "conta_dose"};
        String where = "_animal = '" + animal_id + "' and _remedio = '" + remedio_id + "'";
        Cursor cursor = getRecords(AnimalXRemedio.TABELA, col, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    String tmp_rem = cursor.getString(0);
                    int ct = cursor.getInt(1);
                    Date dt = Dates.getDateForSqlString(tmp_rem);
                    if (dt.equals(dt_ini) && ct == conta_dose) { // Verifica se existe na relação o mesmo animal, remédio, data de início e dose.
                        return -1;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return 0;
        }
        return 1;
    }

    public long addAnimalXVacina(AnimalXVacina ainmal_x_vacina) {
        ContentValues values = putAnimalXVacina(ainmal_x_vacina);
        return insertRecord(AnimalXVacina.TABELA, values);
    }

    public long updateAnimalXRemedio(AnimalXRemedio animal_x_remedio) {
        String where = "_animal = ? AND _remedio = ? AND inicio_em = ?";
        String args[] = new String[] {String.valueOf(animal_x_remedio.get_animal()), String.valueOf(animal_x_remedio.get_remedio()), animal_x_remedio.getInicio_em().toString()};
        ContentValues values = putAnimalXRemedio(animal_x_remedio, false);
        return updateRecord(AnimalXRemedio.TABELA, values, where, args);
    }

    public long addAnimalXRemedio(AnimalXRemedio ainmal_x_remedio) {
        ContentValues values = putAnimalXRemedio(ainmal_x_remedio, true);
        return insertRecord(AnimalXRemedio.TABELA, values);
    }

    public boolean getListAnimalXVacina(ArrayList<ArrayList<String>> content, String[] cols, String tables, String where, String order) {
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    final int count_fields = cursor.getColumnCount();
                    final ArrayList<String> fields = new ArrayList<String>(count_fields);
                    for (int i = 0; i < count_fields; i++) {
                        fields.add(cursor.getString(i));
                    }
                    content.add(fields);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public long getPreviousByDateAnimalXRemedio(long animal_id, long vacina_id, String data_aplic) {
        String aplic = Dates.getSQLStringFromDate(Dates.getShortDateForString(data_aplic));
        /*
        System.out.println(">>> DATA: " + data_aplic);
        System.out.println(">>> APLIC: " + aplic);
        */
        String cols[] = {"_id", "MAX(revacinar_em)"};
        String where = "_animal = " + animal_id + " AND _vacina = " + vacina_id + " AND revacinar_em <= '" + aplic + "'";
        Cursor cursor = getRecords(AnimalXVacina.TABELA, cols, where, null, null, null);
        long result = -1;
        if (cursor != null) {
            int count = cursor.getCount();
            // System.out.println(">>> COUNT: " + count);
            result = 0;
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    result = cursor.getLong(0);
                    /*
                    System.out.println(">>> RESULT: " + result);
                    System.out.println(">>> LAST DATA REVAC.: " + cursor.getString(1));
                    */
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

    public boolean getListAnimalXRemedio(ArrayList<ArrayList<String>> content, String[] cols, String tables, String where, String order) {
        Cursor cursor = getRecords(tables, cols, where, null, order, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    final int count_fields = cursor.getColumnCount();
                    final ArrayList<String> fields = new ArrayList<String>(count_fields);
                    for (int i = 0; i < count_fields; i++) {
                         final String cmp = cursor.getColumnName(i);
                         if (cmp.compareTo("repetir_de") == 0 ||
                                cmp.compareTo("durante") == 0 ||
                                cmp.compareTo("dosagem") == 0) {
                                if (Integer.parseInt(cursor.getString(i)) == 0) {
                                    fields.add(cursor.getString(i+1));
                                } else {
                                    fields.add(cursor.getString(i) + " " + cursor.getString(i + 1));
                                }
                                i++;
                                continue;
                         } else {
                             fields.add(cursor.getString(i));
                         }
                    }
                    content.add(fields);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;

    }

    public boolean getLastAnimalXRemedio(long animal_id, long remedio_id, String[] array) {
        String cols[] = {"MAX(inicio_em)", "repetir_de", "unidade_repetir", "durante", "unidade_durante", "dosagem", "unidade_dosagem", "conta_dose"};
        String where = "_animal = '" + animal_id + "' and _remedio = '" + remedio_id + "'";
        Cursor cursor = getRecords(AnimalXRemedio.TABELA, cols, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                final int count_fields = cursor.getColumnCount();
                for (int i = 0; i < count_fields; i++) {
                     array[i] = cursor.getString(i);
                }
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean deleteAllAnimalXVacinas(String[] ids) {
        if (deleteAllRecords(AnimalXVacina.TABELA, ids) > 0) {
            return true;
        }
        return false;
    }

    public boolean deleteAllAnimalXRemedios(String[] ids) {
        if (deleteAllRecords(AnimalXRemedio.TABELA, ids) > 0) {
            return true;
        }
        return false;
    }

    public boolean getFullVacinas(ArrayList<ArrayList<String>> content) {
        String tables = "Animal AS A, AnimalXVacina AS X, Vacinas AS V, Tipo_Vacina AS T";
        String where  = "A._id = X._animal AND V._id = X._vacina AND T._id = V._tipo";
        String cols[] = {"X._id, A.nome, A.especie, V.descricao, T.descricao, X.aplicada_em, X.revacinar_em"};
        Cursor cursor = getRecords(tables, cols, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();
                do {
                    ArrayList<String> fields = new ArrayList<String>(7);
                    int i = 0;
                    while (i < 5) {
                        fields.add(cursor.getString(i));
                        i++;
                    }
                    fields.add(Dates.DateToString(Dates.getDateForSqlString(cursor.getString(i)), DateFormat.SHORT));
                    fields.add(Dates.DateToString(Dates.getDateForSqlString(cursor.getString(++i)), DateFormat.SHORT));

                    //System.out.println(">>> DATA APLICADA: " + fields.get(5));
                    //System.out.println(">>> DATA REVACINAR: " + fields.get(6));

                    content.add(fields);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public boolean getFullRemedios(ArrayList<ArrayList<String>> content) {
        String tables = "Animal AS A, AnimalXRemedio AS X, Remedios AS R, Tipo_Remedio AS T";
        String where  = "A._id = X._animal AND R._id = X._remedio AND T._id = R._tipo";
        String groupBy = "X._animal, X._remedio, X.inicio_em";
        String cols[] = {"X._id, A.nome, A.especie, R.descricao, T.descricao, X.inicio_em, X.dose_hora, X.repetir_de, X.unidade_repetir, X.durante, X.unidade_durante, MAX(X.conta_dose)," +
                "X.dose_data, X._animal, X._remedio"};
        Cursor cursor = getRecords(tables, cols, where, null, null, groupBy);
        if (cursor != null) {
            int count = cursor.getCount();

            // System.out.println(">>> COUNT: " + count);

            if (count > 0) {
                cursor.moveToFirst();
                do {
                    ArrayList<String> fields = new ArrayList<String>(cursor.getColumnCount());
                    for (int i = 0; i < cursor.getColumnCount(); i++) {

                        // System.out.println("DATA[ " + i + "] = " + cursor.getString(i));

                        if (i == 5) {
                            fields.add(Dates.DateToString(Dates.getDateForSqlString(cursor.getString(i)), DateFormat.SHORT));
                        } else if (i == 6) {

                            //System.out.println(">>> SQL TIME STRING: " + Dates.getTimeStringForSql(cursor.getString(i)));

                            fields.add(Dates.getTimeStringForSql(cursor.getString(i)));
                        } else if (i == 12) {

                            // System.out.println(">>> SQL DATE STRING: " + Dates.DateToString(Dates.getDateForSqlString(cursor.getString(i)), DateFormat.SHORT));

                            fields.add(Dates.DateToString(Dates.getDateForSqlString(cursor.getString(i)), DateFormat.SHORT));
                        } else {
                            fields.add(cursor.getString(i));
                        }

                        // System.out.println("FIELDS[ " + i + "] = " + fields.get(i));

                    }
                    content.add(fields);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public long getIdForLastDoseOfAnimalXRemedio(long animal_id, long remedio_id, java.sql.Date sql_rem, int last_dose) {
        String where = "_animal = " + animal_id + " AND _remedio = " + remedio_id + " AND inicio_em = '" + sql_rem + "' AND conta_dose = " + last_dose;
        String col[] = {"_id"};
        long result = 0;
        Cursor cursor = getRecords(AnimalXRemedio.TABELA, col, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                result = cursor.getLong(0);
            }
            cursor.close();
        } else {
            result = -1;
        }
        return result;
    }

    public int getMaxContaDoseForAnimalXRemedio(long animal_id, long remedio_id, java.sql.Date sqlDate, ArrayList<String> fields) {
        String where = "_animal = " + animal_id + " AND _remedio = " + remedio_id + " AND inicio_em = '" + sqlDate + "'";
        String groupBy = "_animal, _remedio, inicio_em";
        String col[] = {"MAX(conta_dose)", "repetir_de", "unidade_repetir", "durante", "unidade_durante", "dosagem", "unidade_dosagem"};
        Cursor cursor = getRecords(AnimalXRemedio.TABELA, col, where, null, null, groupBy);
        int result = 0;
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
                for (int i = 1; i < cursor.getColumnCount(); i++) {
                    fields.add(cursor.getString(i));
                }
            }
            cursor.close();
        } else {
            result = -1;
        }
        return result;
    }

    public Date getDateTimeFromAnimalXRemedio(AnimalXRemedio animal_x_remedio) {
        long animal_id  = animal_x_remedio.get_animal();
        long remedio_id = animal_x_remedio.get_remedio();
        java.sql.Date sqlDate = animal_x_remedio.getInicio_em();
        int dose = animal_x_remedio.getConta_dose();
        String where = "_animal = " + animal_id + " AND _remedio = " + remedio_id + " AND inicio_em = '" + sqlDate + "' AND conta_dose = " + dose;

        // System.out.println(">>> WHERE: " + where);

        String col[] = {"dose_data", "dose_hora"};
        Cursor cursor = getRecords(AnimalXRemedio.TABELA, col, where, null, null, null);
        Date result = new Date();
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                String date = cursor.getString(0);
                String time = cursor.getString(1);

                // System.out.println(">>> DATE: " + date);
                // System.out.println(">>> TIME: " + time);

                result = Dates.joinDateAndTimeSql(date, time).getTime();
            }
            cursor.close();
        } else {
            return null;
        }

        // System.out.println(">>> LOAD FULL DATE: " + result);

        return result;
    }

    private ArrayList<Long> getArrayListForId(String tables, String where) {
        ArrayList<Long> aList = null;
        String cols[] = {"X._id"};
        Cursor cursor = getRecords(tables, cols, where, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            aList = new ArrayList<>(count);
            if (count > 0) {
                cursor.moveToFirst();
                do {
                    aList.add(cursor.getLong(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return aList;
    }

    public ArrayList<Long> geListIdForAnimalXRemedioByTipo(String tipo_desc) {
        String tables = "AnimalXRemedio AS X, Remedios AS R, Tipo_Remedio AS T";
        String where  = "T.descricao = '" + tipo_desc + "' AND R._tipo = T._id AND X._remedio = R._id";
        return getArrayListForId(tables, where);
    }

    public ArrayList<Long> geListIdForAnimalXRemedioByDescricao(String descricao) {
        String tables = "AnimalXRemedio AS X, Remedios AS R";
        String where  = "R.descricao = '" + descricao + "' AND X._remedio = R._id";
        return getArrayListForId(tables, where);
    }

    public ArrayList<Long> geListIdForAnimalXVacinaByTipo(String tipo_desc) {
        String tables = "AnimalXVacina AS X, Vacinas AS V, Tipo_Vacina AS T";
        String where  = "T.descricao = '" + tipo_desc + "' AND V._tipo = T._id AND X._vacina = V._id";
        return getArrayListForId(tables, where);
    }

    public ArrayList<Long> geListIdForAnimalXVacinaByDescricao(String descricao) {
        String tables = "AnimalXVacina AS X, Vacinas AS V";
        String where  = "V.descricao = '" + descricao + "' AND X._vacina = V._id";
        return getArrayListForId(tables, where);
    }
    public ArrayList<Long> geListIdForAnimalXRemedioByAnimalId(long animal_id) {
        String tables = "AnimalXRemedio AS X";
        String where  = "X._animal = " + animal_id;
        return getArrayListForId(tables, where);
    }
    public ArrayList<Long> geListIdForAnimalXVacinaByAnimalId(long animal_id) {
        String tables = "AnimalXVacina AS X";
        String where  = "X._animal = " + animal_id;
        return getArrayListForId(tables, where);
    }
}
