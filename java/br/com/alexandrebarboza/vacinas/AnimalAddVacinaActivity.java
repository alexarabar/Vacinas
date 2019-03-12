package br.com.alexandrebarboza.vacinas;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import br.com.alexandrebarboza.vacinas.Utility.Messages.Pickers.DateListener;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.Notifications;

public class AnimalAddVacinaActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {
    private long        animal_id;
    private String      animal_name;
    private String      animal_especie;
    private TextView    title;
    private ImageButton bt_add;
    private ImageButton bt_update;
    private Spinner     sp_vacinas;
    private TextView    tx_tipo;
    private TextView    tx_dt_vac;
    private TextView    tx_dt_rev;
    private List<Long>   ls_vac_id;
    private List<String> ls_vac_des;
    private List<Long>   ls_tip_id;
    private List<String> ls_tip_des;
    private Database database;
    private Domain domain;
    private String vac_descricao;
    private DateListener data_dialog;

    private void selectedVacina() {
        for (int i = 0; i < sp_vacinas.getCount(); i++) {
            String cmp = sp_vacinas.getItemAtPosition(i).toString();
            if (cmp.compareTo(vac_descricao) == 0) {
                sp_vacinas.setSelection(i);
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void Salvar(long vacina_id, String d_vac, String d_rev) {
        java.sql.Date sql_vac = Dates.getSQLDate(d_vac, true);
        java.sql.Date sql_rev = Dates.getSQLDate(d_rev, true);
        long n_id = Connector.SaveAddAnimalXVacina(this, domain, animal_id, vacina_id, sql_vac, sql_rev);
        if (n_id > 0) {
            Notifications.addVacinaNotify(this, n_id, animal_especie, animal_name, tx_tipo.getText().toString(), sp_vacinas.getSelectedItem().toString(), d_rev);
            long r_id = Connector.getPreviousAnimalXRemedio(this, domain, animal_id, vacina_id, d_vac);
            if (r_id > 0) {
                Notifications.removeVacinaNotify(this, (int) r_id);
            }
            if (r_id > -1) {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_add_vacina);
        Bundle extra = getIntent().getExtras();
        animal_id      = extra.getLong("ANIMAL_ID");
        animal_name    = extra.getString("ANIMAL_NAME");
        animal_especie = extra.getString("ANIMAL_ESPECIE");
        title = (TextView) findViewById(R.id.text_vacinar_animal);
        title.setText("Vacinar: " + animal_name);
        bt_add = (ImageButton) findViewById(R.id.button_adicionar_vacina);
        bt_add.setOnClickListener(this);
        bt_update = (ImageButton) findViewById(R.id.button_atualizar_vacina);
        bt_update.setOnClickListener(this);
        sp_vacinas = (Spinner) findViewById(R.id.spinner_nome_vacina);
        sp_vacinas.setOnItemSelectedListener(this);
        tx_tipo = (TextView) findViewById(R.id.text_tipo_vacina);
        tx_dt_vac = (TextView) findViewById(R.id.text_vacinado_em);
        tx_dt_rev = (TextView) findViewById(R.id.text_revacinar_em);
        tx_dt_vac.setOnClickListener(this);
        tx_dt_rev.setOnClickListener(this);
        ls_vac_id  = new ArrayList<Long>();
        ls_vac_des = new ArrayList<String>();
        ls_tip_id  = new ArrayList<Long>();
        ls_tip_des = new ArrayList<String>();
        database = Database.getInstance(this);
        domain = domain.getInstance();
        vac_descricao = "";
        data_dialog = new DateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        ls_vac_id.clear();
        ls_vac_des.clear();
        ls_tip_id.clear();
        ls_tip_des.clear();
        Connector.getAllVacinas(this, domain, ls_vac_id, ls_vac_des, ls_tip_id, ls_tip_des);
        ArrayAdapter array = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ls_vac_des.toArray());
        sp_vacinas.setAdapter(array);
        if (sp_vacinas.getCount() > 0) {
            bt_update.setImageResource(R.drawable.ic_update);
            bt_update.setEnabled(true);
        } else {
            bt_update.setImageResource(R.drawable.ic_update_disabled);
            bt_update.setEnabled(false);
            tx_tipo.setText("[tipo]");
        }
        database.Close();
        if (!vac_descricao.isEmpty()) {
            selectedVacina();
        }
    }

    @Override
    public void onClick(View v) {
        Intent it;
        Date dt;
        switch (v.getId()) {
            case R.id.button_adicionar_vacina:
                it = new Intent(this, VacinaEditActivity.class);
                startActivityForResult(it, 1);
                break;
            case R.id.button_atualizar_vacina:
                it = new Intent(this, VacinaEditActivity.class);
                int index = sp_vacinas.getSelectedItemPosition();
                it.putExtra("VACINA_ID", ls_vac_id.get(index));
                it.putExtra("VACINA_DES", ls_vac_des.get(index));
                it.putExtra("TIPO_ID", ls_tip_id.get(index));
                it.putExtra("TIPO_DES", ls_tip_des.get(index));
                startActivityForResult(it, 1);
                break;
            case R.id.text_vacinado_em:
                dt = Dates.StringToDate(getIntent().getStringExtra("DATA_VAC"), "en", "US", false);
                data_dialog.showDialog(this, tx_dt_vac, dt, "DATA_VAC", DateFormat.LONG, false);
                break;
            case R.id.text_revacinar_em:
                dt = Dates.StringToDate(getIntent().getStringExtra("DATA_REV"), "en", "US", false);
                data_dialog.showDialog(this, tx_dt_rev, dt, "DATA_REV", DateFormat.LONG, false);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(3).setVisible(true);
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
                boolean flag_vdes = sp_vacinas.getSelectedItemPosition() == -1;
                boolean flag_dt_1 = tx_dt_vac.getText().toString().isEmpty();
                boolean flag_dt_2 = tx_dt_rev.getText().toString().isEmpty();
                boolean flag_dt_3 = (!tx_dt_vac.getText().toString().isEmpty() && !tx_dt_rev.getText().toString().isEmpty()) && (tx_dt_vac.getText().toString().equals(tx_dt_rev.getText().toString()));
                boolean error = true;
                String msg = "";
                if (!flag_vdes && !flag_dt_1 && !flag_dt_2 && !flag_dt_3) {
                    error = false;
                } else {
                    if (!flag_dt_3) {
                        msg = getResources().getString(R.string.str_empty);
                        String str = "";
                        if (flag_vdes) {
                            str += getResources().getString(R.string.str_vacina) + ", ";
                        }
                        if (flag_dt_1) {
                            str += getResources().getString(R.string.str_data_aplicar) + ", ";
                        }
                        if (flag_dt_2) {
                            str += getResources().getString(R.string.str_data_revacinar) + ", ";
                        }
                        str = str.substring(0, str.length() - 2);
                        msg += str;
                    } else {
                        msg = getResources().getString(R.string.str_dates_equal);
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
                long vacina_id = ls_vac_id.get(sp_vacinas.getSelectedItemPosition());
                String d_vac = getIntent().getStringExtra("DATA_VAC");
                String d_rev = getIntent().getStringExtra("DATA_REV");
                int flag = Connector.LoadAddVacina(domain, animal_id, vacina_id, d_vac, d_rev);
                if (flag == 1) {
                    Salvar(vacina_id, d_vac, d_rev);
                } else {
                    switch (flag) {
                        case -5:  // Ainda não chegou a época de aplicar essa vacina no animal!
                            msg = getResources().getString(R.string.str_vacina_not_yet);
                            break;
                        case -4: // A vacina com esse nome e essa data de aplicação já existe para o animal!
                            msg = getResources().getString(R.string.str_vacina_key_found);
                            break;
                        case -3: // Data de revacinação anterior a aplicação!
                            msg = getResources().getString(R.string.str_vacina_date_error);
                            break;
                        case -2: // Animal já tomou essa vacina nesta data!
                            msg = getResources().getString(R.string.str_vacina_relation_found);
                            break;
                        case -1: // Consulta SQL falhou buscando vacina!
                            msg = getResources().getString(R.string.str_fail_find_vacina);
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
            case R.id.m_history_vacina:
                Intent it =  new Intent(this, VacinaHistoryActivity.class);
                it.putExtra("ANIMAL_ID", animal_id);
                it.putExtra("ANIMAL_NAME", animal_name);
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tx_tipo.setText(ls_tip_des.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            vac_descricao = data.getExtras().getString("VACINA_DES");
        }
    }
}
