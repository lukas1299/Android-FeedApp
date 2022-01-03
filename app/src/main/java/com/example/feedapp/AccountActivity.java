package com.example.feedapp;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class AccountActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private ImageView logOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("lifeCycle",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setTitle(Html.fromHtml("<font color='#00000'>Account</font>"));

        setContentView(R.layout.activity_account);
        bottomNavigationView = findViewById(R.id.bottomNavBar);

        logOut = findViewById(R.id.logOutButton);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("loggedIN",0);
                editor.commit();
                startActivity(new Intent(getApplicationContext(), Login.class));
                overridePendingTransition(0,0);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.ic_meal:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_kitchen:
                        startActivity(new Intent(getApplicationContext(),RecipesActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_account:
                        return true;
                }
                return false;
            }
        });
    }

}