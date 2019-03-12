package br.com.alexandrebarboza.vacinas.Domain.Entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class Animal implements Serializable {
    public static final String TABELA = "Animal";
    public static final String _ID = "_id";
    public static final String NOME = "nome";
    public static final String ESPECIE = "especie";
    public static final String NASCIMENTO = "nascimento";

    private long   _id;
    private String nome;
    private String especie;
    private Date nascimento;

    public Animal() {
        this._id = 0;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    @Override
    public String toString() {
        return nome;
    }
}
