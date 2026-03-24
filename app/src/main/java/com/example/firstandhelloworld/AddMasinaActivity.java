package com.example.firstandhelloworld;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

// Importam Calendar si Date pentru a lucra cu date calendaristice
import java.util.Calendar;
import java.util.Date;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddMasinaActivity extends AppCompatActivity {

    // Variabila care stocheaza data selectata de utilizator (null pana alege o data)
    private Date dataFabricatiei = null;

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

                Intent resultIntent = new Intent();
                resultIntent.putExtra("masina", masina);
                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });

    }




}