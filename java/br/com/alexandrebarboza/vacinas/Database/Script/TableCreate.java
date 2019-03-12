package br.com.alexandrebarboza.vacinas.Database.Script;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class TableCreate {
    public static String Animal() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS Animal (" +
                "_id INTEGER CONSTRAINT pk_animal PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " nome VARCHAR (25) CONSTRAINT uk_animal UNIQUE ON CONFLICT FAIL NOT NULL ON CONFLICT FAIL COLLATE BINARY," +
                " especie VARCHAR (15) NOT NULL ON CONFLICT FAIL COLLATE NOCASE, nascimento DATE NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }
    public static String AnimalXRemedio() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS AnimalXRemedio (" +
                "_id INTEGER CONSTRAINT pk_animal_x_remedio PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " _animal INTEGER CONSTRAINT fk_animal REFERENCES Animal (_id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " _remedio INTEGER CONSTRAINT fk_remedio REFERENCES Remedios (_id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " inicio_em DATE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " repetir_de INTEGER NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " unidade_repetir VARCHAR (15) NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " durante INTEGER NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " unidade_durante VARCHAR (15) NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " dosagem INTEGER NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " unidade_dosagem VARCHAR (15) NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " conta_dose INTEGER NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " dose_data DATE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " dose_hora TIME NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }
    public static String AnimalXVacina() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS AnimalXVacina (" +
                "_id INTEGER COLLATE NOCASE NOT NULL ON CONFLICT FAIL CONSTRAINT pk_animal_x_vacina PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT," +
                " _animal INTEGER CONSTRAINT fk_animal REFERENCES Animal (_id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " _vacina INTEGER CONSTRAINT fk_vacina REFERENCES Vacinas (_id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " aplicada_em DATE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " revacinar_em DATE NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }

    public static String Vacinas() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS Vacinas (" +
                "_id INTEGER CONSTRAINT pk_vacina PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " _tipo INTEGER CONSTRAINT fk_tipo_vacina REFERENCES Tipo_Vacina (_id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " descricao VARCHAR (25) NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }

    public static String Remedios() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS Remedios (" +
                "_id INTEGER CONSTRAINT pk_remedio PRIMARY KEY ASC ON CONFLICT ROLLBACK AUTOINCREMENT NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " _tipo INTEGER CONSTRAINT fk_tipo_remedio REFERENCES Tipo_Remedio (_id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " descricao VARCHAR (25) NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }

    public static String Tipo_Remedio() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS Tipo_Remedio (" +
                "_id INTEGER CONSTRAINT pk_tipo_remedio PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " descricao VARCHAR (25) CONSTRAINT uk_tipo_remedio UNIQUE ON CONFLICT FAIL NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }

    public static String Tipo_Vacina() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS Tipo_Vacina (" +
                "_id INTEGER CONSTRAINT pk_tipo_vacina PRIMARY KEY ASC ON CONFLICT FAIL AUTOINCREMENT NOT NULL ON CONFLICT FAIL COLLATE NOCASE," +
                " descricao VARCHAR (25) CONSTRAINT uk_tipo_vacina UNIQUE ON CONFLICT FAIL NOT NULL ON CONFLICT FAIL COLLATE NOCASE);");
        return sb.toString();
    }

}
