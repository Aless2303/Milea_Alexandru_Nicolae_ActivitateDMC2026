package com.example.firstandhelloworld;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddMasinaActivity extends AppCompatActivity {

    // Variabila care stocheaza data selectata de utilizator (null pana alege o data)
    private Date dataFabricatiei = null;
    // true daca editam o masina existenta, false daca adaugam una noua
    private boolean esteEditare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_masina);

        EditText editTextMarca = findViewById(R.id.marca_edit_Text);
        EditText editTextAn = findViewById(R.id.editTextAnFabricatie);
        CheckBox checkboxElectrica = findViewById(R.id.checkboxElectrica);
        Spinner spinnerCuloare = findViewById(R.id.culoareMasina);
        RatingBar ratingBar = findViewById(R.id.ratingBarMasinaVItezaMaxima);
        Button butonSalvare = findViewById(R.id.salvare);

        // Butonul care deschide DatePickerDialog — utilizatorul alege data fabricatiei
        Button btnSelectData = findViewById(R.id.btnSelectData);

        ArrayAdapter<CuloareMasina> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CuloareMasina.values()
        );
        spinnerCuloare.setAdapter(adapter);

        // Cerinta 3 Lab 6: Verificam daca am primit un obiect Masina prin Intent (editare)
        // getParcelableExtra returneaza null daca nu a fost trimis nimic (adaugare noua)
        // Daca exista, pre-completam TOATE campurile cu datele obiectului primit
        Masina masinaDeEditat = getIntent().getParcelableExtra("masina");
        if (masinaDeEditat != null) {
            // Marcam ca suntem in modul EDITARE — nu vom salva in fisier la sfarsit
            // (fisierul se rescrie complet de SecondActivity dupa editare)
            esteEditare = true;
            // Pre-completam EditText-ul pentru marca cu valoarea existenta
            editTextMarca.setText(masinaDeEditat.getMarca());
            // Pre-completam anul de fabricatie
            editTextAn.setText(String.valueOf(masinaDeEditat.getAnFabricatie()));
            // Pre-completam checkbox-ul (bifat/nebifat)
            checkboxElectrica.setChecked(masinaDeEditat.isEsteElectrica());
            // Pre-selectam culoarea corecta in Spinner
            // Cautam indexul valorii enum in adapter si il setam ca selectat
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i) == masinaDeEditat.getCuloare()) {
                    spinnerCuloare.setSelection(i);
                    break;
                }
            }
            // Pre-completam RatingBar-ul cu viteza existenta
            // setRating primeste float, vitezaMaxima e double, deci facem cast
            ratingBar.setRating((float) masinaDeEditat.getVitezaMaxima());
            // Pre-completam data daca exista
            if (masinaDeEditat.getDataFabricatiei() != null) {
                dataFabricatiei = masinaDeEditat.getDataFabricatiei();
                // Afisam data pe buton in format dd/MM/yyyy
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                btnSelectData.setText(sdf.format(dataFabricatiei));
            }
        }

        // La click pe butonul de data, deschidem un DatePickerDialog
        // Calendar.getInstance() ne da data curenta (an, luna, zi) ca valori implicite
        btnSelectData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calendar = clasa Java care ne da acces la an, luna, zi separat
                Calendar cal = Calendar.getInstance();
                int an = cal.get(Calendar.YEAR);
                int luna = cal.get(Calendar.MONTH);
                int zi = cal.get(Calendar.DAY_OF_MONTH);

                // DatePickerDialog = dialog Android cu selector de data (an/luna/zi)
                // La selectare, se apeleaza onDateSet cu valorile alese
                DatePickerDialog dialog = new DatePickerDialog(
                        AddMasinaActivity.this,
                        (view, year, month, dayOfMonth) -> {
                            // Construim un obiect Calendar cu data aleasa
                            Calendar selected = Calendar.getInstance();
                            selected.set(year, month, dayOfMonth);
                            // getTime() transforma Calendar in Date
                            dataFabricatiei = selected.getTime();
                            // Afisam data pe buton ca feedback vizual
                            btnSelectData.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        },
                        an, luna, zi  // valorile implicite afisate in dialog
                );
                dialog.show();
            }
        });

        // ═══ ANIMAȚII FUTURISTE ═══
        View cardMarca = findViewById(R.id.cardMarca);
        View cardAn = findViewById(R.id.cardAn);
        View cardOptions = findViewById(R.id.cardOptions);
        View cardRating = findViewById(R.id.cardRating);
        View cardData = findViewById(R.id.cardData);
        View headerTitle = findViewById(R.id.headerTitle);
        View neonLine = findViewById(R.id.neonLine);

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
        Animation popIn = AnimationUtils.loadAnimation(this, R.anim.pop_in);
        Animation neonExpand = AnimationUtils.loadAnimation(this, R.anim.neon_expand);
        Animation bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

        headerTitle.startAnimation(popIn);

        neonExpand.setStartOffset(300);
        neonLine.startAnimation(neonExpand);

        slideUp.setStartOffset(400);
        cardMarca.startAnimation(slideUp);

        Animation slideUp2 = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
        slideUp2.setStartOffset(550);
        cardAn.startAnimation(slideUp2);

        Animation slideUp3 = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
        slideUp3.setStartOffset(700);
        cardOptions.startAnimation(slideUp3);

        Animation slideUp4 = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
        slideUp4.setStartOffset(850);
        cardRating.startAnimation(slideUp4);

        Animation slideUp5 = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
        slideUp5.setStartOffset(1000);
        cardData.startAnimation(slideUp5);

        bounceIn.setStartOffset(1150);
        butonSalvare.startAnimation(bounceIn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        butonSalvare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String marca = editTextMarca.getText().toString();
                boolean esteElectrica = checkboxElectrica.isChecked();
                CuloareMasina culoare = (CuloareMasina) spinnerCuloare.getSelectedItem();
                double vitezaMaxima = ratingBar.getRating();
                String anText = editTextAn.getText().toString();
                int anFabricatie = anText.isEmpty() ? 0 : Integer.parseInt(anText);

                // Cream obiectul Masina cu toate cele 6 atribute (inclusiv dataFabricatiei)
                // dataFabricatiei vine din variabila clasei, setata de DatePickerDialog
                Masina masina = new Masina(esteElectrica, marca, anFabricatie, culoare, vitezaMaxima, dataFabricatiei);

                // Cerinta 2 Lab 7: Salvam masina intr-un fisier intern (masini.txt)
                // FileOutputStream cu MODE_APPEND = adaugam la sfarsitul fisierului, NU suprascriem
                // Salvam DOAR la adaugare noua, NU la editare
                // La editare, SecondActivity rescrie tot fisierul prin rescrieFisier()
                if (!esteEditare) {
                    salvareInFisier(masina);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("masina", masina);
                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });

        // Cerinta 4 Lab 7: Citim setarile din SharedPreferences si aplicam pe texte
        // SharedPreferences = fisier XML persistent care pastreaza perechi cheie-valoare
        // Daca utilizatorul a setat dimensiune/culoare in SettingsActivity, le aplicam aici
        aplicaSetariDinPreferinte();

    }

    // Cerinta 2 Lab 7: Metoda care salveaza un obiect Masina intr-un fisier intern
    // Fisierul se numeste "masini.txt" si se afla in memoria interna a aplicatiei
    // MODE_APPEND = daca fisierul exista, adaugam la sfarsit (nu suprascriem)
    private void salvareInFisier(Masina masina) {
        try {
            // openFileOutput deschide/creeaza un fisier in directorul intern al aplicatiei
            // Context.MODE_APPEND = adaugam la sfarsitul fisierului existent
            FileOutputStream fos = openFileOutput("masini.txt", MODE_APPEND);
            // Scriem toString()-ul masinii + newline ca separator intre obiecte
            String linie = masina.toString() + "\n";
            // getBytes() transforma String-ul in array de bytes pentru scriere
            fos.write(linie.getBytes());
            // Inchidem stream-ul pentru a elibera resursele
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cerinta 4 Lab 7: Metoda care citeste SharedPreferences si aplica setarile
    // pe toate TextView-urile din activitatea de adaugare
    private void aplicaSetariDinPreferinte() {
        // Deschidem fisierul SharedPreferences cu numele "setari_utilizator"
        // MODE_PRIVATE = doar aplicatia noastra poate citi/scrie acest fisier
        SharedPreferences prefs = getSharedPreferences("setari_utilizator", MODE_PRIVATE);

        // Citim dimensiunea textului (valoare implicita 14 daca nu e setata)
        float textSize = prefs.getFloat("text_size", 14f);
        // Citim culoarea textului (valoare implicita alb daca nu e setata)
        int textColor = prefs.getInt("text_color", Color.WHITE);

        // Parcurgem TOATE view-urile din layout-ul principal
        // Daca gasim un TextView (sau subclasa: EditText, Button, CheckBox),
        // ii aplicam dimensiunea si culoarea setate de utilizator
        ViewGroup root = findViewById(R.id.main);
        aplicaSetariPeViewGroup(root, textSize, textColor);
    }

    // Metoda recursiva care parcurge toate view-urile dintr-un ViewGroup
    // Si aplica dimensiunea + culoarea textului pe fiecare TextView gasit
    private void aplicaSetariPeViewGroup(ViewGroup group, float textSize, int textColor) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            // Daca view-ul este un TextView (include si EditText, Button, CheckBox)
            if (child instanceof TextView) {
                // setTextSize cu COMPLEX_UNIT_SP = setam in SP (scale-independent pixels)
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                // setTextColor schimba culoarea textului
                ((TextView) child).setTextColor(textColor);
            }
            // Daca view-ul este un ViewGroup (ConstraintLayout, LinearLayout etc.),
            // intram recursiv in el pentru a gasi mai multe TextView-uri
            if (child instanceof ViewGroup) {
                aplicaSetariPeViewGroup((ViewGroup) child, textSize, textColor);
            }
        }
    }


}