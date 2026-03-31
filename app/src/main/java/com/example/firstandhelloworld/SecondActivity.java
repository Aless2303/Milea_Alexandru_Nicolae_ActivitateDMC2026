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

        // Cerinta 7 Lab 5: Long click — stergem din lista + ListView
        listViewMasini.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listaMasini.remove(position);
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
    }
}