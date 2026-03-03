package com.example.firstandhelloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);

        Bundle bundle = getIntent().getExtras();
        int valoare1;
        int valoare2;
        if (bundle != null) {
             String mesaj = bundle.getString("mesaj");
             valoare1 = bundle.getInt("valoare1");
             valoare2 = bundle.getInt("valoare2");

            Toast.makeText(this, mesaj + " | " + valoare1 + " | " + valoare2, Toast.LENGTH_LONG).show();
        } else {
            valoare1 = 0;
            valoare2 = 0;
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonThirdActivity = findViewById(R.id.button4);
        buttonThirdActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("mesajInapoi", "te salut patronae, sunt ThirdActivity");
                resultIntent.putExtra("suma_valorilor", valoare1 + valoare2);
                setResult(RESULT_OK, resultIntent);
                finish(); //inchid activitatea curenta.
            }
        });
    }
}