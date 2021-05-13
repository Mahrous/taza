package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class CareersActivity extends AppCompatActivity {
    Spinner jobsSp;
    Button sendAllplicationBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_careers);

        sendAllplicationBtn = findViewById(R.id.sendAllplicationBtn);
        jobsSp = findViewById(R.id.jobsSpinner);
        jobsSp.setAdapter(new ArrayAdapter<>(this,R.layout.single_row_myorder_spin, General.jobsArray));


        sendAllplicationBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    JSONObject results = General.postUrl(General.url + "careers/new", new JSONObject().put("jb", jobsSp.getSelectedItem().toString())
                            .put("_id", General.user.getString("_id")).put("st", General.user.getString("applicant"))
                    );
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(CareersActivity.this, "failed !!!");
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.showToast(CareersActivity.this, "succeeded ...");
                    }
                }catch (JSONException e){
                    General.handleError("vvvvvv", e.toString());
                }
            }
        });

    }
}
