package com.example.feedapp;

import static com.example.feedapp.Login.IPaddres;
import static com.example.feedapp.Login.TAG_RESPONSEARRAY;
import static com.example.feedapp.Login.TAG_SUCCESS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private static final String getRecipes = IPaddres + "getRecipes.php";
    private static final String addRecipToMeals = IPaddres + "addToMealReceipt.php";
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonProductArray;

    private RelativeLayout recipe1;
    private RelativeLayout recipe2;
    private RelativeLayout recipe3;
    private RelativeLayout recipe4;
    private RelativeLayout recipe5;
    private RelativeLayout recipe6;

    int  loggedInID;
    int recipeToShow = 0;

    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private TextView recipeName;
    private TextView recipeDescription;
    private Spinner mealsSpinner;
    private Spinner multiplayerInput;
    private Button acceptButton;

    public String id_readymeals;
    public String name = "";
    public String description = "";
    public String Recipecalories = "0";



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
        acceptButton = container.findViewById(R.id.acceptButton);

        recipeName.setText(name);
        recipeDescription.setText(description + "\n" + Recipecalories + " kcal");

        mealsSpinner = container.findViewById(R.id.mealsspinner);
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(RecipesActivity.this,R.array.meals, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsSpinner.setAdapter(activityAdapter);

        multiplayerInput = container.findViewById(R.id.multiplayer);
        ArrayAdapter<CharSequence> multiAdapter = ArrayAdapter.createFromResource(RecipesActivity.this,R.array.multi, android.R.layout.simple_spinner_item);
        multiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        multiplayerInput.setAdapter(multiAdapter);

        if (recipeToShow == 1){
            popupWindow.showAsDropDown(recipe1, Gravity.CENTER,0,0);
        }else if (recipeToShow == 2){
            popupWindow.showAsDropDown(recipe2, Gravity.CENTER,0,0);
        }else if (recipeToShow == 3){
            popupWindow.showAsDropDown(recipe3, Gravity.CENTER,0,0);
        }else if (recipeToShow == 4){
            popupWindow.showAsDropDown(recipe4, Gravity.CENTER,0,0);
        }else if (recipeToShow == 5){
            popupWindow.showAsDropDown(recipe5, Gravity.CENTER,0,0);
        }else if (recipeToShow == 6){
            popupWindow.showAsDropDown(recipe5, Gravity.CENTER,0,0);
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addToMeal().execute();
                Toast toastIncorrectLoginOrPassword = Toast.makeText(getApplicationContext(), "Added to history", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
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

                    params.add(new BasicNameValuePair("findCalories", "1"));

                    JSONObject json = jsonParser.makeHttpRequest(getRecipes, "POST", params);

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {//found

                        jsonProductArray = json.getJSONArray(TAG_RESPONSEARRAY);
                        JSONObject jsonTemp = jsonProductArray.getJSONObject(0);

                        id_readymeals = jsonTemp.getString("id_readymeals");
                        name = jsonTemp.getString("nazwa");
                        description =  jsonTemp.getString("opis");
                        Recipecalories = jsonTemp.getString("calories1");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }

    class addToMeal extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            String multiplayerString = multiplayerInput.getSelectedItem().toString();
            String mealSpinnerString = mealsSpinner.getSelectedItem().toString();
            System.out.println(mealSpinnerString);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id_readymeals", String.valueOf(recipeToShow)));
            params.add(new BasicNameValuePair("multiplayer", multiplayerString));
            params.add(new BasicNameValuePair("mealtype", mealSpinnerString));
            params.add(new BasicNameValuePair("id_user", String.valueOf(loggedInID)));

            JSONObject json = jsonParser.makeHttpRequest(addRecipToMeals, "POST", params);

            //Log.d("main", json.toString());

            return null;
        }
    }
}
