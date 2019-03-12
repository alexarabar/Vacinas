package br.com.alexandrebarboza.vacinas.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.widget.ImageButton;
import br.com.alexandrebarboza.vacinas.R;

/**
 * Created by Alexandre on 21/12/2017.
 */

public class Utility {
    public static void resetImages(boolean flag, ImageButton ib_update, ImageButton ib_delete) {
        if (flag) {
            ib_update.setImageResource(R.drawable.ic_update);
            ib_delete.setImageResource(R.drawable.ic_delete);
        } else {
            ib_update.setImageResource(R.drawable.ic_update_disabled);
            ib_delete.setImageResource(R.drawable.ic_delete_disabled);
        }
        ib_update.setEnabled(flag);
        ib_delete.setEnabled(flag);
    }

    public static String UCWords(String src) {
        String[] parts = src.split(" ");
        if (parts.length == 0) {
            return src.substring(0, 1).toUpperCase() + src.substring(1).toLowerCase();
        }
        String target = "";
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase();
            target += parts[i] + " ";
        }
        target = target.trim();
        return target;
    }

    private static boolean isNumber(String ch) {
        String nums[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (int i = 0; i < nums.length; i++) {
            if (ch.compareTo(nums[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    public static int getNumberPart(String src) {
        int result = 0;
        String part = "";
        for (int i = 0; i < src.length(); i++) {
            String ch = src.toString().substring(i, 1);
            if (isNumber(ch)) {
                part += ch;
            }
        }
        try {
            result = Integer.parseInt(part);
        } catch(NumberFormatException e){
            // e.printStackTrace();
        }
        return result;
    }

    public static String getTextPart(String src) {
        String part = "";
        for (int i = 0; i < src.length(); i++) {
            String ch = src.toString().substring(i, 1);
            if (!isNumber(ch)) {
                part += ch;
            }
        }
        return part;
    }

    public static String leadingZeros(int num, int size) {
        String result = "";
        String work = String.valueOf(num);
        for (int i = work.length(); i < size; i++) {
            result += "0";
        }
        result = result + work;
        return result;
    }


    public static String getAnimalEspecie(String src) {
        if (src.toUpperCase().compareTo("FELINO") == 0) {
            return "gato";
        }
        return "cão";
    }

    private Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
}
