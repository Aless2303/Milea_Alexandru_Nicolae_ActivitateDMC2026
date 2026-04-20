package com.example.firstandhelloworld;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

// Lab 8: Activitate principala pentru lucrul cu baza de date ROOM
// Permite apelarea tuturor celor 6 cerinte (metode din MasinaDao)
public class DatabaseActivity extends AppCompatActivity {

    // Referinta catre DAO-ul bazei de date — prin el accesam toate metodele CRUD
    private MasinaDao masinaDao;

    // Lista de rezultate afisata in ListView
    private ArrayList<String> listaRezultate = new ArrayList<>();
    // Adapter generic care leaga lista de String-uri de ListView
    private ArrayAdapter<String> adapterLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_database);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtinem instanta bazei de date (Singleton) si DAO-ul
        // MasinaDatabase.getInstance() creeaza sau returneaza baza de date existenta
        // .masinaDao() returneaza interfata DAO cu toate metodele de acces
        masinaDao = MasinaDatabase.getInstance(this).masinaDao();

        // Buton de intoarcere la activitatea anterioara — finish() inchide activitatea curenta
        Button btnBack = findViewById(R.id.btnBackFromDb);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gasim toate elementele din layout
        EditText etMarca = findViewById(R.id.etMarcaDb);
        EditText etAn = findViewById(R.id.etAnDb);
        EditText etViteza = findViewById(R.id.etVitezaDb);
        EditText etCuloare = findViewById(R.id.etCuloareDb);
        CheckBox cbElectrica = findViewById(R.id.cbElectricaDb);
        Button btnInsert = findViewById(R.id.btnInsert);
        Button btnSelectAll = findViewById(R.id.btnSelectAll);
        EditText etCautaMarca = findViewById(R.id.etCautaMarca);
        Button btnSelectByMarca = findViewById(R.id.btnSelectByMarca);
        EditText etAnMin = findViewById(R.id.etAnMin);
        EditText etAnMax = findViewById(R.id.etAnMax);
        Button btnSelectByInterval = findViewById(R.id.btnSelectByInterval);
        EditText etVitezaPrag = findViewById(R.id.etVitezaPrag);
        Button btnDeleteByViteza = findViewById(R.id.btnDeleteByViteza);
        EditText etLitera = findViewById(R.id.etLitera);
        Button btnIncrementAn = findViewById(R.id.btnIncrementAn);
        ListView lvMasini = findViewById(R.id.lvMasiniDb);

        // Configuram adapter-ul pentru ListView
        // simple_list_item_1 = layout standard Android cu un singur TextView per rand
        adapterLista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaRezultate);
        lvMasini.setAdapter(adapterLista);

        // ═══ Lab 8, Cerinta 1: Inserare in baza de date ═══
        // La click pe buton, citim valorile din campuri si cream un obiect MasinaEntity
        // Apoi apelam inserareMasina() pe un thread separat (ROOM nu permite operatii pe UI thread)
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String marca = etMarca.getText().toString().trim();
                String anText = etAn.getText().toString().trim();
                String vitezaText = etViteza.getText().toString().trim();
                String culoare = etCuloare.getText().toString().trim();
                boolean electrica = cbElectrica.isChecked();

                // Validare simpla — marca nu trebuie sa fie goala
                if (marca.isEmpty()) {
                    Toast.makeText(DatabaseActivity.this, "Completează marca!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int an = anText.isEmpty() ? 0 : Integer.parseInt(anText);
                double viteza = vitezaText.isEmpty() ? 0 : Double.parseDouble(vitezaText);

                // Cream obiectul entity cu datele din formular
                MasinaEntity masina = new MasinaEntity(marca, an, viteza, culoare, electrica);

                // Executam inserarea pe un thread separat
                // new Thread().start() — creeaza si porneste un thread nou
                // ROOM obligatoriu ruleaza pe thread separat (nu pe UI thread)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Apelam metoda de inserare din DAO
                        masinaDao.inserareMasina(masina);
                        // runOnUiThread() — revenim pe UI thread pentru a afisa Toast-ul
                        // (Toast-urile si modificarile UI trebuie facute pe UI thread)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DatabaseActivity.this, "Masina inserata in DB!", Toast.LENGTH_SHORT).show();
                                // Golim campurile dupa inserare
                                etMarca.setText("");
                                etAn.setText("");
                                etViteza.setText("");
                                etCuloare.setText("");
                                cbElectrica.setChecked(false);
                            }
                        });
                    }
                }).start();
            }
        });

        // ═══ Lab 8, Cerinta 2: Selectia tuturor inregistrarilor ═══
        // Apelam selectToateMasinile() si afisam rezultatele in ListView
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Luam toate masinile din baza de date
                        List<MasinaEntity> masini = masinaDao.selectToateMasinile();
                        // Actualizam ListView-ul pe UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afiseazaInListView(masini);
                            }
                        });
                    }
                }).start();
            }
        });

        // ═══ Lab 8, Cerinta 3: Selectie dupa marca (valoare String) ═══
        // Cautam masinile care au marca egala cu textul introdus
        btnSelectByMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String marcaCautata = etCautaMarca.getText().toString().trim();
                if (marcaCautata.isEmpty()) {
                    Toast.makeText(DatabaseActivity.this, "Completează marca!", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Apelam metoda de selectie dupa marca din DAO
                        List<MasinaEntity> masini = masinaDao.selectDupaMarca(marcaCautata);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afiseazaInListView(masini);
                                if (masini.isEmpty()) {
                                    Toast.makeText(DatabaseActivity.this, "Nu s-au gasit masini cu marca: " + marcaCautata, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        // ═══ Lab 8, Cerinta 4: Selectie dupa interval de an (valoare intreaga) ═══
        // Cautam masinile cu anFabricatie intre anMin si anMax
        btnSelectByInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String anMinText = etAnMin.getText().toString().trim();
                String anMaxText = etAnMax.getText().toString().trim();
                if (anMinText.isEmpty() || anMaxText.isEmpty()) {
                    Toast.makeText(DatabaseActivity.this, "Completează ambii ani!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int anMin = Integer.parseInt(anMinText);
                int anMax = Integer.parseInt(anMaxText);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Apelam metoda de selectie dupa interval din DAO
                        List<MasinaEntity> masini = masinaDao.selectDupaIntervalAn(anMin, anMax);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                afiseazaInListView(masini);
                                if (masini.isEmpty()) {
                                    Toast.makeText(DatabaseActivity.this, "Nu s-au gasit masini in intervalul " + anMin + "-" + anMax, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        // ═══ Lab 8, Cerinta 5: Stergere dupa viteza (valoare numerica) ═══
        // Stergem masinile cu vitezaMaxima mai mare decat pragul introdus
        // Dupa stergere, reactualizam ListView-ul cu toate inregistrarile ramase
        btnDeleteByViteza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vitezaPragText = etVitezaPrag.getText().toString().trim();
                if (vitezaPragText.isEmpty()) {
                    Toast.makeText(DatabaseActivity.this, "Completează viteza prag!", Toast.LENGTH_SHORT).show();
                    return;
                }
                double vitezaPrag = Double.parseDouble(vitezaPragText);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Apelam metoda de stergere din DAO
                        masinaDao.stergeDupaViteza(vitezaPrag);
                        // Dupa stergere, reincarcam toate masinile ramase
                        List<MasinaEntity> masiniRamase = masinaDao.selectToateMasinile();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Afisam lista actualizata dupa stergere — verificam ca s-au sters
                                afiseazaInListView(masiniRamase);
                                Toast.makeText(DatabaseActivity.this, "Sterse masinile cu viteza > " + vitezaPrag, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        // ═══ Lab 8, Cerinta 6: Increment an dupa litera (update) ═══
        // Crestem anFabricatie cu 1 pentru toate masinile a caror marca incepe cu litera data
        // Dupa update, reactualizam ListView-ul pentru verificare
        btnIncrementAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String litera = etLitera.getText().toString().trim();
                if (litera.isEmpty()) {
                    Toast.makeText(DatabaseActivity.this, "Completează litera!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Luam doar primul caracter
                String primaLitera = String.valueOf(litera.charAt(0));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Apelam metoda de update din DAO
                        masinaDao.cresteAnFabricatieDupaLitera(primaLitera);
                        // Dupa update, reincarcam toate masinile pentru verificare
                        List<MasinaEntity> masiniActualizate = masinaDao.selectToateMasinile();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Afisam lista actualizata dupa update — verificam modificarile
                                afiseazaInListView(masiniActualizate);
                                Toast.makeText(DatabaseActivity.this, "An +1 pentru marca care incepe cu '" + primaLitera + "'", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    // Metoda helper care actualizeaza ListView-ul cu o lista de MasinaEntity
    // Goleste lista veche, adauga toString()-ul fiecarui obiect, si notifica adapter-ul
    private void afiseazaInListView(List<MasinaEntity> masini) {
        listaRezultate.clear();
        for (MasinaEntity m : masini) {
            // toString() returneaza un text formatat cu toate atributele
            listaRezultate.add(m.toString());
        }
        // notifyDataSetChanged() spune adapter-ului sa redeseneze ListView-ul
        adapterLista.notifyDataSetChanged();
    }
}
