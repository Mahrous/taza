package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeoActivity extends AppCompatActivity {
//    JSONObject geoData;
//    JSONArray countries,governorates,towns,nhs;
    Spinner countriesSp, governoratesSp, townsSp, nhsSp;
    EditText countriesTV, governoratesTV, townsTV, nhsTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);

        countriesSp = findViewById(R.id.countriesSp);
        governoratesSp = findViewById(R.id.governoratesSp);
        townsSp =  findViewById(R.id.townsSp);
        nhsSp =  findViewById(R.id.nhsSp);
        countriesTV = findViewById(R.id.countriesTV);
        governoratesTV = findViewById(R.id.governoratesTV);
        townsTV = findViewById(R.id.townsTV);
        nhsTV = findViewById(R.id.nhsTV);
        refreshGeo();
        (findViewById(R.id.countriesBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (countriesTV.getText().toString().length() == 0) {
                    countriesTV.setError("برجاء تسجيل اسم الدولة !!!");
                    countriesTV.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل اسم الدولة !!!", Toast.LENGTH_LONG);
                    return;
                }
                try {
                    JSONObject results = General.postUrl(General.url + "geos/newCountry",new JSONObject().put("nm", ((TextView)findViewById(R.id.countriesTV)).getText().toString()));
                    if (results.length() == 0 || !results.getBoolean("state")){
                        General.showToast(GeoActivity.this, "failed !!!");
                        Log.e("eeeeeeeeee", "failed !!!"+ (results.has("err")?results.getString("err"):""));
                    }else {
                        General.showToast(GeoActivity.this, "succeeded ...");
                    }
                }catch (JSONException e){
                    Log.e("zzzzz", e.toString());
                }
            }
        });
        (findViewById(R.id.governoratesBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (governoratesTV.getText().toString().length() == 0) {
                    governoratesTV.setError("برجاء تسجيل اسم المحافظة !!!");
                    governoratesTV.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل اسم المحافظة !!!", Toast.LENGTH_LONG);
                    return;
                }
                try {
                    JSONObject results = General.postUrl(General.url + "geos/newGov",new JSONObject()
                        .put("nm",((TextView)findViewById(R.id.governoratesTV)).getText())
                        .put("co", new JSONObject().put("nm", countriesSp.getSelectedItem().toString().trim()).put("id", General.countries.getJSONObject(countriesSp.getSelectedItemPosition()).getString("_id")))
                        .put("cu", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                    );
                    if (results.length() == 0 || !results.getBoolean("state")){
                        General.showToast(GeoActivity.this, "failed !!!");
                        Log.e("eeeeeeeeee", "failed !!!"+ (results.has("err")?results.getString("err"):""));
                    }else {
                        Log.e("eeeeeeeeee", results.toString());
                        General.showToast(GeoActivity.this, "succeeded ...");
                    }
                }catch (JSONException e){
                    Log.e("zzzzz", e.toString());
                }

            }
        });
