package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportsActivity extends AppCompatActivity {
    JSONArray reports = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        try {
            reports.put(new JSONObject().put("nm", "salesList"));

            reports.put(new JSONObject().put("nm", "salesByTowns"));
            reports.put(new JSONObject().put("nm", "salesByGovs"));
            reports.put(new JSONObject().put("nm", "salesByNhs"));
            reports.put(new JSONObject().put("nm", "salesByStores"));


            reports.put(new JSONObject().put("nm", "itemSales"));

            reports.put(new JSONObject().put("nm", "storesBalances"));
        } catch (JSONException e) {
            General.handleError("Reports 00!: ", e.toString());
        }

    }
}
