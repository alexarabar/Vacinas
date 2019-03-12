package br.com.alexandrebarboza.vacinas.Database.Script;

/**
 * Created by Alexandre on 19/12/2017.
 */

public class TableDrop {
    private static String Drop(String table) {
        return "DROP TABLE IF EXISTS " + table + ";";
    }
    public static String Animal() {
        return Drop("Animal");
    }
    public static String AnimalXRemedio() {
        return Drop("AnimalXRemedio");
    }
    public static String AnimalXVacina() {
        return Drop("AnimalXVacina");
    }
    public static String Remedios() {
        return Drop("Remedios");
    }
    public static String Tipo_Remedio() {
        return Drop("Tipo_Remedio");
    }
    public static String Tipo_Vacina() {
        return Drop("Tipo_Vacina");
    }
    public static String Vacinas() {
        return Drop("Vacinas");
    }
}
