package com.example.feedapp;

import static com.example.feedapp.Login.IPaddres;
import static com.example.feedapp.Login.TAG_RESPONSEARRAY;
import static com.example.feedapp.Login.TAG_SUCCESS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class AccountActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private ImageView logOut;

    private static final String setUserDemandURL = IPaddres + "setUserDemand.php";
    private static final String achivemenstInfo = IPaddres + "getAchivementsInfo.php";
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonProductArray;

    private TextInputEditText ageInput;
    private TextInputEditText weightInput;
    private TextInputEditText heightInput;

    private ImageView oneDayStreak;
    private ImageView twoDaysStreak;
    private ImageView threeDaysStreak;
    private TextView oneDayText;
    private TextView twoDayText;
    private TextView threeDayText;
    private TextView userInfo;
    private TextView userInfo2;

    private TextView userName;

    private Spinner activitySpinner;
    private Spinner goalSpinner;
    private Spinner genderSpinner;

    private Button acceptButton;

    private int loggedIn;
    private String userNameString;
    private String info;
    private String info2;
    private String oneDayStreakRespond;
    private String towDaysStreakRespond;
    private String threeDaysStreakRespond;
    private String CHANNEL_ID = "uniqueID";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("lifeCycle",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loggedIn = sharedPreferences.getInt("loggedIN",0);
        userNameString = sharedPreferences.getString("userName","Mona lisa");
        info = sharedPreferences.getString("info","");
        info2 = sharedPreferences.getString("info2","");
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
                editor.putInt("notificationPushed",0);
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

        oneDayStreak = findViewById(R.id.oneDayStreak);
        twoDaysStreak = findViewById(R.id.twoDayStreak);
        threeDaysStreak = findViewById(R.id.threeDayStreak);
        oneDayText = findViewById(R.id.oneDayText);
        twoDayText = findViewById(R.id.twoDayText);
        threeDayText = findViewById(R.id.threeDayText);

        oneDayStreak.setVisibility(View.INVISIBLE);
        twoDaysStreak.setVisibility(View.INVISIBLE);
        threeDaysStreak.setVisibility(View.INVISIBLE);
        oneDayText.setVisibility(View.INVISIBLE);
        twoDayText.setVisibility(View.INVISIBLE);
        threeDayText.setVisibility(View.INVISIBLE);

        userInfo = findViewById(R.id.userInfo);
        userInfo.setText(info);
        userInfo2 = findViewById(R.id.userInfo2);
        userInfo2.setText(info2);
        System.out.println(info+ " dfghdfghdfgsdfg");
        System.out.println(info2 + " dfghdfghdfgsdfg2");

        createNotificationChannel();

        try {
            achivementHandling();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        editor.putInt("notificationPushed",1);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putInt("notificationPushed",0);
        editor.commit();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void achivementHandling() throws ExecutionException, InterruptedException {
        new getAchivementsInfo().execute().get();
        if(sharedPreferences.getInt("notificationPushed",0) == 0) {

            if (Integer.parseInt(oneDayStreakRespond) == 1) {
                NotificationCompat.Builder oneDayNotifi = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle("FeedApp")
                        .setContentText("Hurra... You did one day streak of your meals history")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(100, oneDayNotifi.build());
            }

            if (Integer.parseInt(towDaysStreakRespond) == 1) {

                NotificationCompat.Builder twoDayNotifi = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle("FeedApp")
                        .setContentText("Hurra... You did two days streak of your meals history")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(110, twoDayNotifi.build());
            }

            if (Integer.parseInt(threeDaysStreakRespond) == 1) {

                NotificationCompat.Builder threeDayNotifi = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle("FeedApp")
                        .setContentText("Hurra... You did three days streak of your meals history. Congratz")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(120, threeDayNotifi.build());
            }
        }

        if (Integer.parseInt(oneDayStreakRespond) == 1) {
            oneDayStreak.setVisibility(View.VISIBLE);
            oneDayText.setVisibility(View.VISIBLE);
        }

        if (Integer.parseInt(towDaysStreakRespond) == 1) {
            twoDaysStreak.setVisibility(View.VISIBLE);
            twoDayText.setVisibility(View.VISIBLE);
        }

        if (Integer.parseInt(threeDaysStreakRespond) == 1) {
            threeDaysStreak.setVisibility(View.VISIBLE);
            threeDayText.setVisibility(View.VISIBLE);
        }

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

            if(!ageString.equals("") && !weightString.equals("") && !heightString.equals("")){
                if (Integer.parseInt(ageString) < 8 || Integer.parseInt(weightString) < 30 || Integer.parseInt(heightString) < 100){
                    showMessage = 2;
                }else {
                    System.out.println(ageString + " " + weightString + " " + heightString + " " + genderSpinnerString + " " + activitySpinnerString + " " + goalSpinnerString + " " + loggedIn);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id_user", String.valueOf(loggedIn)));
                    params.add(new BasicNameValuePair("age", ageString));
                    params.add(new BasicNameValuePair("height", heightString));
                    params.add(new BasicNameValuePair("weight", weightString));
                    params.add(new BasicNameValuePair("activity", activitySpinnerString));
                    params.add(new BasicNameValuePair("goal", goalSpinnerString));
                    params.add(new BasicNameValuePair("sex", genderSpinnerString));

                    editor.putString("info","Age: " + ageString + " Height: " + heightString + "cm Weight: " + weightString + "kg Goal: " + goalSpinnerString);
                    editor.commit();

                    JSONObject json = jsonParser.makeHttpRequest(setUserDemandURL, "POST", params);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            userInfo.setText("Age: " + ageString + " Height: " + heightString + "cm Weight: " + weightString + "kg Goal: " + goalSpinnerString);
                            userInfo2.setText("Goal: " + goalSpinnerString);

                        }
                    });
                    showMessage = 0;
                }
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
            }else if (showMessage == 2){
                Toast toastIncorrectLoginOrPassword = Toast.makeText(getApplicationContext(), "Provide real informations", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }else if ( showMessage == 0){
                Toast toastIncorrectLoginOrPassword = Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }
        }
    }

    class getAchivementsInfo extends AsyncTask<String,String,String>{
        int success;
        @Override
        protected String doInBackground(String... strings) {

            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id_user", String.valueOf(loggedIn)));

                JSONObject json = jsonParser.makeHttpRequest(achivemenstInfo, "POST", params);
                Log.d("main", json.toString());

                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {//found

                    jsonProductArray = json.getJSONArray(TAG_RESPONSEARRAY);
                    JSONObject jsonTemp = jsonProductArray.getJSONObject(0);

                    oneDayStreakRespond = jsonTemp.getString("oneDay");
                    towDaysStreakRespond = jsonTemp.getString("twoDays");
                    threeDaysStreakRespond = jsonTemp.getString("threeDays");

                }else{
                    oneDayStreakRespond = String.valueOf(0);
                    towDaysStreakRespond = String.valueOf(0);
                    threeDaysStreakRespond = String.valueOf(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}