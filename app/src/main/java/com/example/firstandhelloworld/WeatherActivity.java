package com.example.firstandhelloworld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Lab 10: Activitate pentru afisarea prognozei meteo folosind API-ul AccuWeather
// Partea I: City Search → obtinere Key oras
// Partea II: Forecast 1 zi → temperaturi min/max
// Partea III: Spinner pentru 1/5/10 zile + afisare toate zilele
public class WeatherActivity extends AppCompatActivity {

    // Lab 10: Cheia API AccuWeather folosita pentru autentificarea la API
    private static final String API_KEY = "";

    private EditText editTextOras;
    private Button buttonCauta;
    private TextView textViewRezultat;
    private Spinner spinnerZile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextOras = findViewById(R.id.editTextOras);
        buttonCauta = findViewById(R.id.buttonCauta);
        textViewRezultat = findViewById(R.id.textViewRezultat);
        spinnerZile = findViewById(R.id.spinnerZile);

        // Activam scroll pe TextView pentru a putea vedea toate zilele (Partea III)
        textViewRezultat.setMovementMethod(new ScrollingMovementMethod());

        // Lab 10 Partea III: Populam Spinner-ul cu cele 3 optiuni de prognoza
        // Selectia utilizatorului determina ce endpoint se apeleaza: 1day / 5day / 10day
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"1 zi", "5 zile", "10 zile"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZile.setAdapter(spinnerAdapter);

        // Lab 10 Partea I: La click pe buton, lansam AsyncTask-ul cu orasul introdus
        buttonCauta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oras = editTextOras.getText().toString().trim();
                if (oras.isEmpty()) {
                    Toast.makeText(WeatherActivity.this, "Introduceti numele orasului!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lab 10 Partea III: Determinam endpoint-ul in functie de selectia din Spinner
                // "1 zi" → "1day", "5 zile" → "5day", "10 zile" → "10day"
                String selectat = spinnerZile.getSelectedItem().toString();
                String endpoint;
                switch (selectat) {
                    case "5 zile":  endpoint = "5day";  break;
                    case "10 zile": endpoint = "10day"; break;
                    default:        endpoint = "1day";  break;
                }

                // Afisam un mesaj de asteptare pana primim raspunsul
                textViewRezultat.setText("Se cauta vremea pentru: " + oras + "...");

                // Lab 10 Partea I: Lansam AsyncTask-ul — apelul HTTP nu se face pe UI thread
                new WeatherAsyncTask(endpoint).execute(oras);
            }
        });
    }

    // Lab 10 Partea I: AsyncTask pentru apelurile HTTP catre AccuWeather
    // doInBackground returneaza String (cerinta explicita din lab)
    // onPostExecute primeste String-ul si il afiseaza in TextView
    @SuppressWarnings("deprecation")
    private class WeatherAsyncTask extends AsyncTask<String, Void, String> {

        // Lab 10 Partea III: endpoint-ul ales de utilizator din Spinner (1day/5day/10day)
        private final String endpoint;

        WeatherAsyncTask(String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        protected String doInBackground(String... params) {
            String oras = params[0];
            try {
                // Lab 10 Partea I: Pasul 1 — City Search
                // Apelam API-ul de cautare orase pentru a obtine Key-ul numeric al orasului
                // IMPORTANT: https:// in loc de http:// — Android blocheaza cleartext HTTP pe API 28+
                String citySearchUrl = "https://dataservice.accuweather.com/locations/v1/cities/search"
                        + "?apikey=" + API_KEY
                        + "&q=" + java.net.URLEncoder.encode(oras, "UTF-8");

                String citySearchResponse = getHttpResponse(citySearchUrl);
                if (citySearchResponse == null) {
                    return "Eroare: nu s-a putut contacta serverul AccuWeather.";
                }

                // Lab 10 Partea I: Parsam raspunsul JSON de la City Search
                // Raspunsul este un array — luam primul element (cel mai relevant)
                JSONArray cityArray = new JSONArray(citySearchResponse);
                if (cityArray.length() == 0) {
                    return "Orasul '" + oras + "' nu a fost gasit in baza de date AccuWeather.";
                }

                JSONObject firstCity = cityArray.getJSONObject(0);
                // Lab 10 Partea I: Extragem campul "Key" din raspunsul JSON (ex: "287292" pentru Oradea)
                String cityKey = firstCity.getString("Key");
                String cityName = firstCity.getString("LocalizedName");

                // Lab 10 Partea II: Pasul 2 — Forecast
                // Folosim Key-ul obtinut pentru a cere prognoza meteo
                // Lab 10 Partea III: endpoint-ul se schimba: "1day", "5day" sau "10day"
                // metric=true → temperaturile sunt in grade Celsius
                // IMPORTANT: https:// in loc de http:// — Android blocheaza cleartext HTTP pe API 28+
                String forecastUrl = "https://dataservice.accuweather.com/forecasts/v1/daily/"
                        + endpoint + "/"
                        + cityKey
                        + "?apikey=" + API_KEY
                        + "&metric=true";

                String forecastResponse = getHttpResponse(forecastUrl);
                if (forecastResponse == null) {
                    return "Key oras: " + cityKey + "\nEroare: nu s-a putut obtine prognoza meteo.";
                }

                // Lab 10 Partea II: Parsam raspunsul JSON de la Forecast
                // Structura: { "DailyForecasts": [ { "Temperature": { "Minimum": {...}, "Maximum": {...} } } ] }
                JSONObject forecastJson = new JSONObject(forecastResponse);
                JSONArray dailyForecasts = forecastJson.getJSONArray("DailyForecasts");

                // Lab 10 Partea I: Afisam Key-ul orasului (cerinta explicita)
                StringBuilder result = new StringBuilder();
                result.append("Oras: ").append(cityName)
                      .append("\nKey oras: ").append(cityKey)
                      .append("\nPrognoza pentru ").append(endpoint).append(":\n");
                result.append("─────────────────────────\n");

                // Lab 10 Partea II + III: Parcurgem toate zilele din DailyForecasts
                // Partea II: o singura zi (1day), Partea III: toate zilele (5day/10day)
                for (int i = 0; i < dailyForecasts.length(); i++) {
                    JSONObject day = dailyForecasts.getJSONObject(i);
                    JSONObject temperature = day.getJSONObject("Temperature");

                    // Lab 10 Partea II: Extragem temperatura minima si maxima
                    double tempMin = temperature.getJSONObject("Minimum").getDouble("Value");
                    double tempMax = temperature.getJSONObject("Maximum").getDouble("Value");

                    result.append("Ziua ").append(i + 1).append(":\n");
                    result.append("  Min: ").append(tempMin).append("°C\n");
                    result.append("  Max: ").append(tempMax).append("°C\n\n");
                }

                return result.toString();

            } catch (Exception e) {
                return "Eroare la procesarea datelor: " + e.getMessage();
            }
        }

        // Lab 10 Partea I: onPostExecute primeste rezultatul de tip String returnat de doInBackground
        // si il afiseaza in TextView-ul din activitate
        @Override
        protected void onPostExecute(String rezultat) {
            textViewRezultat.setText(rezultat);
        }

        // Metoda helper care face un apel HTTP GET si returneaza raspunsul ca String
        // Folosita atat pentru City Search cat si pentru Forecast
        private String getHttpResponse(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                connection.disconnect();
                return sb.toString();

            } catch (Exception e) {
                return null;
            }
        }
    }
}
