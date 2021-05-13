package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoresActivity extends AppCompatActivity {

    Button saveNewUserBtn, addNhBtn, newStoreBtn;
    JSONObject nowStore = new JSONObject();
    JSONArray stores;//storeAreas = new JSONArray(),
    EditText userNameText, adrsDtsET, adrsAreaET;
    ListView storeAreasLV;
    Spinner adrsNhsSp, storesSp, adrsGvsSp, adrsTnsSp;//, distsSp, storesNhSp
    ArrayList<String> storeAreasArray = new ArrayList<>(), storesArray = new ArrayList<>();
    Boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        saveNewUserBtn = findViewById(R.id.saveNewUserBtn);
        addNhBtn = findViewById(R.id.addNhBtn);
        newStoreBtn = findViewById(R.id.newStoreBtn);

        userNameText = findViewById(R.id.userNameText);
        adrsDtsET = findViewById(R.id.adrsDtsET);
        adrsAreaET = findViewById(R.id.adrsAreaET);
        storeAreasLV = findViewById(R.id.storeAreasLV);
        adrsNhsSp = findViewById(R.id.adrsNhsSp);
        storesSp = findViewById(R.id.storesSp);
        adrsGvsSp = findViewById(R.id.adrsGvsSp);
        adrsTnsSp = findViewById(R.id.adrsTnsSp);
        try {
            JSONObject results = General.postUrl(General.url + "stores/getStores", new JSONObject().put("userId", General.user.getString("_id")));//new JSONObject().put("order",
            stores = results.getJSONArray("stores");
            for (int i = 0; i < stores.length(); i++) {
                storesArray.add(stores.getJSONObject(i).getString("nm"));//
            }
            adrsGvsSp.setAdapter(new ArrayAdapter<>(this,R.layout.single_row_myorder_spin, General.gvsArray));
            adrsTnsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this, R.layout.single_row_myorder_spin, General.townsArray));//General.getTowns(General.governorates.getJSONObject(adrsGvsSp.getSelectedItemPosition()).getString("_id"))));
            adrsNhsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this, R.layout.single_row_myorder_spin, General.nhsArray));
            storesSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin
                    , storesArray));
        } catch (JSONException e) {
            General.handleError("MyOrdersActivity, 777: ", e.toString());
        }
        setObjEvents();
        isFinished = true;
    }

    void setStoreArea(final int index) {
        LinearLayout layout = new LinearLayout(StoresActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText dfsET = new EditText(StoresActivity.this);
        dfsET.setHint("رسوم التوصيل ...");
        dfsET.setText("10");
        final TextView dd = new TextView(StoresActivity.this);
        dd.setText("توزيعها من: ");
        final Spinner nhsSp = new Spinner(StoresActivity.this);
        final Spinner tnsSp = new Spinner(StoresActivity.this);
        final Spinner gvsSp = new Spinner(StoresActivity.this);
        final Spinner distsSp = new Spinner(StoresActivity.this);

        dfsET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        nhsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this, R.layout.single_row_myorder_spin, General.nhsArray));
        tnsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this, R.layout.single_row_myorder_spin, General.townsArray));
        gvsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this, R.layout.single_row_myorder_spin, General.gvsArray));
        distsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this, R.layout.single_row_myorder_spin, storesArray));

        layout.addView(nhsSp);
        layout.addView(dfsET);
        layout.addView(dd);
        layout.addView(distsSp);
        if (index > -1) {
            try {
                dfsET.setText(nowStore.getJSONArray("ars").getJSONObject(index).getString("dFs"));
                nhsSp.setSelection(((ArrayAdapter<String>) nhsSp.getAdapter()).getPosition(nowStore.getJSONArray("ars").getJSONObject(index).getString("nh")));
                distsSp.setSelection(((ArrayAdapter<String>) distsSp.getAdapter()).getPosition(nowStore.getJSONArray("ars").getJSONObject(index).getJSONObject("dst").getString("nm")));
            } catch (JSONException e) {
                General.handleError("Register", e.toString());
            }
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(StoresActivity.this)
                .setView(layout)
                .setTitle("بيانات المنطقة ...")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    if (!nowStore.has("ars")) {
                        nowStore.put("ars", new JSONArray());
                    }
                    if (index == -1) {
                        JSONObject obj = new JSONObject()
                                .put("av", true)
                                .put("dst", new JSONObject()
                                        .put("nm", distsSp.getSelectedItem().toString())
                                        .put("id", stores.getJSONObject(distsSp.getSelectedItemPosition()).getString("_id"))
                                )
                                .put("nm", nhsSp.getSelectedItem().toString())
                                .put("ar", nhsSp.getSelectedItem().toString())
                                .put("dt", nhsSp.getSelectedItem().toString())
                                .put("co", "مصر")

                                .put("nh", nhsSp.getSelectedItem().toString())
                                .put("tn", tnsSp.getSelectedItem().toString())
                                .put("gv", gvsSp.getSelectedItem().toString())
                                .put("id",
                                        General.getNhId(nhsSp.getSelectedItem().toString() + "," + tnsSp.getSelectedItem().toString() + "," + gvsSp.getSelectedItem().toString()));
                        nowStore.getJSONArray("ars").put(obj);
                        Log.e("ffffff", obj.toString());
                    } else {
                        nowStore.getJSONArray("ars").put(index, new JSONObject()
                                        .put("av", true)
                                        .put("dst", new JSONObject()
                                                .put("nm", distsSp.getSelectedItem().toString())
                                                .put("id", stores.getJSONObject(distsSp.getSelectedItemPosition()).getString("_id"))
                                        )
                                        .put("nm", nhsSp.getSelectedItem().toString())
                                        .put("ar", nhsSp.getSelectedItem().toString())
                                        .put("dt", nhsSp.getSelectedItem().toString())
                                        .put("nh", nhsSp.getSelectedItem().toString())
                                        .put("tn", "الزقازيق")
                                        .put("gv", "الشرقية")
                                        .put("co", "مصر")
                                        .put("id", General.getNhId(nhsSp.getSelectedItem().toString() + "," + tnsSp.getSelectedItem().toString() + "," + gvsSp.getSelectedItem().toString()))
                        );
                    }
                    storeAreasArray = new ArrayList<>();
                    for (int j = 0; j < nowStore.getJSONArray("ars").length(); j++) {
                        storeAreasArray.add(nowStore.getJSONArray("ars").getJSONObject(j).getString("nh") + "," + nowStore.getJSONArray("ars").getJSONObject(j).getJSONObject("dst").getString("nm"));
                        if (j == nowStore.getJSONArray("ars").length() -1){
                            Log.e("ccccccc", nowStore.getJSONArray("ars").getJSONObject(j).toString());
                        }
                    }
                    storeAreasLV.setAdapter(new ArrayAdapter<>(StoresActivity.this, android.R.layout.simple_list_item_checked, storeAreasArray));
                    Log.e("fffff", nowStore.getJSONArray("ars").toString());
                    alertDialog.dismiss();
                } catch (JSONException e) {
                    General.handleError("sss", e.toString());
                }
            }
        });
    }

    void setStoreData(int i) {
        try {
            if (i == -1) {
                userNameText.setText("");
                adrsDtsET.setText("");
                storeAreasArray.clear();
                storeAreasLV.setAdapter(new ArrayAdapter<>(StoresActivity.this, android.R.layout.simple_list_item_checked, storeAreasArray));
            } else {
                nowStore.put("_id", stores.getJSONObject(i).getString("_id"));
                if (!nowStore.has("ars")) {
                    nowStore.put("ars", new JSONArray());
                }


                for (int j = 0; j < stores.getJSONObject(i).getJSONArray("ars").length(); j++) {
                    Log.e("000000   ", i+"");
                    nowStore.getJSONArray("ars").put( new JSONObject()
                            .put("av", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getBoolean("av"))
                            .put("dst", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getJSONObject("dst"))
                            .put("nm", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("nm"))
                            .put("ar", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("ar"))
                            .put("dt", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("dt"))
                            .put("co", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("co"))
                            .put("nh", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("nh"))
                            .put("tn", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("tn"))
                            .put("gv", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("gv"))
                            .put("id", stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getJSONObject("id").getString("_id")))

                    ;
                }
                userNameText.setText(stores.getJSONObject(i).getString("nm"));
                adrsGvsSp.setSelection(((ArrayAdapter<String>) adrsGvsSp.getAdapter()).getPosition(stores.getJSONObject(i).getJSONArray("adrs").getJSONObject(0).getString("gv")));
                adrsTnsSp.setSelection(((ArrayAdapter<String>) adrsTnsSp.getAdapter()).getPosition(stores.getJSONObject(i).getJSONArray("adrs").getJSONObject(0).getString("tn")));
                adrsNhsSp.setSelection(((ArrayAdapter<String>) adrsNhsSp.getAdapter()).getPosition(stores.getJSONObject(i).getJSONArray("adrs").getJSONObject(0).getString("nh")));
                adrsDtsET.setText(stores.getJSONObject(i).getJSONArray("adrs").getJSONObject(0).getString("dt"));
                adrsAreaET.setText(stores.getJSONObject(i).getJSONArray("adrs").getJSONObject(0).getString("ar"));
                storeAreasArray.clear();
                for (int j = 0; j < stores.getJSONObject(i).getJSONArray("ars").length(); j++) {
                    storeAreasArray.add(stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getString("nh") + "," + stores.getJSONObject(i).getJSONArray("ars").getJSONObject(j).getJSONObject("dst").getString("nm"));
                }
                storeAreasLV.setAdapter(new ArrayAdapter<>(StoresActivity.this, android.R.layout.simple_list_item_checked, storeAreasArray));
            }
        } catch (JSONException e) {
            General.handleError("listviewAdapter, 777: ", e.toString());
        }
    }

    void setObjEvents() {
        storesSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
                if (isFinished) {
                    setStoreData(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
//        adrsGvsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
//                if (isFinished){
//                    try {
//                        adrsTnsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this,android.R.layout.simple_spinner_item, General.getTowns(General.governorates.getJSONObject(adrsGvsSp.getSelectedItemPosition()).getString("_id"))));
//                        adrsNhsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this,android.R.layout.simple_spinner_item,
//                                General.getNhs(General.governorates.getJSONObject(adrsGvsSp.getSelectedItemPosition()).getString("_id"),
//                                        adrsTnsSp.getSelectedItem().toString())));
//                    } catch (JSONException e) {
//                        General.handleError("Stores", e.toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//        });
//        adrsTnsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
//                if (isFinished){
//                    try {
//                        adrsNhsSp.setAdapter(new ArrayAdapter<>(StoresActivity.this,android.R.layout.simple_spinner_item, General.getNhs(General.governorates.getJSONObject(adrsGvsSp.getSelectedItemPosition()).getString("_id"), adrsTnsSp.getSelectedItem().toString())));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//        });

        storeAreasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setStoreArea(position);
            }
        });
        newStoreBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nowStore = new JSONObject();
                setStoreData(-1);
            }
        });
        addNhBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setStoreArea(-1);
            }
        });

        saveNewUserBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (userNameText.getText().toString().length() == 0) {
                    userNameText.setError("برجاء تسجيل اسم المخزن");
                    userNameText.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل اسمك", Toast.LENGTH_LONG);
                    return;
                }
                if (adrsAreaET.getText().toString().length() == 0) {
                    adrsAreaET.setError("برجاء تسجيل المنطقة !!!");
                    adrsAreaET.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل المنطقة !!!", Toast.LENGTH_LONG);
                    return;
                }
                if (adrsDtsET.getText().toString().length() == 0) {
                    adrsDtsET.setError("برجاء تسجيل العنوان");
                    adrsDtsET.requestFocus();
                    General.showToast(getApplicationContext(), "برجاء تسجيل العنوان", Toast.LENGTH_LONG);
                    return;
                }
                try {
                    nowStore.put("nm", userNameText.getText().toString().trim())
                            .put("tp", "store")
                            .put("adrs", new JSONArray().put(new JSONObject()
                                    .put("av", true)
                                    .put("nm", userNameText.getText().toString().trim())
                                    .put("ar", adrsAreaET.getText().toString().trim())
                                    .put("dt", adrsDtsET.getText().toString().trim())
                                    .put("co", "مصر")
                                    .put("gv", adrsGvsSp.getSelectedItem().toString())
                                    .put("tn", adrsTnsSp.getSelectedItem().toString())
                                    .put("nh", adrsNhsSp.getSelectedItem().toString())
                                    .put("id", General.getNhId(adrsNhsSp.getSelectedItem().toString().trim() + "," + adrsTnsSp.getSelectedItem().toString().trim() + "," + adrsGvsSp.getSelectedItem().toString().trim()))
                            ));
                    JSONObject results = General.postUrl(nowStore.has("_id") ? General.url + "stores/editStore" : General.url + "stores/newStore", nowStore);
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(StoresActivity.this, "هناك خطأ ما, راجع مديرك !!!");
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.showToast(StoresActivity.this, "تم التسجيل بنجاح");
                    }
                } catch (JSONException e) {
                    General.handleError("iiiiii", e.toString());
                }
            }
        });
    }
}
