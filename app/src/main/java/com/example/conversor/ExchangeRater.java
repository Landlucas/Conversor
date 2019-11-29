package com.example.conversor;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ExchangeRater {
    AsyncTask downloader;
    JSONObject exchangeRatesJSON;
    Map<String,Double> rates = new HashMap<String,Double>();
    Boolean hasRates = false;

    @SuppressLint("StaticFieldLeak")
    public void update() throws JSONException, ExecutionException, InterruptedException {
        String out = new JsonDownloader().execute("https://api.exchangeratesapi.io/latest?base=BRL").get();
        exchangeRatesJSON = new JSONObject(out);
        JSONObject rates = exchangeRatesJSON.getJSONObject("rates");
        Iterator<String> iter = rates.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            rates.put(key,Double.parseDouble(rates.getString(key)));
            hasRates = true;
        }
    }

}
