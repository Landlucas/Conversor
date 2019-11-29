package com.example.conversor;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ExchangeRater {

    private AsyncTask downloader;
    private JSONObject exchangeRatesJSON;
    private Map<String,Double> rates = new HashMap<String,Double>();
    private ArrayList<String> labels = new ArrayList<String>();
    private Boolean hasRates = false;

    @SuppressLint("StaticFieldLeak")
    public void updateRates() throws JSONException, ExecutionException, InterruptedException {
        String out = new JsonDownloader().execute("https://api.exchangeratesapi.io/latest?base=BRL").get();
        exchangeRatesJSON = new JSONObject(out);
        JSONObject rates = exchangeRatesJSON.getJSONObject("rates");
        Iterator<String> iter = rates.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            this.labels.add(key);
            rates.put(key,Double.parseDouble(rates.getString(key)));
            hasRates = true;
        }
    }

    public Double convertCurrency(String fromCurrencyKey, Double fromCurrencyValue, String toCurrencyKey) {
        Double fromCurrencyRate = this.rates.get(fromCurrencyKey);
        Double toCurrencyRate = this.rates.get(toCurrencyKey);
        return ( fromCurrencyValue * toCurrencyRate ) / fromCurrencyRate;
    }

    public AsyncTask getDownloader() {
        return downloader;
    }

    public void setDownloader(AsyncTask downloader) {
        this.downloader = downloader;
    }

    public JSONObject getExchangeRatesJSON() {
        return exchangeRatesJSON;
    }

    public void setExchangeRatesJSON(JSONObject exchangeRatesJSON) {
        this.exchangeRatesJSON = exchangeRatesJSON;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public Boolean getHasRates() {
        return hasRates;
    }

    public void setHasRates(Boolean hasRates) {
        this.hasRates = hasRates;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
}