/////////////
        (findViewById(R.id.townsBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (townsTV.getText().toString().length() == 0) {
                    townsTV.setError("برجاء تسجيل اسم المدينة !!!");
                    townsTV.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل اسم المدينة !!!", Toast.LENGTH_LONG);
                    return;
                }

                try {
                    JSONObject results = General.postUrl(General.url + "geos/newTown",new JSONObject()
                            .put("nm",((TextView)findViewById(R.id.townsTV)).getText())
                            .put("gv", new JSONObject().put("nm", governoratesSp.getSelectedItem().toString().trim()).put("id", General.governorates.getJSONObject(governoratesSp.getSelectedItemPosition()).getString("_id")))
                            .put("co", new JSONObject().put("nm", countriesSp.getSelectedItem().toString().trim()).put("id", General.countries.getJSONObject(countriesSp.getSelectedItemPosition()).getString("_id")))
                            .put("cu", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                    );
                    if (results.length() == 0 || !results.getBoolean("state")){
                        General.showToast(GeoActivity.this, "failed !!!");
                        Log.e("eeeeeeeeee", "failed !!!"+ (results.has("err")?results.getString("err"):""));
                    }else {
                        General.showToast(GeoActivity.this, "succeeded ...");
                    }
                }catch (JSONException e){
                    Log.e("zzzzz", e.toString());
                }
            }
        });
        (findViewById(R.id.nhsBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (nhsTV.getText().toString().length() == 0) {
                    nhsTV.setError("برجاء تسجيل اسم الحى !!!");
                    nhsTV.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل اسم الحى !!!", Toast.LENGTH_LONG);
                    return;
                }

                try {
                    JSONObject results = General.postUrl(General.url + "geos/newNh",new JSONObject()
                                    .put("nm",((TextView)findViewById(R.id.nhsTV)).getText())
                                    .put("tn", new JSONObject().put("nm", townsSp.getSelectedItem().toString().trim()).put("id", General.towns.getJSONObject(townsSp.getSelectedItemPosition()).getString("_id")))
                                    .put("gv", new JSONObject().put("nm", governoratesSp.getSelectedItem().toString().trim()).put("id", General.governorates.getJSONObject(governoratesSp.getSelectedItemPosition()).getString("_id")))
                                    .put("co", new JSONObject().put("nm", countriesSp.getSelectedItem().toString().trim()).put("id", General.countries.getJSONObject(countriesSp.getSelectedItemPosition()).getString("_id")))
                                    .put("cu", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
//                            .put("gv", new JSONObject().put("nm", governoratesSp.getSelectedItem().toString().split(",")[0]).put("id", governoratesSp.getSelectedItem().toString().split(",")[1]))
                    );
                    if (results.length() == 0 || !results.getBoolean("state")){
                        General.showToast(GeoActivity.this, "failed !!!");
                        Log.e("eeeeeeeeee", "failed !!!"+ (results.has("err")?results.getString("err"):""));
                    }else {
                        General.showToast(GeoActivity.this, "succeeded ...");
                    }
                }catch (JSONException e){
                    Log.e("zzzzz", e.toString());
                }
//
            }
        });
//////////////
    }

    void refreshGeo (){
        countriesSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this, R.layout.single_row_myorder_spin , General.countriesArray));
        governoratesSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,R.layout.single_row_myorder_spin, General.gvsArray));
        townsSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,R.layout.single_row_myorder_spin, General.townsArray));
        nhsSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,R.layout.single_row_myorder_spin, General.nhsArray));

