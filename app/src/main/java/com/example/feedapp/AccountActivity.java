package com.example.feedapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AccountActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private ImageView logOut;

    private static final String setUserDemandURL = "http://192.168.100.9/android/setUserDemand.php";
    JSONParser jsonParser = new JSONParser();

    private TextInputEditText ageInput;
    private TextInputEditText weightInput;
    private TextInputEditText heightInput;

    private TextView userName;

    private Spinner activitySpinner;
    private Spinner goalSpinner;
    private Spinner genderSpinner;

    private Button acceptButton;

    private int loggedIn;
    private String userNameString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("lifeCycle",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        loggedIn = sharedPreferences.getInt("loggedIN",0);
        userNameString = sharedPreferences.getString("userName","Mona lisa");
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

        ageInput = findViewById(R.id.ageInput);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);

        activitySpinner = findViewById(R.id.Activityspinner);
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,R.array.activitySpinnerEntities, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);

        goalSpinner = findViewById(R.id.Goalspinner);
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,R.array.goalSpinnerEntities, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);

        genderSpinner = findViewById(R.id.Genderspinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,R.array.genderSpinnerEntities, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new sendBodyInformation().execute();
            }
        });

        userName = findViewById(R.id.userName);
        userName.setText(userNameString);

    }

    class sendBodyInformation extends AsyncTask<String, String, String>{
        int showMessage;
        @Override
        protected String doInBackground(String... strings) {

            String ageString = ageInput.getText().toString();
            String weightString = weightInput.getText().toString();
            String heightString = heightInput.getText().toString();
            String genderSpinnerString = genderSpinner.getSelectedItem().toString();
            String activitySpinnerString = activitySpinner.getSelectedItem().toString();
            String goalSpinnerString = goalSpinner.getSelectedItem().toString();
//TODO:xd
            if(!ageString.equals("") && !weightString.equals("") && !heightString.equals("")){
                System.out.println(ageString + " " + weightString + " " + heightString + " " + genderSpinnerString + " " + activitySpinnerString + " " + goalSpinnerString + " " + loggedIn);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_user", String.valueOf(loggedIn)));
                params.add(new BasicNameValuePair("age", ageString));
                params.add(new BasicNameValuePair("height", heightString));
                params.add(new BasicNameValuePair("weight", weightString));
                params.add(new BasicNameValuePair("activity", activitySpinnerString));
                params.add(new BasicNameValuePair("goal", goalSpinnerString));
                params.add(new BasicNameValuePair("sex", genderSpinnerString));

                JSONObject json = jsonParser.makeHttpRequest(setUserDemandURL, "POST", params);

                Log.d("main", json.toString());
                showMessage = 0;
            }else{
                showMessage = 1;
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Context context = getApplicationContext();
            if(showMessage == 1) {
                Toast toastIncorrectLoginOrPassword = Toast.makeText(getApplicationContext(), "Fill in all fields", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }
        }
    }
}