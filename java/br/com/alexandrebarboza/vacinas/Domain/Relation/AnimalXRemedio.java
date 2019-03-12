package br.com.alexandrebarboza.vacinas.Domain.Relation;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;

import br.com.alexandrebarboza.vacinas.Utility.Dates;

/**
 * Created by Alexandre on 30/12/2017.
 */

public class AnimalXRemedio implements Serializable {
    public static final String TABELA = "AnimalXRemedio";
    public static final String _ID = "_id";
    public static final String _ANIMAL = "_animal";
    public static final String _REMEDIO = "_remedio";
    public static final String INICIO_EM  = "inicio_em";
    public static final String REPETIR_DE = "repetir_de";
    public static final String UNIDADE_REPETIR = "unidade_repetir";
    public static final String DURANTE = "durante";
    public static final String UNIDADE_DURANTE = "unidade_durante";
    public static final String DOSAGEM = "dosagem";
    public static final String UNIDADE_DOSAGEM = "unidade_dosagem";
    public static final String CONTA_DOSE = "conta_dose";
    public static final String DOSE_DATA = "dose_data";
    public static final String DOSE_HORA = "dose_hora";

    private long   _id;
    private long _animal;
    private long _remedio;
    private Date inicio_em;
    private int repetir_de;
    private String unidade_repetir;
    private int durante;
    private String unidade_durante;
    private int dosagem;
    private String unidade_dosagem;
    private int conta_dose;
    private Date dose_data;
    private Time dose_hora;

    public AnimalXRemedio() {
        this._id = 0;
        this._animal = 0;
        this._remedio = 0;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_id() {
        return _id;
    }

    public void set_animal(long _animal) {
        this._animal = _animal;
    }

    public long get_animal() {
        return _animal;
    }

    public void set_remedio(long _remedio) {
        this._remedio = _remedio;
    }

    public long get_remedio() {
        return _remedio;
    }

    public void setInicio_em(Date inicio_em) {
        this.inicio_em = inicio_em;
    }

    public Date getInicio_em() {
        return inicio_em;
    }

    public void setRepetir_de(int repetir_de) {
        this.repetir_de = repetir_de;
    }

    public int getRepetir_de() {
        return repetir_de;
    }

    public void setUnidade_repetir(String unidade_repetir) {
        this.unidade_repetir = unidade_repetir;
    }

    public String getUnidade_repetir() {
        return this.unidade_repetir;
    }

    public void setDurante(int durante) {
        this.durante = durante;

    }

    public int getDurante() {
        return durante;
    }

    public void setUnidade_durante(String unidade_durante) {
        this.unidade_durante = unidade_durante;
    }

    public String getUnidade_durante() {
        return unidade_durante;
    }

    public void setDosagem(int dosagem) {
        this.dosagem = dosagem;
    }

    public int getDosagem() {
        return dosagem;
    }

    public void setUnidade_dosagem(String unidade_dosagem) {
        this.unidade_dosagem = unidade_dosagem;
    }

    public String getUnidade_dosagem() {
        return unidade_dosagem;
    }

    public void setConta_dose(int conta_dose) {
        this.conta_dose = conta_dose;
    }

    public int getConta_dose() {
        return conta_dose;
    }

    public void setDose_data(Date dose_data) {
        this.dose_data =  dose_data;
    }

    public Date getDose_data() {
        return dose_data;
    }

    public void setDose_hora(Time dose_hora) {
        this.dose_hora =  dose_hora;
    }

    public Time getDose_hora() {
        return dose_hora;
    }

    @Override
    public String toString() {
        java.util.Date date = Dates.convertSQLFromDefaultDate(this.inicio_em);
        String txt = Dates.DateToString(date, DateFormat.LONG);
        return txt;
    }
}
