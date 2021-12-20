package com.example.feedapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   //TODO zmienne przechowujace cele kcal dla uzytkownika i wyliczone cele dla fat i carbohydrates
    int full = 1000;


    BottomNavigationView bottomNavigationView;
    RelativeLayout relativeBarLayout;
    LinearLayout linearLayout;
    ScrollView scrollView1, scrollView2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setTitle(Html.fromHtml("<font color='#00000'>Menu</font>"));





        scrollView1 = (ScrollView)findViewById(R.id.scrollView3);
        relativeBarLayout = (RelativeLayout) findViewById(R.id.id_bar);
        scrollView2 = (ScrollView) findViewById(R.id.chart);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);

        relativeBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Dziala bar ");


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



        bottomNavigationView = findViewById(R.id.bottomNavBar);

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
    }
}
