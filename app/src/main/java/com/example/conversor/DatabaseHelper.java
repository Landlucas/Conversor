package com.example.conversor;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    public static final String DATABASE_NAME = "conversor.db";
    public static final Integer DATABASE_VERSION = 3;
    private SQLiteDatabase db;
    private Context ctx;

    private DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = ctx;
        this.db = this.getWritableDatabase();
    }

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static class ExchangeRatesTable implements BaseColumns {
        public static final String TABLE_NAME = "exchangerates";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_DATE = "date";

        public static String getSQL() {
            String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_CURRENCY + " TEXT, " +
                    COLUMN_RATE + " INTEGER, " +
                    COLUMN_DATE + " TEXT )";
            return sql;
        }
    }

    public void addRates(ExchangeRater e) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < e.getLabels().size(); i++) {
            String label =  e.getLabels().get(i);
            values.put(ExchangeRatesTable.COLUMN_CURRENCY, label);
            values.put(ExchangeRatesTable.COLUMN_RATE, e.getRates().get(label));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            values.put(ExchangeRatesTable.COLUMN_DATE, dateFormat.format(e.getRatesDate()));
            db.insert(ExchangeRatesTable.TABLE_NAME, null, values);
        }
        return;
    }

    public Map<String,Double> getRatesMapbyDate(Date date) {
        String cols[] = {ExchangeRatesTable._ID, ExchangeRatesTable.COLUMN_CURRENCY, ExchangeRatesTable.COLUMN_RATE, ExchangeRatesTable.COLUMN_DATE};
        Cursor cursor = db.query(ExchangeRatesTable.TABLE_NAME, cols, null, null, null, null, ExchangeRatesTable._ID);
        Map<String,Double> rates = new HashMap<String,Double>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(ExchangeRatesTable.COLUMN_DATE)).equals(dateFormat.format(date)) ) {
                rates.put(cursor.getString(cursor.getColumnIndex(ExchangeRatesTable.COLUMN_CURRENCY)),cursor.getDouble(cursor.getColumnIndex(ExchangeRatesTable.COLUMN_RATE)));
            }
        }
        return rates;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExchangeRatesTable.getSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExchangeRatesTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
