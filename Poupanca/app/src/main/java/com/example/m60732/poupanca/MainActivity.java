package com.example.m60732.poupanca;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.poupanca_icon);
    }

    public void calculate(View v) {
        try {
            Double initialValue = parseDouble((TextView) findViewById(R.id.text_initial_value));
            Double monthlyApplication = parseDouble((TextView) findViewById(R.id.text_monthly_application));
            Integer applicationTime = parseInt((TextView) findViewById(R.id.text_application_time));
            Double interestRate = parseDouble((TextView) findViewById(R.id.text_interest_rate));

            Double value = (monthlyApplication * (1 + (interestRate * applicationTime))) + initialValue;

            Toast.makeText(this, "$ " + value.toString(), Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.warn_invalid_value), Toast.LENGTH_LONG).show();
        }
    }

    private Double parseDouble(TextView text) {
        String textValue = text.getText().toString();
        if ("".equals(textValue)) {
            return 0.0;
        }
        return Double.parseDouble(textValue);
    }

    private Integer parseInt(TextView text) {
        String textValue = text.getText().toString();
        if ("".equals(textValue)) {
            return 0;
        }
        return Integer.parseInt(textValue);
    }
}
