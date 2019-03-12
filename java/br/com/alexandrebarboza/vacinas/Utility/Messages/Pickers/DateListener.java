package br.com.alexandrebarboza.vacinas.Utility.Messages.Pickers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import br.com.alexandrebarboza.vacinas.Utility.Dates;

/**
 * Created by Alexandre on 23/12/2017.
 */

public class DateListener extends DatePickerDialog implements DatePickerDialog.OnDateSetListener {
    private Activity activity;
    private TextView text_data;
    private String key;
    private int format;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DateListener(@NonNull Context context) {
        super(context);
    }

    private void setActivity(Activity activity) {
        this.activity   = activity;

    }
    private void setTextData(TextView text_data) {
        this.text_data  = text_data;
    }
    private void setKey(String key) {
        this.key = key;
    }
    private void setFormat(int format) {
        this.format = format;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        String str = Dates.getShortDateForString(date.toString(), "en", "US");
        if (key != null) {
            this.activity.getIntent().putExtra(key, str);
        }
        this.text_data.setText(Dates.DateToString(date, format));
    }

    public void showDialog(Activity activity, TextView text_data, Date dt, String key, int format, boolean listen) {
        Calendar calendar = Calendar.getInstance();
        if (dt != null) calendar.setTime(dt);
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        setActivity(activity);
        setTextData(text_data);
        setKey(key);
        setFormat(format);
        DatePickerDialog dialog = new DatePickerDialog(activity, this, year, month, day);
        if (listen) {
            dialog.setOnDismissListener((OnDismissListener) activity);
            dialog.setOnCancelListener((OnCancelListener) activity);
        }
        InputMethodManager im = (InputMethodManager) text_data.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(text_data.getWindowToken(), 0); // Oculta teclado.
        dialog.show();
    }

}
