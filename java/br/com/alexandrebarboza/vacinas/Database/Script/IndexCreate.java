package br.com.alexandrebarboza.vacinas.Database.Script;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class IndexCreate {
    public static String animal_x_remedio() {
        return "CREATE UNIQUE INDEX  IF NOT EXISTS animal_x_remedio ON AnimalXRemedio (_animal COLLATE NOCASE ASC, _remedio COLLATE NOCASE ASC, inicio_em COLLATE NOCASE ASC, conta_dose COLLATE NOCASE ASC);";
    }
    public static String animal_x_vacina() {
       return  "CREATE UNIQUE INDEX IF NOT EXISTS animal_x_vacina ON AnimalXVacina (_animal COLLATE NOCASE ASC, _vacina COLLATE NOCASE ASC, aplicada_em COLLATE NOCASE ASC);";
    }
}
