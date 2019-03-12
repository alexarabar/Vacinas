package br.com.alexandrebarboza.vacinas;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Tipo_Vacina;
import br.com.alexandrebarboza.vacinas.Utility.Connector;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Input;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Output;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.Notifications;
import br.com.alexandrebarboza.vacinas.Utility.Utility;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Vacina;

public class VacinaEditActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private final short INSERT_TIPO = 1;
    private final short UPDATE_TIPO = 2;
    private final short DELETE_TIPO = 3;
    private final short DELETE_VACINA = 4;
    private short operator;
    private Spinner       sp_tipo;
    private ImageButton    bt_add;
    private ImageButton bt_update;
    private ImageButton bt_delete;
    private EditText ed_descricao;
    private TextView tx_title;
    private Vacina   vacina;
    private Database database;
    private Domain   domain;
    private ArrayAdapter<String> adapter_tipo;
    private List<Long>   list_tipo_id;
    private MenuItem mn_excluir;

    private boolean cancelNotifications(ArrayList<Long> ids) {
        if (ids == null) {
            return false;
        }
        final int size = ids.size();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = ids.get(i).intValue();

            // System.out.println(">>> CANCEL NOTIFICATION [" + i + "] = " + values[i]);

        }
        Notifications.removeAllVacinasNotify(this, values);
        return true;
    }

    private void loadTipos(boolean cascade) {
        list_tipo_id.clear();
        adapter_tipo = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        if (Connector.getAllTiposVacina(this, domain, adapter_tipo, list_tipo_id)) {
            if (adapter_tipo.getCount() > 0) {
                Utility.resetImages(true, bt_update, bt_delete);
            } else {
                Utility.resetImages(false, bt_update, bt_delete);
            }
        }
        sp_tipo.setAdapter(adapter_tipo);
        if (cascade) {
            if (mn_excluir != null) {
                mn_excluir.setVisible(false);
                if (vacina != null) {
                    tx_title.setText("Incluir Vacina");
                    ed_descricao.setText("");
                    vacina.set_id(0);
                }
            }
        }
    }

    private void selectedTipo(String tipo) {
        for (int i = 0; i < sp_tipo.getCount(); i++) {
            if (tipo.compareTo(sp_tipo.getItemAtPosition(i).toString()) == 0) {
                sp_tipo.setSelection(i);
            }
        }
    }

    private void Preencher(String tipo) {
        if (!tipo.isEmpty()) {
            selectedTipo(tipo);
        }
        tx_title.setText("Alterar Vacina");
        ed_descricao.setText(vacina.getDescricao());
    }

    private void Excluir() {
        String st1, st2, st3;
        st1 = getResources().getString(R.string.str_confirm_delete);
        st2 = getResources().getString(R.string.str_vacinas);
        st2 = st2.substring(0, st2.length() -1);
        st2 = st2.toLowerCase();
        st3 = ed_descricao.getText().toString();
        operator = DELETE_VACINA;
        Output.Question(this, st1 + " a " + st2 + " " + st3 + "?", getResources().getString(R.string.str_remove));
    }

    private boolean Salvar(String descricao) {
        long r_id = 0;
        long t_id = list_tipo_id.get(sp_tipo.getSelectedItemPosition());
        long v_id = getIntent().getLongExtra("VACINA_ID", 0);
        if (vacina.get_id() == 0) {   // Modo de Inclusão.
            r_id = Connector.SaveAddVacina(this, domain, vacina, t_id, descricao);
        } else {                      // Modo de Alteração.
            r_id = Connector.SaveUpdateVacina(this, domain, vacina, v_id, t_id, descricao);
        }
        if (r_id > 0) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacina_edit);
        operator = 0; // nenhuma operação.
        bt_add = (ImageButton) findViewById(R.id.button_adicionar_tipo);
        bt_add.setOnClickListener(this);
        bt_update = (ImageButton) findViewById(R.id.button_atualizar_tipo);
        bt_update.setOnClickListener(this);
        bt_delete = (ImageButton) findViewById(R.id.button_remover_tipo);
        bt_delete.setOnClickListener(this);
        sp_tipo = (Spinner) findViewById(R.id.spinner_tipo_vacina);
        ed_descricao = (EditText) findViewById(R.id.edit_descricao_vacina);
        tx_title = (TextView) findViewById(R.id.text_operation);
        list_tipo_id  = new ArrayList<Long>();
        database = Database.getInstance(this);
        domain = domain.getInstance();
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        loadTipos(false);
        vacina = new Vacina();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("VACINA_ID")) { // modo de alteração;
            vacina.set_id(bundle.getLong("VACINA_ID"));
            vacina.set_tipo(bundle.getLong("TIPO_ID"));
            vacina.setDescricao(bundle.getString("VACINA_DES"));
            Preencher(bundle.getString("TIPO_DES"));
        }
        database.close();
    }

    @Override
    public void onClick(View v) {
        Input input;
        switch (v.getId()) {
            case R.id.button_adicionar_tipo:
                operator = INSERT_TIPO;
                input = new Input(this, "Tipo", "", 25);
                input.setInput();
                break;
            case R.id.button_atualizar_tipo:
                operator = UPDATE_TIPO;
                input = new Input(this, "Tipo", sp_tipo.getSelectedItem().toString(), 25);
                input.setInput();
                break;
            case R.id.button_remover_tipo:
                operator = DELETE_TIPO;
                String st1, st2, st3;
                st1 = getResources().getString(R.string.str_confirm_delete);
                st2 = getResources().getString(R.string.str_tipos);
                st2 = st2.substring(0, st2.length() -1);
                st2 = st2.toLowerCase();
                st3 = sp_tipo.getSelectedItem().toString();
                Output.Question(this, st1 + " o " + st2 + " " + st3 + "?", getResources().getString(R.string.str_remove));
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        String str = null;
        Tipo_Vacina tipo = null;
        if (operator != DELETE_TIPO && operator != DELETE_VACINA) {
            Intent intent = getIntent();
            str = intent.getStringExtra("INPUT");
            str = str.trim();
            if (str.isEmpty()) {
                return;
            }
            str = str.toLowerCase();
            tipo = new Tipo_Vacina();
        }
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        long n_id = -1;
        boolean cascade = false;
        boolean is_end  = false;
        boolean is_completed = true;
        int flag = 0;
        switch (operator) {
            case INSERT_TIPO:
                flag = Connector.LoadAddTipoVacina(domain, str);
                if (flag > 0) {
                    n_id = Connector.SaveAddTipoVacina(this, domain, tipo, str);
                }
                break;
            case UPDATE_TIPO:
                flag = Connector.LoadUpdateTipoVacina(domain, sp_tipo.getSelectedItem().toString(), str);
                if (flag > 0) {
                    long v_id = list_tipo_id.get(sp_tipo.getSelectedItemPosition());
                    n_id = Connector.SaveUpdateTipoVacina(this, domain, tipo, v_id, str);
                }
                break;
            case DELETE_TIPO:
                long t_id = list_tipo_id.get(sp_tipo.getSelectedItemPosition());
                cascade = (vacina != null && vacina.get_tipo() == t_id);
                ArrayList<Long> tipo_ids = Connector.geListIdForAnimalXVacinaByTipo(this, domain, sp_tipo.getSelectedItem().toString());
                is_completed = cancelNotifications(tipo_ids);
                if (Connector.DeleteTipoVacina(domain, t_id)) {
                    n_id = 1;
                } else {
                    String msg = getResources().getString(R.string.str_err_delete) + " " + getResources().getString(R.string.str_tipos_vacina) + getResources().getString(R.string.str_msg_plus) + domain.getError();
                    Output.Alert(this, getResources().getString(R.string.str_fail), msg);
                    cascade = false;
                }
                break;
            case DELETE_VACINA:
                ArrayList<Long> desc_ids = Connector.geListIdForAnimalXVacinaByDescricao(this, domain, vacina.getDescricao());
                is_completed = cancelNotifications(desc_ids);
                if (Connector.DeleteVacina(domain, vacina.get_id())) {
                    n_id = 1;
                    is_end = true;
                } else {
                    if (domain.getError() != null) {
                        String msg = getResources().getString(R.string.str_err_delete) + " " + getResources().getString(R.string.str_vacinas) + " " + getResources().getString(R.string.str_msg_plus) + domain.getError();
                        Output.Alert(this, getResources().getString(R.string.str_fail), msg);
                    } else {
                        Toast toast = Toast.makeText(this, getResources().getString(R.string.str_delete_disabled), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                break;
        }
        if (n_id > 0) {
            if (is_completed) {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (!is_end) {
                loadTipos(cascade);
                if (operator == UPDATE_TIPO || operator == INSERT_TIPO) {
                    selectedTipo(tipo.getDescricao());
                }
            }
        } else {
            if (operator != DELETE_TIPO && operator != DELETE_VACINA) {
                String msg = "";
                switch (flag) {
                    case -2: // Já existe um tipo de vacina com essa descrição!
                        msg = getResources().getString(R.string.str_vacina_tipo_found);
                        break;
                    case -1: // Consulta SQL falhou buscando descrição!
                        msg = getResources().getString(R.string.str_fail_find_desc);
                        break;
                    case 0: // Nenhuma alteração foi feita!
                        msg = getResources().getString(R.string.str_update_none);
                        break;
                }
                if (!msg.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

        }
        database.close();
        operator = 0;
        if (is_end && is_completed) {
            finish();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (vacina.get_id() > 0) {
            menu.getItem(2).setVisible(true);
        }
        mn_excluir = menu.getItem(2);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_voltar:
                finish();
                break;
            case R.id.m_salvar:
                String msg = getResources().getString(R.string.str_empty);
                boolean flag_tipo = (sp_tipo.getCount() == 0);
                boolean flag_desc = ed_descricao.getText().toString().trim().isEmpty();
                boolean error = true;
                if (!flag_tipo && !flag_desc) {
                    error = false;
                } else if (flag_tipo && flag_desc) {
                    msg += getResources().getString(R.string.str_tipo) + ", " + getResources().getString(R.string.str_descricao);
                } else if (flag_tipo)  {
                    msg += getResources().getString(R.string.str_tipo);
                } else if (flag_desc) {
                    msg += getResources().getString(R.string.str_descricao);
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
                int flag = 0;
                boolean stay = false;
                String str = ed_descricao.getText().toString().trim().toUpperCase();
                if (vacina.get_id() == 0) { // Inclusão.
                    flag = Connector.LoadAddVacina(domain, str);
                } else {                    // Alteração.
                    Bundle bundle = getIntent().getExtras();
                    long   v_id  = list_tipo_id.get(sp_tipo.getSelectedItemPosition());
                    String v_des = ed_descricao.getText().toString();
                    flag = Connector.LoadUpdateVacina(domain, vacina, v_id, v_des);
                }
                if (flag == 1) {
                    if (Salvar(str) == false) {
                        stay = true;
                    }
                    msg = "";
                } else {
                    switch (flag) {
                        case -2: // Já existe uma vacina com essa descrição!
                            msg = getResources().getString(R.string.str_vacina_found);
                            break;
                        case -1: // Consulta SQL falhou buscando descrição!
                            msg = getResources().getString(R.string.str_fail_find_desc);
                            break;
                        case 0: // Nenhuma alteração foi feita!
                            msg = getResources().getString(R.string.str_update_none);
                            break;
                        default:
                            msg = "";
                    }
                }
                database.Close();
                if (!msg.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    stay = true;
                }
                if (!stay) {
                    Intent data = new Intent();
                    data.putExtra("VACINA_DES", str);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            case R.id.m_excluir:
                Excluir();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
