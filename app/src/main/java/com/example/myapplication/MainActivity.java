package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText inputAmount;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private TextView resultText;

    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputAmount = findViewById(R.id.input_amount);
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        Button convertButton = findViewById(R.id.convert_button);
        resultText = findViewById(R.id.result_text);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        convertButton.setOnClickListener(v -> {
            String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
            String toCurrency = toCurrencySpinner.getSelectedItem().toString();
            double amount = Double.parseDouble(inputAmount.getText().toString());

            convertCurrency(fromCurrency, toCurrency, amount);
        });
    }

    @SuppressLint("SetTextI18n")
    private void convertCurrency(String fromCurrency, String toCurrency, double amount) {
        String apiKey = "ecd089e0e984f4397761c484";
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + fromCurrency + "/" + toCurrency + "/" + amount;

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Add a log statement to indicate successful API fetch
                Log.d("API Fetch", "Successful");

                JSONObject jsonResponse = new JSONObject(response.toString());
                double conversionResult = jsonResponse.getDouble("conversion_result");

                runOnUiThread(() -> resultText.setText(String.valueOf(conversionResult)));

                reader.close();
                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> resultText.setText("Error converting currency"));
            }
        }).start();
    }
}
