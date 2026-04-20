package com.example.firstandhelloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    // Lista de obiecte Masina — fiecare masina adaugata/modificata se pastreaza aici
    private ArrayList<Masina> listaMasini = new ArrayList<>();

    // Cerinta 2 Lab 6: Adapter personalizat in loc de ArrayAdapter generic
    // MasinaAdapter controleaza cum arata fiecare rand din ListView (layout propriu)
    private MasinaAdapter adapter;

    // Variabila care tine minte pozitia obiectului selectat pentru editare
    // -1 inseamna "nu editam nimic, adaugam unul nou"
    private int editPosition = -1;

    // Launcher pentru ThirdActivity — primeste rezultat inapoi (mesaj + suma)
    private final ActivityResultLauncher<Intent> thirdActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String mesajInapoi = result.getData().getStringExtra("mesajInapoi");
                    int suma = result.getData().getIntExtra("suma_valorilor", 0);
                    Toast.makeText(this, mesajInapoi + " | Suma: " + suma, Toast.LENGTH_LONG).show();
                }
            });

    // Launcher pentru AddMasinaActivity — folosit atat pentru ADAUGARE cat si EDITARE
    // Diferenta o face editPosition: daca e -1 = adaugam, altfel = modificam la pozitia respectiva
    private final ActivityResultLauncher<Intent> addMasinaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Cerinta 4: Extragem obiectul Masina prin Parcelable
                    Masina masina = result.getData().getParcelableExtra("masina");
                    if (masina != null) {
                        if (editPosition >= 0) {
                            // Cerinta 3 Lab 6: EDITARE — modificam obiectul existent la pozitia salvata
                            // set() inlocuieste elementul de la index cu noul obiect
                            // NU adaugam altul, ci il INLOCUIM pe cel vechi
                            listaMasini.set(editPosition, masina);
                            // Rescriem fisierul complet cu lista actualizata (inclusiv modificarea)
                            rescrieFisier();
                        } else {
                            // ADAUGARE — obiect nou in lista
                            listaMasini.add(masina);
                        }
                        // Resetam pozitia de editare
                        editPosition = -1;
                        // Notificam adapter-ul ca lista s-a schimbat — ListView se redeseneaza
                        adapter.notifyDataSetChanged();
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
        ListView listViewMasini = findViewById(R.id.listViewMasini);

        // Cerinta 2 Lab 6: Folosim MasinaAdapter (personalizat) in loc de ArrayAdapter generic
        // MasinaAdapter foloseste layout-ul item_masina.xml cu 3 linii de text per rand
        adapter = new MasinaAdapter(this, listaMasini);
        listViewMasini.setAdapter(adapter);

        // Setam callback-ul pe adapter: cand se apasa butonul ❌ de pe un rand,
        // adapter-ul ne notifica prin onDeleteClick cu pozitia randului
        adapter.setOnDeleteClickListener(new MasinaAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                // Stergem masina din lista
                listaMasini.remove(position);
                // Notificam adapter-ul ca lista s-a schimbat — ListView se redeseneaza
                adapter.notifyDataSetChanged();
                // Rescriem fisierul cu lista actualizata (fara masina stearsa)
                rescrieFisier();
                Toast.makeText(SecondActivity.this, "Masina stearsa!", Toast.LENGTH_SHORT).show();
            }
        });

        // Citim masinile salvate anterior din fisierul "masini.txt" la pornirea aplicatiei
        // Astfel datele PERSISTA intre sesiuni — utilizatorul vede masinile adaugate anterior
        citesteDinFisier();

        //aici apas pe obiect afisat si se deschide sa l modific.
        // Cerinta 3 Lab 6: La click pe un element din ListView, deschidem AddMasinaActivity
        // pentru EDITARE (nu mai afisam Toast ca inainte)
        // Trimitem obiectul Masina selectat prin Parcelable + pozitia in lista
        listViewMasini.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Salvam pozitia selectata — o folosim in callback la intoarcere
                editPosition = position;

                // Cerinta 4: Trimitem obiectul Masina prin Parcelable catre AddMasinaActivity
                Intent intent = new Intent(SecondActivity.this, AddMasinaActivity.class);
                intent.putExtra("masina", listaMasini.get(position));
                addMasinaLauncher.launch(intent);
            }
        });

        // Cerinta 3 Lab 7: Long click — salvam masina selectata in fisierul de FAVORITE
        // Fisierul "masini_favorite.txt" este SEPARAT de "masini.txt" (fisierul general)
        // MODE_APPEND = adaugam la sfarsitul fisierului, nu suprascriem
        listViewMasini.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Luam obiectul Masina de la pozitia selectata
                Masina masinaFavorita = listaMasini.get(position);
                // Salvam in fisierul separat de favorite
                salvareInFisierFavorite(masinaFavorita);
                Toast.makeText(SecondActivity.this, "Masina adaugata la favorite!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Butonul care deschide ThirdActivity cu Bundle
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

        // Butonul care deschide AddMasinaActivity pentru ADAUGARE (masina noua)
        // editPosition ramane -1, deci in callback se va face add(), nu set()
        buttonSecondActivityToCAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPosition = -1;
                Intent intent = new Intent(SecondActivity.this, AddMasinaActivity.class);
                addMasinaLauncher.launch(intent);
            }
        });

        // Cerinta 4 Lab 7: Buton care deschide SettingsActivity (activitate de setari)
        Button buttonSettings = findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Lab 8: Buton care deschide DatabaseActivity (baza de date ROOM)
        Button buttonDatabase = findViewById(R.id.button_database);
        buttonDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, DatabaseActivity.class);
                startActivity(intent);
            }
        });

        // Lab 9: Buton care deschide GalerieActivity (imagini din internet + WebView)
        Button buttonGalerie = findViewById(R.id.button_galerie);
        buttonGalerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, GalerieActivity.class);
                startActivity(intent);
            }
        });
    }

    // Cerinta 3 Lab 7: Metoda care salveaza un obiect Masina in fisierul de FAVORITE
    // Fisierul se numeste "masini_favorite.txt" — SEPARAT de "masini.txt"
    // MODE_APPEND = adaugam la sfarsitul fisierului existent
    private void salvareInFisierFavorite(Masina masina) {
        try {
            // openFileOutput deschide/creeaza fisierul in memoria interna
            // "masini_favorite.txt" = fisier separat doar pentru favorite
            FileOutputStream fos = openFileOutput("masini_favorite.txt", MODE_APPEND);
            // Scriem toString()-ul masinii + newline ca separator
            String linie = masina.toString() + "\n";
            fos.write(linie.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda care citeste masinile din fisierul "masini.txt" la pornirea aplicatiei
    // Fiecare linie = un obiect Masina salvat prin toString()
    // Folosim Masina.fromString() pentru a reconstrui obiectul din text
    private void citesteDinFisier() {
        try {
            // openFileInput deschide fisierul din memoria interna pentru CITIRE
            FileInputStream fis = openFileInput("masini.txt");
            // BufferedReader + InputStreamReader = citim fisierul linie cu linie
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String linie;
            // readLine() returneaza o linie sau null cand s-a terminat fisierul
            while ((linie = reader.readLine()) != null) {
                // Ignoram liniile goale
                if (!linie.trim().isEmpty()) {
                    // fromString() parseaza linia si creeaza un obiect Masina
                    Masina masina = Masina.fromString(linie);
                    if (masina != null) {
                        listaMasini.add(masina);
                    }
                }
            }
            reader.close();
            // Notificam adapter-ul ca lista s-a schimbat — ListView se redeseneaza
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            // Fisierul nu exista inca (prima rulare) — nu e o eroare
            e.printStackTrace();
        }
    }

    // Metoda care RESCRIE complet fisierul "masini.txt" cu lista curenta
    // Se apeleaza dupa stergere: nu putem "scoate o linie" dintr-un fisier,
    // asa ca il suprascriem cu tot ce a ramas in lista
    // MODE_PRIVATE (fara APPEND) = suprascrie fisierul de la zero
    private void rescrieFisier() {
        try {
            // MODE_PRIVATE = suprascrie tot (nu APPEND)
            FileOutputStream fos = openFileOutput("masini.txt", MODE_PRIVATE);
            // Scriem fiecare masina ramasa in lista, cate una pe linie
            for (Masina m : listaMasini) {
                String linie = m.toString() + "\n";
                fos.write(linie.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}