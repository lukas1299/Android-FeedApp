package com.example.feedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

    String pid;
    JSONParser jsonParser = new JSONParser();

    private static final String loginURL = "http://192.168.100.6/android/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";

    int showMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView = findViewById(R.id.forwardToRegister);
        loginButton = findViewById(R.id.btnLogin);
        editTextUserName = findViewById(R.id.usernameText);
        editTextPassword = findViewById(R.id.passwordText);
        test = findViewById(R.id.textView9);
        Intent i = getIntent();

        pid = i.getStringExtra(TAG_PID);

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

            if(!username.equals("") && !password.equals("")) {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_PASSWORD, password));

                    JSONObject json = jsonParser.makeHttpRequest(loginURL, "POST", params);

                    Log.d("main", json.toString());

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        JSONArray responseArray = json.getJSONArray(TAG_PRODUCT);
                        JSONObject responseObject = responseArray.getJSONObject(0);

                        if(responseObject.getInt("main") == 1) {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            overridePendingTransition(0,0);
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





