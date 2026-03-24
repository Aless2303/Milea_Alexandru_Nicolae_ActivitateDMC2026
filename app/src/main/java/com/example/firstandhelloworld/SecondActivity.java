package com.example.firstandhelloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

// Importam ArrayList — lista dinamica in care stocam obiectele Masina
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    // Cerinta 3: Lista de obiecte Masina — fiecare masina adaugata se pune aici
    // ArrayList = lista care creste automat cand adaugi elemente (nu are dimensiune fixa)
    private ArrayList<Masina> listaMasini = new ArrayList<>();

    // Cerinta 5: ArrayAdapter = "punte" intre lista de date (ArrayList) si interfata (ListView)
    // Adapter-ul ia fiecare Masina din lista, apeleaza toString() si o afiseaza ca rand in ListView
    private ArrayAdapter<Masina> adapter;

    // Launcher pentru ThirdActivity — primeste rezultat inapoi (mesaj + suma)
    private final ActivityResultLauncher<Intent> thirdActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String mesajInapoi = result.getData().getStringExtra("mesajInapoi");
                    int suma = result.getData().getIntExtra("suma_valorilor", 0);
                    Toast.makeText(this, mesajInapoi + " | Suma: " + suma, Toast.LENGTH_LONG).show();
                }
            });

    // Cerinta 3: Launcher pentru AddMasinaActivity — primeste obiectul Masina inapoi
    // Cand AddMasinaActivity face finish(), acest callback se executa automat
    private final ActivityResultLauncher<Intent> addMasinaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // getParcelableExtra extrage obiectul Masina din Intent (deserializare Parcelable)
                    Masina masina = result.getData().getParcelableExtra("masina");
                    if (masina != null) {
                        // Adaugam masina in lista
                        listaMasini.add(masina);
                        // notifyDataSetChanged() spune adapter-ului ca lista s-a schimbat
                        // si ListView-ul trebuie redesenat cu noile date
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

        // Cerinta 5: Gasim ListView-ul din layout
        ListView listViewMasini = findViewById(R.id.listViewMasini);

        // Cerinta 5: Cream ArrayAdapter-ul
        // Parametri: context (this), layout-ul pentru fiecare rand (simple_list_item_1 = un TextView simplu),
        // si lista de date (listaMasini). Adapter-ul apeleaza automat toString() pe fiecare Masina.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMasini);

        // Conectam adapter-ul la ListView — acum ListView stie de unde sa ia datele
        listViewMasini.setAdapter(adapter);

        // Cerinta 6: La click pe un element din ListView, afisam obiectul intr-un Toast
        // position = indexul elementului apasat in lista (0, 1, 2...)
        listViewMasini.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Luam obiectul Masina de la pozitia apasata
                Masina masinaSelectata = listaMasini.get(position);
                // toString() se apeleaza automat cand concatenam cu String
                Toast.makeText(SecondActivity.this, masinaSelectata.toString(), Toast.LENGTH_LONG).show();
            }
        });

        // Cerinta 7: La long click (apasare lunga) pe un element, il stergem din lista
        // return true = am consumat evenimentul (nu se mai propaga la click normal)
        listViewMasini.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Stergem din ArrayList la pozitia respectiva
                listaMasini.remove(position);
                // Notificam adapter-ul ca lista s-a modificat — ListView se redeseneaza
                adapter.notifyDataSetChanged();
                Toast.makeText(SecondActivity.this, "Masina stearsa!", Toast.LENGTH_SHORT).show();
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

        // Butonul care deschide AddMasinaActivity (formularul de adaugare masina)
        buttonSecondActivityToCAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, AddMasinaActivity.class);
                addMasinaLauncher.launch(intent);
            }
        });
    }
}