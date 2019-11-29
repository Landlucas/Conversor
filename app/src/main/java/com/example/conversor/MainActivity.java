package com.example.conversor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView checkJSON;
    ExchangeRater exchangeRater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkJSON = (TextView) findViewById(R.id.checkJSON);

        exchangeRater = new ExchangeRater();
        Toast toast;
        try {
            exchangeRater.update();
            toast = Toast.makeText(getBaseContext(), "Cotações atualizadas!", Toast.LENGTH_LONG);
        } catch (Exception e) {
            toast = Toast.makeText(getBaseContext(), "Não foi possível atualizar as cotações!", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

}
