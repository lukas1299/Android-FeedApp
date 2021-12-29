package com.example.feedapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   //TODO zmienne przechowujace cele kcal dla uzytkownika i wyliczone cele dla fat i carbohydrates
    int full = 1000;

    BottomNavigationView bottomNavigationView;

    private static final String getMealHistory = "http://192.168.100.9/android/getMealHistory.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESPONSEARRAY = "responseArray";

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;

    private ImageView arrowLeft;
    private ImageView arrowRight;
    private TextView previousDay;
    private TextView nextDay;
    private TextView presentDay;

    private TextView breakfastKcal;
    private TextView breakfastIIKcal;
    private TextView lunchKcal;
    private TextView dinerKcal;
    private TextView snackKcal;
    private TextView supperKcal;

    private Double[][] mealsKcal;

    String loggedInUser;
    Date currentDay;
    Date correctDate;

    RelativeLayout relativeBarLayout;
    LinearLayout linearLayout;
    ScrollView scrollView1, scrollView2;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray responseArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggedInUser = getIntent().getStringExtra("loggedIN");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setTitle(Html.fromHtml("<font color='#00000'>Menu</font>"));

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        imageView1 = findViewById(R.id.forwardToMeal1);
        imageView2 = findViewById(R.id.forwardToMeal2);
        imageView3 = findViewById(R.id.forwardToMeal3);
        imageView4 = findViewById(R.id.forwardToMeal4);
        imageView5 = findViewById(R.id.forwardToMeal5);
        imageView6 = findViewById(R.id.forwardToMeal6);
        scrollView1 = (ScrollView)findViewById(R.id.scrollView3);
        relativeBarLayout = (RelativeLayout) findViewById(R.id.id_bar);
        scrollView2 = (ScrollView) findViewById(R.id.chart);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);

        arrowLeft = findViewById(R.id.arrowLeft);
        arrowRight = findViewById(R.id.arrowRight);
        presentDay = findViewById(R.id.presentDay);
        nextDay = findViewById(R.id.nextDay);
        previousDay = findViewById(R.id.previousDay);

        breakfastKcal = findViewById(R.id.kcal1);
        breakfastIIKcal = findViewById(R.id.kcal2);
        lunchKcal = findViewById(R.id.kcal3);
        dinerKcal = findViewById(R.id.kcal4);
        snackKcal = findViewById(R.id.kcal5);
        supperKcal =  findViewById(R.id.kcal6);

        relativeBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(scrollView2.getVisibility() == View.VISIBLE){

                    actionBar.setTitle(Html.fromHtml("<font color='#00000'>Menu</font>"));

                    scrollView1.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    scrollView2.setVisibility(View.INVISIBLE);

                }else {

                    scrollView1.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.INVISIBLE);
                    scrollView2.setVisibility(View.VISIBLE);

                    actionBar.setTitle(Html.fromHtml("<font color='#00000'>Information</font>"));

                    //Fat chart
                    PieChart pieChart = (PieChart) findViewById(R.id.pieChart1);

                    ArrayList<PieEntry> info = new ArrayList<>();

                    info.add(new PieEntry(350,"Fat"));
                    info.add(new PieEntry(full - 350,"Free"));

                    PieDataSet pieDataSet = new PieDataSet(info,"Fat");
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                    pieDataSet.setValueTextSize(16f);

                    PieData pieData = new PieData(pieDataSet);

                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setCenterText("Fat");
                    pieChart.animate();

                    //Kcal chart

                    PieChart pieChart2 = (PieChart) findViewById(R.id.pieChart2);

                    ArrayList<PieEntry> info2 = new ArrayList<>();

                    info2.add(new PieEntry(350,"Calories"));
                    info2.add(new PieEntry(full - 350,"Free"));

                    PieDataSet pieDataSet2 = new PieDataSet(info2,"Calories");
                    pieDataSet2.setColors(ColorTemplate.VORDIPLOM_COLORS);
                    pieDataSet2.setValueTextColor(Color.BLACK);

                    pieDataSet2.setValueTextSize(16f);


                    PieData pieData2 = new PieData(pieDataSet2);

                    pieChart2.setData(pieData2);
                    pieChart2.getDescription().setEnabled(false);
                    pieChart2.setCenterText("Calories");
                    pieChart2.animate();

                    // Carbohydrates chart

                }

            }
        });

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

        currentDay = new Date();
        correctDate = new Date();
        properDayName(0, currentDay.getTime());

        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                properDayName(-1, currentDay.getTime());
            }
        });

        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                properDayName(1, currentDay.getTime());
            }
        });

    }

    public void properDayName(int daysQuantity, long time){

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Long dayInMilliSeconds = Long.valueOf(24 * 60 * 60 * 1000);

        if (daysQuantity == 0){//biezacy dzien

            Date nextDate = new Date(time + dayInMilliSeconds);
            Date presentDate = new Date(time);
            Date previousDate = new Date(time - dayInMilliSeconds);
            presentDay.setText(dayFormat.format(presentDate));
            nextDay.setText(dayFormat.format(nextDate));
            previousDay.setText(dayFormat.format(previousDate));
            currentDay = presentDate;

            if ((dayFormat.format(correctDate)).equals(dayFormat.format(currentDay))){
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.VISIBLE);
                imageView5.setVisibility(View.VISIBLE);
                imageView6.setVisibility(View.VISIBLE);
            }else {
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
            }

            new loadHistory().execute();

        }else {//nastepne

            Date nextDate = new Date(time + dayInMilliSeconds + (daysQuantity * dayInMilliSeconds));
            Date presentDate = new Date(time + (daysQuantity * dayInMilliSeconds));
            Date previousDate = new Date(time - dayInMilliSeconds + (daysQuantity * dayInMilliSeconds));
            presentDay.setText(dayFormat.format(presentDate));
            nextDay.setText(dayFormat.format(nextDate));
            previousDay.setText(dayFormat.format(previousDate));
            currentDay = presentDate;

            if ((dayFormat.format(correctDate)).equals(dayFormat.format(currentDay))){
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.VISIBLE);
                imageView5.setVisibility(View.VISIBLE);
                imageView6.setVisibility(View.VISIBLE);
            }else {
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                imageView6.setVisibility(View.INVISIBLE);
            }

            new loadHistory().execute();

        }
    }

    class loadHistory extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateToSend = dateFormat.format(currentDay);

            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_user", loggedInUser));
                params.add(new BasicNameValuePair("date", dateToSend));

                JSONObject json = jsonParser.makeHttpRequest(getMealHistory, "POST", params);

                //Log.d("main", json.toString());

                success = json.getInt(TAG_SUCCESS);

                mealsKcal = new Double[6][1];

                for (int i = 0; i < mealsKcal.length; i++){
                    for (int c = 0; c < mealsKcal[i].length; c++) mealsKcal[i][c] = 0.0;
                }

                if (success == 1) {
                    responseArray = json.getJSONArray(TAG_RESPONSEARRAY);
                    for (int i = 0; i < responseArray.length(); i++){
                       JSONObject jsonObject = responseArray.getJSONObject(i);

                       String mealtype = jsonObject.getString("mealtype");
                       String name = jsonObject.getString("name");
                       String quantity = jsonObject.getString("quantity");
                       String protein = jsonObject.getString("protein");
                       String fat = jsonObject.getString("fat");
                       String carbohydrates = jsonObject.getString("carbohydrates");
                       String calories = jsonObject.getString("calories");

                        for (int c = 0; c < 6; c++){
                            int temp = c + 1;
                            if (String.valueOf(temp).equals(mealtype)){
                                mealsKcal[c][0] = mealsKcal[c][0] + Double.parseDouble(quantity) / 100 * Double.parseDouble(calories);
                                break;
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            breakfastKcal.setText(String.valueOf(String.format("%.0f", mealsKcal[0][0])) + " kcal");
            breakfastIIKcal.setText(String.valueOf(String.format("%.0f", mealsKcal[1][0])) + " kcal");
            lunchKcal.setText(String.valueOf(String.format("%.0f", mealsKcal[2][0])) + " kcal");
            dinerKcal.setText(String.valueOf(String.format("%.0f", mealsKcal[3][0])) + " kcal");
            snackKcal.setText(String.valueOf(String.format("%.0f", mealsKcal[4][0])) + " kcal");
            supperKcal.setText(String.valueOf(String.format("%.0f", mealsKcal[5][0])) + " kcal");

            return null;
        }
    }
}

