package com.example.conversor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        this.labels = new ArrayList<>();
        this.ratesDate = new Date(System.currentTimeMillis());
        this.rates = new HashMap<>();
    }

    public int loadMonthlyRates() {
        boolean fetchedJSON = false;
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date());
        startCalendar.add(Calendar.DAY_OF_YEAR, -30);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new Date());
        endCalendar.get(Calendar.DAY_OF_YEAR);
        while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
            Date date = startCalendar.getTime();
            if(db.getRatesMapbyDate(date).isEmpty()) {
                try {
                    this.fetchRatesByDate(date);
                    fetchedJSON = true;
                } catch (JSONException | ExecutionException | InterruptedException e) {
                    return 0;
                }
            }
            startCalendar.add(Calendar.DATE, 1);
        }
        if (fetchedJSON) {
            return 2;
        } else {
            return 1;
        }
    }

    public void changeRatesByDate(Date date) {
        Log.v("changeRatesByDate()", "alterando para cotações de " + new SimpleDateFormat("dd/MM/yyyy").format(date));
        this.ratesDate = date;
        this.rates = db.getRatesMapbyDate(date);
        this.labels.clear();
        Iterator it = this.rates.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            this.labels.add(pair.getKey().toString());
        }
        Collections.sort(this.labels, String.CASE_INSENSITIVE_ORDER);
    }

    @SuppressLint("StaticFieldLeak")
    public void fetchRatesByDate(Date date) throws JSONException, ExecutionException, InterruptedException {
        String out = new JsonDownloader().execute("https://api.exchangeratesapi.io/" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "?base=BRL").get();
        exchangeRatesJSON = new JSONObject(out);
        JSONObject jsonRates = exchangeRatesJSON.getJSONObject("rates");
        this.rates = new HashMap<>();
        this.labels.clear();
        Iterator<String> iter = jsonRates.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            this.labels.add(key);
            this.rates.put(key,Double.parseDouble(jsonRates.getString(key)));
        }
        this.ratesDate = date;
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
