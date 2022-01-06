package com.example.feedapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feedapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private static final String setMealHistory = "http://192.168.100.9/android/mealHistory.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESPONSEARRAY = "responseArray";
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonProductArray;

    private RelativeLayout recipe1;
    private RelativeLayout recipe2;
    private RelativeLayout recipe3;
    private RelativeLayout recipe4;
    private RelativeLayout recipe5;
    private RelativeLayout recipe6;

    int  loggedInID;
    int recipeToShow;

    String id_readymeals;
    String name;
    String description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("lifeCycle",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        loggedInID = sharedPreferences.getInt("loggedIN",0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setTitle(Html.fromHtml("<font color='#00000'>Recipes</font>"));
        setContentView(R.layout.activity_recipes);
        bottomNavigationView = findViewById(R.id.bottomNavBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.ic_meal:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.ic_kitchen:
                        return true;
                    case R.id.ic_account:
                        startActivity(new Intent(getApplicationContext(),AccountActivity.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });

        recipe1 = findViewById(R.id.recipe1);
        recipe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 1;
                new loadRecipe().execute();
            }
        });

        recipe2 = findViewById(R.id.recipe2);
        recipe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 2;
                new loadRecipe().execute();
            }
        });

        recipe3 = findViewById(R.id.recipe3);
        recipe3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 3;
                new loadRecipe().execute();
            }
        });

        recipe4 = findViewById(R.id.recipe4);
        recipe4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 4;
                new loadRecipe().execute();
            }
        });

        recipe5 = findViewById(R.id.recipe5);
        recipe5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 5;
                new loadRecipe().execute();
            }
        });

        recipe6 = findViewById(R.id.recipe6);
        recipe6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 6;
                new loadRecipe().execute();
            }
        });
    }

    class loadRecipe extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            int success;

                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id_readymeals", String.valueOf(recipeToShow)));

                    JSONObject json = jsonParser.makeHttpRequest(setMealHistory, "POST", params);

                    Log.d("main", json.toString());

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {//found

                        jsonProductArray = json.getJSONArray(TAG_RESPONSEARRAY);
                        JSONObject jsonTemp = jsonProductArray.getJSONObject(0);

                        id_readymeals = jsonTemp.getString("id_readymeals");
                        name = jsonTemp.getString("nazwa");
                        description = jsonTemp.getString("opis");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }
}
