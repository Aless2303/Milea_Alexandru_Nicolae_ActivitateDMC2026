package com.example.firstandhelloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {
    private TextView tvMarca, tvElectrica, tvAn, tvCuloare, tvViteza;

    private final ActivityResultLauncher<Intent> thirdActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String mesajInapoi = result.getData().getStringExtra("mesajInapoi");
                    int suma = result.getData().getIntExtra("suma_valorilor", 0);

                    Toast.makeText(this, mesajInapoi + " | Suma: " + suma, Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> addMasinaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Masina masina = result.getData().getParcelableExtra("masina");
                    if (masina != null) {
                        tvMarca.setText("Marca: " + masina.getMarca());
                        tvElectrica.setText("Electrica: " + (masina.isEsteElectrica() ? "Da" : "Nu"));
                        tvAn.setText("An fabricatie: " + masina.getAnFabricatie());
                        tvCuloare.setText("Culoare: " + masina.getCuloare());
                        tvViteza.setText("Rating viteza: " + masina.getVitezaMaxima());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Button buttonSecondActivity = findViewById(R.id.button3);
        Button buttonSecondActivityToCAR = findViewById(R.id.button_to_masin);

        tvMarca = findViewById(R.id.tvMarca);
        tvElectrica = findViewById(R.id.tvElectrica);
        tvAn = findViewById(R.id.tvAn);
        tvCuloare = findViewById(R.id.tvCuloare);
        tvViteza = findViewById(R.id.tvViteza);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonSecondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("mesaj", "te salut patronae, sunt SecondActivity");
                bundle.putInt("valoare1", 23);
                bundle.putInt("valoare2", 43);

                intent.putExtras(bundle);

                thirdActivityLauncher.launch(intent);
            }
        });

        buttonSecondActivityToCAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, AddMasinaActivity.class);
                addMasinaLauncher.launch(intent);
            }
        });
    }









}