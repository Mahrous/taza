package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecruitActivity extends AppCompatActivity {
    Button searchCandidateBtn, responseCandidateBtn;
    TextView candidateDataTextView, candidateMobileET;
    EditText empNameET, empTellET;
    JSONObject candidate;
    ArrayList<String> myRolesArray = new ArrayList<>();
    Spinner recruitStatesSpinner, userTypeSp, storesSp, jobsSp;
    ListView rolesLV;
    String[] recruitStatesArray, userTypesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);
        rolesLV = findViewById(R.id.rolesLV);
        empNameET = findViewById(R.id.empNameET);
        empTellET = findViewById(R.id.empTellET);
        empTellET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        searchCandidateBtn = findViewById(R.id.searchCandidateBtn);
        responseCandidateBtn = findViewById(R.id.responseCandidateBtn);

        candidateDataTextView = findViewById(R.id.candidateDataTextView);
        candidateMobileET = findViewById(R.id.candidateMobileET);

        recruitStatesSpinner = findViewById(R.id.recruitStatesSpinner);
        userTypeSp = findViewById(R.id.userTypeSp);
        storesSp = findViewById(R.id.storesSp);
        jobsSp = findViewById(R.id.jobsSp);
        jobsSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, General.jobsArray));
        candidateMobileET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        recruitStatesArray = new String[]{"accepted", "refused", "suspended"};
        userTypesArray = new String[]{ "manager", "supervisor" , "user" , "preparator" , "checker" , "delivery" , "driver" , "pr" };
        recruitStatesSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, recruitStatesArray));
        userTypeSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, userTypesArray));

        try {
            for (int i = 0; i < General.user.getJSONObject("emp").getJSONArray("rls").length(); i++) {
                myRolesArray.add(General.translator.getString(General.user.getJSONObject("emp").getJSONArray("rls").getString(i)));
            }
        } catch (JSONException e) {
            General.handleError("recruit", e.toString());
        }

        rolesLV.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, myRolesArray));
        try {
            String[] storesArray = new String[General.appStores.length()];
            for (int i = 0; i < General.appStores.length(); i++) {
                storesArray[i] = General.appStores.getJSONObject(i).getString("nm");
            }
            storesSp.setAdapter(new ArrayAdapter<>(RecruitActivity.this, R.layout.single_row_myorder_spin, storesArray));
        } catch (JSONException e) {
            General.handleError("RecruitActivity, onCreate, 002: ", e.toString());
        }

        searchCandidateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    JSONObject results = General.postUrl(General.url + "careers/findCandidate", new JSONObject().put("mo", candidateMobileET.getText().toString().trim()));
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.showToast(RecruitActivity.this, "succeeded ...");
                        candidate = results.getJSONObject("data");

                        empNameET.setText(candidate.getString("nm"));
                        empTellET.setText(candidate.getString("mo"));
                        candidateDataTextView.setText(
                                candidate.getString("nm") + ", " +
                                        candidate.getString("mo") + ", " +
                                        (candidate.getJSONObject("emp").has("jb") ? candidate.getJSONObject("emp").getString("jb") : "") + ", " +
                                        (candidate.getJSONObject("emp").has("st") ? candidate.getJSONObject("emp").getString("st") : "")
                        );
                    }
                } catch (JSONException e) {
                    Log.e("JSONExceptionssss: ", e.toString());
                }
            }
        });
        responseCandidateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (empNameET.getText().toString().length() == 0) {
                    empNameET.setError("برجاء تسجيل اسم الموظف");
                    empNameET.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل الموظف", Toast.LENGTH_LONG);
                    return;
                }
                if (empTellET.getText().toString().length() == 0) {
                    empTellET.setError("برجاء تسجيل تليفون الموظف !!!");
                    empTellET.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل تليفون الموظف !!!", Toast.LENGTH_LONG);
                    return;
                }
                try {
                    JSONArray roles = new JSONArray();
                    for (int i = 0; i < rolesLV.getCount(); i++) {
                        if (rolesLV.isItemChecked(i)) {
                            roles.put(General.translator.getString(myRolesArray.get(i)));/* do whatever you want with the checked item */
                        }
                    }
                    Log.e("vvvv", roles.toString());
                    JSONObject results = General.postUrl(General.url + "careers/evaluateCandidate", new JSONObject().put("_id", candidate.getString("_id"))
                            .put("st", recruitStatesSpinner.getSelectedItem().toString())//applicant
                            .put("tp", userTypeSp.getSelectedItem().toString())
                            .put("nm", empNameET.getText().toString())
                            .put("tl", empTellET.getText().toString())
                            .put("av", true)
                            .put("rls", roles)
                            .put("jb", jobsSp.getSelectedItem().toString())//jobsSp.getSelectedItem().toString()
                            .put("storeId", General.appStores.getJSONObject(storesSp.getSelectedItemPosition()).getString("_id"))
                            .put("storeName", storesSp.getSelectedItem().toString())
                            .put("ops", new JSONArray().put(0, new JSONObject().put("av", true)
                                    .put("nm", "توظيف").put("nts", "تعيين جديد"))));

                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(RecruitActivity.this, "هناك خطأ ما, راجع مديرك !!!");
                        Log.e("eeeeeeeeee", "failed !!! " + results.toString());
                    } else {
                        General.showToast(RecruitActivity.this, "تم التسجيل بنجاح");
                    }
                } catch (JSONException e) {
                    General.handleError("9u9u", e.toString());
                }
            }
        });
    }
}

