package com.example.feedapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    String loggedInUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setTitle(Html.fromHtml("<font color='#00000'>Menu </font>"));
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        imageView1 = findViewById(R.id.forwardToMeal1);
        imageView2 = findViewById(R.id.forwardToMeal2);
        imageView3 = findViewById(R.id.forwardToMeal3);
        imageView4 = findViewById(R.id.forwardToMeal4);
        imageView5 = findViewById(R.id.forwardToMeal5);
        imageView6 = findViewById(R.id.forwardToMeal6);

        loggedInUser = getIntent().getStringExtra("loggedIN");
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.ic_meal:
                        return true;
                    case R.id.ic_kitchen:
                        startActivity(new Intent(getApplicationContext(),RecipesActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_account:
                        startActivity(new Intent(getApplicationContext(),AccountActivity.class));
                        overridePendingTransition(0,0);
                }
                return false;
            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealTime = "Breakfast" + " " + loggedInUser;
                Intent i = new Intent(MainActivity.this,ProductList.class);
                i.putExtra("mealTime",mealTime);
                startActivity(i);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealTime = "BreakfastII" + " " + loggedInUser;
                Intent i = new Intent(MainActivity.this,ProductList.class);
                i.putExtra("mealTime",mealTime);
                startActivity(i);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealTime = "Lunch" + " " + loggedInUser;
                Intent i = new Intent(MainActivity.this,ProductList.class);
                i.putExtra("mealTime",mealTime);
                startActivity(i);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealTime = "Dinner" + " " + loggedInUser;
                Intent i = new Intent(MainActivity.this,ProductList.class);
                i.putExtra("mealTime",mealTime);
                startActivity(i);
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealTime = "Snack" + " " + loggedInUser;
                Intent i = new Intent(MainActivity.this,ProductList.class);
                i.putExtra("mealTime",mealTime);
                startActivity(i);
            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealTime = "Supper" + " " + loggedInUser;
                Intent i = new Intent(MainActivity.this,ProductList.class);
                i.putExtra("mealTime",mealTime);
                startActivity(i);
            }
        });
    }
}
