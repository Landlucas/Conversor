package com.example.conversor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private Boolean hasRates;
    private Date ratesDate;
    public DatabaseHelper db;

    public ExchangeRater(Context context) {
        db = DatabaseHelper.getInstance(context);
        this.labels = new ArrayList<String>();
        this.ratesDate = new Date(System.currentTimeMillis());
        this.rates = db.getRatesMapbyDate(this.ratesDate);
        if ( this.rates.isEmpty() ) {
            this.hasRates = false;
        } else {
            this.hasRates = true;
            Iterator it = this.rates.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                this.labels.add(pair.getKey().toString());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void fetchNewRates() throws JSONException, ExecutionException, InterruptedException {
        String out = new JsonDownloader().execute("https://api.exchangeratesapi.io/latest?base=BRL").get();
        exchangeRatesJSON = new JSONObject(out);
        JSONObject rates = exchangeRatesJSON.getJSONObject("rates");
        this.rates = new HashMap<String,Double>();
        Iterator<String> iter = rates.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            this.labels.add(key);
            rates.put(key,Double.parseDouble(rates.getString(key)));
            hasRates = true;
        }
        this.ratesDate = new Date(System.currentTimeMillis());
        db.addRates(this);
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

    public Date getRatesDate() {
        return ratesDate;
    }

    public void setRatesDate(Date ratesDate) {
        this.ratesDate = ratesDate;
    }
}
