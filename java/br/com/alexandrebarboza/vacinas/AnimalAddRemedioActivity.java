package br.com.alexandrebarboza.vacinas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Domain.Relation.AnimalXRemedio;
import br.com.alexandrebarboza.vacinas.Utility.Connector;
import br.com.alexandrebarboza.vacinas.Utility.Dates;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Dose;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Output;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Pickers.DateListener;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.Notifications;

public class AnimalAddRemedioActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener, View.OnFocusChangeListener, TextView.OnKeyListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private boolean IS_DATA_PICK;
    private boolean IS_CONTA_DOSE;
    private long        animal_id;
    private String      animal_name;
    private TextView    title;
    private EditText    ed_de;
    private EditText    ed_durante;
    private EditText    ed_dosagem;
    private ImageButton bt_add;
    private ImageButton bt_update;
    private Spinner     sp_remedios;
    private Spinner     sp_unidade_repetir;
    private Spinner     sp_unidade_durante;
    private Spinner     sp_unidade_dosagem;
    private ArrayAdapter<String> ar_unidade_repetir;
    private ArrayAdapter<String> ar_unidade_durante;
    private ArrayAdapter<String> ar_unidade_dosagem;
    private TextView    tx_tipo;
    private TextView    tx_dt_ini;
    private TextView    tx_conta_dose;
    private List<Long>  ls_rem_id;
    private List<String> ls_rem_des;
    private List<Long>   ls_tip_id;
    private List<String> ls_tip_des;
    private String field_inicio_em;
    private String field_repetir_de;
    private String field_unidade_repetir;
    private String field_durante;
    private String field_unidade_durante;
    private String field_dosagem;
    private String field_unidade_dosagem;
    private Database database;
    private Domain domain;
    private String rem_descricao;
    private int conta_dose;
    private int old_conta_dose;
    private AnimalXRemedio animal_x_remedio;
    private String animal_especie;
    private DateListener data_dialog;
    private String hidden_date, hidden_time;
    private Date hidden_limit;

    private int getNumber(String str) {
        int num;
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            num = 0;
        }
        return num;
    }

    private void setContaDoseListener() {
        if (conta_dose > 1) {
            String d_rem = getIntent().getStringExtra("DATA_REM");
            java.sql.Date sql_rem = Dates.getSQLDate(d_rem, true);
            int in_de = getNumber(ed_de.getText().toString().trim());
            String st_de = sp_unidade_repetir.getSelectedItem().toString();
            int index = sp_remedios.getSelectedItemPosition();
            if (index == -1) {
                tx_conta_dose.setPaintFlags(0);
                tx_conta_dose.setOnClickListener(null);
                return;
            }
            tx_conta_dose.setPaintFlags(tx_conta_dose.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tx_conta_dose.setOnClickListener(this);
            if (animal_x_remedio == null) {
                animal_x_remedio = new AnimalXRemedio();
            }
            animal_x_remedio.set_animal(animal_id);
            long remedio_id = ls_rem_id.get(index);
            animal_x_remedio.set_remedio(remedio_id);
            animal_x_remedio.setInicio_em(sql_rem);
            animal_x_remedio.setConta_dose(conta_dose -1);
            animal_x_remedio.setRepetir_de(in_de);
            animal_x_remedio.setUnidade_repetir(st_de);
            if (Connector.OpenDatabase(getResources(), this, database, domain, false)) {
                Connector.setDateAndTimeForRemedio(animal_x_remedio, domain, this);
                database.Close();
            } else {
                return;
            }
            hidden_date  = Dates.getShortDateForString(Dates.convertSQLFromDefaultDate(animal_x_remedio.getDose_data()).toString(), "en", "US");
            hidden_time  = animal_x_remedio.getDose_hora().toString();
            int durante     = getNumber(ed_durante.getText().toString());
            String un_durante = sp_unidade_durante.getSelectedItem().toString();
            Calendar cal = Dates.joinDateAndTimeSql(d_rem, hidden_time);
            hidden_limit = Dates.getDateLimitFor(cal.getTime(), un_durante, durante);
            animal_x_remedio = null;
        } else {
            tx_conta_dose.setPaintFlags(0);
            tx_conta_dose.setOnClickListener(null);
        }
    }

    private int getContaDose() {
        int index = sp_remedios.getSelectedItemPosition();
        if (index == -1 || !Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return 0;
        }
        long remedio_id = ls_rem_id.get(index);
        Date dt = Dates.StringToDate(getIntent().getStringExtra("DATA_REM"), "en", "US", false);
        java.sql.Date sqlDate = Dates.getSQLDateFromUtilDate(dt);
        ArrayList<String> fields = new ArrayList<String>();
        int result = Connector.getLastContaDoseForAnimalXRemedio(this, domain, animal_id, remedio_id, sqlDate, fields);
        /*
        System.out.println(">>> RESULT: " + result);
        System.out.println(">>> FIELD SIZE: " + fields.size());
        */
        if (result > 0 && fields.size() > 5) {
            ed_de.setText(fields.get(0));
            selectedOtherSpinner(sp_unidade_repetir, fields.get(1));
            ed_durante.setText(fields.get(2));
            selectedOtherSpinner(sp_unidade_durante, fields.get(3));
            ed_dosagem.setText(fields.get(4));
            selectedOtherSpinner(sp_unidade_dosagem, fields.get(5));
        }
        /*
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                System.out.println(">>> FIELD[" + i + "] = " + fields.get(i) + " <<<");
            }
        } else {
            System.out.println(">>> RESULT IS NULL! <<<");
        }
        */
        database.Close();
        return result;
    }

    private boolean isValidNumber(String txt, String selected) {
        int number = getNumber(txt);
        if (selected.toUpperCase().compareTo("HORAS") == 0) {
            if (number > 24) return false;
        } else if (selected.toUpperCase().compareTo("DIAS") == 0) {
            if (number > 30) return false;
        } else if (selected.toUpperCase().compareTo("SEMANAS") == 0) {
            if (number > 10) return false;
        } else if (selected.toUpperCase().compareTo("MESES") == 0) {
            if (number > 12) return false;
        } else if (selected.toUpperCase().compareTo("ANOS") == 0) {
            if (number > 10) return false;
        }
        return true;
    }

    private void resetFields() {
        int a, b, c;
        a = getNumber(ed_de.getText().toString());
        b = getNumber(ed_durante.getText().toString());
        c = getNumber(ed_dosagem.getText().toString());
        if (a == 0) ed_de.setText("");
        if (b == 0 && sp_unidade_durante.getSelectedItemPosition() != 4) ed_durante.setText("");
        if (c == 0) ed_dosagem.setText("");
    }

    private boolean isMassive(String p1, String p2) {
        if (p1.toUpperCase().compareTo("HORAS") == 0) {
            if (p2.toUpperCase().compareTo("MESES") == 0 || p2.toUpperCase().compareTo("ANOS") == 0 || p2.toUpperCase().compareTo("PARA SEMPRE") == 0) {
                return true;
            }
        } else if (p1.toUpperCase().compareTo("DIAS") == 0) {
            if (p2.toUpperCase().compareTo("ANOS") == 0 || p2.toUpperCase().compareTo("PARA SEMPRE") == 0) {
                return true;
            }
        } else if (p1.toUpperCase().compareTo("SEMANAS") == 0) {
            if (p2.toUpperCase().compareTo("PARA SEMPRE") == 0) {
                return true;
            }
        }
        return false;
    }

    private void selectedRemedio() {
        for (int i = 0; i < sp_remedios.getCount(); i++) {
            String cmp = sp_remedios.getItemAtPosition(i).toString();
            if (cmp.compareTo(rem_descricao) == 0) {
                sp_remedios.setSelection(i);
                break;
            }
        }
    }

    private void readAnimalXRemedio() {

        // System.out.println(">>> HAS BACK TO? " + getIntent().hasExtra("BACK_TO"));

        if (getIntent().hasExtra("BACK_TO") == false) {
            return;
        }
        animal_x_remedio = (AnimalXRemedio) getIntent().getSerializableExtra("BACK_TO");
        Date dt = Dates.convertSQLFromDefaultDate(animal_x_remedio.getInicio_em());
        String str =  Dates.DateToString(dt, DateFormat.LONG);
        tx_dt_ini.setText(str);
        ed_de.setText(String.valueOf(animal_x_remedio.getRepetir_de()));
        selectedOtherSpinner(sp_unidade_repetir, animal_x_remedio.getUnidade_repetir());
        ed_durante.setText(String.valueOf(animal_x_remedio.getDurante()));
        selectedOtherSpinner(sp_unidade_durante, animal_x_remedio.getUnidade_durante());
        toggleSpinnerDurante(sp_unidade_durante.getSelectedItemPosition());
        ed_dosagem.setText(String.valueOf(animal_x_remedio.getDosagem()));
        selectedOtherSpinner(sp_unidade_dosagem, animal_x_remedio.getUnidade_dosagem());
        conta_dose = animal_x_remedio.getConta_dose();
        tx_conta_dose.setText("Dose: #" + conta_dose);
        setContaDoseListener();
    }

    private void writeAnimalXRemedio() {
        /*
        System.out.println(">>> HAS REMEDIO? " + getIntent().hasExtra("DATA_REM"));
        System.out.println(">>> DATA REMEDIO: " + getIntent().getStringExtra("DATA_REM"));
        */
        if (getIntent().hasExtra("DATA_REM") == false) {
            return;
        }
        animal_x_remedio = new AnimalXRemedio();
        String str = tx_dt_ini.getText().toString();
        Date dt = Dates.StringToDate(getIntent().getStringExtra("DATA_REM"), "en", "US", false);
        java.sql.Date sqlDate = Dates.getSQLDateFromUtilDate(dt);
        animal_x_remedio.setInicio_em(sqlDate);
        animal_x_remedio.setRepetir_de(getNumber(ed_de.getText().toString()));
        animal_x_remedio.setDurante(getNumber(ed_durante.getText().toString()));
        animal_x_remedio.setDosagem(getNumber(ed_dosagem.getText().toString()));
        animal_x_remedio.setUnidade_repetir(sp_unidade_repetir.getSelectedItem().toString());
        animal_x_remedio.setUnidade_durante(sp_unidade_durante.getSelectedItem().toString());
        animal_x_remedio.setUnidade_dosagem(sp_unidade_dosagem.getSelectedItem().toString());
        animal_x_remedio.setConta_dose(conta_dose);
        getIntent().putExtra("BACK_TO", animal_x_remedio);
    }

    private void selectedOtherSpinner(Spinner src, String cmp) {
        for (int i = 0; i < src.getCount(); i++) {
            String tmp = src.getItemAtPosition(i).toString();
            try {
                if (tmp.compareTo(cmp) == 0) {
                    src.setSelection(i);
                    break;
                }
            } catch (NullPointerException e) {
                // e.printStackTrace();
            }
        }
    }

    private void toggleSpinnerDurante(int pos) {
        if (pos == 4) {
            if (ed_durante.isEnabled()) {
                ed_durante.setText("0");
                ed_durante.setEnabled(false);
            }
        } else {
            if (!ed_durante.isEnabled()) {
                ed_durante.setEnabled(true);
                if (ed_durante.getText().toString().compareTo("0") == 0) {
                    ed_durante.setText("");
                    ed_durante.requestFocus();
                }
            }
        }
    }

    private boolean dateChanged() {

        // System.out.println(">>> INICIO EM: " + field_inicio_em);

        if (field_inicio_em == null) {
            return false;
        }
        String date_cmp =  Dates.DateToString(Dates.getDateForSqlString(field_inicio_em), DateFormat.LONG);
        String date_src =  tx_dt_ini.getText().toString();
        return (!date_cmp.isEmpty() && date_cmp.compareTo(date_src) != 0);
    }

    private boolean oneRestChanged() {
        boolean result = ((!field_repetir_de.isEmpty() && ed_de.getText().toString().compareTo(field_repetir_de) != 0) ||
        (!field_unidade_repetir.isEmpty() && sp_unidade_repetir.getSelectedItem().toString().compareTo(field_unidade_repetir) != 0) ||
        (!field_durante.isEmpty() && ed_durante.getText().toString().compareTo(field_durante) != 0) ||
        (!field_unidade_durante.isEmpty() && sp_unidade_durante.getSelectedItem().toString().compareTo(field_unidade_durante) != 0) ||
        (!field_dosagem.isEmpty() && ed_dosagem.getText().toString().compareTo(field_dosagem) != 0) ||
        (!field_unidade_dosagem.isEmpty() && sp_unidade_dosagem.getSelectedItem().toString().compareTo(field_unidade_dosagem) != 0));
        return result;
    }

    private void setFields() {
        if (animal_x_remedio != null || sp_remedios.getCount() == 0) {
            return;
        }
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        int index = sp_remedios.getSelectedItemPosition();
        long remedio_id = ls_rem_id.get(index);
        String[] array = new String[8];
        Date dt;
        if (Connector.getLastAnimalXRemedio(this, domain, animal_id, remedio_id, array)) {

            // System.out.println(">>> CONTA DOSE: " + array[7]);

            if (array[7] != null) {
                field_inicio_em = array[0];
                field_repetir_de = array[1];
                field_unidade_repetir = array[2];
                field_durante = array[3];
                field_unidade_durante = array[4];
                field_dosagem = array[5];
                field_unidade_dosagem = array[6];
                conta_dose = getNumber(array[7]);
                conta_dose++;
            } else {
                dt = new Date();
                String tmp = Dates.getSQLStringFromDate(dt);
                field_inicio_em = tmp;
                field_repetir_de = field_durante = field_dosagem = "";
                field_unidade_repetir = "Horas";
                field_unidade_durante =  "Dias";
                field_unidade_dosagem = "Drágeas";
                conta_dose = 1;
            }
            old_conta_dose = conta_dose;
        } else {
            return;
        }
        database.Close();
        dt = Dates.getDateForSqlString(field_inicio_em);
        String str1 = Dates.DateToString(dt, DateFormat.LONG);
        String str2 = Dates.getShortDateForString(dt.toString(), "en", "US");
        getIntent().putExtra("DATA_REM", str2);
        tx_dt_ini.setText(str1);
        ed_de.setText(field_repetir_de);
        selectedOtherSpinner(sp_unidade_repetir, field_unidade_repetir);
        ed_durante.setText(field_durante);
        selectedOtherSpinner(sp_unidade_durante, field_unidade_durante);
        ed_dosagem.setText(field_dosagem);
        selectedOtherSpinner(sp_unidade_dosagem, field_unidade_dosagem);
        tx_conta_dose.setText("Dose: #" + conta_dose);
        setContaDoseListener();
    }

    private void setAnimalXRemedio(long n_id, long remedio_id, java.sql.Date sql_rem, int in_de, String st_de, int in_durante, String st_durante, int in_dosagem, String st_dosagem, String data_dose, String hora_dose) {

        // System.out.println(">>> Set Animal X Remedio! <<<");

        animal_x_remedio = new AnimalXRemedio();
        animal_x_remedio.set_id(n_id);
        animal_x_remedio.set_remedio(remedio_id);
        animal_x_remedio.setInicio_em(sql_rem);
        animal_x_remedio.setRepetir_de(in_de);
        animal_x_remedio.setDurante(in_durante);
        animal_x_remedio.setDosagem(in_dosagem);
        animal_x_remedio.setUnidade_repetir(st_de);
        animal_x_remedio.setUnidade_durante(st_durante);
        animal_x_remedio.setUnidade_dosagem(st_dosagem);
        /*
        System.out.println(">>> DATA DOSE: " + data_dose);
        System.out.println(">>> HORA DOSE: " + hora_dose);
        */
        //data_dose = Dates.getSumDateAndTime(data_dose, hora_dose);

        //System.out.println(">>> DATA DOSE: " + data_dose);

        java.sql.Date sql_date_dose = Dates.getSQLDate(data_dose, true);
        java.sql.Time sql_time_dose = Dates.getSQLTime(hora_dose);
        /*
        System.out.println(">>> DATA DOSE: " + sql_date_dose);
        System.out.println(">>> HORA DOSE: " + sql_time_dose);
        */
        animal_x_remedio.setDose_data(sql_date_dose);
        animal_x_remedio.setDose_hora(sql_time_dose);

    }

    private boolean updateAnimalXRemedio() {
        long remedio_id = animal_x_remedio.get_remedio();
        java.sql.Date sql_rem = animal_x_remedio.getInicio_em();
        int in_de = animal_x_remedio.getRepetir_de();
        String st_de = animal_x_remedio.getUnidade_repetir();
        int in_durante = animal_x_remedio.getDurante();
        String st_durante = animal_x_remedio.getUnidade_durante();
        int in_dosagem = animal_x_remedio.getDosagem();
        String st_dosagem = animal_x_remedio.getUnidade_dosagem();
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return false;
        }
        long result = Connector.SaveUpdateAnimalXRemedio(this, domain, animal_id, remedio_id, sql_rem, in_de, st_de, in_durante, st_durante, in_dosagem, st_dosagem);
        if (result < 0) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean setupNotifications(long n_id, long remedio_id, java.sql.Date sql_rem, Date full, int num) {

        // System.out.println(">>> Setup Notifications! <<<");

        Notifications.addRemedioNotify(this, n_id, animal_especie, animal_name, tx_tipo.getText().toString(), sp_remedios.getSelectedItem().toString(),
        full, num, sp_unidade_repetir.getSelectedItem().toString(), conta_dose+1);
        if (conta_dose > 1) {
            boolean flag = true;
            if (!domain.getConnection().isOpen()) {

                // System.out.println(">>> BANCO DE DADOS ESTAVA FECHADO! <<<");

                if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
                    return false;
                }
            } else {

                // System.out.println(">>> BANCO DE DADOS ESTAVA ABERTO! <<<");

                flag = false;
            }
            long r_id = Connector.getLastDoseOfRemedio(this, domain, animal_id, remedio_id, sql_rem, conta_dose-1);
            if (flag == true) {
                database.Close();
            }
            if (r_id > 0) {

                // System.out.println(">>> Remove Remedio Notify Id: " + r_id);

                Notifications.removeRemedioNotify(this, (int) r_id);
            } else {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void Salvar(long remedio_id, String d_rem) {
        java.sql.Date sql_rem = Dates.getSQLDate(d_rem, true);
        int in_de = getNumber(ed_de.getText().toString().trim());
        String st_de = sp_unidade_repetir.getSelectedItem().toString();
        int in_durante = getNumber(ed_durante.getText().toString());
        String st_durante = sp_unidade_durante.getSelectedItem().toString();
        int in_dosagem = getNumber(ed_dosagem.getText().toString());
        String st_dosagem = sp_unidade_dosagem.getSelectedItem().toString();
        String dose[] = new String[2];
        long n_id = Connector.SaveAddAnimalXRemedio(this, domain, animal_id, remedio_id, sql_rem, in_de, st_de, in_durante, st_durante, in_dosagem, st_dosagem, conta_dose, dose);
        boolean data_changed = oneRestChanged();
        if (conta_dose > 1 && data_changed) {
            IS_DATA_PICK  = false;
            IS_CONTA_DOSE = false;
            setAnimalXRemedio(n_id, remedio_id, sql_rem, in_de, st_de, in_durante, st_durante, in_dosagem, st_dosagem, dose[0], dose[1]);
            Output.Question(this, getResources().getString(R.string.str_all_rest_changed), getResources().getString(R.string.str_extended));
        } else {
            if (n_id > 0) {
                Date full = Dates.joinDateAndTimeSql(dose[0], dose[1]).getTime();
                /*
                System.out.println(">>> SAVE DATE :" + dose[0]);
                System.out.println(">>> SAVE TIME :" + dose[1]);
                System.out.println(">>> DATE FULL :" + full.toString());
                */
                int num = getNumber(ed_de.getText().toString());
                if (setupNotifications(n_id, remedio_id, sql_rem, full, num)) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_add_remedio);
        Bundle extra = getIntent().getExtras();
        animal_id   = extra.getLong("ANIMAL_ID");
        animal_name = extra.getString("ANIMAL_NAME");
        animal_especie = extra.getString("ANIMAL_ESPECIE");
        title = (TextView) findViewById(R.id.text_medicar_animal);
        title.setText("Medicar: " + animal_name);
        bt_add = (ImageButton) findViewById(R.id.button_adicionar_remedio);
        bt_add.setOnClickListener(this);
        bt_update = (ImageButton) findViewById(R.id.button_atualizar_remedio);
        bt_update.setOnClickListener(this);
        sp_remedios = (Spinner) findViewById(R.id.spinner_nome_remedio);
        sp_remedios.setOnItemSelectedListener(this);
        String[] repetir = { "Horas", "Dias", "Semanas", "Meses", "Anos"};
        String[] durante = { "Dias", "Semanas", "Meses", "Anos", "Para sempre"};
        String[] dosagem = { "Ampolas", "Comprimidos", "Drágeas", "Gotas", "Pipetas", "Tabletes"};
        sp_unidade_repetir = (Spinner) findViewById(R.id.spinner_unidade_repetir);
        sp_unidade_durante = (Spinner) findViewById(R.id.spinner_unidade_durante);
        sp_unidade_dosagem = (Spinner) findViewById(R.id.spinner_unidade_dosagem);
        ar_unidade_repetir = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repetir);
        ar_unidade_durante = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, durante);
        ar_unidade_dosagem = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dosagem);
        sp_unidade_repetir.setAdapter(ar_unidade_repetir);
        sp_unidade_durante.setAdapter(ar_unidade_durante);
        sp_unidade_dosagem.setAdapter(ar_unidade_dosagem);
        sp_unidade_repetir.setOnItemSelectedListener(this);
        sp_unidade_durante.setOnItemSelectedListener(this);
        sp_unidade_dosagem.setOnItemSelectedListener(this);
        sp_unidade_dosagem.setSelection(2);
        tx_tipo = (TextView) findViewById(R.id.text_tipo_remedio);
        tx_dt_ini = (TextView) findViewById(R.id.text_medicado_em);
        tx_dt_ini.setOnClickListener(this);
        ed_de      = (EditText) findViewById(R.id.edit_repetir_de);
        ed_durante = (EditText) findViewById(R.id.edit_durante);
        ed_dosagem = (EditText) findViewById(R.id.edit_dosagem);
        ed_de.setOnFocusChangeListener(this);
        ed_durante.setOnFocusChangeListener(this);
        ed_dosagem.setOnFocusChangeListener(this);
        ed_de.setOnKeyListener(this);
        ed_durante.setOnKeyListener(this);
        ed_dosagem.setOnKeyListener(this);
        tx_conta_dose = (TextView)  findViewById(R.id.text_dose_remedio);
        ls_rem_id  = new ArrayList<Long>();
        ls_rem_des = new ArrayList<String>();
        ls_tip_id  = new ArrayList<Long>();
        ls_tip_des = new ArrayList<String>();
        database = Database.getInstance(this);
        domain = domain.getInstance();
        rem_descricao = "";
        conta_dose = 1;
        animal_x_remedio = null;
        data_dialog = new DateListener(this);
        IS_DATA_PICK = false;
        IS_CONTA_DOSE = false;
        field_repetir_de = field_durante = field_dosagem = null; // 21/02/2018
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animal_x_remedio != null) {
            animal_x_remedio = null;
            return;
        }
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        ls_rem_id.clear();
        ls_rem_des.clear();
        ls_tip_id.clear();
        ls_tip_des.clear();
        Connector.getAllRemedios(this, domain, ls_rem_id, ls_rem_des, ls_tip_id, ls_tip_des);
        ArrayAdapter array = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ls_rem_des.toArray());
        sp_remedios.setAdapter(array);
        if (sp_remedios.getCount() > 0) {
            bt_update.setImageResource(R.drawable.ic_update);
            bt_update.setEnabled(true);
        } else {
            bt_update.setImageResource(R.drawable.ic_update_disabled);
            bt_update.setEnabled(false);
            tx_tipo.setText("[tipo]");
            tx_conta_dose.setText("[dose]");
        }
        database.Close();
        if (!rem_descricao.isEmpty()) {
            selectedRemedio();
        }
    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            case R.id.button_adicionar_remedio:
                it = new Intent(this, RemedioEditActivity.class);
                startActivityForResult(it, 1);
                break;
            case R.id.button_atualizar_remedio:
                it = new Intent(this, RemedioEditActivity.class);
                int index = sp_remedios.getSelectedItemPosition();
                it.putExtra("REMEDIO_ID", ls_rem_id.get(index));
                it.putExtra("REMEDIO_DES", ls_rem_des.get(index));
                it.putExtra("TIPO_ID", ls_tip_id.get(index));
                it.putExtra("TIPO_DES", ls_tip_des.get(index));
                startActivityForResult(it, 1);
                break;
            case R.id.text_medicado_em:
                IS_DATA_PICK = true;
                IS_CONTA_DOSE = false;
                Date dt = Dates.StringToDate(getIntent().getStringExtra("DATA_REM"), "en", "US", false);
                data_dialog.showDialog(this, tx_dt_ini, dt, "DATA_REM", DateFormat.LONG, true);
                break;
            case R.id.text_dose_remedio:
                IS_DATA_PICK = false;
                IS_CONTA_DOSE = true;
                Dose dose = new Dose(this, hidden_date, hidden_time, hidden_limit);
                dose.setDose();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (animal_x_remedio != null) {
            return;
        }
        Spinner item = (Spinner) parent;
        switch (item.getId()) {
            case R.id.spinner_nome_remedio:
                tx_tipo.setText(ls_tip_des.get(position));
                setFields();
                break;
            case R.id.spinner_unidade_repetir:
                if (!isValidNumber(ed_de.getText().toString(), sp_unidade_repetir.getSelectedItem().toString())) {
                    ed_de.setText("");
                }
                break;
            case R.id.spinner_unidade_durante:
                toggleSpinnerDurante(item.getSelectedItemPosition());
                if (!isValidNumber(ed_durante.getText().toString(), sp_unidade_durante.getSelectedItem().toString())) {
                    ed_durante.setText("");
                }
                break;
            case R.id.spinner_unidade_dosagem:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(4).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_voltar:
                finish();
                break;
            case R.id.m_salvar:
                boolean flag_vdes = sp_remedios.getSelectedItemPosition() == -1;
                resetFields();
                boolean flag_1 = tx_dt_ini.getText().toString().isEmpty();
                boolean flag_2 = ed_de.getText().toString().isEmpty();
                boolean flag_3 = ed_durante.getText().toString().isEmpty();
                boolean flag_4 = ed_dosagem.getText().toString().isEmpty();
                boolean flag_5 = sp_unidade_repetir.getSelectedItemPosition() > sp_unidade_durante.getSelectedItemPosition(); //  A frequência é maior ou igual ao intervalo!
                boolean flag_6 = isMassive(sp_unidade_repetir.getSelectedItem().toString(), sp_unidade_durante.getSelectedItem().toString());
                boolean error = true;
                String msg = "";
                if (!flag_vdes && !flag_1 && !flag_2 && !flag_3 && !flag_4 && !flag_5 && !flag_6) {
                    error = false;
                } else {
                    if (!flag_5 && !flag_6) {
                        msg = getResources().getString(R.string.str_empty);
                        String str = "";
                        if (flag_vdes) {
                            str += getResources().getString(R.string.str_remedio) + ", ";
                        }
                        if (flag_1) {
                            str += getResources().getString(R.string.str_data_inicial) + ", ";
                        }
                        if (flag_2) {
                            str += getResources().getString(R.string.str_frequencia) + ", ";
                        }
                        if (flag_3) {
                            str += getResources().getString(R.string.str_intervalo) + ", ";
                        }
                        if (flag_4) {
                            str += getResources().getString(R.string.str_dosagem) + ", ";
                        }
                        str = str.substring(0, str.length() - 2);
                        msg += str;
                    } else {
                        if (flag_5) {
                            msg = getResources().getString(R.string.str_invalid_remedio);
                        } else {
                            msg = getResources().getString(R.string.str_massive);
                        }
                    }
                }
                if (error == true) {
                    Toast toast = Toast.makeText(getApplicationContext(), msg + ".", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
                if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
                    return false;
                }
                long remedio_id = ls_rem_id.get(sp_remedios.getSelectedItemPosition());
                String d_rem = getIntent().getStringExtra("DATA_REM");
                String str_de = ed_de.getText().toString();
                String str_de_un = sp_unidade_repetir.getSelectedItem().toString();
                String str_durante = ed_durante.getText().toString();
                String str_durante_un = sp_unidade_durante.getSelectedItem().toString();
                String str_dosagem = ed_dosagem.getText().toString();
                String str_dosagem_un = sp_unidade_dosagem.getSelectedItem().toString();
                int flag = Connector.LoadAddRemedio(domain, animal_id, remedio_id, d_rem, str_de, str_de_un, str_durante, str_durante_un, str_dosagem, str_dosagem_un, conta_dose);
                if (flag == 1) {
                    Salvar(remedio_id, d_rem);
                } else {
                    switch (flag) {
                        case -2: // Animal já tomou essa dose!
                            msg = getResources().getString(R.string.str_remedio_relation_found);
                            break;
                        case -1: // Consulta SQL falhou buscando remédio!
                            msg = getResources().getString(R.string.str_fail_find_remedio);
                            break;
                        default:
                            msg = "";
                    }
                    if (!msg.isEmpty()) {
                        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                database.Close();
                break;
            case R.id.m_history_remedio:
                Intent it =  new Intent(this, RemedioHistoryActivity.class);
                it.putExtra("ANIMAL_ID", animal_id);
                it.putExtra("ANIMAL_NAME", animal_name);
                if (sp_remedios.getCount() > 0) {
                    it.putExtra("REMEDIO_DES", sp_remedios.getSelectedItem().toString());
                }
                writeAnimalXRemedio();

                //System.out.println(">>> CALL HISTORY! <<<");

                startActivityForResult(it, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("REMEDIO_DES")) {
                rem_descricao = data.getExtras().getString("REMEDIO_DES");
            } else if (data.hasExtra("FLAG_DELETED")){
                if (data.getExtras().getBoolean("FLAG_DELETED") == true) {
                    animal_x_remedio = null;
                } else {
                    selectedOtherSpinner(sp_remedios, getIntent().getStringExtra("REMEDIO_DES"));
                    readAnimalXRemedio();
                }
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText src = (EditText) v;
        if (hasFocus) {
            if (!src.isEnabled()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        EditText txt = (EditText) v;
        switch (txt.getId()) {
            case R.id.edit_repetir_de:
                if (!isValidNumber(txt.getText().toString(), sp_unidade_repetir.getSelectedItem().toString())) {
                    txt.setText("");
                }
                break;
            case R.id.edit_durante:
                if (!isValidNumber(txt.getText().toString(), sp_unidade_durante.getSelectedItem().toString())) {
                    txt.setText("");
                }
                break;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCancel(DialogInterface dialog) {

        // System.out.println(">>> CANCEL DIALOG! <<<");

        if (!IS_DATA_PICK && !IS_CONTA_DOSE) {
            String dose_data = animal_x_remedio.getDose_data().toString();
            String dose_hora = animal_x_remedio.getDose_hora().toString();

            Date full = Dates.joinDateAndTimeSql(dose_data, dose_hora).getTime();
            /*
            System.out.println(">>> SAVE DATE: " + dose_data);
            System.out.println(">>> SAVE TIME: " + dose_hora);
            System.out.println(">>> DATE FULL: " + full.toString());
            */
            int num = getNumber(ed_de.getText().toString());

            if (setupNotifications(animal_x_remedio.get_id(), animal_x_remedio.get_remedio(), animal_x_remedio.getInicio_em(), full, num)) {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDismiss(DialogInterface dialog) {

        // System.out.println(">>> DISMISS DIALOG! <<<");

        if (IS_DATA_PICK) {
            boolean is_changed = false;
            boolean is_empty   = false;
            if (!dateChanged()) {
                /*
                System.out.println(">>> NOT DATA CHANGED! <<<");
                System.out.println(">>> Conta Dose: " + conta_dose);
                System.out.println(">>> Old Conta Dose: " + old_conta_dose);
                System.out.println(">>> Repetir de: " + field_repetir_de);
                System.out.println(">>> Durante: " + field_durante);
                System.out.println(">>> Dosagem: " + field_dosagem);
                */
                if ((field_repetir_de == null || field_repetir_de.isEmpty()) &&
                    (field_durante == null || field_durante.isEmpty()) &&
                    (field_dosagem == null || field_dosagem.isEmpty())) {
                // if (field_repetir_de.isEmpty() && field_durante.isEmpty() && field_dosagem.isEmpty()) { - 21/02/2018
                    is_empty = true;
                } else {
                    conta_dose = old_conta_dose;
                    ed_de.setText(field_repetir_de);
                    selectedOtherSpinner(sp_unidade_repetir, field_unidade_repetir);
                    ed_durante.setText(field_durante);
                    selectedOtherSpinner(sp_unidade_durante, field_unidade_durante);
                    ed_dosagem.setText(field_dosagem);
                    selectedOtherSpinner(sp_unidade_dosagem, field_unidade_dosagem);
                }
            } else {

                // System.out.println(">>> DATA CHANGED! <<<");

                is_changed = true;
            }
            if (sp_remedios.getCount() > 0) {
                if (is_empty || is_changed) {
                    conta_dose = getContaDose() + 1;
                }
                tx_conta_dose.setText("Dose: #" + conta_dose);
            } else {
                conta_dose = 0;
                tx_conta_dose.setText("[dose]");
            }
            setContaDoseListener();
        } else if (IS_CONTA_DOSE) {
            // Nothing!
        } else {
            if (updateAnimalXRemedio()) {
                // String d_rem = getIntent().getStringExtra("DATA_REM");
                // Date full = Dates.puttTimeForLastDate(Dates.getShortDateForString(d_rem)).getTime();
                String dose_data = animal_x_remedio.getDose_data().toString();
                String dose_hora = animal_x_remedio.getDose_hora().toString();

                Date full = Dates.joinDateAndTimeSql(dose_data, dose_hora).getTime();
                /*
                System.out.println(">>> SAVE DATE: " + dose_data);
                System.out.println(">>> SAVE TIME: " + dose_hora);
                System.out.println(">>> DATE FULL: " + full.toString());
                */
                int num = getNumber(ed_de.getText().toString());
                if (setupNotifications(animal_x_remedio.get_id(), animal_x_remedio.get_remedio(), animal_x_remedio.getInicio_em(), full, num)) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                animal_x_remedio = null;
            }
        }
    }
}
