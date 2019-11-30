package com.example.conversor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ExchangeRater exchangeRater;
    Spinner fromCurrency;
    Spinner toCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);

        exchangeRater = new ExchangeRater(getBaseContext());
        if ( exchangeRater.getRates().isEmpty() ) {
            Toast toast;
            try {
                exchangeRater.fetchNewRates();
                toast = Toast.makeText(getBaseContext(), "Cotações atualizadas!", Toast.LENGTH_LONG);
            } catch (Exception e) {
                toast = Toast.makeText(getBaseContext(), "Não foi possível atualizar as cotações!", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        ArrayAdapter<String> fromCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exchangeRater.getLabels());
        fromCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrency.setAdapter(fromCurrencyAdapter);

        ArrayAdapter<String> toCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exchangeRater.getLabels());
        toCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toCurrency.setAdapter(toCurrencyAdapter);

    }

}
