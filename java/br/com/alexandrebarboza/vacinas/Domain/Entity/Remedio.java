package br.com.alexandrebarboza.vacinas.Domain.Entity;

import java.io.Serializable;

/**
 * Created by Alexandre on 30/12/2017.
 */

public class Remedio implements Serializable {
    public static final String TABELA = "Remedios";
    public static final String _ID = "_id";
    public static final String _TIPO = "_tipo";
    public static final String DESCRICAO = "descricao";

    private long   _id;
    private long _tipo;
    private String descricao;

    public Remedio() {
        this._id = 0;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_tipo() {
        return _tipo;
    }

    public void set_tipo(long _tipo) {
        this._tipo = _tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
