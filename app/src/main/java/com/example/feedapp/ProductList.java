package com.example.feedapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProductList extends ListActivity {

    private EditText nameOfProduct;
    private Button qrButton;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonProductArray;
    private ListView listView;

    boolean emptyProductName;

    private ArrayList<HashMap<String,String>> productList;

    private static final String getProductListURL = "http://192.168.100.8/android/productSearch.php";
    private static final String setMealHistory = "http://192.168.100.8/android/mealHistory.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESPONSEARRAY = "responseArray";
    private static final String TAG_ID= "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CALORIES = "calories";

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText popupQuantity;
    private TextView popupProductName;
    private TextView calculatedCalories;
    private Button popupAcceptQuantity;

    String mealTime;
    String  loggedInID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mealTime = getIntent().getStringExtra("mealTime");
        String[] temp = mealTime.split(" ");
        mealTime = temp[0];
        loggedInID = temp[1];

        nameOfProduct = findViewById(R.id.productSearch);
        qrButton = findViewById(R.id.qrButton);
        listView = findViewById(android.R.id.list);

        nameOfProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = nameOfProduct.getText().toString();
                if (!temp.equals("")) {
                    new productSearching().execute();
                }else{

                }
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//TODO: scanner qr
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] kcalTemp = (((TextView)view.findViewById(R.id.caloriesInfo)).getText().toString()).split(" ");
                String kcal = kcalTemp[0];
                int kcalInt = Integer.parseInt(kcal);
                addToMealsDialog(Integer.parseInt((((TextView)view.findViewById(R.id.id)).getText().toString())),
                        kcalInt,
                        ((TextView)view.findViewById(R.id.nameInfo)).getText().toString()
                );
            }
        });
    }

    class productSearching extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            int success;
            emptyProductName = false;
            String productName = nameOfProduct.getText().toString();

            if(!productName.equals("")) {
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair(TAG_NAME, productName));

                    JSONObject json = jsonParser.makeHttpRequest(getProductListURL, "POST", params);

                    //System.out.println(mealTime);
                    //Log.d("main", json.toString());

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {//found

                        jsonProductArray = json.getJSONArray(TAG_RESPONSEARRAY);
                        productList = new ArrayList<>();
                        for (int i = 0; i < jsonProductArray.length(); i++) {
                            JSONObject jsonTemp = jsonProductArray.getJSONObject(i);

                            String name = jsonTemp.getString(TAG_NAME);
                            String calories = jsonTemp.getString(TAG_CALORIES);
                            String id = jsonTemp.getString(TAG_ID);

                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_ID, id);
                            map.put(TAG_NAME, name);
                            map.put(TAG_CALORIES, calories + " kcal");

                            productList.add(map);
                        }
                    } else {//not found
                        emptyProductName = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                emptyProductName = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!emptyProductName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListAdapter adapter = new SimpleAdapter(ProductList.this, productList, R.layout.item_list, new String[]{TAG_ID,TAG_NAME, TAG_CALORIES}, new int[]{R.id.id ,R.id.nameInfo, R.id.caloriesInfo});
                        setListAdapter(adapter);
                    }
                });
            }
        }
    }

    public void addToMealsDialog(int id, int kcal, String name){
        dialogBuilder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.quantity_popup,null);

        popupQuantity = (EditText) view.findViewById(R.id.quantity);
        popupProductName = (TextView) view.findViewById(R.id.productName);
        popupAcceptQuantity = (Button) view.findViewById(R.id.acceptQuantity);
        calculatedCalories = (TextView) view.findViewById(R.id.calculatedCalories);

        popupProductName.setText(name);
        calculatedCalories.setText(String.valueOf(kcal) + " kcal");

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        popupQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (popupQuantity.equals("")) popupQuantity.setText("0");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = popupQuantity.getText().toString();
                if (!temp.equals("")) {
                    float calculated = Math.round((Float.parseFloat(popupQuantity.getText().toString())) / 100 * kcal);
                    calculatedCalories.setText(calculated + " kcal");
                }else{
                    calculatedCalories.setText(String.valueOf(kcal) + " kcal");
                }
            }
        });

        popupAcceptQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = popupQuantity.getText().toString();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                if(!temp.equals("")){
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id_user",loggedInID));
                    params.add(new BasicNameValuePair("id_product",String.valueOf(id)));
                    params.add(new BasicNameValuePair("quantity",temp));
                    params.add(new BasicNameValuePair("date",currentDate));

                    JSONObject jsonObject = jsonParser.makeHttpRequest(setMealHistory,"POST",params);
                }
            }
        });
    }
}