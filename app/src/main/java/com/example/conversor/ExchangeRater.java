package com.example.conversor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ExchangeRater {

    private AsyncTask downloader;
    private JSONObject exchangeRatesJSON;
    private Map<String,Double> rates;
    private ArrayList<String> labels;
    private Date ratesDate;
    private DatabaseHelper db;

    public ExchangeRater(Context context) {
        db = DatabaseHelper.getInstance(context);
        this.labels = new ArrayList<String>();
        this.ratesDate = new Date(System.currentTimeMillis());
        this.rates = db.getRatesMapbyDate(this.ratesDate);
        if ( ! this.rates.isEmpty() ) {
            Iterator it = this.rates.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                this.labels.add(pair.getKey().toString());
            }
            Collections.sort(this.labels, String.CASE_INSENSITIVE_ORDER);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void fetchNewRates() throws JSONException, ExecutionException, InterruptedException {
        String out = new JsonDownloader().execute("https://api.exchangeratesapi.io/latest?base=BRL").get();
        exchangeRatesJSON = new JSONObject(out);
        JSONObject jsonRates = exchangeRatesJSON.getJSONObject("rates");
        this.rates = new HashMap<String,Double>();
        Iterator<String> iter = jsonRates.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            this.labels.add(key);
            this.rates.put(key,Double.parseDouble(jsonRates.getString(key)));
        }
        this.ratesDate = new Date(System.currentTimeMillis());
        db.addRates(this);
    }

    public Double convertCurrency(String fromCurrencyKey, Double fromCurrencyValue, String toCurrencyKey) {
        if (this.rates.containsKey(fromCurrencyKey) && this.rates.containsKey(fromCurrencyKey)) {
            Double fromCurrencyRate = this.rates.get(fromCurrencyKey);
            Double toCurrencyRate = this.rates.get(toCurrencyKey);
            return ( fromCurrencyValue * toCurrencyRate ) / fromCurrencyRate;
        } else {
            return new Double(0);
        }
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

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public Date getRatesDate() {
        return ratesDate;
    }

    public void setRatesDate(Date ratesDate) {
        this.ratesDate = ratesDate;
    }
}
