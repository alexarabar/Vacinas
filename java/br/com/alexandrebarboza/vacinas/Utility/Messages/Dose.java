package br.com.alexandrebarboza.vacinas.Utility.Messages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import br.com.alexandrebarboza.vacinas.R;
import br.com.alexandrebarboza.vacinas.Utility.Dates;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by Alexandre on 17/01/2018.
 */

public class Dose implements DialogInterface.OnClickListener {
    private Activity activity;
    private View view;
    private LayoutInflater inflater;
    private String title;

    public Dose(Activity activity, String date, String time, Date limit) {
        this.activity = activity;
        this.title    = activity.getResources().getString(R.string.str_prev);
        this.inflater = LayoutInflater.from(activity);
        this.view = inflater.inflate(R.layout.message_dose, null);
        TextView tx_date, tx_time, tx_limit;
        tx_date  = (TextView) view.findViewById(R.id.data_notif);
        tx_time  = (TextView) view.findViewById(R.id.hora_notif);
        tx_limit = (TextView) view.findViewById(R.id.limit_notif);
        tx_date.setText(date);
        tx_time.setText(time);
        Date today = new Date();
        if (limit.compareTo(today) < 0) {
            tx_limit.setTextColor(activity.getResources().getColor(R.color.dark_red));
        } else {
            tx_limit.setTextColor(activity.getResources().getColor(R.color.dark_green));
        }
        tx_limit.setText(Dates.getShortDateForString(limit.toString(), "en", "US"));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                dialog.dismiss();
                break;
        }
    }

    public void setDose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.str_ok, this);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener((DialogInterface.OnDismissListener) activity);
        dialog.show();
    }
}

