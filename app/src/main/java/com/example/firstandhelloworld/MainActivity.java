package com.example.firstandhelloworld;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button butonulMeu = findViewById(R.id.button);
        TextView textulMeu = findViewById(R.id.textView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        butonulMeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textulMeu.setText(R.string.switched_text);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: Error log");
        Log.w(TAG, "onStart: Warning log");
        Log.d(TAG, "onStart: Debug log");
        Log.i(TAG, "onStart: Info log");
        Log.v(TAG, "onStart: Verbose log");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: Error log");
        Log.w(TAG, "onResume: Warning log");
        Log.d(TAG, "onResume: Debug log");
        Log.i(TAG, "onResume: Info log");
        Log.v(TAG, "onResume: Verbose log");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: Error log");
        Log.w(TAG, "onPause: Warning log");
        Log.d(TAG, "onPause: Debug log");
        Log.i(TAG, "onPause: Info log");
        Log.v(TAG, "onPause: Verbose log");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: Error log");
        Log.w(TAG, "onStop: Warning log");
        Log.d(TAG, "onStop: Debug log");
        Log.i(TAG, "onStop: Info log");
        Log.v(TAG, "onStop: Verbose log");
    }

}