package com.example.firstandhelloworld;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Lab 9: Activitate care contine un WebView — deschide link-ul paginii web
// asociate imaginii selectate din ListView-ul din GalerieActivity.
// Primeste URL-ul si titlul prin Intent (putExtra).
public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_webview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lab 9: Buton de intoarcere la GalerieActivity — finish() inchide activitatea
        Button btnBack = findViewById(R.id.btnBackWebView);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lab 9: Preluam datele trimise prin Intent de la GalerieActivity
        // "webUrl" = link-ul paginii web de deschis
        // "titlu" = descrierea imaginii (afisata in bara de sus)
        String webUrl = getIntent().getStringExtra("webUrl");
        String titlu = getIntent().getStringExtra("titlu");

        // Lab 9: Setam titlul in bara de sus
        TextView tvTitlu = findViewById(R.id.tvTitluWeb);
        if (titlu != null) {
            tvTitlu.setText(titlu);
        }

        // Lab 9: Configuram WebView-ul
        WebView webView = findViewById(R.id.webView);

        // WebSettings — configuram comportamentul WebView-ului
        WebSettings webSettings = webView.getSettings();
        // setJavaScriptEnabled(true) — activam JavaScript (multe site-uri nu functioneaza fara)
        webSettings.setJavaScriptEnabled(true);
        // setDomStorageEnabled(true) — activam DOM Storage (localStorage, sessionStorage)
        // Necesar pentru site-urile moderne care salveaza date local
        webSettings.setDomStorageEnabled(true);

        // Lab 9: WebViewClient — spunem WebView-ului sa deschida linkurile IN APLICATIE
        // Fara acest WebViewClient, link-urile s-ar deschide in browser-ul extern
        webView.setWebViewClient(new WebViewClient());

        // Lab 9: Incarcam URL-ul primit prin Intent
        // loadUrl() deschide pagina web in WebView (ca un mini-browser in aplicatie)
        if (webUrl != null && !webUrl.isEmpty()) {
            webView.loadUrl(webUrl);
        }
    }
}
