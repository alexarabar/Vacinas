package br.com.alexandrebarboza.vacinas;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class VacinaHistoryFiltroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnClickListener, CheckBox.OnCheckedChangeListener  {
    LinearLayout layout_main, layout_tipo, layout_descricao, layout_aplicada, layout_revacinar;
    Spinner sp_tipo, sp_descricao;
    ArrayAdapter<String> ar_tipo, ar_descricao;
    CheckBox check_aplicada, check_revacinar;
    TextView text_aplicada_start, text_aplicada_end, text_revacinar_start, text_revacinar_end;
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

    private void resetWhereClause() {
        if (where_clause == null || where_clause.isEmpty())
            return;
        String value;
        String[] parts = where_clause.split("AND");
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(parts));
        for (int i = 0; i < list.size(); i++) {
            String[] subs = list.get(i).split("=");
            value = subs[1].trim();
            value = value.substring(1, value.length() -1);
            if (subs[0].trim().compareTo("T.descricao") == 0) {
                if (layout_tipo.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_tipo, value);
                    loadArrayVacinas(sp_tipo.getSelectedItem().toString());
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("V.descricao") == 0) {
                if (layout_descricao.getVisibility() == View.VISIBLE) {
                    findItemOnSpinner(sp_descricao, value);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.aplicada_em >") == 0) {
                if (layout_aplicada.getVisibility() == View.VISIBLE) {
                    Date dt = Dates.getDateForSqlString(value);
                    String str = Dates.DateToString(dt, DateFormat.SHORT);
                    text_aplicada_start.setText(str);
                    check_aplicada.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.aplicada_em <") == 0) {
                if (layout_aplicada.getVisibility() == View.VISIBLE) {
                    Date dt = Dates.getDateForSqlString(value);
                    String str = Dates.DateToString(dt, DateFormat.SHORT);
                    text_aplicada_end.setText(str);
                    check_aplicada.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.revacinar_em >") == 0) {
                if (layout_revacinar.getVisibility() == View.VISIBLE) {
                    Date dt = Dates.getDateForSqlString(value);
                    String str = Dates.DateToString(dt, DateFormat.SHORT);
                    text_revacinar_start.setText(str);
                    check_revacinar.setChecked(true);
                } else {
                    parts[i] = "";
                }
            } else if (subs[0].trim().compareTo("X.revacinar_em <") == 0) {
                if (layout_revacinar.getVisibility() == View.VISIBLE) {
                    Date dt = Dates.getDateForSqlString(value);
                    String str = Dates.DateToString(dt, DateFormat.SHORT);
                    text_revacinar_end.setText(str);
                    check_revacinar.setChecked(true);
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
        //System.out.println(">>> WHERE SUB CLAUSE: " + where_clause + " <<<");
    }

    private void mountWhereClause() {
        where_clause = "";
        java.sql.Date sql;
        Date dt;
        if (sp_tipo.getSelectedItemPosition() > 0) {
            where_clause += "T.descricao = '" + sp_tipo.getSelectedItem().toString() + "' AND ";
        }
        if (sp_descricao.getSelectedItemPosition() > 0) {
            where_clause += "V.descricao = '" + sp_descricao.getSelectedItem().toString() + "' AND ";
        }
        if (!text_aplicada_start.getText().toString().isEmpty()) {
            dt = Dates.getShortDateForString(text_aplicada_start.getText().toString());
            sql = new java.sql.Date(dt.getTime());
            where_clause += "X.aplicada_em >= '" + sql.toString() + "' AND ";
        }
        if (!text_aplicada_end.getText().toString().isEmpty()) {
            dt = Dates.getShortDateForString(text_aplicada_end.getText().toString());
            sql = new java.sql.Date(dt.getTime());
            where_clause += "X.aplicada_em <= '" + sql.toString() + "' AND ";
        }
        if (!text_revacinar_start.getText().toString().isEmpty()) {
            dt = Dates.getShortDateForString(text_revacinar_start.getText().toString());
            sql = new java.sql.Date(dt.getTime());
            where_clause += "X.revacinar_em >= '" + sql.toString() + "' AND ";
        }
        if (!text_revacinar_end.getText().toString().isEmpty()) {
            dt = Dates.getShortDateForString(text_revacinar_end.getText().toString());
            sql = new java.sql.Date(dt.getTime());
            where_clause += "X.revacinar_em <= '" + sql.toString() + "' AND ";
        }
        if (!where_clause.isEmpty()) {
            where_clause = where_clause.substring(0, where_clause.length() - 4);
        }
    }

    private void getMinAndMaxDates() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getMinMaxDatesForAnimalXVacina(this, domain, dates, animal_id);
        database.Close();
    }

    private void loadArrayTipos() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getTiposVacinaAnimal(this, domain, ar_tipo, animal_id);
        ar_tipo.notifyDataSetChanged();
        database.Close();
    }

    private void loadArrayAllVacinas() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getAllVacinasByAnimal(this, domain, ar_descricao, animal_id);
        ar_descricao.notifyDataSetChanged();
        database.Close();
    }

    private void loadArrayVacinas(String src) {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        Connector.getVacinasAnimalByTipo(this, domain, ar_descricao, animal_id, src);
        ar_descricao.notifyDataSetChanged();
        database.Close();
    }

    private void hiddenLayouts(ArrayList<String> fields) {
        boolean flag_tipo = false;
        boolean flag_descricao = false;
        boolean flag_aplicada = false;
        boolean flag_revacinar = false;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).equals(getResources().getString(R.string.str_tipo))) {
                flag_tipo = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_descricao))) {
                flag_descricao = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_aplicada))) {
                flag_aplicada = true;
            } else if (fields.get(i).equals(getResources().getString(R.string.str_revacinar))) {
                flag_revacinar = true;
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
        if (!flag_aplicada) {
            layout_aplicada.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
        if (!flag_revacinar) {
            layout_revacinar.setVisibility(View.GONE);
            layout_main.setWeightSum(layout_main.getWeightSum() - 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacina_history_filtro);
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> fields = bundle.getStringArrayList("LIST_NAMES");
        animal_id    = bundle.getLong("ANIMAL_ID");
        where_clause = bundle.getString("WHERE_CLAUSE");
        layout_main = (LinearLayout) findViewById(R.id.layout_history_vacinas_filtro);
        layout_tipo = (LinearLayout) findViewById(R.id.layout_tipo);
        layout_descricao = (LinearLayout) findViewById(R.id.layout_descricao);
        layout_aplicada  = (LinearLayout) findViewById(R.id.layout_aplicada);
        layout_revacinar  = (LinearLayout) findViewById(R.id.layout_revacinar);
        sp_tipo = (Spinner) findViewById(R.id.spinner_tipo_vacina);
        sp_descricao = (Spinner) findViewById(R.id.spinner_descricao_vacina);
        check_aplicada = (CheckBox) findViewById(R.id.check_aplicada);
        check_revacinar = (CheckBox) findViewById(R.id.check_revacinar);
        text_aplicada_start = (TextView) findViewById(R.id.text_aplicada_vacina_inicio);
        text_aplicada_end = (TextView) findViewById(R.id.text_aplicada_vacina_fim);
        text_revacinar_start = (TextView) findViewById(R.id.text_revacinar_vacina_inicio);
        text_revacinar_end = (TextView) findViewById(R.id.text_revacinar_vacina_fim);
        String[] array = { "[Selecionar todos]" };
        ArrayList<String> list1 = new ArrayList<String>(Arrays.asList(array));
        ArrayList<String> list2 = new ArrayList<String>(Arrays.asList(array));
        ar_tipo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list1);
        ar_descricao = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list2);
        sp_tipo.setAdapter(ar_tipo);
        sp_descricao.setAdapter(ar_descricao);
        sp_tipo.setOnItemSelectedListener(this);
        sp_descricao.setOnItemSelectedListener(this);
        check_aplicada.setOnCheckedChangeListener(this);
        check_revacinar.setOnCheckedChangeListener(this);
        text_aplicada_start.setOnClickListener(this);
        text_aplicada_end.setOnClickListener(this);
        text_revacinar_start.setOnClickListener(this);
        text_revacinar_end.setOnClickListener(this);
        database = Database.getInstance(this);
        domain = domain.getInstance();
        hiddenLayouts(fields);
        if (layout_tipo.getVisibility() == View.VISIBLE) {
            loadArrayTipos();
        } else {
            if (layout_descricao.getVisibility() == View.VISIBLE) {
                loadArrayAllVacinas();
            }
        }
        dates = new String[4];
        getMinAndMaxDates();
        resetWhereClause();
        data_dialog = new DateListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView src = (TextView) v;
        switch (src.getId()) {
            case R.id.text_aplicada_vacina_inicio:
                if (check_aplicada.isChecked()) {
                    Date dt = Dates.getShortDateForString(text_aplicada_start.getText().toString());
                    data_dialog.showDialog(this, text_aplicada_start, dt, null, DateFormat.SHORT, false);
                }
                break;
            case R.id.text_aplicada_vacina_fim:
                if (check_aplicada.isChecked()) {
                    Date dt = Dates.getShortDateForString(text_aplicada_end.getText().toString());
                    data_dialog.showDialog(this, text_aplicada_end, dt, null, DateFormat.SHORT, false);
                }
                break;
            case R.id.text_revacinar_vacina_inicio:
                if (check_revacinar.isChecked()) {
                    Date dt = Dates.getShortDateForString(text_revacinar_start.getText().toString());
                    data_dialog.showDialog(this, text_revacinar_start, dt, null, DateFormat.SHORT, false);
                }
                break;
            case R.id.text_revacinar_vacina_fim:
                if (check_revacinar.isChecked()) {
                    Date dt = Dates.getShortDateForString(text_revacinar_end.getText().toString());
                    data_dialog.showDialog(this, text_revacinar_end, dt, null, DateFormat.SHORT, false);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner src = (Spinner) parent;
        if (src.getId() == R.id.spinner_tipo_vacina) {
            for (int i = 1; i < sp_descricao.getCount(); i++) {
                ar_descricao.remove(sp_descricao.getItemAtPosition(i).toString());
            }
            if (position  > 0) {
                loadArrayVacinas(src.getItemAtPosition(position).toString());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CheckBox chk = (CheckBox) buttonView;
        if (chk.getId() == R.id.check_aplicada) {
            if (isChecked) {
                text_aplicada_start.setText(dates[0]);
                text_aplicada_end.setText(dates[1]);
            } else {
                text_aplicada_start.setText("");
                text_aplicada_end.setText("");
            }
        } else if (chk.getId() == R.id.check_revacinar) {
            if (isChecked) {
                text_revacinar_start.setText(dates[2]);
                text_revacinar_end.setText(dates[3]);
            } else {
                text_revacinar_start.setText("");
                text_revacinar_end.setText("");
            }
        }
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
}
