package br.com.alexandrebarboza.vacinas.Database.Script;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class IndexDrop {
    private static String Drop(String index) {
        return "DROP INDEX IF EXISTS " + index + ";";
    }
    public static String animal_x_remedio() {
        return Drop("animal_x_remedio");
    }
    public static String animal_x_vacina() {
        return Drop("animal_x_vacina");
    }
}
