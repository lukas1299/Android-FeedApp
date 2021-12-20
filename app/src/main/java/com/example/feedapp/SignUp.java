package com.example.feedapp;

import androidx.appcompat.app.AppCompatActivity;

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

public class SignUp extends AppCompatActivity {

    EditText editTextEmail;
    EditText editTextUserName;
    EditText editTextPassword;

    TextView textViewForward;

    Button buttonRegister;

    JSONParser jsonParser = new JSONParser();
    private static final String registerURL = "http://192.168.100.8/android/signup.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESPONSEARRAY = "responseArray";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_EMAIL = "email";

    int showMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textViewForward = findViewById(R.id.forwardToLogin);
        buttonRegister = findViewById(R.id.btnRegister);
        editTextPassword= findViewById(R.id.editTextPassword);
        editTextUserName = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);

        textViewForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                overridePendingTransition(0,0);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new registerInTask().execute();
            }
        });
    }

    class registerInTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            int success;
            showMessage = 0;
            String username = editTextUserName.getText().toString();
            String password = editTextPassword.getText().toString();
            String email = editTextEmail.getText().toString();
//TODO: checkbox z privacy policy
            if(!username.equals("") && !password.equals("") && !email.equals("")) {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair(TAG_USERNAME, username));
                    params.add(new BasicNameValuePair(TAG_PASSWORD, password));
                    params.add(new BasicNameValuePair(TAG_EMAIL, email));

                    JSONObject json = jsonParser.makeHttpRequest(registerURL, "POST", params);

                    Log.d("main", json.toString());

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        JSONArray responseArray = json.getJSONArray(TAG_RESPONSEARRAY);
                        JSONObject responseObject = responseArray.getJSONObject(0);

                        if(responseObject.getInt("main") == 1) {
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            overridePendingTransition(0,0);
                        }else if (responseObject.getInt("main") == 0) {
                            showMessage = 1;
                        }else if(responseObject.getInt("main") == 2){
                            showMessage = 3;
                        }else if (responseObject.getInt("main") == 3) {
                            showMessage = 4;
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
                Toast toastIncorrectLoginOrPassword = Toast.makeText(context, "User with that email already exist.", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }else if (showMessage == 2){
                Toast toastIncorrectLoginOrPassword = Toast.makeText(context, "Insert username and password.", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }else if (showMessage == 3){
                Toast toastIncorrectLoginOrPassword = Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT);
                toastIncorrectLoginOrPassword.show();
            }else if (showMessage == 4){
                Toast toastIncorrectLoginOrPassword = Toast.makeText(context, "Password must be at least 8 characters in length and must contain at least one number, one upper case letter, one lower case letter and one special character.", Toast.LENGTH_LONG);
                toastIncorrectLoginOrPassword.show();
            }
        }
    }
}