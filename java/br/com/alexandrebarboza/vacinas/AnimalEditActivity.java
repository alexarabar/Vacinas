package br.com.alexandrebarboza.vacinas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Animal;
import br.com.alexandrebarboza.vacinas.Utility.Connector;
import br.com.alexandrebarboza.vacinas.Utility.Dates;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Output;
import br.com.alexandrebarboza.vacinas.Utility.Messages.Pickers.DateListener;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.Notifications;
import br.com.alexandrebarboza.vacinas.Utility.Utility;

public class AnimalEditActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private Database database;
    private Domain domain;
    private Animal animal;
    private ArrayAdapter<String> array_especies;
    private Spinner spinner_especies;
    private EditText edit_nome;
    private TextView text_data;
    private Button button_vacinar;
    private Button button_medicar;
    private MenuItem mn_excluir;
    private MenuItem mn_history;
    private DateListener data_dialog;

    private boolean cancelNotifications(ArrayList<Long> vac_ids, ArrayList<Long> rem_ids) {
        /*
        System.out.println(">>> CANCEL NOTIFICATION! <<<");
        System.out.println("Vacina  Ids:" + vac_ids);
        System.out.println("Remedio Ids:" + rem_ids);
        */
        if (vac_ids == null || rem_ids == null) {
            return false;
        }
        final int s1 = vac_ids.size();
        final int s2 = rem_ids.size();
        int[] v1 = new int[s1];
        int[] v2 = new int [s2];
        for (int i = 0; i < s1; i++) {
            v1[i] = vac_ids.get(i).intValue();

            // System.out.println(">>> CANCEL VACINA NOTIFICATION [" + i + "] = " + v1[i]);

        }
        Notifications.removeAllVacinasNotify(this, v1);

        for (int i = 0; i < s2; i++) {
            v2[i] = rem_ids.get(i).intValue();

            // System.out.println(">>> CANCEL REMEDIO NOTIFICATION [" + i + "] = " + v2[i]);

        }
        Notifications.removeAllRemediosNotify(this, v2);
        return true;
    }

    private void Preencher() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        edit_nome.setText(animal.getNome());
        spinner_especies.setSelection(array_especies.getPosition(animal.getEspecie()));
        Date date = Dates.convertSQLFromDefaultDate(animal.getNascimento());
        text_data.setText(Dates.DateToString(date, DateFormat.LONG));
        String str = Dates.getShortDateForString(date.toString(), "en", "US");
        getIntent().putExtra("DATA", str);
        database.Close();
    }

    private void Salvar() {
        long n_id  = 0;
        String dt = getIntent().getStringExtra("DATA");
        String str_name = edit_nome.getText().toString().trim();
        if (str_name.isEmpty()) return;
        str_name = Utility.UCWords(str_name);
        if (animal.get_id() == 0) {   // Modo de Inclusão.
            n_id = Connector.SaveAddAnimal(this, domain, animal, str_name, spinner_especies.getSelectedItem().toString(), dt);
        } else {                      // Modo de Alteração.
            n_id = Connector.SaveUpdateAnimal(this, domain, animal, str_name, spinner_especies.getSelectedItem().toString(), dt);
        }
        if (n_id > 0) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (animal.get_id() == 0) {
                animal.set_id(n_id);
                button_medicar.setVisibility(View.VISIBLE);
                button_vacinar.setVisibility(View.VISIBLE);
                mn_excluir.setVisible(true);
                mn_history.setVisible(true);
            }
        }
    }

    private void Excluir() {
        String st1, st2, st3;
        st1 = getResources().getString(R.string.str_confirm_delete);
        st2 = getResources().getString(R.string.str_animais);
        st2 = st2.substring(0, st2.length() -1);
        st2 = st2.toLowerCase();
        st3 = edit_nome.getText().toString();
        Output.Question(this, st1 + " o " + st2 + " " + st3 + "?", getResources().getString(R.string.str_remove));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_edit);
        String[] especies = { "Canino", "Felino"};
        spinner_especies = (Spinner) findViewById(R.id.spinner_especie_animal);
        array_especies = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, especies);
        spinner_especies.setAdapter(array_especies);
        edit_nome = (EditText) findViewById(R.id.edit_nome_animal);
        text_data = (TextView) findViewById(R.id.text_nascimento_animal);
        text_data.setOnClickListener(this);
        button_vacinar = (Button) findViewById(R.id.button_vacinar);
        button_medicar = (Button) findViewById(R.id.button_medicar);
        button_vacinar.setOnClickListener(this);
        button_medicar.setOnClickListener(this);
        database = Database.getInstance(this);
        domain = domain.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(MainActivity.p_animal)) { // modo de alteração;
            animal = (Animal) bundle.getSerializable(MainActivity.p_animal);
            Preencher();
        } else {                                         // modo de inclusão.
            button_medicar.setVisibility(View.INVISIBLE);
            button_vacinar.setVisibility(View.INVISIBLE);
            animal = new Animal();
            Bundle extra = getIntent().getExtras();
            edit_nome.setText(extra.getString("ANIMAL_TEXT"));
            edit_nome.setSelection(edit_nome.getText().toString().length());
            if (edit_nome.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isShowing = imm.showSoftInput(edit_nome, InputMethodManager.SHOW_IMPLICIT);
                if (!isShowing) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            }
        }
        data_dialog = new DateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (animal.get_id() > 0) {
            menu.getItem(2).setVisible(true);
            menu.getItem(5).setVisible(true);
        } else {
            mn_excluir = menu.getItem(2);
            mn_history = menu.getItem(5);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it;
        switch(item.getItemId()) {
            case R.id.m_voltar:
                it =  new Intent(this, MainActivity.class);
                it.putExtra("ANIMAL_TEXT", edit_nome.getText().toString());
                startActivity(it);
                finish();
                break;
            case R.id.m_salvar:
                String str_name = edit_nome.getText().toString().trim();
                if (!str_name.isEmpty()) {
                    str_name = Utility.UCWords(str_name);
                }
                String msg = getResources().getString(R.string.str_empty);
                boolean flag_name = str_name.isEmpty();
                boolean flag_data = text_data.getText().toString().trim().isEmpty();
                boolean error = true;
                if (!flag_name && !flag_data) {
                    error = false;
                } else if (flag_name && flag_data) {
                    msg += getResources().getString(R.string.str_animal) + ", " + getResources().getString(R.string.str_nascimento);
                } else if (flag_name)  {
                    msg += getResources().getString(R.string.str_animal);
                } else if (flag_data) {
                    msg += getResources().getString(R.string.str_nascimento);
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
                if (animal.get_id() == 0) { // Inclusão.
                    flag = Connector.LoadAddAnimal(domain, str_name);
                } else {                    // Alteração.
                    flag = Connector.LoadUpdateAnimal(domain, animal, str_name, spinner_especies.getSelectedItem().toString(), text_data.getText().toString());
                }
                if (flag == 1) {
                    Salvar();
                } else {
                    switch (flag) {
                        case -2: // Já existe um animal com esse nome!
                            msg = getResources().getString(R.string.str_name_found);
                            break;
                        case -1: // Consulta SQL falhou buscando nome!
                            msg = getResources().getString(R.string.str_fail_find_name);
                            break;
                        case 0: // Nenhuma alteração foi feita!
                            msg = getResources().getString(R.string.str_update_none);
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
                edit_nome.setText(str_name);
                break;
            case R.id.m_excluir:
                Excluir();
                break;
            case R.id.m_vacina:
                it =  new Intent(this, VacinaHistoryActivity.class);
                it.putExtra("ANIMAL_ID", animal.get_id());
                it.putExtra("ANIMAL_NAME", animal.getNome());
                startActivity(it);
                break;
            case R.id.m_remedio:
                it =  new Intent(this, RemedioHistoryActivity.class);
                it.putExtra("ANIMAL_ID", animal.get_id());
                it.putExtra("ANIMAL_NAME", animal.getNome());
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            case R.id.text_nascimento_animal:
                Date dt = Dates.StringToDate(getIntent().getStringExtra("DATA"), "en", "US", false);
                data_dialog.showDialog(this, text_data, dt, "DATA", DateFormat.LONG, false);
                break;
            case R.id.button_vacinar:
                it = new Intent(this, AnimalAddVacinaActivity.class);
                it.putExtra("ANIMAL_ID", animal.get_id());
                it.putExtra("ANIMAL_NAME", animal.getNome());
                it.putExtra("ANIMAL_ESPECIE", Utility.getAnimalEspecie(spinner_especies.getSelectedItem().toString()));
                startActivity(it);
                break;
            case R.id.button_medicar:
                it = new Intent(this, AnimalAddRemedioActivity.class);
                it.putExtra("ANIMAL_ID", animal.get_id());
                it.putExtra("ANIMAL_NAME", animal.getNome());
                it.putExtra("ANIMAL_ESPECIE", Utility.getAnimalEspecie(spinner_especies.getSelectedItem().toString()));
                startActivity(it);
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, true)) {
            return;
        }
        String[] v = {""};
        ArrayList<Long> vac_ids = Connector.geListIdForAnimalXVacinaByAnimalId(this, domain, animal.get_id());
        ArrayList<Long> rem_ids = Connector.geListIdForAnimalXRemedioByAnimalId(this, domain, animal.get_id());
        boolean is_completed = cancelNotifications(vac_ids, rem_ids);
        boolean flag = Connector.DeleteAnimal(this, domain, animal, v);
        String str = v[0];
        if (!str.isEmpty()) {
            String msg = getResources().getString(R.string.str_err_delete) + " " + str.toLowerCase() + getResources().getString(R.string.str_msg_plus)  + domain.getError();
            Output.Alert(this, getResources().getString(R.string.str_fail), msg);
        } else {
            if (is_completed) {
                if (flag) {
                    Toast toast = Toast.makeText(this, getResources().getString(R.string.str_success), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                Intent it = new Intent(this, MainActivity.class);
                it.putExtra("ANIMAL_TEXT", "");
                startActivity(it);
                finish();
            }
        }
        database.Close();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
