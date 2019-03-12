package br.com.alexandrebarboza.vacinas;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class VacinaHIstoryOrdemActivity extends AppCompatActivity implements AdapterView.OnDragListener, AdapterView.OnTouchListener {
    LinearLayout layout_primeiro, layout_segundo, layout_terceiro, layout_quarto;
    TextView txt_tipo, txt_descricao, txt_aplicada, txt_revacinar;
    CheckBox check_primeiro, check_segundo, check_terceiro, check_quarto;

    private boolean hasOneFieldSelected() {
        if (check_primeiro.isChecked() || check_segundo.isChecked() || check_terceiro.isChecked() || check_quarto.isChecked()) {
            return true;
        }
        return false;
    }

    private void swapFields(TextView src, TextView target) {
        if (src.equals(target)) return;
        try {
            LinearLayout layout_src = (LinearLayout) src.getParent();
            LinearLayout layout_target = (LinearLayout) target.getParent();
            CheckBox chk_src = (CheckBox) layout_src.getChildAt(2);
            CheckBox chk_target = (CheckBox) layout_target.getChildAt(2);
            layout_src.removeView(chk_src);
            layout_src.removeView(src);
            layout_target.removeView(chk_target);
            layout_target.removeView(target);
            layout_src.addView(target);
            layout_target.addView(src);
            layout_src.addView(chk_src);
            layout_target.addView(chk_target);
        } catch (IllegalStateException e) {
            // e.printStackTrace();
        }
    }

    private void setItemChecked(String item) {
        TextView text_1 = (TextView) layout_primeiro.getChildAt(1);
        TextView text_2 = (TextView) layout_segundo.getChildAt(1);
        TextView text_3 = (TextView) layout_terceiro.getChildAt(1);
        TextView text_4 = (TextView) layout_quarto.getChildAt(1);
        if (item.compareTo(text_1.getText().toString()) == 0) {
            check_primeiro.setChecked(true);
        } else if (item.compareTo(text_2.getText().toString()) == 0) {
            check_segundo.setChecked(true);
        } else if (item.compareTo(text_3.getText().toString()) == 0) {
            check_terceiro.setChecked(true);
        } else if (item.compareTo(text_4.getText().toString()) == 0) {
            check_quarto.setChecked(true);
        }
    }

    private void loadFields(ArrayList<String> fields, int index) {
        TextView text_1 = (TextView) layout_primeiro.getChildAt(1);
        TextView text_2 = (TextView) layout_segundo.getChildAt(1);
        TextView text_3 = (TextView) layout_terceiro.getChildAt(1);
        TextView text_4 = (TextView) layout_quarto.getChildAt(1);
        try {
            int flag = -1;
            switch (index) {
                case 0:
                    if (fields.get(0).compareTo(text_1.getText().toString()) == 0) {
                        flag = 0; // Default
                    } else if (fields.get(0).compareTo(text_2.getText().toString()) == 0) {
                        swapFields(text_2, text_1);
                        flag = 1;
                    } else if (fields.get(0).compareTo(text_3.getText().toString()) == 0) {
                        swapFields(text_3, text_1);
                        flag = 2;
                    } else if (fields.get(0).compareTo(text_4.getText().toString()) == 0) {
                        swapFields(text_4, text_1);
                        flag = 3;
                    }
                    if (flag != 0 && fields.size() > 1) loadFields(fields, flag);
                    break;
                case 1:
                    if (fields.get(1).compareTo(text_1.getText().toString()) == 0) {
                        swapFields(text_1, text_2);
                        flag = 0;
                    } else if (fields.get(1).compareTo(text_2.getText().toString()) == 0) {
                        flag = 1; // Default
                    } else if (fields.get(1).compareTo(text_3.getText().toString()) == 0) {
                        swapFields(text_3, text_2);
                        flag = 2;
                    } else if (fields.get(1).compareTo(text_4.getText().toString()) == 0) {
                        swapFields(text_4, text_2);
                        flag = 3;
                    }
                    if (flag != 1 && fields.size() > 2) loadFields(fields, flag);
                    break;
                case 2:
                    if (fields.get(2).compareTo(text_1.getText().toString()) == 0) {
                        swapFields(text_1, text_3);
                        flag = 0;
                    } else if (fields.get(2).compareTo(text_2.getText().toString()) == 0) {
                        swapFields(text_2, text_3);
                        flag = 1;
                    } else if (fields.get(2).compareTo(text_3.getText().toString()) == 0) {
                        flag = 2; // Default
                    } else if (fields.get(2).compareTo(text_4.getText().toString()) == 0) {
                        swapFields(text_4, text_3);
                        flag = 3;
                    }
                    if (flag != 2 && fields.size() > 3) loadFields(fields, flag);
                    break;
                case 3:
                    if (fields.get(3).compareTo(text_1.getText().toString()) == 0) {
                        swapFields(text_1, text_4);
                    } else if (fields.get(3).compareTo(text_2.getText().toString()) == 0) {
                        swapFields(text_2, text_4);
                    } else if (fields.get(3).compareTo(text_3.getText().toString()) == 0) {
                        swapFields(text_3, text_4);
                    } else if (fields.get(3).compareTo(text_4.getText().toString()) == 0) {
                    }
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            // e.printStackTrace();
        }
    }

    private void mountLayout(ArrayList<String> fields) {
        for (int i = 0; i < fields.size(); i++) {
            loadFields(fields, i);
            setItemChecked(fields.get(i));
        }
    }

    private ArrayList<String> getFieldsList() {
        ArrayList<String> result = new ArrayList<String>(4);
        TextView text_1 = (TextView) layout_primeiro.getChildAt(1);
        if (check_primeiro.isChecked()) {
            result.add(text_1.getText().toString());
        }
        TextView text_2 = (TextView) layout_segundo.getChildAt(1);
        if (check_segundo.isChecked()) {
            result.add(text_2.getText().toString());
        }
        TextView text_3 = (TextView) layout_terceiro.getChildAt(1);
        if (check_terceiro.isChecked()) {
            result.add(text_3.getText().toString());
        }
        TextView text_4 = (TextView) layout_quarto.getChildAt(1);
        if (check_quarto.isChecked()) {
            result.add(text_4.getText().toString());
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacina_history_ordem);
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> fields = bundle.getStringArrayList("LIST_NAMES");
        layout_primeiro = (LinearLayout) findViewById(R.id.layout_primeiro);
        layout_segundo = (LinearLayout) findViewById(R.id.layout_segundo);
        layout_terceiro = (LinearLayout) findViewById(R.id.layout_terceiro);
        layout_quarto = (LinearLayout) findViewById(R.id.layout_quarto);
        txt_tipo = (TextView) findViewById(R.id.text_tipo);
        txt_descricao = (TextView) findViewById(R.id.text_descricao);
        txt_aplicada = (TextView) findViewById(R.id.text_aplicada);
        txt_revacinar = (TextView) findViewById(R.id.text_revacinar);
        check_primeiro = (CheckBox) findViewById(R.id.check_primeiro);
        check_segundo = (CheckBox) findViewById(R.id.check_segundo);
        check_terceiro = (CheckBox) findViewById(R.id.check_terceiro);
        check_quarto = (CheckBox) findViewById(R.id.check_quarto);
        txt_tipo.setOnTouchListener(this);
        txt_tipo.setOnDragListener(this);
        txt_descricao.setOnTouchListener(this);
        txt_descricao.setOnDragListener(this);
        txt_aplicada.setOnTouchListener(this);
        txt_aplicada.setOnDragListener(this);
        txt_revacinar.setOnTouchListener(this);
        txt_revacinar.setOnDragListener(this);
        mountLayout(fields);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                View view = (View) event.getLocalState();
                TextView dropTarget = (TextView) v;
                TextView dropped = (TextView) view;
                swapFields(dropped, dropTarget);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.m_voltar) {
            if (hasOneFieldSelected()) {
                Intent data = new Intent();
                data.putStringArrayListExtra("LIST_NAMES", getFieldsList());
                setResult(RESULT_OK, data);
                finish();
            } else {
                Toast toast = Toast.makeText(this, getResources().getString(R.string.str_select_one), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}