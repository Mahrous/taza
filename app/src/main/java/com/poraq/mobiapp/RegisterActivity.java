package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class RegisterActivity extends AppCompatActivity {
    int pressedState;
    Button saveNewUserBtn, addAddressBtn;
    TextView showEditeInfo, showEditPassword;
    ListView addressesLV;
    JSONObject newUser;
    EditText passwordET, passwordAgainET;
    EditText mobileNoET, userNameET;//, areaNameET, addressDtETaddressNameET,
    ArrayList<String> addressesArray = new ArrayList<>();
    JSONObject adrs;
    LinearLayout l1, l2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pressedState = 0;
        newUser = General.user;

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        showEditeInfo = findViewById(R.id.edit_profile_info);
        saveNewUserBtn = findViewById(R.id.saveNewUserBtn);
        addAddressBtn = findViewById(R.id.addAddressBtn);
        mobileNoET = findViewById(R.id.mobileNoET);
        passwordET = findViewById(R.id.passwordET);
        passwordAgainET = findViewById(R.id.passwordAgainET);
        userNameET = findViewById(R.id.userNameET);
        addressesLV = findViewById(R.id.addressesLV);
        addressesLV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        showEditPassword = findViewById(R.id.edit_profile_password);

        showEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();

            }
        });
        if (General.user.length()>0) {
            passwordET.setHint("كلمة المرور للتأكيد");
            saveNewUserBtn.setText("تعديــل");
            l2.setVisibility(View.GONE);

            try {
                addressesArray = new ArrayList<>();
                for (int i = 0; i < newUser.getJSONArray("adrs").length(); i++) {
                    adrs = newUser.getJSONArray("adrs").getJSONObject(i);
                    Log.e("xxx", adrs.toString());
                    addressesArray.add(adrs.getString("nm") + ", " + adrs.getString("nh"));
                }

                addressesLV.setAdapter(new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, addressesArray));


            } catch (JSONException e) {
                e.printStackTrace();
            }


            saveNewUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeInfo();
                    //toChangeUserInfo
                }
            });


        } else if (General.user.length()==0) {
            showEditPassword.setVisibility(View.GONE);
            showEditeInfo.setVisibility(View.GONE);

            saveNewUserBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (mobileNoET.getText().toString().length() < 6) {
                        mobileNoET.setError("برجاء تسجيل رقم موبايل من 6 إلى 15 رقم !!!");
                        mobileNoET.requestFocus();
                        General.showToast(getApplicationContext(), "برجاء تسجيل رقم موبايل من 6 إلى 15 رقم !!!", Toast.LENGTH_LONG);
                        return;
                    }
                    // TODO validate user name
                    if (passwordET.getText().toString().length() < 6 || passwordET.getText().toString().length() > 24) {
                        passwordET.setError("برجاء تسجيل رمز دخول بين 6 إلى 24 حرف !!!");
                        passwordET.requestFocus();
                        General.showToast(getApplicationContext(), "برجاء تسجيل رمز دخول بين 6 إلى 24 حرف !!!", Toast.LENGTH_LONG);
                        return;
                    }
                    if (passwordAgainET.getText().toString().length() == 0) {
                        passwordAgainET.setError("برجاء إعادة تسجيل رمز الدخول");
                        passwordAgainET.requestFocus();
                        General.showToast(getApplicationContext(), "برجاء إعادة تسجيل رمز الدخول", Toast.LENGTH_LONG);
                        return;
                    }
                    if (!passwordET.getText().toString().matches(passwordAgainET.getText().toString())) {
                        passwordET.setError("كلمتى المرور غير متطابقتين, أعد إدخال كلمة المرور !!!");
                        passwordET.requestFocus();
                        General.showToast(getApplicationContext(), "كلمتى المرور غير متطابقتين, أعد إدخال كلمة المرور !!!", Toast.LENGTH_LONG);
                        return;
                    }
                    if (userNameET.getText().toString().length() == 0) {
                        userNameET.setError("برجاء تسجيل اسمك");
                        userNameET.requestFocus();
                        General.showToast(getApplicationContext(), "برجاء تسجيل اسمك", Toast.LENGTH_LONG);
                        return;
                    }
                    try {
                        if (newUser.getJSONArray("adrs").length() == 0) {
                            General.showToast(getApplicationContext(), "برجاء تسجيل العنوان !!!", Toast.LENGTH_LONG);

                            General.showToast(getApplicationContext(), "لايوجد عنوان, من فضلك أضف عنوان", Toast.LENGTH_LONG);
                            setAddress(-1);
                            return;
                        }

                        newUser.put("nm", userNameET.getText().toString().trim());
                        newUser.put("mo", mobileNoET.getText().toString());
                        newUser.put("pw", passwordET.getText().toString().trim());
                        newUser.put("mbls", new JSONArray());
                        newUser.put("mls", new JSONArray());
                        newUser.put("tp", "user");
                        JSONObject results = General.postUrl(General.user.length() == 0 ? General.url + "auth/signup" : General.url + "users/editInfo", newUser);
                        if (results.length() == 0 || !results.getBoolean("state")) {
                            if (results.getInt("st") ==403)
                            {
                                General.showToast(getApplicationContext(), "هذا الرقم مستخدم من قبل ", Toast.LENGTH_LONG);
                                Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));

                        }else {
                            General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                            Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));}
                        } else {
                            General.showToast(RegisterActivity.this, "تمت العملية بنجاح ...");
                            //   General.user = results.getJSONObject("user");
                            // General.userName = General.user.getString("nm");
                            General.setPreference(RegisterActivity.this, "mobile", mobileNoET.getText().toString().trim());
                            General.setPreference(RegisterActivity.this, "password", passwordET.getText().toString().trim());
                            General.isExceptionalLogin = true;
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        }
                    } catch (JSONException e) {
                        General.handleError("vvvvvv", e.toString());
                    }
                }

            });
        }

        try {
            if (General.user.length() > 0) {

                saveNewUserBtn.setText("تعديل");

                userNameET.setText(newUser.getString("nm"));
                mobileNoET.setText(newUser.getString("mo"));
            } else {
                newUser = new JSONObject().put("adrs", new JSONArray());
            }
            addressesArray = new ArrayList<>();
            for (int i = 0; i < newUser.getJSONArray("adrs").length(); i++) {
                adrs = newUser.getJSONArray("adrs").getJSONObject(i);
                addressesArray.add(adrs.getString("nm") + ", " + adrs.getString("nh"));
            }
            addressesLV.setAdapter(new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, addressesArray));
        } catch (JSONException e) {
            General.handleError("RegisterActivity, 55555", e.toString());
        }
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAddress(-1);
            }
        });

        addressesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setAddress(i);
            }
        });
    }

    void setAddress(final Integer index) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_address, null, false);
        final EditText addNameET = view.findViewById(R.id.add_adres_complete_address);
        final EditText addDtsET = view.findViewById(R.id.add_adres_details);
        final EditText addAreaET = view.findViewById(R.id.add_adres_area);
        final Spinner nhsSp = view.findViewById(R.id.add_adress_elhai);
        final Spinner townsSp = view.findViewById(R.id.add_adres_city);
        final Spinner gvsSp = view.findViewById(R.id.add_adres_country);
        Button ok = view.findViewById(R.id.add_adres);
        Button cancel = view.findViewById(R.id.cancel_adres);

        nhsSp.setAdapter(new ArrayAdapter<>(RegisterActivity.this, R.layout.single_row_myorder_spin, General.avNhsArray));
        townsSp.setAdapter(new ArrayAdapter<>(RegisterActivity.this, R.layout.single_row_myorder_spin, General.townsArray));
        gvsSp.setAdapter(new ArrayAdapter<>(RegisterActivity.this, R.layout.single_row_myorder_spin, General.gvsArray));
        try {
            if (index > -1) {
                addNameET.setText(newUser.getJSONArray("adrs").getJSONObject(index).getString("nm"));
                addDtsET.setText(newUser.getJSONArray("adrs").getJSONObject(index).getString("dt"));
                addAreaET.setText(newUser.getJSONArray("adrs").getJSONObject(index).getString("ar"));

                gvsSp.setSelection(((ArrayAdapter<String>) gvsSp.getAdapter()).getPosition(newUser.getJSONArray("adrs").getJSONObject(index).getString("gv")));
                townsSp.setSelection(((ArrayAdapter<String>) townsSp.getAdapter()).getPosition(newUser.getJSONArray("adrs").getJSONObject(index).getString("tn")));
                nhsSp.setSelection(((ArrayAdapter<String>) nhsSp.getAdapter()).getPosition(newUser.getJSONArray("adrs").getJSONObject(index).getString("nh")));
            }
        } catch (JSONException e) {
            General.handleError("Register000", e.toString());
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                .setView(view)
                .show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addNameET.getText().toString().trim().matches("")) {
                    addNameET.requestFocus();
                    addNameET.setError("برجاء تسجيل اسم العنوان !!!");
                    General.showToast(RegisterActivity.this, "برجاء تسجيل اسم العنوان !!!");
                    return;
                }

                if (addDtsET.getText().toString().trim().matches("")) {
                    addDtsET.requestFocus();
                    addDtsET.setError("برجاء تسجيل تفاصيل العنوان !!!");
                    General.showToast(RegisterActivity.this, "برجاء تسجيل تفاصيل العنوان !!!");
                    return;
                }
                if (addAreaET.getText().toString().trim().matches("")) {
                    addAreaET.requestFocus();
                    addAreaET.setError("برجاء تسجيل المنطقة !!!");
                    General.showToast(RegisterActivity.this, "برجاء تسجيل المنطقة !!!");
                    return;
                }
                try {
                    if (!newUser.has("adrs")) {
                        newUser.put("adrs", new JSONArray());
                        Log.d("hhhhh", "onClick: " + newUser.get("adrs"));
                    }
                    if (index == -1) {

                        Log.e("oooo", General.neighborhoods
                                .getJSONObject(General.nhsArray.indexOf(nhsSp.getSelectedItem().toString()))
                                .getString("_id"));

                        newUser.getJSONArray("adrs")
                                .put(new JSONObject().put("av", true)
                                .put("id",
                                        General.neighborhoods.getJSONObject(
                                                General.nhsArray.indexOf(nhsSp.getSelectedItem().toString())
                                        ).getString("_id")
                                )
                                .put("nm", addNameET.getText().toString().trim())
                                .put("co", "مصر")
                                .put("gv", gvsSp.getSelectedItem())
                                .put("tn", townsSp.getSelectedItem())
                                .put("nh", nhsSp.getSelectedItem().toString())
                                .put("ar", addAreaET.getText().toString().trim())
                                .put("dt", addDtsET.getText().toString().trim()));
                    } else {
                        Log.e("wwww", General.neighborhoods.getJSONObject(General.nhsArray.indexOf(nhsSp.getSelectedItem().toString())).getString("_id"));
                        newUser.getJSONArray("adrs").put(index, new JSONObject().put("av", true)
                                .put("id", General.neighborhoods.getJSONObject(General.nhsArray.indexOf(nhsSp.getSelectedItem().toString())).getString("_id"))
                                .put("nm", addNameET.getText().toString().trim())
                                .put("co", "مصر")
                                .put("gv", gvsSp.getSelectedItem())
                                .put("tn", townsSp.getSelectedItem())
                                .put("nh", nhsSp.getSelectedItem().toString())
                                .put("ar", addAreaET.getText().toString().trim())
                                .put("dt", addDtsET.getText().toString().trim()));
                    }

                    addressesArray = new ArrayList<>();
                    for (int i = 0; i < newUser.getJSONArray("adrs").length(); i++) {
                        adrs = newUser.getJSONArray("adrs").getJSONObject(i);
                        Log.e("xxx", adrs.toString());
                        addressesArray.add(adrs.getString("nm") + ", " + adrs.getString("nh"));
                    }
                    addressesLV.setAdapter(new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, addressesArray));
                } catch (JSONException e) {
                    General.handleError("003", e.toString());
                }
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
    }

    void changePassword() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.change_password, null, false);

        final EditText oldPassword = view.findViewById(R.id.change_old_password);
        final EditText newPassword = view.findViewById(R.id.change_new_password);
        final EditText newPasswordAgain = view.findViewById(R.id.change_new_password_again);

        Button submitChand = view.findViewById(R.id.change_password_bt);
        Button cancelChand = view.findViewById(R.id.cancel_change);

        final AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                .setView(view).show();
        alertDialog.setCanceledOnTouchOutside(false);


        submitChand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (oldPassword.getText().toString().length() == 0) {
                    oldPassword.setError("برجاء تسجيل كلمة المرور");
                    oldPassword.requestFocus();
                    return;
                }
                if (newPassword.getText().toString().length() == 0) {
                    newPassword.setError("برجاء تسجيل كلمة المرور الجديدة");
                    newPassword.requestFocus();
                    return;
                }
                if (newPasswordAgain.getText().toString().length() == 0) {
                    newPasswordAgain.setError("برجاء إعادة تسجيل كلمة المرور الجديدة");
                    newPasswordAgain.requestFocus();
                    return;
                }
                if (!newPassword.getText().toString().matches(newPasswordAgain.getText().toString())) {
                    newPassword.setError("كلمتى المرور غير متطابقتين, أعد إدخال كلمة المرور !!!");
                    newPassword.requestFocus();
                    return;
                }

                JSONObject results = null;
                try {
                    results = General.postUrl(General.url + "users/editPass", new JSONObject().put("pw", oldPassword.getText().toString().trim()).put("nPw", newPassword.getText().toString().trim()).put("_id", General.user.getString("_id")));
                    if (results.length() > 0 && results.getInt("st") < 210) {
                        Toast.makeText(RegisterActivity.this, "تم تغيير الباسورد بنجاح", Toast.LENGTH_SHORT).show();
                        General.setPreference(RegisterActivity.this, "password", newPassword.getText().toString().trim());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });

        cancelChand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    void changeInfo() {
        try {
            if (newUser.getJSONArray("adrs").length() == 0) {
                General.showToast(getApplicationContext(), "برجاء تسجيل رقم الموبايل", Toast.LENGTH_LONG);
                setAddress(-1);
                return;
            }
            if (userNameET.getText().toString().length() == 0) {
                userNameET.setError("برجاء تسجيل اسم المستخدم");
                userNameET.requestFocus();
                return;
            }
            if (mobileNoET.getText().toString().length() == 0) {
                mobileNoET.setError("برجاء تسجيل رقم التليفون");
                mobileNoET.requestFocus();
                return;
            }
            if (passwordET.getText().toString().length() == 0) {
                passwordET.setError("برجاء تسجيل كلمة المرور");
                passwordET.requestFocus();
                return;
            }
            JSONObject results = General.postUrl(General.url + "users/editInfo", new JSONObject()
                    .put("nm", userNameET.getText().toString().trim())
                    .put("mo", mobileNoET.getText().toString().trim())
                    .put("pw", passwordET.getText().toString().trim())
                    .put("adrs", newUser.getJSONArray("adrs"))
                    .put("_id", General.user.getString("_id")));


            if (results.length() > 0 && results.getInt("st") < 210) {
                Toast.makeText(RegisterActivity.this, "تم تغيير التفاصيل", Toast.LENGTH_SHORT).show();
                General.isExceptionalLogin = true;
                pressedState = 1;
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
                General.setPreference(RegisterActivity.this, "mobile", mobileNoET.getText().toString().trim());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
