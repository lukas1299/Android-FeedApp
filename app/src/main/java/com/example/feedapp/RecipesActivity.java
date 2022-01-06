package com.example.feedapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.concurrent.ExecutionException;

public class RecipesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private static final String setMealHistory = "http://192.168.100.9/android/getRecipes.php";
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

    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private TextView recipeName;
    private TextView recipeDescription;
    private Spinner mealsSpinner;

    public String id_readymeals;
    public String name = "";
    public String description = "";

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
                try {
                    new loadRecipe().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recipePopup();
            }
        });

        recipe2 = findViewById(R.id.recipe2);
        recipe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 2;
                try {
                    new loadRecipe().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recipePopup();
            }
        });

        recipe3 = findViewById(R.id.recipe3);
        recipe3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 3;
                try {
                    new loadRecipe().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recipePopup();
            }
        });

        recipe4 = findViewById(R.id.recipe4);
        recipe4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 4;
                try {
                    new loadRecipe().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recipePopup();
            }
        });

        recipe5 = findViewById(R.id.recipe5);
        recipe5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 5;
                try {
                    new loadRecipe().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recipePopup();
            }
        });

        recipe6 = findViewById(R.id.recipe6);
        recipe6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeToShow = 6;
                try {
                    new loadRecipe().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recipePopup();
            }
        });
    }

    public void recipePopup(){

        layoutInflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container  = (ViewGroup) layoutInflater.inflate(R.layout.recipe_details_popup,null);

        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        recipeName = container.findViewById(R.id.RecipeNameTextview);
        recipeDescription = container.findViewById(R.id.RecipeDescTextView);

        recipeName.setText(name);
        System.out.println(description);
        recipeDescription.setText(description);

        mealsSpinner = container.findViewById(R.id.mealsspinner);
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(RecipesActivity.this,R.array.meals, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsSpinner.setAdapter(activityAdapter);

        popupWindow.showAsDropDown(recipe1, Gravity.CENTER,0,0);
    }

    class loadRecipe extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            int success;

                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id_readymeals", String.valueOf(recipeToShow)));

                    JSONObject json = jsonParser.makeHttpRequest(setMealHistory, "POST", params);
                    System.out.println(recipeToShow);
                    Log.d("main", json.toString());

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {//found

                        jsonProductArray = json.getJSONArray(TAG_RESPONSEARRAY);
                        JSONObject jsonTemp = jsonProductArray.getJSONObject(0);

                        id_readymeals = jsonTemp.getString("id_readymeals");
                        name = jsonTemp.getString("nazwa");
                        description =  jsonTemp.getString("opis");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }
}
