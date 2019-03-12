package br.com.alexandrebarboza.vacinas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.alexandrebarboza.vacinas.Database.Database;
import br.com.alexandrebarboza.vacinas.Domain.Domain;
import br.com.alexandrebarboza.vacinas.Domain.Entity.Animal;
import br.com.alexandrebarboza.vacinas.Utility.Connector;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.BootReceiver;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.TaskRemedios;
import br.com.alexandrebarboza.vacinas.Utility.Notifications.TaskVacinas;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, View.OnTouchListener, AdapterView.OnItemClickListener {
    private Database   database;
    private Domain       domain;
    private DataFilter   filter;
    private ImageButton  bt_add;
    private EditText    ed_name;
    private ListView ls_animais;
    private ArrayAdapter<Animal> adapter;

    public Activity getActivity() {
         return this;
    }

    public static final String p_animal = "ANIMAL";

    private class DataFilter implements TextWatcher {
        private ArrayAdapter<Animal> adapter;

        private DataFilter(ArrayAdapter<Animal> adapter) {
            this.adapter = adapter;
        }

        public void setAdapter(ArrayAdapter<Animal> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void executeCancelNotifications() {
        if (Connector.OpenDatabase(database, domain, false)) {
            ArrayList<ArrayList<String>>  content1 = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> content2 = new ArrayList<ArrayList<String>>();
            Connector.LoadVacinasForNotifications(this, domain, content1);
            Connector.LoadRemediosForNotifications(this, domain, content2);
            database.Close();
            TaskVacinas notif1 = new TaskVacinas(this, this.getIntent(), 1, true);
            TaskRemedios notif2 = new TaskRemedios(this, 2, true);
            notif1.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, content1);
            notif2.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, content2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_add = (ImageButton) findViewById(R.id.button_adicionar_animal);
        bt_add.setOnClickListener(this);
        ed_name = (EditText) findViewById(R.id.edit_nome_animal);
        ed_name.setOnTouchListener(this);
        ls_animais = (ListView) findViewById(R.id.list_animais);
        ls_animais.setOnItemClickListener(this);
        ls_animais.setOnTouchListener(this);
        ed_name.setOnFocusChangeListener(this);
        ls_animais.setOnFocusChangeListener(this);
        database = Database.getInstance(this);
        domain = domain.getInstance();
        // System.out.println(">>> MAIN ACTIVITY <<<");
        // Test Boot Receiver!
        // BootReceiver receiver = new BootReceiver();
        // receiver.onReceive(this, this.getIntent());
        executeCancelNotifications();
    }

    @Override
    protected void onStart() {
        if (!Connector.OpenDatabase(getResources(), this, database, domain, false)) {
            return;
        }
        adapter = domain.findAnimais(this);
        ls_animais.setAdapter(adapter);
        filter = new DataFilter(adapter);
        ed_name.addTextChangedListener(filter);
        database.Close();
        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.containsKey("ANIMAL_TEXT")) {
            ed_name.setText(extra.getString("ANIMAL_TEXT"));
            ed_name.setSelection(ed_name.getText().toString().length());
        }
        ls_animais.requestFocus();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        Intent it = new Intent(this, AnimalEditActivity.class);
        it.putExtra("ANIMAL_TEXT", ed_name.getText().toString());
        startActivity(it);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Animal animal = adapter.getItem(position);
        Intent it = new Intent(this, AnimalEditActivity.class);
        it.putExtra(p_animal, animal);
        startActivity(it);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.list_animais) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.list_animais) {
            ls_animais.requestFocus();
        } else if (v.getId() == R.id.edit_nome_animal) {
            ed_name.setText("");
        }
        return false;
    }
}
