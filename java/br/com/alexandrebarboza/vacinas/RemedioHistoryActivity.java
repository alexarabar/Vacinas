package br.com.alexandrebarboza.vacinas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Utility.Connector;
import br.com.alexandrebarboza.vacinas.Utility.Dates;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Output;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.Notifications;

public class RemedioHistoryActivity extends AppCompatActivity implements CheckBox.OnCheckedChangeListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private long animal_id;
    private long[] record_id;
    private String animal_name;
    private ArrayList<Long> deletion_ids;
    private ArrayList<ArrayList<String>> content;
    private Database database;
    private Domain domain;
    private TableLayout table_layout;
    private String cols[];
    private String tables;
    private String where;
    private String where_part;
    private String order;
    private Menu menu;
    ArrayList<String> nameFields;
    private boolean flag_deleted;

    private boolean execAnimalXRemedio() {
        boolean flag = false;
        if (Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            if (Connector.ListAnimalXRemedio(this, domain, content, cols, tables, where, order)) {
                flag = true;
            }
            database.Close();
        }
        return flag;
    }
    private boolean loadAnimalXRemedio(String[] fields) {
        final int numFields = fields.length;
        nameFields = new ArrayList<String>(numFields);
        tables = "AnimalXRemedio AS X, ";
        where  = "X._animal = " + animal_id + " AND ";
        order  = "";
        boolean flag_tipo = false;
        boolean flag_descricao = false;
        ArrayList<String> list_cols = new ArrayList<String>();
        for (int i = 0, j = 1; i < numFields; i++, j++) {
            nameFields.add(fields[i]);
            if (fields[i].equals(getResources().getString(R.string.str_tipo))) {
                list_cols.add("T.descricao");
                tables += "Tipo_Remedio AS T, Remedios AS R, ";
                where  += "T._id =  R._tipo AND ";
                order  += "T.descricao ASC, ";
                flag_tipo = true;
            } else if (fields[i].equals(getResources().getString(R.string.str_descricao))) {
                list_cols.add("R.descricao");
                where  += "R._id == X._remedio AND ";
                order += "R.descricao ASC, ";
                flag_descricao = true;
            } else if (fields[i].equals(getResources().getString(R.string.str_ini))) {
                list_cols.add("X.inicio_em");
                order += "X.inicio_em ASC, ";
            } else if (fields[i].equals(getResources().getString(R.string.str_frequencia))) {
                list_cols.add("X.repetir_de");
                list_cols.add("X.unidade_repetir");
                order += "X.repetir_de ASC, X.unidade_repetir ASC, ";
            } else if (fields[i].equals(getResources().getString(R.string.str_intervalo))) {
                list_cols.add("X.durante");
                list_cols.add("X.unidade_durante");
                order += "X.durante ASC, X.unidade_durante ASC, ";
            } else if (fields[i].equals(getResources().getString(R.string.str_dosagem))) {
                list_cols.add("X.dosagem");
                list_cols.add("X.unidade_dosagem");
                order += "X.dosagem ASC, X.unidade_dosagem ASC, ";
            } else if (fields[i].equals(getResources().getString(R.string.str_dose))) {
                list_cols.add("X.conta_dose");
                order += "X.conta_dose ASC, ";
            }
        }
        if (!flag_tipo && flag_descricao) {
            tables += "Remedios AS R, ";
        } else if (flag_tipo && !flag_descricao) {
            where  += "R._id == X._remedio AND ";
        }
        if (where_part == null || where_part.isEmpty()) {
            where = where.substring(0, where.length() - 4);
        } else {
            if (where_part.indexOf("T.descricao") > -1) {
                if (tables.indexOf("Tipo_Remedio") == -1) {
                    tables += "Tipo_Remedio AS T, ";
                }
            }
            if (where_part.indexOf("R.descricao") > -1) {
                if (tables.indexOf("Remedios") == -1) {
                    tables += "Remedios AS R, ";
                }
            }
            where += where_part;
        }
        tables = tables.substring(0, tables.length() -2);
        if (!order.isEmpty()) {
            order = order.substring(0, order.length() -2);
        }
        /*
        System.out.println("TABLES: " + tables);
        System.out.println("WHERE: " + where);
        System.out.println("ORDER: " + order);
        */
        int size = list_cols.size();
        cols   = new String[size+1];
        cols[0] = "X._id";
        for (int i = 0, j = 1; i < size; i++, j++) {
             cols[j] = list_cols.get(i);
        }
        content.add(nameFields);
        return execAnimalXRemedio();
    }

    private void mountTableHistory() {
        final int field_height = 40;
        table_layout.setStretchAllColumns(true);
        record_id = new long[content.size() -1];

        // System.out.println(">>> mountTableHistory <<<");

        for (int i = 0; i < content.size(); i++) {
            TableRow rowRemedio = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            int color;
            float tx_size;
            int tx_style;
            CheckBox box = new CheckBox(this);
            box.setHeight(field_height);
            if (i == 0) {
                rowRemedio.setBackgroundColor(getResources().getColor(R.color.light_cyan));
                color    = R.color.dark_blue;
                tx_size  = (float) 14.0;
                tx_style = Typeface.BOLD;
            } else {
                if (i % 2 == 0) {
                    rowRemedio.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                } else {
                    rowRemedio.setBackgroundColor(getResources().getColor(R.color.white));
                }
                color    = R.color.black;
                tx_size  = (float) 15.0;
                tx_style = Typeface.NORMAL;
            }
            rowRemedio.setLayoutParams(params);
            rowRemedio.addView(box);
            box.setOnCheckedChangeListener(this);
            for (int j = 0; j < content.get(i).size(); j++) {
                if (j == 0 && i != 0) {
                    try {
                        record_id[i - 1] = Long.parseLong(content.get(i).get(j));
                        box.setTag(record_id[i -1]);
                    } catch (NumberFormatException e) {
                        // e.printStackTrace();
                    }
                } else {
                    if (i == 0) {
                        params.column = j;
                        box.setTag("all");
                    } else {
                        params.column = j -1;
                    }
                    Date dt = Dates.getDateForSqlString(content.get(i).get(j));
                    TextView text = new TextView(this);
                    if (dt == null) {
                        text.setText(content.get(i).get(j));
                    } else {
                        text.setText(Dates.DateToString(dt, DateFormat.SHORT));
                    }
                    text.setTextColor(getResources().getColor(color));
                    text.setPadding(5, 0, 5, 0);
                    text.setTextSize(tx_size);
                    text.setTypeface(null, tx_style);
                    text.setHeight(field_height);
                    rowRemedio.setLayoutParams(params);
                    rowRemedio.addView(text);
                }
            }
            table_layout.addView(rowRemedio);
        }
        TextView text = (TextView) findViewById(R.id.text_empty_table);
        if (table_layout.getChildCount() == 1) {
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    private void checkAllBoxes(boolean checked) {
        for (int i = 0; i < table_layout.getChildCount(); i++) {
            TableRow row = (TableRow) table_layout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View view = row.getChildAt(j);
                Object tag = view.getTag();
                if (tag != null && tag.toString().compareTo("all") != 0) {
                    CheckBox box = (CheckBox) view;
                    box.setChecked(checked);
                }
            }
        }
    }

    private void Reload() {
        table_layout.removeAllViewsInLayout();
        content.clear();
        content.add(nameFields);
        if (execAnimalXRemedio()) {
            mountTableHistory();
        }
        if (table_layout.getChildCount() <= 1) {
            menu.getItem(2).setVisible(false);
            menu.getItem(6).setVisible(false);
            menu.getItem(7).setVisible(false);
        }
    }

    private void Reload(String[] array) {
        table_layout.removeAllViewsInLayout();
        content.clear();
        if (loadAnimalXRemedio(array)) {
            mountTableHistory();
        }
    }

    private void Excluir() {
        if (deletion_ids == null || deletion_ids.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_not_selection), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        Output.Question(this, getResources().getString(R.string.str_delete_all), getResources().getString(R.string.str_remove));
    }

    private void Ordem() {
        Intent it = new Intent(this, RemedioHIstoryOrdemActivity.class);
        it.putStringArrayListExtra("LIST_NAMES", nameFields);
        startActivityForResult(it, 1);
    }

    private void Filtro() {
        Intent it = new Intent(this, RemedioHistoryFiltroActivity.class);
        it.putStringArrayListExtra("LIST_NAMES", nameFields);
        it.putExtra("WHERE_CLAUSE", where_part);
        it.putExtra("ANIMAL_ID", animal_id);
        startActivityForResult(it, 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedio_history);

        // System.out.println(">>> CREATE ACTIVITY HISTORY! <<<");

        Bundle extra = getIntent().getExtras();
        animal_id   = extra.getLong("ANIMAL_ID");
        animal_name = extra.getString("ANIMAL_NAME");
        database = Database.getInstance(this);
        domain   = domain.getInstance();
        content  = new ArrayList<ArrayList<String>>();
        table_layout = (TableLayout) findViewById(R.id.table_remedios);
        deletion_ids = new ArrayList<Long>();
        flag_deleted = false;
        String fields[] = {getResources().getString(R.string.str_tipo), getResources().getString(R.string.str_descricao), getResources().getString(R.string.str_ini), getResources().getString(R.string.str_frequencia), getResources().getString(R.string.str_intervalo), getResources().getString(R.string.str_dosagem), getResources().getString(R.string.str_dose)};
        if (loadAnimalXRemedio(fields)) {
            mountTableHistory();
        }

        // System.out.println(">>> FINISH CREATE ACTIVITY HISTORY! <<<");

    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        final int size = deletion_ids.size();
        String[] ids = new String[size];
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            ids[i] = deletion_ids.get(i).toString();
            values[i] = Integer.parseInt(ids[i]);
        }
        if (Connector.DeleteHistoryRemedios(domain, ids)) {
            flag_deleted = true;
            Notifications.removeAllRemediosNotify(this, values);
            Reload();
        } else {
            String msg = getResources().getString(R.string.str_err_delete) + " " + getResources().getString(R.string.str_animal_x_remedio) + getResources().getString(R.string.str_msg_plus) + domain.getError();
            Output.Alert(this, getResources().getString(R.string.str_fail), msg);
        }
        database.Close();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String tag = buttonView.getTag().toString();
        if (tag == "all") {
            checkAllBoxes(isChecked);
        } else {
            if (isChecked) {
                deletion_ids.add(Long.parseLong(tag));
            } else {
                deletion_ids.remove(Long.parseLong(tag));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        if (table_layout.getChildCount() > 1) {
            menu.getItem(2).setVisible(true);
            menu.getItem(6).setVisible(true);
            menu.getItem(7).setVisible(true);
        }
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_voltar:
                Intent data = new Intent();
                data.putExtra("FLAG_DELETED", flag_deleted);
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.m_excluir:
                Excluir();
                break;
            case R.id.m_ordem:
                Ordem();
                break;
            case R.id.m_filtro:
                Filtro();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String[] array;
            if (requestCode == 1) {
                ArrayList<String> fields = data.getStringArrayListExtra("LIST_NAMES");
                try {
                    array = new String[fields.size()];
                    array = fields.toArray(array);
                    Reload(array);
                } catch (NullPointerException e) {
                    // e.printStackTrace();
                }
            } else {
                where_part = data.getStringExtra("WHERE_CLAUSE");
                //System.out.println(">>> WHERE PART: " + where_part + " <<<");
                array = new String[nameFields.size()];
                array = nameFields.toArray(array);
                Reload(array);
            }
        }
    }
}
