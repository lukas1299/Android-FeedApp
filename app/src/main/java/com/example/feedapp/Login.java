package com.example.feedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    EditText editTextUserName;
    EditText editTextPassword;

    Button loginButton;

    TextView textView;
    TextView test;

    JSONParser jsonParser = new JSONParser();
    public static final String IPaddres = "http://192.168.100.9/android/";
    private static final String loginURL = IPaddres + "login.php";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_RESPONSEARRAY = "responseArray";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";

    int showMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("lifeCycle",MODE_PRIVATE);
        int temp = sharedPreferences.getInt("loggedIN",0);

        if (temp != 0 ){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(0,0);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView = findViewById(R.id.forwardToRegister);
        loginButton = findViewById(R.id.btnLogin);
        editTextUserName = findViewById(R.id.usernameText);
        editTextPassword = findViewById(R.id.passwordText);
        test = findViewById(R.id.textView9);
        Intent i = getIntent();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                overridePendingTransition(0,0);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new logInTask().execute();
            }
        });
    }

    class logInTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            int success;
            showMessage = 0;
            String username = editTextUserName.getText().toString();
            String password = editTextPassword.getText().toString();

            SharedPreferences sharedPreferences = getSharedPreferences("lifeCycle",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(!username.equals("") && !password.equals("")) {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_PASSWORD, password));

                    JSONObject json = jsonParser.makeHttpRequest(loginURL, "POST", params);

                    //Log.d("main", json.toString());

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        JSONArray responseArray = json.getJSONArray(TAG_RESPONSEARRAY);
                        JSONObject responseObject = responseArray.getJSONObject(0);

                        if(responseObject.getInt("main") == 1) {

                            int id = responseObject.getInt("loggedIn");
                            String userName = responseObject.getString("userName");
                            String info;
                            String info2;
                            if (!responseObject.getString("age").equals("1")) {
                                info = "Age: " + responseObject.getString("age") + " Height: " + responseObject.getString("height") + "cm Weight: " + responseObject.getString("weight") + "kg ";
                                info2 = "Goal: " + responseObject.getString("goal");
                            }else{
                                info = "Provide informations!";
                                info2 = "";
                            }
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            overridePendingTransition(0,0);
                            editor.putString("userName",userName);
                            editor.putString("info",info);
                            editor.putString("info2",info2);
                            editor.putInt("loggedIN",id);
                            editor.commit();

                        }else {
                            showMessage = 1;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                showMessage = 2;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Context context = getApplicationContext();
            if(showMessage == 1) {
                Toast toastIncorrectLoginOrPassword = Toast.makeText(context, "Incorrect login or password", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }else if (showMessage == 2){
                Toast toastIncorrectLoginOrPassword = Toast.makeText(context, "Insert username and password", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }
        }
    }
}