//                candidateData = new JSONObject();
//                try {
//                    candidateData.put("mo",candidateMobileET.getText().toString().trim());
//                }catch (JSONException e){
//                    General.handleError("zzzzz", e.toString());
//                }
//
//                new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//                        OkHttpClient client = new OkHttpClient();
//                        client.setConnectTimeout(30, TimeUnit.SECONDS);
//                        client.setReadTimeout(30, TimeUnit.SECONDS);
//                        client.setWriteTimeout(30, TimeUnit.SECONDS);
//                        RequestBody body = RequestBody.create(General.JSON, candidateData.toString());
//                        Request request = new Request.Builder()
//                                .url(General.url+"careers/findCandidate")
//                                .post(body)
//                                .build();
//                        Response response = null;
//                        try {
//                            response = new OkHttpClient().newCall(request).execute();
//                            return  response.body().string();
//                        }catch(IOException e){
//                            Log.e("er new user:", e.toString());
//                            return ("failed: "+e.toString());
//                        }
//                    }
//
//                    @Override
//                    protected void onPostExecute(Object result) {
//                        Log.e("now career:", result.toString());
//                        try {
//                            JSONObject results = new JSONObject(result.toString());
//                            if (results.getString("st").matches("ok")){
//                                candidate = results.getJSONObject("data");
//                                candidateDataTextView.setText(
//                                        candidate.getString("nm")+", "+
//                                        candidate.getString("mo")+", "+
//                                                (candidate.getJSONObject("emp").has("jb")?candidate.getJSONObject("emp").getString("jb"): "")+", "+
//                                                (candidate.getJSONObject("emp").has("st")? candidate.getJSONObject("emp").getString("st"): "")
//                                );
//                            }else if (results.getString("st").matches("no")){
//                                General.showToast(getApplicationContext(),"Incorrect data: ", Toast.LENGTH_LONG);
//                            }
//                        }catch (JSONException e){
//                            Log.e("JSONExceptionssss: ",e.toString());
//                        }
//                    }
//                }.execute() ;


//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                OkHttpClient client = new OkHttpClient();
//                client.setConnectTimeout(30, TimeUnit.SECONDS);
//                client.setReadTimeout(30, TimeUnit.SECONDS);
//                client.setWriteTimeout(30, TimeUnit.SECONDS);
//                Request request = new Request.Builder()
//                        .url(General.url+"stores")
//                        .build();
//                Response response = null;
//                try {
//                    response = new OkHttpClient().newCall(request).execute();
//                    return  response.body().string();
//                }catch(IOException e){
//                    General.handleError("RecruitActivity, onCreate, 001: ",e.toString());
//                    return ("failed: "+e.toString());
//                }
//            }
//            @Override
//            protected void onPostExecute(Object result) {
//                try {
//                    JSONObject results = new JSONObject(result.toString());
//                    if (results.getString("st").matches("ok")){
////                        stores = new JSONArray();
//                        stores = results.getJSONArray("stores");
//                        Log.e("ooooooo",stores.toString());
//                        if (stores.length() > 0){
//                            String [] storesArray = new String[stores.length()];
//                            for (int i=0;i<stores.length();i++){
//                                storesArray[i] = stores.getJSONObject(i).getString("nm")+","+stores.getJSONObject(i).getString("_id");
//                            }
//                            storesSp.setAdapter(new ArrayAdapter<String>(RecruitActivity.this,android.R.layout.simple_spinner_item, storesArray));
//                        }
//                    }else if (results.getString("st").matches("no")){
//                        General.showToast(getApplicationContext(),"Incorrect data: ", Toast.LENGTH_LONG);
//                    }
//                }catch (JSONException e){
//                    General.handleError("RecruitActivity, onCreate, 002: ",e.toString());
//                }
//            }
//        }.execute() ;

//                Log.e("candidate: ", candidate.toString());
//                new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//                        OkHttpClient client = new OkHttpClient();
//                        client.setConnectTimeout(30, TimeUnit.SECONDS);
//                        client.setReadTimeout(30, TimeUnit.SECONDS);
//                        client.setWriteTimeout(30, TimeUnit.SECONDS);
//                        RequestBody body = RequestBody.create(General.JSON, candidate.toString());
//                        Request request = new Request.Builder()
//                                .url(General.url+"careers/evaluateCandidate")
//                                .post(body)
//                                .build();
//                        Response response = null;
//                        try {
//                            response = new OkHttpClient().newCall(request).execute();
//                            return  response.body().string();
//                        }catch(IOException e){
//                            Log.e("er new user:", e.toString());
//                            return ("failed: "+e.toString());
//                        }
//                    }
//                    @Override
//                    protected void onPostExecute(Object result) {
//                        Log.e("now career:", result.toString());
//                        try {
//                            JSONObject results = new JSONObject(result.toString());
//                            if (results.getString("st").matches("ok")){
//                                JSONObject candidate = new JSONObject(result.toString()).getJSONObject("data");
////                                candidateDataTextView.setText(candidate.getString("nm")+", "+candidate.getString("mo")+", "+candidate.getString("jb")+", "+candidate.getString("st"));
//
//                            }else if (results.getString("st").matches("no")){
//                                General.showToast(getApplicationContext(),"Incorrect data: ", Toast.LENGTH_LONG);
//                            }
//                        }catch (JSONException e){
//                            General.handleError("e3e4: ",e.toString());
//                        }
//                    }
//                }.execute() ;

