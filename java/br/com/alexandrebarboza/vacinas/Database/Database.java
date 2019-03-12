package br.com.alexandrebarboza.vacinas.Database;

import br.com.alexandrebarboza.vacinas.Database.Script.*;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class Database extends SQLiteOpenHelper {
    private static final String NOME_BANCO =  "Veterinario";
    private static final int VERSAO =  12;
    private static Database instance = null;
    private SQLiteDatabase connection;
    private String error;

    private Database(Context context) { // Singleton
        super(context, NOME_BANCO, null, VERSAO);
        connection = null;
        error = "";
    }

    public static Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }

    public SQLiteDatabase getConnection() {
        return connection;
    }

    public String getError(){
        return error;
    }

    public boolean setReadable() {
        try {
            connection = getReadableDatabase();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
            return false;
        }
    }

    public boolean setWritable() {
        try {
            connection = getWritableDatabase();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
            return false;
        }
    }

    public void Close() {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TableCreate.Tipo_Vacina());
            db.execSQL(TableCreate.Tipo_Remedio());
            db.execSQL(TableCreate.Animal());
            db.execSQL(TableCreate.Vacinas());
            db.execSQL(TableCreate.Remedios());
            db.execSQL(TableCreate.AnimalXVacina());
            db.execSQL(TableCreate.AnimalXRemedio());
            db.execSQL(IndexCreate.animal_x_vacina());
            db.execSQL(IndexCreate.animal_x_remedio());
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(IndexDrop.animal_x_remedio());
            db.execSQL(IndexDrop.animal_x_vacina());
            db.execSQL(TableDrop.AnimalXRemedio());
            db.execSQL(TableDrop.AnimalXVacina());
            db.execSQL(TableDrop.Remedios());
            db.execSQL(TableDrop.Vacinas());
            db.execSQL(TableDrop.Animal());
            db.execSQL(TableDrop.Tipo_Remedio());
            db.execSQL(TableDrop.Tipo_Vacina());
            onCreate(db);
        } catch (SQLException e) {
            // e.printStackTrace();
            error = e.getMessage();
        }
    }
}
