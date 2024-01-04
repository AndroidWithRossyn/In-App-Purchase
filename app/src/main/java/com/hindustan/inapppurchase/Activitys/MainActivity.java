package com.hindustan.inapppurchase.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hindustan.inapppurchase.R;

public class MainActivity extends AppCompatActivity {

    CardView cv_premium;

    TextView tv_pref;

    boolean pref;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cv_premium = findViewById(R.id.cv_premium);

        tv_pref = findViewById(R.id.tv_pref);

        SharedPreferences sharedPreferences = getSharedPreferences("premium",MODE_PRIVATE);

        pref = sharedPreferences.getBoolean("premium",false);

        tv_pref.setText(String.valueOf(pref));

        cv_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PremiumActivity.class);
                startActivity(intent);
            }
        });
    }
}