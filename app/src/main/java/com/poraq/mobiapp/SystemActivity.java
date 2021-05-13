package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SystemActivity extends AppCompatActivity {
    EditText vNoET, vNameET, minUsersVersionET, minEmpsVersionET, orderDelayET, orderLastHourET, roleET, customerServiceNoET;
    ListView rolesLV;
    Button addRoleBtn, saveRolesDataBtn, saveVersionDataBtn, saveOrdersDataBtn, saveCustomerServiceNoBtn;
    ArrayList<String> systemRolesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        setTitle(", ضبط النظام");
        vNoET = findViewById(R.id.vNoET);
        vNameET = findViewById(R.id.vNameET);
        minUsersVersionET = findViewById(R.id.minUsersVersionET);
        minEmpsVersionET = findViewById(R.id.minEmpsVersionET);
        orderDelayET = findViewById(R.id.orderDelayET);
        orderLastHourET = findViewById(R.id.orderLastHourET);
        customerServiceNoET = findViewById(R.id.customerServiceNoET);
        roleET = findViewById(R.id.roleET);
        rolesLV = findViewById(R.id.rolesLV);
        addRoleBtn = findViewById(R.id.addRoleBtn);
        saveRolesDataBtn = findViewById(R.id.saveRolesDataBtn);
        saveVersionDataBtn = findViewById(R.id.saveVersionDataBtn);
        saveOrdersDataBtn = findViewById(R.id.saveOrdersDataBtn);
        saveCustomerServiceNoBtn = findViewById(R.id.saveCustomerServiceNoBtn);
        try {
            Log.e("sssssss", General.system.toString());
            vNoET.setText(General.system.getJSONObject("version").getString("id"));
            vNameET.setText(General.system.getJSONObject("version").getString("nm"));
            minUsersVersionET.setText(General.system.getJSONObject("version").getString("minUsersVersion"));
            minEmpsVersionET.setText(General.system.getJSONObject("version").getString("minEmpsVersion"));
            orderDelayET.setText(General.system.getJSONObject("orders").getString("delay"));
            orderLastHourET.setText(General.system.getJSONObject("orders").getString("lastHour"));
            customerServiceNoET.setText(General.system.getJSONObject("tells").getString("cS"));
            Log.e("hhh", General.system.getJSONArray("roles").toString());
            for (int i = 0; i < General.system.getJSONArray("roles").length(); i++) {
                systemRolesArray.add(General.system.getJSONArray("roles").getString(i));
            }
            rolesLV.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, systemRolesArray));
        } catch (JSONException e) {
            General.handleError("system", e.toString());
        }
        addRoleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (roleET.getText().toString().trim().length() == 0){
                General.showToast(SystemActivity.this, "ادخل اسم الصلاحية !!!");
                roleET.setError("حقل فارغ !!!");
                roleET.requestFocus();
                roleET.selectAll();
                return;
            }
            if (systemRolesArray.indexOf(roleET.getText().toString().trim()) > -1){
                General.showToast(SystemActivity.this, "بيان مكرر !!!");
                roleET.setError("بيان مكرر !!!");
                roleET.requestFocus();
                roleET.selectAll();
                return;
            }
            systemRolesArray.add(roleET.getText().toString().trim());
            rolesLV.setAdapter(new ArrayAdapter<>(SystemActivity.this, android.R.layout.simple_list_item_1, systemRolesArray));
            roleET.requestFocus();
            roleET.selectAll();
            }
        });
        saveVersionDataBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (vNoET.getText().toString().trim().length() == 0){
                vNoET.setError("أدخل رقم النسخة !!!");
                vNoET.requestFocus();
                return;
            }
            if (vNameET.getText().toString().trim().length() == 0){
                vNameET.setError("أدخل اسم النسخة !!!");
                vNameET.requestFocus();
                return;
            }
            if (minUsersVersionET.getText().toString().trim().length() == 0){
                minUsersVersionET.setError("أدخل رقم نسخة المستخدمين !!!");
                minUsersVersionET.requestFocus();
                return;
            }
            if (minEmpsVersionET.getText().toString().trim().length() == 0){
                minEmpsVersionET.setError("أدخل رقم نسخة الموظفين !!!");
                minEmpsVersionET.requestFocus();
                return;
            }
            try {
                JSONObject results = General.postUrl(General.url+"system/editVersion", new JSONObject().put("version", new JSONObject()
                        .put("id", vNoET.getText().toString().trim())
                        .put("nm", vNameET.getText().toString().trim())
                        .put("minUsersVersion", minUsersVersionET.getText().toString().trim())
                        .put("minEmpsVersion", minEmpsVersionET.getText().toString().trim())
                ));
//                Log.e("ggggg", op);
//                JSONObject results = new JSONObject(op);
                General.system = results.getJSONObject("system");
                if (results.getString("st").matches("ok")){
                    General.showToast( SystemActivity.this, "تم إرسال الطلب بنجاح ...");
                }else if (results.getString("st").matches("no")){
                    General.showToast(SystemActivity.this,"Incorrect data: ", Toast.LENGTH_LONG);
                }
            } catch (JSONException e) {
                General.handleError("system", e.toString());
            }
            }
        });
        saveOrdersDataBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (orderDelayET.getText().toString().trim().length() == 0){
                orderDelayET.setError("أدخل تأخير الطلب !!!");
                orderDelayET.requestFocus();
                return;
            }
            if (orderLastHourET.getText().toString().trim().length() == 0){
                orderLastHourET.setError("أدخل ساعة اخر طلب لليوم !!!");
                orderLastHourET.requestFocus();
                return;
            }
            try {
//                    General.system.getJSONArray("rls").put(roleET.getText().toString());
                JSONObject results = General.postUrl(General.url+"system/editOrders", new JSONObject().put("orders", new JSONObject()
                        .put("delay", orderDelayET.getText().toString().trim())
                        .put("lastHour", orderLastHourET.getText().toString().trim())
                ));
//                Log.e("ggggg", op);
//                JSONObject results = new JSONObject(op);
                General.system = results.getJSONObject("system");
                if (results.getString("st").matches("ok")){
                    General.showToast( SystemActivity.this, "تم إرسال الطلب بنجاح ...");
                }else if (results.getString("st").matches("no")){
                    General.showToast(SystemActivity.this,"Incorrect data: ", Toast.LENGTH_LONG);
                }
            } catch (JSONException e) {
                General.handleError("system", e.toString());
            }
            }
        });
        saveCustomerServiceNoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (customerServiceNoET.getText().toString().trim().length() == 0){
                customerServiceNoET.setError("أدخل رقم خدمة العملاء !!!");
                customerServiceNoET.requestFocus();
                return;
            }
            try {
                JSONObject results = General.postUrl(General.url+"system/editTells", new JSONObject()
                        .put("tells", new JSONObject().put("cS", customerServiceNoET.getText().toString())
                ));
//                Log.e("ggggg", op);
//                JSONObject results = new JSONObject(op);
                General.system = results.getJSONObject("system");
                if (results.getString("st").matches("ok")){
                    General.showToast( SystemActivity.this, "تم إرسال الطلب بنجاح ...");
                }else if (results.getString("st").matches("no")){
                    General.showToast(SystemActivity.this,"Incorrect data: ", Toast.LENGTH_LONG);
                }
            } catch (JSONException e) {
                General.handleError("system", e.toString());
            }
            }
        });

        saveRolesDataBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    JSONArray roles = new JSONArray();
                    for (int i = 0; i <systemRolesArray.size() ; i++) {
                        roles.put(systemRolesArray.get(i));
                    }
                    JSONObject results = General.postUrl(General.url+"system/editRoles", new JSONObject().put("roles", roles));
//                    Log.e("ggggg", op);
//                    JSONObject results = new JSONObject(op);
                    General.system = results.getJSONObject("system");
                    if (results.getString("st").matches("ok")){
                        General.showToast( SystemActivity.this, "تم إرسال الطلب بنجاح ...");
                    }else if (results.getString("st").matches("no")){
                        General.showToast(SystemActivity.this,"Incorrect data: ", Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    General.handleError("system", e.toString());
                }
            }
        });


    }
}
