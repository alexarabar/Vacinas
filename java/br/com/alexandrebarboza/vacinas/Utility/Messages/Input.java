package br.com.alexandrebarboza.vacinas.Utility.Messages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import br.com.alexandrebarboza.vacinas.R;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by Alexandre on 23/02/2017.
 */

public class Input implements DialogInterface.OnClickListener {
    private Activity activity;
    private View view;
    private LayoutInflater inflater;
    private EditText edit;
    private String title;

    public Input(Activity activity, String title, String input, int len) {
        this.activity = activity;
        this.title    = title;
        this.inflater = LayoutInflater.from(activity);
        this.view = inflater.inflate(R.layout.message_input, null);
        this.edit = (EditText) view.findViewById(R.id.edit_input);
        this.edit.setText(input);
        this.edit.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(len) });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                Intent it = activity.getIntent();
                it.putExtra("INPUT", edit.getText().toString());
                dialog.dismiss();
                break;
            case BUTTON_NEGATIVE:
                AlertDialog ad = (AlertDialog) dialog;
                ad.setOnDismissListener(null);
                dialog.cancel();
                break;
        }
    }

    public void setInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setTitle(title);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.str_ok, this);
        builder.setNegativeButton(R.string.str_cancel, this);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener((DialogInterface.OnDismissListener) activity);
        dialog.setOnCancelListener((DialogInterface.OnCancelListener) activity);
        dialog.show();
    }
}
