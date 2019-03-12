package br.com.alexandrebarboza.vacinas.Utility;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import br.com.alexandrebarboza.vacinas.R;

/**
 * Created by Alexandre on 19/02/2017.
 */

public class Glossary extends Activity {
    private static int GLOSSARY_COUNT = 0;

    public void SetGlossary(TextView text, String string, Context context, int position, String compare) {
        if (position == 0)
            GLOSSARY_COUNT = 0;
        for (int i = 65; i <= 90; i++ )
            GlossaryStart(text, string, Character.toString ((char) i), compare);
        int color = GlossaryColor();
        text.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
        text.setTextColor(ContextCompat.getColor(context, color));
    }

    private static int GlossaryColor() {
        int color = R.color.black;
        if (GLOSSARY_COUNT == 0) {
            // nothing...
        } else if (GLOSSARY_COUNT % 6 == 0) {
            color = R.color.dark_magenta;
        } else if (GLOSSARY_COUNT % 5 == 0) {
            color = R.color.dark_cyan;
        } else if (GLOSSARY_COUNT % 4 == 0) {
            color = R.color.dark_yellow;
        } else if (GLOSSARY_COUNT % 3 == 0) {
            color = R.color.dark_green;
        } else if (GLOSSARY_COUNT % 2 == 0) {
            color = R.color.dark_red;
        } else {
            color = R.color.dark_blue;
        }
        return color;
    }

    private void GlossaryStart(TextView text, String string, String start, String compare) {
        if (string.isEmpty()) return;
        boolean flag = string.toUpperCase().substring(0, 1).compareTo(compare) == 0;
        if (string.toUpperCase().startsWith(start)) {
            if (!flag) {
                text.setText(start);
                text.setVisibility(View.VISIBLE);
                GLOSSARY_COUNT++;
            } else {
                text.setVisibility(View.GONE);
            }
        }
    }
}