//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                OkHttpClient client = new OkHttpClient();
//                client.setConnectTimeout(30, TimeUnit.SECONDS);
//                client.setReadTimeout(30, TimeUnit.SECONDS);
//                client.setWriteTimeout(30, TimeUnit.SECONDS);
//                Request request = new Request.Builder()
//                        .url(General.url+"geos")
//                        .build();
//                Response response = null;
//                try {
//                    response = new OkHttpClient().newCall(request).execute();
//                    return  response.body().string();
//                }catch(IOException e){
//                    Log.e("er new user:", e.toString());
//                    return ("failed: "+e.toString());
//                }
//            }
//            @Override
//            protected void onPostExecute(Object result) {
//                Log.e("now user:", result.toString());
//                try {
//                    JSONObject results = new JSONObject(result.toString());
//                    if (results.getString("st").matches("ok")){
//                        if (results.getJSONArray("countries").length() > 0){
//                            countries = results.getJSONArray("countries");
//                            String [] countriesArray = new String[countries.length()];
//                            JSONObject country = new JSONObject();
//                            for (int i=0;i<countries.length();i++){
//                                country = countries.getJSONObject(i);
//                                countriesArray[i] = country.getString("nm")+","+country.getString("_id");
//                            }
//                            countriesSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,android.R.layout.simple_spinner_item, countriesArray));
//                            Log.e("tttttttttttttt",countriesSp.getSelectedItem().toString().split(",")[1]);
//                        }
//                        if (results.getJSONArray("governorates").length() > 0){
//                            governorates = results.getJSONArray("governorates");
//                            String [] governoratesArray = new String[governorates.length()];
//                            JSONObject governorate = new JSONObject();
//                            for (int i=0;i<governorates.length();i++){
//                                governorate = governorates.getJSONObject(i);
//                                governoratesArray[i] = governorate.getString("nm")+","+governorate.getString("_id");
//                            }
//                            governoratesSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,android.R.layout.simple_spinner_item, governoratesArray));
//                        }
//                        if (results.getJSONArray("towns").length() > 0){
//                            towns = results.getJSONArray("towns");
//                            String [] townsArray = new String[towns.length()];
//                            JSONObject town = new JSONObject();
//                            for (int i=0;i<towns.length();i++){
//                                town = towns.getJSONObject(i);
//                                townsArray[i] = town.getString("nm")+","+town.getString("_id");
//                            }
//                            townsSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,android.R.layout.simple_spinner_item, townsArray));
//                        }
//                        if (results.getJSONArray("nhs").length() > 0){
//                            nhs = results.getJSONArray("nhs");
//                            String [] nhsArray = new String[nhs.length()];
//                            JSONObject nh = new JSONObject();
//                            for (int i=0;i<nhs.length();i++){
//                                nh = nhs.getJSONObject(i);
//                                nhsArray[i] = nh.getString("nm")+","+nh.getString("_id");
//                            }
//                            nhsSp.setAdapter(new ArrayAdapter<String>(GeoActivity.this,android.R.layout.simple_spinner_item, nhsArray));
//                        }
//                    }else if (results.getString("st").matches("no")){
//                        General.showToast(getApplicationContext(),"Incorrect data: ", Toast.LENGTH_LONG);
//                    }
//                }catch (JSONException e){
//                    Log.e("JSONException: ",e.toString());
//                }
//            }
//        }.execute() ;
    }
}
//                new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//                        OkHttpClient client = new OkHttpClient();
//                        client.setConnectTimeout(30, TimeUnit.SECONDS);
//                        client.setReadTimeout(30, TimeUnit.SECONDS);
//                        client.setWriteTimeout(30, TimeUnit.SECONDS);
//                        RequestBody body = RequestBody.create(General.JSON, geoData.toString());
//                        Request request = new Request.Builder()
//                                .url(General.url+"geos/newCountry")
//                                .post(body)
//                                .build();
//                        Response response = null;
//                        try {
//                            response = new OkHttpClient().newCall(request).execute();
//                            return  response.body().string();
//                        }catch(IOException e){
//                            Log.e("er new country:", e.toString());
//                            return ("failed: "+e.toString());
//                        }
//                    }
//                    @Override
//                    protected void onPostExecute(Object result) {
//                        refreshGeo();
//                        Log.e("now career:", result.toString());
//
//                    }
//                }.execute() ;
//                new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//                        OkHttpClient client = new OkHttpClient();
//                        client.setConnectTimeout(30, TimeUnit.SECONDS);
//                        client.setReadTimeout(30, TimeUnit.SECONDS);
//                        client.setWriteTimeout(30, TimeUnit.SECONDS);
//                        RequestBody body = RequestBody.create(General.JSON, geoData.toString());
//                        Request request = new Request.Builder()
//                                .url(General.url+"geos/newNh")
//                                .post(body)
//                                .build();
//                        Response response = null;
//                        try {
//                            response = new OkHttpClient().newCall(request).execute();
//                            return  response.body().string();
//                        }catch(IOException e){
//                            Log.e("er new country:", e.toString());
//                            return ("failed: "+e.toString());
//                        }
//                    }
//                    @Override
//                    protected void onPostExecute(Object result) {
//                        refreshGeo();
//                        Log.e("now career:", result.toString());
//
//                    }
//                }.execute() ;