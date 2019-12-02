package com.example.conversor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ExchangeRater exchangeRater;
    Spinner historySelector;
    ListView listCurrencyHistory;
    List<HistoryItem> historyItemList;
    HistoryListAdapter historyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        exchangeRater = new ExchangeRater(getBaseContext());
        exchangeRater.changeRatesByDate(new Date());
        historyItemList = new ArrayList<>();

        historySelector = findViewById(R.id.historySelector);
        listCurrencyHistory = findViewById(R.id.listCurrencyHistory);

        ArrayAdapter<String> historySelectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exchangeRater.getLabels());
        historySelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        historySelector.setAdapter(historySelectorAdapter);
        historySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                populateHistoryItemList();
                historyListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        populateHistoryItemList();
        historyListAdapter = new HistoryListAdapter(getBaseContext(), historyItemList);
        listCurrencyHistory.setAdapter(historyListAdapter);
    }

    private void populateHistoryItemList() {
        historyItemList.clear();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());
        long count = 0;
        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            exchangeRater.changeRatesByDate(endCalendar.getTime());
            HistoryItem item = new HistoryItem();
            item.setId(count);
            item.setDate(endCalendar.getTime());
            item.setRate(exchangeRater.getRates().get(historySelector.getSelectedItem().toString()));
            historyItemList.add(item);
            endCalendar.add(Calendar.DATE, -1);
            count++;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
