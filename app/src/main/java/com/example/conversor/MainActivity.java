package com.example.conversor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    ExchangeRater exchangeRater;
    Spinner dateSelector;
    Spinner fromCurrency;
    EditText fromValue;
    Spinner toCurrency;
    EditText toValue;
    Button convert;
    Toast toast;
    ArrayList<String> datesInRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exchangeRater = new ExchangeRater(getBaseContext());
        datesInRange = new ArrayList<>();

        dateSelector = findViewById(R.id.dateSelector);
        fromCurrency = findViewById(R.id.fromCurrency);
        fromValue = findViewById(R.id.fromValue);
        toCurrency = findViewById(R.id.toCurrency);
        toValue = findViewById(R.id.toValue);
        convert = findViewById(R.id.convert);

        switch (exchangeRater.loadMonthlyRates()) {
            case 0:
                toast = Toast.makeText(getBaseContext(), "Não foi possível carregar novas cotações!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1:
                exchangeRater.changeRatesByDate(new Date());
                populateDateSelector();
                break;
            case 2:
                exchangeRater.changeRatesByDate(new Date());
                populateDateSelector();
                toast = Toast.makeText(getBaseContext(), "Cotações atualizadas online!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
        }

        ArrayAdapter<String> dateSelectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datesInRange);
        dateSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSelector.setAdapter(dateSelectorAdapter);
        dateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateSelector.getSelectedItem().toString());
                    exchangeRater.changeRatesByDate(date);
                    if (!toValue.getText().toString().isEmpty()) {
                        showConversionResult();
                    }
                } catch (ParseException e) {
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("ITEM", "NOT");
            }
        });

        ArrayAdapter<String> fromCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exchangeRater.getLabels());
        fromCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrency.setAdapter(fromCurrencyAdapter);

        ArrayAdapter<String> toCurrencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exchangeRater.getLabels());
        toCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toCurrency.setAdapter(toCurrencyAdapter);

    }

    public void btnConvertClick(View v) {
        showConversionResult();
    }

    private void showConversionResult() {
        if (!fromValue.getText().toString().isEmpty()) {
            Double fromNumber = (Double.parseDouble((fromValue.getText().toString())));
            toValue.setText(exchangeRater.convertCurrency(fromCurrency.getSelectedItem().toString(), fromNumber, toCurrency.getSelectedItem().toString()).toString());
        }
    }

    public void btnCreditsClick(View v) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    private void populateDateSelector() {
        datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());
        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            datesInRange.add(new SimpleDateFormat("dd/MM/yyyy").format(endCalendar.getTime()));
            endCalendar.add(Calendar.DATE, -1);
        }
    }

}
