package com.example.firstandhelloworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

// Cerinta 2 Lab 6: Adapter personalizat pentru ListView
// Extinde ArrayAdapter<Masina> — la fel ca adapter-ul generic, dar noi controlam
// CUM arata fiecare rand (getView), in loc sa folosim doar toString()
public class MasinaAdapter extends ArrayAdapter<Masina> {

    // Context = referinta la activitate (necesar pentru LayoutInflater)
    private final Context context;
    // Lista de obiecte Masina — aceeasi lista din SecondActivity
    private final List<Masina> masini;

    // Constructor: primeste contextul si lista de masini
    // super() apeleaza constructorul ArrayAdapter care leaga adapter-ul de lista
    public MasinaAdapter(@NonNull Context context, @NonNull List<Masina> masini) {
        super(context, R.layout.item_masina, masini);
        this.context = context;
        this.masini = masini;
    }

    // getView() este metoda cheie — apelata de ListView pentru FIECARE rand vizibil
    // position = indexul randului (0, 1, 2...), convertView = randul reciclat (daca exista)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Daca convertView e null, inseamna ca nu exista un rand reciclat
        // si trebuie sa "umflam" (inflate) layout-ul item_masina.xml intr-un View
        // LayoutInflater = transforma XML in obiecte View Java
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_masina, parent, false);
        }

        // Luam obiectul Masina de la pozitia curenta din lista
        Masina masina = masini.get(position);

        // Gasim cele 3 TextView-uri din layout-ul item_masina.xml
        TextView tvMarca = convertView.findViewById(R.id.tvItemMarca);
        TextView tvDetalii = convertView.findViewById(R.id.tvItemDetalii);
        TextView tvExtra = convertView.findViewById(R.id.tvItemExtra);

        // Linia 1: Marca (mare, bold)
        tvMarca.setText(masina.getMarca());

        // Linia 2: An + Culoare
        tvDetalii.setText("An: " + masina.getAnFabricatie() + "  |  Culoare: " + masina.getCuloare());

        // Linia 3: Electrica + Viteza + Data
        // SimpleDateFormat formateaza un Date intr-un String lizibil (dd/MM/yyyy)
        String dataText = "N/A";
        if (masina.getDataFabricatiei() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataText = sdf.format(masina.getDataFabricatiei());
        }
        tvExtra.setText("Electric: " + (masina.isEsteElectrica() ? "Da" : "Nu") +
                "  |  Viteza: " + masina.getVitezaMaxima() +
                "  |  Data: " + dataText);

        // Returnam View-ul completat — ListView il afiseaza ca rand
        return convertView;
    }
}
