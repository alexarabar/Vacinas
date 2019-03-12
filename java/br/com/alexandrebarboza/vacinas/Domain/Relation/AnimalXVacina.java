package br.com.alexandrebarboza.vacinas.Domain.Relation;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;

import br.com.alexandrebarboza.vacinas.Utility.Dates;

/**
 * Created by Alexandre on 23/12/2017.
 */

public class AnimalXVacina implements Serializable {
    public static final String TABELA = "AnimalXVacina";
    public static final String _ID = "_id";
    public static final String _ANIMAL = "_animal";
    public static final String _VACINA = "_vacina";
    public static final String APLICADA_EM  = "aplicada_em";
    public static final String REVACINAR_EM = "revacinar_em";

    private long   _id;
    private long _animal;
    private long _vacina;
    private Date aplicada_em;
    private Date revacinar_em;

    public AnimalXVacina() {
        this._id = 0;
        this._animal = 0;
        this._vacina = 0;
    }
    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_animal() {
        return _animal;
    }

    public void set_animal(long _animal) {
        this._animal = _animal;
    }

    public long get_vacina() {
        return _vacina;
    }

    public void set_vacina(long _vacina) {
        this._vacina = _vacina;
    }

    public Date getAplicada_em() {
        return aplicada_em;
    }

    public void setAplicadaEm(Date aplicada_em) {
        this.aplicada_em = aplicada_em;
    }

    public Date getRevacinar_em() {
        return revacinar_em;
    }

    public void setRevacinarEm(Date revacinar_em) {
        this.revacinar_em = revacinar_em;
    }

    @Override
    public String toString() {
        java.util.Date date = Dates.convertSQLFromDefaultDate(this.revacinar_em);
        String txt = Dates.DateToString(date, DateFormat.LONG);
        return txt;
    }
}
