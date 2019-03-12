package br.com.alexandrebarboza.vacinas;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Utility.Connector;
import br.com.alexandrebarboza.vacinas.Utility.Dates;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Pickers.DateListener;

public class RemedioHistoryFiltroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnClickListener, CheckBox.OnCheckedChangeListener, AdapterView.OnFocusChangeListener {
    LinearLayout layout_main, layout_tipo, layout_descricao, layout_inicio_em, layout_repetir_de , layout_durante, layout_dosagem, layout_dose;
    Spinner sp_tipo, sp_descricao, sp_un_repetir, sp_un_durante, sp_un_dosagem;
    ArrayAdapter<String> ar_tipo, ar_descricao;
    CheckBox check_inicio_em, check_repetir_de, check_durante, check_dosagem, check_dose;
    TextView text_inicio_start, text_inicio_end, text_repetir, text_durante, text_dosagem, text_dose;
    EditText edit_repetir, edit_durante, edit_dosagem, edit_dose;
    private ArrayAdapter<String> ar_un_repetir, ar_un_durante, ar_un_dosagem;
    String where_clause;
    private long animal_id;
    private Database database;
    private Domain domain;
    private String[] dates;
    private DateListener data_dialog;

    private void findItemOnSpinner(Spinner src, String value) {
        for (int i = 0; i < src.getCount(); i++) {
            if (src.getItemAtPosition(i).toString().equals(value)) {
                src.setSelection(i);
                break;
            }
        }
    }

    private void loadArrayRemedios(String src) {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getRemediosAnimalByTipo(this, domain, ar_descricao, animal_id, src);
        ar_descricao.notifyDataSetChanged();
        database.Close();
    }

