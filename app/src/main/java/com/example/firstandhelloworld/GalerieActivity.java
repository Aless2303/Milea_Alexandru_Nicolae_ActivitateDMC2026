package com.example.firstandhelloworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Lab 9: Activitate care afiseaza un ListView cu 5 imagini descarcate din internet.
// Fiecare imagine are o descriere si un link web asociat.
// La click pe un item, se deschide WebViewActivity cu link-ul aferent.
// Imaginile sunt descarcate pe thread-uri separate folosind Executors.
public class GalerieActivity extends AppCompatActivity {

    // Lab 9: Lista de obiecte ImageItem — fiecare contine URL imagine, descriere, URL web
    private ArrayList<ImageItem> listaImagini = new ArrayList<>();

    // Lab 9: CustomAdapter care afiseaza imaginile + descrierile in ListView
    private ImageItemAdapter adapter;

    // Lab 9: ExecutorService cu 5 thread-uri — descarca toate cele 5 imagini in paralel
    // Executors.newFixedThreadPool(5) creeaza un pool de 5 thread-uri
    // Fiecare thread descarca o imagine din internet fara a bloca UI thread-ul
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_galerie);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lab 9: Buton de intoarcere — finish() inchide activitatea curenta
        Button btnBack = findViewById(R.id.btnBackGalerie);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lab 9: Cream lista cu cele 5 imagini — fiecare are URL imagine, descriere, URL web
        // Imaginile sunt din domeniul masinilor (clasa Masina din proiect)
        listaImagini.add(new ImageItem(
                "https://cumsa.ro/wp-content/uploads/2021/09/cum-sa-iti-alegi-masina-in-functie-de-zodie-768x432.jpg",
                "Cum să îți alegi mașina potrivită",
                "https://www.mercedes-benz.ro"
        ));
        listaImagini.add(new ImageItem(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2ntv_G69GQkm9EgOVul6b-ANgHV8Hy0guxw&s",
                "Mercedes-Benz C-Class Coupe",
                "https://www.mercedes-benz.ro"
        ));
        listaImagini.add(new ImageItem(
                "https://frankfurt.apollo.olxcdn.com/v1/files/20zei7wfo0y43-RO/image;s=1024x768",
                "Mașină second-hand pe OLX",
                "https://www.mercedes-benz.ro"
        ));
        listaImagini.add(new ImageItem(
                "https://static.automarket.ro/img/auto_resized/db/article/069/879/307239l-1000x640-w-a7336a9b.jpg",
                "Știri auto — Automarket România",
                "https://www.mercedes-benz.ro"
        ));
        listaImagini.add(new ImageItem(
                "https://cdn-ds.com/blogs-media/sites/178/2018/03/12192825/2019-S-Class-Coupe-Special-Model_o-1.jpg",
                "Mercedes-Benz CLE Coupe 2024",
                "https://www.mercedes-benz.ro/passengercars/models/coupe/cle-coupe.html"
        ));

        // Lab 9: Configuram CustomAdapter si il setam pe ListView
        adapter = new ImageItemAdapter(this, listaImagini);
        ListView lvImagini = findViewById(R.id.lvImagini);
        lvImagini.setAdapter(adapter);

        // Lab 9: La click pe un item din ListView, deschidem WebViewActivity
        // Trimitem URL-ul paginii web prin Intent ca sa il deschidem in WebView
        lvImagini.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Luam obiectul ImageItem de la pozitia apasata
                ImageItem item = listaImagini.get(position);
                // Cream Intent catre WebViewActivity si trimitem URL-ul web
                Intent intent = new Intent(GalerieActivity.this, WebViewActivity.class);
                intent.putExtra("webUrl", item.getWebUrl());
                intent.putExtra("titlu", item.getDescriere());
                startActivity(intent);
            }
        });

        // Lab 9: Pornim descarcarea imaginilor folosind Executors
        // newFixedThreadPool(5) = pool cu 5 thread-uri care ruleaza in paralel
        // Fiecare imagine e descarcata pe un thread separat (nu pe UI thread)
        executorService = Executors.newFixedThreadPool(5);
        descarcaToateImaginile();
    }

    // Lab 9: Metoda care porneste descarcarea tuturor imaginilor in paralel
    // Pentru fiecare ImageItem din lista, submit un Runnable pe ExecutorService
    // Fiecare Runnable descarca imaginea de la URL si seteaza bitmap-ul pe obiect
    private void descarcaToateImaginile() {
        for (int i = 0; i < listaImagini.size(); i++) {
            final int index = i;
            final ImageItem item = listaImagini.get(i);

            // Lab 9: executorService.submit() trimite un task pe unul din cele 5 thread-uri
            // Task-ul ruleaza pe background thread (nu blocheaza interfata)
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    // Lab 9: Descarcam imaginea de la URL-ul specificat
                    Bitmap bitmap = descarcaImagine(item.getImageUrl());
                    if (bitmap != null) {
                        // Setam bitmap-ul pe obiectul ImageItem
                        item.setBitmap(bitmap);
                        // Lab 9: runOnUiThread() — revenim pe UI thread pentru a actualiza ListView
                        // Modificarile UI (redesenare ListView) TREBUIE facute pe UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Notificam adapter-ul ca datele s-au schimbat
                                // ListView se redeseneaza si afiseaza imaginea descarcata
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }
    }

    // Lab 9: Metoda care descarca o imagine de la un URL si returneaza un Bitmap
    // Foloseste HttpURLConnection pentru a deschide conexiunea
    // BitmapFactory.decodeStream() converteste stream-ul de bytes in Bitmap
    // Aceasta metoda ruleaza pe background thread (nu pe UI thread)
    private Bitmap descarcaImagine(String imageUrl) {
        try {
            // Cream un obiect URL din string-ul primit
            URL url = new URL(imageUrl);
            // Deschidem conexiunea HTTP catre server
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Setam metoda GET si timeout-uri pentru a evita blocarea
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 secunde timeout conectare
            connection.setReadTimeout(10000);    // 10 secunde timeout citire
            // User-Agent — unele servere refuza conexiuni fara acest header
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();

            // Verificam codul de raspuns — 200 = OK
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Obtinem stream-ul de date (bytes-ii imaginii)
                InputStream inputStream = connection.getInputStream();
                // BitmapFactory.decodeStream() converteste bytes in Bitmap (imagine)
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                connection.disconnect();
                return bitmap;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lab 9: onDestroy() — cand activitatea se inchide, oprim ExecutorService
    // shutdown() opreste acceptarea de task-uri noi si asteapta terminarea celor in curs
    // Previne memory leak-uri (thread-uri care raman active dupa inchiderea activitatii)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
