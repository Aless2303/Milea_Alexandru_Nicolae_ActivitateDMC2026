package com.example.firstandhelloworld;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddMasinaActivity extends AppCompatActivity {

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

        ArrayAdapter<CuloareMasina> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CuloareMasina.values()
        );
        spinnerCuloare.setAdapter(adapter);

        // ═══ ANIMAȚII FUTURISTE ═══
        View cardMarca = findViewById(R.id.cardMarca);
        View cardAn = findViewById(R.id.cardAn);
        View cardOptions = findViewById(R.id.cardOptions);
        View cardRating = findViewById(R.id.cardRating);
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

        bounceIn.setStartOffset(1000);
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

                Masina masina = new Masina(esteElectrica, marca, anFabricatie, culoare, vitezaMaxima);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("masina", masina);
                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });

    }




}