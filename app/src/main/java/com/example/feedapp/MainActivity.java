package com.example.feedapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   //TODO zmienne przechowujace cele kcal dla uzytkownika i wyliczone cele dla fat i carbohydrates
    int full = 1000;
    public int kcalLimit;
    public int proteinLimit;
    public int fatLimit;
    public int carbohydratesLimit;

    BottomNavigationView bottomNavigationView;

    private static final String getMealHistory = "http://192.168.100.9/android/getMealHistory.php";
    private static final String getUserDemand = "http://192.168.100.9/android/getUserDemand.php";
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

    Double[][] mealsKcal = new Double[6][4];

    String loggedInUser;
    Date currentDay;
    Date correctDate;

    RelativeLayout relativeBarLayout;
    LinearLayout linearLayout;
    ScrollView scrollView1, scrollView2;
    ProgressBar fatBar;
    ProgressBar proteingBar;
    ProgressBar carbohydratesBar;

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

        proteingBar = findViewById(R.id.proteinBar);
        fatBar = findViewById(R.id.fatBar);
        carbohydratesBar = findViewById(R.id.carboBar);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        imageView1 = findViewById(R.id.forwardToMeal1);
        imageView2 = findViewById(R.id.forwardToMeal2);
        imageView3 = findViewById(R.id.forwardToMeal3);
        imageView4 = findViewById(R.id.forwardToMeal4);
        imageView5 = findViewById(R.id.forwardToMeal5);
        imageView6 = findViewById(R.id.forwardToMeal6);
        scrollView1 = (ScrollView)findViewById(R.id.scrollView3);
        relativeBarLayout = (RelativeLayout) findViewById(R.id.id_bar);
        scrollView2 = (ScrollView) findViewById(R.id.chart);//layout z wykresami
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);//bar z data

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

                    //Kcal chart

                    PieChart pieChart2 = (PieChart) findViewById(R.id.pieChart2);

                    ArrayList<PieEntry> info2 = new ArrayList<>();

                    if((kcalLimit - (mealsKcal[0][0] + mealsKcal[1][0] + mealsKcal[2][0] +
                            mealsKcal[3][0] + mealsKcal[4][0] + mealsKcal[5][0])) < 0){
                        info2.add(new PieEntry((float) (mealsKcal[0][0] + mealsKcal[1][0] + mealsKcal[2][0] +
                                                        mealsKcal[3][0] + mealsKcal[4][0] + mealsKcal[5][0]),"Out of " + kcalLimit));
                    }else {
                        info2.add(new PieEntry(kcalLimit,"Calories"));
                        info2.add(new PieEntry((float) (kcalLimit - (mealsKcal[0][0] + mealsKcal[1][0] + mealsKcal[2][0] +
                                mealsKcal[3][0] + mealsKcal[4][0] + mealsKcal[5][0])),"Free"));
                    }


                    PieDataSet pieDataSet2 = new PieDataSet(info2,"");
                    pieDataSet2.setColors(ColorTemplate.PASTEL_COLORS);


                    pieDataSet2.setValueTextSize(16f);


                    PieData pieData2 = new PieData(pieDataSet2);

                    pieChart2.setData(pieData2);
                    pieChart2.getDescription().setEnabled(false);
                    pieChart2.setCenterText("Calories");
                    pieChart2.animate();


                    //Fat chart
                    PieChart pieChart = (PieChart) findViewById(R.id.pieChart1);

                    ArrayList<PieEntry> info = new ArrayList<>();
                    if ((fatLimit - (mealsKcal[0][2] + mealsKcal[1][2] + mealsKcal[2][2] +
                            mealsKcal[3][2] + mealsKcal[4][2] + mealsKcal[5][2])) < 0){
                        info.add(new PieEntry((float) (mealsKcal[0][2] + mealsKcal[1][2] + mealsKcal[2][2] +
                                                        mealsKcal[3][2] + mealsKcal[4][2] + mealsKcal[5][2]),"Out of " + fatLimit));
                    }else {
                        info.add(new PieEntry(fatLimit,"Fat"));
                        info.add(new PieEntry((float) (fatLimit - (mealsKcal[0][2] + mealsKcal[1][2] + mealsKcal[2][2] +
                                mealsKcal[3][2] + mealsKcal[4][2] + mealsKcal[5][2])),"Free"));
                    }


                    PieDataSet pieDataSet = new PieDataSet(info,"");
                    pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    pieDataSet.setValueTextColor(Color.BLACK);

                    pieDataSet.setValueTextSize(16f);

                    PieData pieData = new PieData(pieDataSet);

                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setCenterText("Fat");
                    pieChart.animate();

                    //Protein chart
                    PieChart pieChart3 = (PieChart) findViewById(R.id.pieChart3);

                    ArrayList<PieEntry> info3 = new ArrayList<>();

                    if((proteinLimit - (mealsKcal[0][1] + mealsKcal[1][1] + mealsKcal[2][1] +
                            mealsKcal[3][1] + mealsKcal[4][1] + mealsKcal[5][1])) < 0 ){
                        info3.add(new PieEntry((float) (mealsKcal[0][1] + mealsKcal[1][1] + mealsKcal[2][1] +
                                                        mealsKcal[3][1] + mealsKcal[4][1] + mealsKcal[5][1]),"Out of " + proteinLimit));
                    }else {
                        info3.add(new PieEntry(proteinLimit,"Protein"));
                        info3.add(new PieEntry((float) (proteinLimit - (mealsKcal[0][1] + mealsKcal[1][1] + mealsKcal[2][1] +
                                mealsKcal[3][1] + mealsKcal[4][1] + mealsKcal[5][1])),"Free"));
                    }

                    PieDataSet pieDataSet3 = new PieDataSet(info3,"");
                    pieDataSet3.setColors(ColorTemplate.COLORFUL_COLORS);

                    pieDataSet3.setValueTextSize(16f);

                    PieData pieData3 = new PieData(pieDataSet3);

                    pieChart3.setData(pieData3);
                    pieChart3.getDescription().setEnabled(false);
                    pieChart3.setCenterText("Protein");
                    pieChart3.animate();

                    //Carbo chart
                    PieChart pieChart4 = (PieChart) findViewById(R.id.pieChart4);

                    ArrayList<PieEntry> info4 = new ArrayList<>();

                    if ((carbohydratesLimit - (mealsKcal[0][3] + mealsKcal[1][3] + mealsKcal[2][3] +
                            mealsKcal[3][3] + mealsKcal[4][3] + mealsKcal[5][3])) < 0){
                        info4.add(new PieEntry((float) (mealsKcal[0][3] + mealsKcal[1][3] + mealsKcal[2][3] +
                                                        mealsKcal[3][3] + mealsKcal[4][3] + mealsKcal[5][3]),"Out of " + carbohydratesLimit));
                    }else{
                        info4.add(new PieEntry(carbohydratesLimit,"Carbo"));
                        info4.add(new PieEntry((float) (carbohydratesLimit - (mealsKcal[0][3] + mealsKcal[1][3] + mealsKcal[2][3] +
                                mealsKcal[3][3] + mealsKcal[4][3] + mealsKcal[5][3])),"Free"));
                    }

                    PieDataSet pieDataSet4 = new PieDataSet(info4,"");
                    pieDataSet4.setColors(ColorTemplate.JOYFUL_COLORS);
                    pieDataSet4.setValueTextColor(Color.BLACK);

                    pieDataSet4.setValueTextSize(16f);

                    PieData pieData4 = new PieData(pieDataSet4);

                    pieChart4.setData(pieData4);
                    pieChart4.getDescription().setEnabled(false);
                    pieChart4.setCenterText("Carbohydrates");
                    pieChart4.animate();


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

        currentDay = new Date();
        correctDate = new Date();
        properDayName(0, currentDay.getTime());
        new loadDemand().execute();

    }

    public void properDayName(int daysQuantity, long time){

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat dateComprasionFormat = new SimpleDateFormat("dd-MM-yyyy");
        Long dayInMilliSeconds = Long.valueOf(24 * 60 * 60 * 1000);

        if (daysQuantity == 0){//biezacy dzien

            Date nextDate = new Date(time + dayInMilliSeconds);
            Date presentDate = new Date(time);
            Date previousDate = new Date(time - dayInMilliSeconds);
            presentDay.setText(dayFormat.format(presentDate));
            nextDay.setText(dayFormat.format(nextDate) + " " + dateFormat.format(nextDate));
            previousDay.setText(dayFormat.format(previousDate) + " " + dateFormat.format(previousDate));
            currentDay = presentDate;

            if ((dateComprasionFormat.format(correctDate)).equals(dateComprasionFormat.format(currentDay))){
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
            new loadDemand().execute();

        }else {//nastepne

            Date nextDate = new Date(time + dayInMilliSeconds + (daysQuantity * dayInMilliSeconds));
            Date presentDate = new Date(time + (daysQuantity * dayInMilliSeconds));
            Date previousDate = new Date(time - dayInMilliSeconds + (daysQuantity * dayInMilliSeconds));
            presentDay.setText(dayFormat.format(presentDate));
            nextDay.setText(dayFormat.format(nextDate)  + " " + dateFormat.format(nextDate));
            previousDay.setText(dayFormat.format(previousDate)  + " " + dateFormat.format(previousDate));
            currentDay = presentDate;

            if ((dateComprasionFormat.format(correctDate)).equals(dateComprasionFormat.format(currentDay))){
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
            new loadDemand().execute();

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
                System.out.println(loggedInUser);
                JSONObject json = jsonParser.makeHttpRequest(getMealHistory, "POST", params);

                //Log.d("main", json.toString());

                success = json.getInt(TAG_SUCCESS);



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
                                mealsKcal[c][0] = mealsKcal[c][0] + Double.parseDouble(quantity) / 100 * Double.parseDouble(calories);//spozyte kalorie
                                mealsKcal[c][1] = mealsKcal[c][1] + Double.parseDouble(quantity) / 100 * Double.parseDouble(protein);// -||- bialko
                                mealsKcal[c][2] = mealsKcal[c][2] + Double.parseDouble(quantity) / 100 * Double.parseDouble(fat);// -||- tluszcz
                                mealsKcal[c][3] = mealsKcal[c][3] + Double.parseDouble(quantity) / 100 * Double.parseDouble(carbohydrates);//-||- carbo
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

    class loadDemand extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_user", loggedInUser));
                JSONObject json = jsonParser.makeHttpRequest(getUserDemand, "POST", params);

                Log.d("main", json.toString());

                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    responseArray = json.getJSONArray(TAG_RESPONSEARRAY);
                    JSONObject jsonObject = responseArray.getJSONObject(0);

                    kcalLimit = Integer.parseInt(jsonObject.getString("demand"));
                    proteinLimit = Integer.parseInt(jsonObject.getString("protein"));
                    carbohydratesLimit = Integer.parseInt(jsonObject.getString("carbohydrates"));
                    fatLimit = Integer.parseInt(jsonObject.getString("fat"));


                    proteingBar.setMax(proteinLimit);
                    fatBar.setMax(fatLimit);
                    carbohydratesBar.setMax(carbohydratesLimit);

                    double proteinSummary = 0;
                    for (Double[] doubles : mealsKcal) {
                        proteinSummary += doubles[1];
                    }
                    double fatSummary = 0;
                    for (Double[] doubles : mealsKcal) {
                        fatSummary += doubles[2];
                    }
                    double carbohydratesSummary = 0;
                    for (Double[] doubles : mealsKcal) {
                        carbohydratesSummary += doubles[3];
                    }
                    //TODO: zmiana koloru wzrast z % zapelnieniem paska
                    proteingBar.setProgress((int) proteinSummary);
                    fatBar.setProgress((int) fatSummary);
                    carbohydratesBar.setProgress((int) carbohydratesSummary);

                    if (proteinSummary > proteinLimit * 0.5 && proteinSummary < proteinLimit * 0.9){
                        proteingBar.getProgressDrawable().setColorFilter(
                                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
                    }else if(proteinSummary > proteinLimit * 0.9){
                        proteingBar.getProgressDrawable().setColorFilter(
                                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    if (fatSummary > fatLimit * 0.5 && fatSummary < fatLimit * 0.9){
                        fatBar.getProgressDrawable().setColorFilter(
                                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
                    }else if(fatSummary > fatLimit * 0.9){
                        fatBar.getProgressDrawable().setColorFilter(
                                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    if (carbohydratesSummary> carbohydratesLimit * 0.5 && carbohydratesSummary < carbohydratesLimit * 0.9){
                        carbohydratesBar.getProgressDrawable().setColorFilter(
                                Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
                    }else if(carbohydratesSummary > carbohydratesLimit * 0.9){
                        carbohydratesBar.getProgressDrawable().setColorFilter(
                                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }//and stats
}

