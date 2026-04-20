package com.example.firstandhelloworld;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Cerinta 4 Lab 7: Activitate de setari — utilizatorul alege dimensiunea si culoarea textului
// Setarile sunt salvate in SharedPreferences ("setari_utilizator")
// Aceste setari sunt citite de AddMasinaActivity la fiecare deschidere
public class SettingsActivity extends AppCompatActivity {

    // Array cu numele culorilor disponibile (afisate in Spinner)
    private final String[] numeCulori = {"Alb", "Rosu", "Verde", "Albastru", "Galben", "Cyan", "Magenta", "Portocaliu"};
    // Array cu valorile int corespunzatoare fiecarei culori
    // Fiecare index din numeCulori corespunde cu acelasi index din valoriCulori
    private final int[] valoriCulori = {
            Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.CYAN, Color.MAGENTA, 0xFFFF8800
    };



    // Variabilele care tin minte selectia curenta a utilizatorului
    private float dimensiuneText = 14f;  // dimensiune implicita 14sp
    private int culoareText = Color.WHITE; // culoare implicita alb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gasim view-urile din layout
        SeekBar seekBarSize = findViewById(R.id.seekBarTextSize);
        TextView tvSizeValue = findViewById(R.id.tvTextSizeValue);
        Spinner spinnerColor = findViewById(R.id.spinnerTextColor);
        TextView tvPreview = findViewById(R.id.tvPreview);
        Button btnSave = findViewById(R.id.btnSaveSettings);

        // Citim setarile existente din SharedPreferences (daca exista)
        // Astfel, la redeschidere, utilizatorul vede setarile salvate anterior
        SharedPreferences prefs = getSharedPreferences("setari_utilizator", MODE_PRIVATE);
        dimensiuneText = prefs.getFloat("text_size", 14f);
        culoareText = prefs.getInt("text_color", Color.WHITE);

        // Setam SeekBar-ul la valoarea salvata
        // progress = dimensiune - 8 (deoarece minimul e 8sp, progress 0 = 8sp)
        seekBarSize.setProgress((int) dimensiuneText - 8);
        tvSizeValue.setText((int) dimensiuneText + " SP");

        // Setam previzualizarea cu setarile curente
        tvPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, dimensiuneText);
        tvPreview.setTextColor(culoareText);

        // Configuram Spinner-ul cu lista de culori
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                numeCulori
        );
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);

        // Pre-selectam culoarea salvata in Spinner
        // Cautam indexul culorii salvate in array-ul valoriCulori
        for (int i = 0; i < valoriCulori.length; i++) {
            if (valoriCulori[i] == culoareText) {
                spinnerColor.setSelection(i);
                break;
            }
        }

        // Listener pe SeekBar: cand utilizatorul muta cursorul, actualizam dimensiunea
        // onProgressChanged se apeleaza la fiecare miscare a cursorului
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress + 8 = dimensiunea reala (minim 8sp, maxim 30sp)
                dimensiuneText = progress + 8;
                // Actualizam textul care arata valoarea curenta
                tvSizeValue.setText((int) dimensiuneText + " SP");
                // Actualizam previzualizarea in timp real
                tvPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, dimensiuneText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Listener pe Spinner: cand utilizatorul alege o culoare, o aplicam pe previzualizare
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Luam valoarea int a culorii de la pozitia selectata
                culoareText = valoriCulori[position];
                // Actualizam previzualizarea cu noua culoare
                tvPreview.setTextColor(culoareText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Buton de salvare: salveaza dimensiunea si culoarea in SharedPreferences
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SharedPreferences.Editor = obiectul care permite scrierea in SharedPreferences
                // putFloat/putInt salveaza perechea cheie-valoare
                // apply() salveaza asincron (fara a bloca UI-ul)
                SharedPreferences.Editor editor = getSharedPreferences("setari_utilizator", MODE_PRIVATE).edit();
                editor.putFloat("text_size", dimensiuneText);
                editor.putInt("text_color", culoareText);
                editor.apply();

                Toast.makeText(SettingsActivity.this, "Setari salvate!", Toast.LENGTH_SHORT).show();
                // Inchidem activitatea si ne intoarcem la SecondActivity
                finish();
            }
        });
    }
}