    private void loadArrayTipos() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getTiposRemedioAnimal(this, domain, ar_tipo, animal_id);
        ar_tipo.notifyDataSetChanged();
        database.Close();
    }

    private void loadArrayAllRemedios() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getAllRemediosByAnimal(this, domain, ar_descricao, animal_id);
        ar_descricao.notifyDataSetChanged();
        database.Close();
    }

    private void getMinAndMaxDates() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getMinMaxDatesForAnimalXRemedio(this, domain, dates, animal_id);
        database.Close();
    }

    private void mountWhereClause() {
        where_clause = "";
        java.sql.Date sql;
        Date dt;
        String str, sel;
        if (sp_tipo.getSelectedItemPosition() > 0) {
            where_clause += "T.descricao = '" + sp_tipo.getSelectedItem().toString() + "' AND ";
        }
        if (sp_descricao.getSelectedItemPosition() > 0) {
            where_clause += "R.descricao = '" + sp_descricao.getSelectedItem().toString() + "' AND ";
        }
        if (!text_inicio_start.getText().toString().isEmpty()) {
            dt = Dates.getShortDateForString(text_inicio_start.getText().toString());
            sql = new java.sql.Date(dt.getTime());
            where_clause += "X.inicio_em >= '" + sql.toString() + "' AND ";
        }
        if (!text_inicio_end.getText().toString().isEmpty()) {
            dt = Dates.getShortDateForString(text_inicio_end.getText().toString());
            sql = new java.sql.Date(dt.getTime());
            where_clause += "X.inicio_em <= '" + sql.toString() + "' AND ";
        }
        if (!edit_repetir.getText().toString().isEmpty()) {
            str = edit_repetir.getText().toString();
            sel = sp_un_repetir.getSelectedItem().toString();
            where_clause += "X.repetir_de = " + str + " AND X.unidade_repetir = '" + sel + "' AND ";
        }
        if (!edit_durante.getText().toString().isEmpty()) {
            str = edit_durante.getText().toString();
            sel = sp_un_durante.getSelectedItem().toString();
            where_clause += "X.durante = " + str + " AND X.unidade_durante = '" + sel + "' AND ";
        }
        if (!edit_dosagem.getText().toString().isEmpty()) {
            str = edit_dosagem.getText().toString();
            sel = sp_un_dosagem.getSelectedItem().toString();
            where_clause += "X.dosagem = " + str + " AND X.unidade_dosagem = '" + sel + "' AND ";
        }
        if (!edit_dose.getText().toString().isEmpty())    {
            str = edit_dose.getText().toString();
            where_clause += "X.conta_dose = " + str +  " AND ";
        }

        if (!where_clause.isEmpty()) {
            where_clause = where_clause.substring(0, where_clause.length() - 4);
        }
    }

    private void resetWhereClause() {
        if (where_clause == null || where_clause.isEmpty())
            return;
        String value;
        String[] parts = where_clause.split("AND");
        // System.out.println(">>> WHERE SUB CLAUSE: " + where_clause + " <<<");
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(parts));
        for (int i = 0; i < list.size(); i++) {
            String[] subs = list.get(i).split("=");
            value = subs[1].trim();
            if (value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (subs[0].trim().compareTo("T.descricao") == 0) {
                if (layout_tipo.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_tipo, value);
                    loadArrayRemedios(sp_tipo.getSelectedItem().toString());
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("R.descricao") == 0) {
                if (layout_descricao.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_descricao, value);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.inicio_em >") == 0) {
                if (layout_inicio_em.getVisibility() == View.VISIBLE) {
                    Date dt = Dates.getDateForSqlString(value);
                    String str = Dates.DateToString(dt, DateFormat.SHORT);
                    text_inicio_start.setText(str);
                    check_inicio_em.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.inicio_em <") == 0) {
                if (layout_inicio_em.getVisibility() == View.VISIBLE) {
                    Date dt = Dates.getDateForSqlString(value);
                    String str = Dates.DateToString(dt, DateFormat.SHORT);
                    text_inicio_end.setText(str);
                    check_inicio_em.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.repetir_de") == 0) {
                if (layout_repetir_de.getVisibility() == View.VISIBLE) {
                    edit_repetir.setText(value);
                    check_repetir_de.setChecked(true);
                }else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.unidade_repetir") == 0) {
                if (layout_repetir_de.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_un_repetir, value);
                    check_repetir_de.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.durante") == 0) {
                if (layout_durante.getVisibility() == View.VISIBLE) {
                    edit_durante.setText(value);
                    check_durante.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.unidade_durante") == 0) {
                if (layout_durante.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_un_durante, value);
                    check_durante.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.dosagem") == 0) {
                if (layout_dosagem.getVisibility() == View.VISIBLE) {
                    edit_dosagem.setText(value);
                    check_dosagem.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.unidade_dosagem") == 0) {
                if (layout_dosagem.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_un_dosagem, value);
                    check_dosagem.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.conta_dose") == 0) {
                if (layout_dose.getVisibility() == View.VISIBLE) {
                    edit_dose.setText(value);
                    check_dose.setChecked(true);
                } else {
                    parts[i] = "";
                }
            }
        }
        ArrayList<String> n_list = new ArrayList<String>();
        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                n_list.add(list.get(i));
            }
        }
        where_clause = "";
        for (int i = 0; i < n_list.size(); i++) {
            where_clause = where_clause + n_list.get(i).toString() + " AND ";
        }
        if (!where_clause.isEmpty()) {
            where_clause = where_clause.substring(0, where_clause.length() - 4);
        }
        // System.out.println(">>> WHERE SUB CLAUSE: " + where_clause + " <<<");
    }

    private void hiddenLayouts(ArrayList<String> fields) {
        boolean flag_tipo = false;
        boolean flag_descricao = false;
        boolean flag_inicio = false;
        boolean flag_frequencia = false;
        boolean flag_intervalo = false;
        boolean flag_dosagem = false;
        boolean flag_dose = false;

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).equals(getResources().getString(R.string.str_tipo))) {
                flag_tipo = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_descricao))) {
                flag_descricao = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_ini))) {
                flag_inicio = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_frequencia))) {
                flag_frequencia = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_intervalo))) {
                flag_intervalo = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_dosagem))) {
                flag_dosagem = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_dose))) {
                flag_dose = true;
            }
        }
        if (!flag_tipo) {
            layout_tipo.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_descricao) {
            layout_descricao.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_inicio) {
            layout_inicio_em.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_frequencia) {
            layout_repetir_de.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_intervalo) {
            layout_durante.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_dosagem) {
            layout_dosagem.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_dose) {
            layout_dose.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedio_history_filtro);
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> fields = bundle.getStringArrayList("LIST_NAMES");
        animal_id    = bundle.getLong("ANIMAL_ID");
        where_clause = bundle.getString("WHERE_CLAUSE");
        layout_main = (LinearLayout) findViewById(R.id.layout_history_remedios_filtro);
        layout_tipo = (LinearLayout) findViewById(R.id.layout_tipo);
        layout_descricao = (LinearLayout) findViewById(R.id.layout_descricao);
        layout_inicio_em = (LinearLayout) findViewById(R.id.layout_inicio_em);
        layout_repetir_de = (LinearLayout) findViewById(R.id.layout_repetir_de);
        layout_durante = (LinearLayout) findViewById(R.id.layout_durante);
        layout_dosagem = (LinearLayout) findViewById(R.id.layout_dosagem);
        layout_dose = (LinearLayout) findViewById(R.id.layout_dose);
        sp_tipo = (Spinner) findViewById(R.id.spinner_tipo_remedio);
        sp_descricao = (Spinner) findViewById(R.id.spinner_descricao_remedio);
        sp_un_repetir = (Spinner) findViewById(R.id.spinner_unidade_repetir_de);
        sp_un_durante = (Spinner) findViewById(R.id.spinner_unidade_durante);
        sp_un_dosagem = (Spinner) findViewById(R.id.spinner_unidade_dosagem);
        String[] array = { "[Selecionar todos]" };
        ArrayList<String> list1 = new ArrayList<String>(Arrays.asList(array));
        ArrayList<String> list2 = new ArrayList<String>(Arrays.asList(array));
        ar_tipo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list1);
        ar_descricao = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list2);
        sp_tipo.setAdapter(ar_tipo);
        sp_descricao.setAdapter(ar_descricao);
        sp_tipo.setOnItemSelectedListener(this);
        sp_descricao.setOnItemSelectedListener(this);
        String[] repetir = { "Horas", "Dias", "Semanas", "Meses", "Anos"};
        String[] durante = { "Dias", "Semanas", "Meses", "Anos", "Para sempre"};
        String[] dosagem = { "Ampolas", "Comprimidos", "Drágeas", "Gotas", "Pipetas", "Tabletes"};
        ar_un_repetir = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repetir);
        ar_un_durante = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, durante);
        ar_un_dosagem = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dosagem);
        sp_un_repetir.setOnItemSelectedListener(this);
        sp_un_durante.setOnItemSelectedListener(this);
        sp_un_dosagem.setOnItemSelectedListener(this);
        text_inicio_start = (TextView) findViewById(R.id.text_remedio_data_inicio);
        text_inicio_end   = (TextView) findViewById(R.id.text_remedio_data_fim);
        text_inicio_start.setOnClickListener(this);
        text_inicio_end.setOnClickListener(this);
        text_repetir = (TextView) findViewById(R.id.text_repetir);
        text_durante = (TextView) findViewById(R.id.text_durante);
        text_dosagem = (TextView) findViewById(R.id.text_dosagem);
        text_dose = (TextView) findViewById(R.id.text_dose);
        edit_repetir = (EditText) findViewById(R.id.edit_repetir_de);
        edit_durante = (EditText) findViewById(R.id.edit_durante);
        edit_dosagem = (EditText) findViewById(R.id.edit_dosagem);
        edit_dose = (EditText) findViewById(R.id.edit_dose);
        edit_repetir.setOnFocusChangeListener(this);
        edit_durante.setOnFocusChangeListener(this);;
        edit_dosagem.setOnFocusChangeListener(this);;
        edit_dose.setOnFocusChangeListener(this);;
        check_inicio_em  =(CheckBox) findViewById(R.id.check_inicio_em);
        check_repetir_de =(CheckBox) findViewById(R.id.check_repetir_de);
        check_durante =(CheckBox) findViewById(R.id.check_durante);
        check_dosagem =(CheckBox) findViewById(R.id.check_dosagem);
        check_dose    =(CheckBox) findViewById(R.id.check_dose);
        check_inicio_em.setOnCheckedChangeListener(this);
        check_repetir_de.setOnCheckedChangeListener(this);
        check_durante.setOnCheckedChangeListener(this);
        check_dosagem.setOnCheckedChangeListener(this);
        check_dose.setOnCheckedChangeListener(this);
        database = Database.getInstance(this);
        domain = domain.getInstance();
        hiddenLayouts(fields);
        if (layout_tipo.getVisibility() == View.VISIBLE) {
            loadArrayTipos();
        } else {
            if (layout_descricao.getVisibility() == View.VISIBLE) {
                loadArrayAllRemedios();
            }
        }
        dates = new String[2];
        getMinAndMaxDates();
        resetWhereClause();
        data_dialog = new DateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.m_voltar) {
            Intent data = new Intent();
            mountWhereClause();
            data.putExtra("WHERE_CLAUSE", where_clause);
            setResult(RESULT_OK, data);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        TextView src = (TextView) v;
        switch (src.getId()) {
            case R.id.text_remedio_data_inicio:
                if (check_inicio_em.isChecked()) {
                    Date dt = Dates.getShortDateForString(text_inicio_start.getText().toString());
                    data_dialog.showDialog(this, text_inicio_start, dt, null, DateFormat.SHORT, false);
                }
                break;
            case R.id.text_remedio_data_fim:
                if (check_inicio_em.isChecked()) {
                    Date dt = Dates.getShortDateForString(text_inicio_end.getText().toString());
                    data_dialog.showDialog(this, text_inicio_end, dt, null, DateFormat.SHORT, false);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner src = (Spinner) parent;
        switch (src.getId()) {
            case R.id.spinner_tipo_remedio:
                for (int i = 1; i < sp_descricao.getCount(); i++) {
                     ar_descricao.remove(sp_descricao.getItemAtPosition(i).toString());
                }
                if (position  > 0) {
                    loadArrayRemedios(src.getItemAtPosition(position).toString());
                }
                break;
            case R.id.spinner_descricao_remedio:
                break;
            case R.id.spinner_unidade_repetir_de:
                break;
            case R.id.spinner_unidade_durante:
                if (src.getSelectedItemPosition() == 4) {
                    if (edit_durante.isEnabled()) {
                        edit_durante.setText("0");
                        edit_durante.setEnabled(false);
                    }
                } else {
                    if (!edit_durante.isEnabled()) {
                        edit_durante.setEnabled(true);
                        edit_durante.setText("");
                        edit_durante.requestFocus();
                    }
                }
                break;
            case R.id.spinner_unidade_dosagem:
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CheckBox chk = (CheckBox) buttonView;
        EditText src  = null;
        switch (chk.getId()) {
            case R.id.check_inicio_em:
                if (isChecked) {
                    text_inicio_start.setText(dates[0]);
                    text_inicio_end.setText(dates[1]);
                } else {
                    text_inicio_start.setText("");
                    text_inicio_end.setText("");
                }
                break;
            case R.id.check_repetir_de:
                if (isChecked) {
                     text_repetir.setTextColor(getResources().getColor(R.color.black));
                     edit_repetir.setEnabled(true);
                     sp_un_repetir.setAdapter(ar_un_repetir);
                     edit_repetir.requestFocus();
                } else {
                    text_repetir.setTextColor(getResources().getColor(R.color.dark_gray));
                    edit_repetir.setEnabled(false);
                    edit_repetir.setText("");
                    sp_un_repetir.setAdapter(null);
                    src = edit_repetir;
                }
                break;
            case R.id.check_durante:
                if (isChecked) {
                    text_durante.setTextColor(getResources().getColor(R.color.black));
                    edit_durante.setEnabled(true);
                    sp_un_durante.setAdapter(ar_un_durante);
                    edit_durante.requestFocus();
                } else {
                    text_durante.setTextColor(getResources().getColor(R.color.dark_gray));
                    edit_durante.setEnabled(false);
                    edit_durante.setText("");
                    sp_un_durante.setAdapter(null);
                    src = edit_durante;
                }
                break;
            case R.id.check_dosagem:
                if (isChecked) {
                    text_dosagem.setTextColor(getResources().getColor(R.color.black));
                    edit_dosagem.setEnabled(true);
                    sp_un_dosagem.setAdapter(ar_un_dosagem);
                    sp_un_dosagem.setSelection(2);
                    edit_dosagem.requestFocus();
                } else {
                    text_dosagem.setTextColor(getResources().getColor(R.color.dark_gray));
                    edit_dosagem.setEnabled(false);
                    edit_dosagem.setText("");
                    sp_un_dosagem.setAdapter(null);
                    src = edit_dosagem;
                }
                break;
            case R.id.check_dose:
                if (isChecked) {
                    text_dose.setTextColor(getResources().getColor(R.color.black));
                    edit_dose.setEnabled(true);
                    edit_dose.requestFocus();
                } else {
                    text_dose.setTextColor(getResources().getColor(R.color.dark_gray));
                    edit_dose.setEnabled(false);
                    edit_dose.setText("");
                    src = edit_dose;
                }
                break;
        }
        if (src != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(src.getWindowToken(), 0);
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
}
