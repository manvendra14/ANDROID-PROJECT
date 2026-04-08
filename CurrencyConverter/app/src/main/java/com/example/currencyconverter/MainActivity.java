package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText amtInput;
    Spinner fromSpin, toSpin;
    TextView resText;
    Button btnConvert, btnSettings;

    HashMap<String, Double> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amtInput = findViewById(R.id.amount);
        fromSpin = findViewById(R.id.fromCurrency);
        toSpin = findViewById(R.id.toCurrency);
        resText = findViewById(R.id.result);
        btnConvert = findViewById(R.id.convertBtn);
        btnSettings = findViewById(R.id.settingsBtn);

        // rates
        map.put("INR", 1.0);
        map.put("USD", 83.0);
        map.put("EUR", 90.0);
        map.put("JPY", 0.55);

        String[] arr = {"INR", "USD", "EUR", "JPY"};

        ArrayAdapter<String> ad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, arr);

        fromSpin.setAdapter(ad);
        toSpin.setAdapter(ad);

        btnConvert.setOnClickListener(v -> doConvert());

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }

    private void doConvert() {
        String s = amtInput.getText().toString();

        if (s.isEmpty()) {
            resText.setText("Enter amount");
            return;
        }

        double val = Double.parseDouble(s);
        String f = fromSpin.getSelectedItem().toString();
        String t = toSpin.getSelectedItem().toString();

        double ans = (val / map.get(f)) * map.get(t);

        resText.setText("Ans: " + ans + " " + t);
    }
}