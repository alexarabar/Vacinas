package br.com.alexandrebarboza.vacinas.Utility.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.alexandrebarboza.vacinas.Domain.Entity.Animal;
import br.com.alexandrebarboza.vacinas.R;
import br.com.alexandrebarboza.vacinas.Utility.Glossary;

/**
 * Created by Alexandre on 20/12/2017.
 */

    public class AnimalAdapter extends ArrayAdapter<Animal> {
    private int resource = 0;
    private LayoutInflater inflater;
    private Context context;

    private String get_especie(String especie) {
        if (especie.toUpperCase().compareTo("CANINO") == 0) {
            return "cão";
        }
        if (especie.toUpperCase().compareTo("FELINO") == 0) {
            return "gato";
        }
        return "[espécie]";
    }

    private long get_idade_anos(Date dataNasc) {
        Calendar today = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(dataNasc);
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        birth.add(Calendar.YEAR, age);
        if (today.before(birth)) {
            age--;
        }
        return age;
    }

    private int get_idade_meses(Date dataNasc) {
        Calendar today = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(dataNasc);
        int yearsInBetween = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        int monthsDiff = today.get(Calendar.MONTH) - birth.get(Calendar.MONTH);
        int age = yearsInBetween*12 + monthsDiff;
        if (today.before(birth)) {
            age--;
        }
        return age;
    }

    private int get_idade_dias(Date dataNasc) {
        Calendar today = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(dataNasc);
        int age = today.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH);
        birth.add(Calendar.DAY_OF_MONTH, age);
        if (today.before(birth)) {
            age--;
        }
        return age;
    }

    public AnimalAdapter(Context context, int resource) {
        super(context, resource);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context  = context;
        this.resource = resource;
        setNotifyOnChange (true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(resource, parent, false);
            holder.text_cor_animal = (TextView) view.findViewById(R.id.text_cor_animal);
            holder.text_nome       = (TextView) view.findViewById(R.id.text_nome_animal);
            holder.text_detalhes   = (TextView) view.findViewById(R.id.text_detalhes_animal);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
        }
        Animal animal =  getItem(position);
        Animal previous = null;
        String compare   = " ";
        if (position > 0) {
            previous = getItem(position - 1);
            if (previous.getNome().length() > 0) {
                compare = previous.getNome().toUpperCase().substring(0, 1);
            }
        }
        Glossary glos = new Glossary();
        glos.SetGlossary(holder.text_cor_animal, animal.getNome(), context, position, compare);
        holder.text_nome.setText(animal.getNome().toString());
        long idade_anos = get_idade_anos(animal.getNascimento());
        String especie = get_especie(animal.getEspecie()) + " ";
        if (idade_anos <= 0){
            int idade_meses = get_idade_meses(animal.getNascimento());
            if (idade_meses <= 0) {
                int idade_dias = get_idade_dias(animal.getNascimento());
                if (idade_dias < 0) {
                    holder.text_detalhes.setText(especie + " ainda não nasceu...");
                } else if (idade_dias == 0){
                    holder.text_detalhes.setText(especie + " nasceu hoje!");
                } else {
                    holder.text_detalhes.setText(especie + String.valueOf(idade_dias) + " dias");
                }
            } else {
                holder.text_detalhes.setText(especie + String.valueOf(idade_meses) + " meses");
            }
        } else {
            holder.text_detalhes.setText(especie + String.valueOf(idade_anos) + " anos");
        }
        return view;
    }

    static class ViewHolder {
        TextView text_cor_animal;
        TextView text_nome;
        TextView text_detalhes;
    }
}
