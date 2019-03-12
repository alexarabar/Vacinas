package br.com.alexandrebarboza.vacinas.Utility.Messages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.com.alexandrebarboza.vacinas.R;

/**
 * Created by Alexandre on 20/12/2017.
 */

public class Output {
    private static void Dialog(Context context, String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(message);
        builder.setNeutralButton(context.getResources().getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        builder.setCancelable(false);
        dialog.show();
    }

    public static void Alert(Context context, String title, String message) {
        Dialog(context, title, message, android.R.drawable.ic_dialog_alert);
    }

    public static void Question(Activity activity, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(activity.getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog ad = (AlertDialog) dialog;
                ad.setOnDismissListener(null);
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener((DialogInterface.OnDismissListener) activity);
        dialog.setOnCancelListener((DialogInterface.OnCancelListener) activity);
        dialog.show();
    }

}
