package com.poraq.mobiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.text.InputType;

import android.text.method.PasswordTransformationMethod;
import android.util.Base64;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.webkit.MimeTypeMap;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.app.AlertDialog;


import com.google.android.material.navigation.NavigationView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //إضافة عنوان لا تعمل

    //getsystem
    // handle server off
    // close nav when log out
    //telesalesuser store, address
    // total item
    // 2 edittext for pur
    private static final int PERMISSION_REQUEST_CODE = 100;

    GridLayoutAnimationController controller;
    static String imageName;
    static double b;
    private final int PIEK_REQEST_CODE = 1;
    Uri mImageUri;
    AlertDialog hi;
    View noUserAddress;
    JSONObject newUser;
    ArrayList<String> catsArray;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    Boolean isNewOrder = false, isFinished = false;
    GridView itemsLV;//, catsLV
    MaterialSearchView searchView;
    TextView navUsername;
    Spinner gvsSp, townsSp, nhsSp;//myAddressesSp,catsSp,
    String password, mobile;
    Double ttO = 0.0, ttS = 0.0, ttR = 0.0, ttA = 0.0, ttPrft = 0.0;//, ttN = 0.0
    listviewAdapter adapter;
    JSONObject newAddress = new JSONObject(), callUser = new JSONObject(), callUserStore = new JSONObject(), callUserAddress = new JSONObject();
    JSONArray addresses = new JSONArray(), callList = new JSONArray();
    ArrayList<String> callListArray = new ArrayList<>(), callUserAddressArray = new ArrayList<>();
    RecyclerView recyclerView;
    JSONObject nowStore;
    String[] fromHours = new String[]{"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"}, toHours = new String[]{"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    MainAdapter mainAdapter;
    private Bitmap bitmap;
    public static String addressOrder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        General.context = MainActivity.this;
        newUser = General.user;
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_main);
        swipeRefreshLayout = findViewById(R.id.itemsRefresh);
        searchView = findViewById(R.id.searchItemsTextt);


        DrawerLayout drawer = findViewById(R.id.mem);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.userTV);
        navUsername.setText("تسجيل الدخول");


        navUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navUsername.getText().toString() == "تسجيل الدخول") {
                    login(true);
                } else {
                    DrawerLayout drawer = findViewById(R.id.mem);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            }
        });

        Menu menu = navigationView.getMenu();
        General.myOrdersMI = menu.findItem(R.id.myOrdersSI);
        General.registerMI = menu.findItem(R.id.signUpSubItem);
        General.logOutMI = menu.findItem(R.id.logoutSubItem);
        General.careersMI = menu.findItem(R.id.careersSubItem);
        General.storesMI = menu.findItem(R.id.storesSubItem);
        General.geoMI = menu.findItem(R.id.geoSubItem);
        General.itemsMI = menu.findItem(R.id.itemsSubItem);
        General.systemMI = menu.findItem(R.id.systemSubItem);
        General.recruitMI = menu.findItem(R.id.recruitSubItem);
        General.purchaseMI = menu.findItem(R.id.purchasesSubItem);
        General.transMI = menu.findItem(R.id.transesSubItem);
        General.salesMI = menu.findItem(R.id.salesSubItem);
        General.movesMI = menu.findItem(R.id.transferSubItem);
        itemsLV = findViewById(R.id.itemsLV);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.grid);

        controller = new GridLayoutAnimationController(anim);
        itemsLV.setLayoutAnimation(controller);

//        SharedPreferences sharedPrefs = getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.clear();
//        editor.commit();
        mobile = General.getPreference(this, "mobile");
        password = General.getPreference(this, "password");
        if (savedInstanceState == null) {
            if (General.geos.length() == 0) {
                getGeoData();
            }
            if (General.itemsJson.length() == 0) {
                if (getItems()) {
                    populateItems();
                }
            } else {
                populateItems();
            }
            if (General.user.length() == 0 || General.isExceptionalLogin) {
                login(false);
            }
        }
        if (General.user.length() > 0) {
            populateUser();
        }
        if (General.system.length() == 0) {
            getSystem();
        }
        if (General.nowOp.split(",")[0].matches("sO") && General.nowOp.split(",")[1].matches("app")) {
            Log.e("sssssss", "wwwwwwww");
            try {
                Log.e("vvvvv", General.postUrl(General.url + "users/getCallList", new JSONObject()).toString());
                callList = General.postUrl(General.url + "users/getCallList", new JSONObject()).getJSONArray("list");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setObjEvents();
        isFinished = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.search);
        searchView.setMenuItem(item);

        MenuItem item2 = menu.findItem(R.id.sendOrder2);

        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (General.myOrderDetails.length() == 0) {
                    General.showToast(MainActivity.this, "قم بتحديد كميات الأصناف المراد شراءها أولا. !!!");
                    return true;
                }
                if (General.user.length() == 0) {
                    General.showToast(MainActivity.this, "قم بتسجيل الدخول أولا !!!");
                    login(true);
                    return true;
                }
                if (General.nowOp.matches("sO,user") && General.myStore.length() == 0) {

                    General.showToast(MainActivity.this, "عفوا لا يوجد مخازن تدعم منطقتك الان, حاول فى وقت لاحق !!!");
                    return true;
                }
                switch (General.nowOp.split(",")[0]) {
                    case "pO":
                        setPurchaseOrder();
                        break;
                    case "tO":
                        setTransOrder();
                        break;
                    case "sO":
                        setSalesOrder();
                        break;
                }
                return true;
            }
        });
        MenuItem item3 = menu.findItem(R.id.cancelOrderBtn2);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                clearOrder();
                return true;
            }
        });
        return true;
    }

    public void checkIsNewInv() {
        if (General.nowOp.split(",")[0].matches("sO") && General.nowOp.split(",")[1].matches("user")) {
            if (General.user.length() > 0) {
                if (addresses.length() == 1) {
                    try {
                        General.userAddress = addresses.getJSONObject(0);
                        for (int i = 0; i < General.appStores.length(); i++) {
                            nowStore = General.appStores.getJSONObject(i);
                            for (int s = 0; s < nowStore.getJSONArray("ars").length(); s++) {
                                if (General.userAddress.getString("id").matches
                                        (nowStore.getJSONArray("ars").getJSONObject(s)
                                                .getJSONObject("id").getString("_id"))) {
                                    General.myStore = General.appStores.getJSONObject(i);
                                    General.aavStores.put(nowStore);
                                    General.orderDist = nowStore.getJSONArray("ars").getJSONObject(s).getJSONObject("dst");
                                    General.deliveryFees = nowStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getDouble("dFs");
//                                    s = General.myStore.getJSONArray("ars").length();
//                                    i = General.appStores.length();
                                }
                            }
                        }
                        isNewOrder = true;
                    } catch (JSONException e) {
                        General.handleError("003", e.toString());
                    }
                } else {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.my_address, null, false);
                    final Spinner userAddressesSp = view.findViewById(R.id.my_address);
                    Button choose = view.findViewById(R.id.chose_adres_bt);
                    Button addUserAdress = view.findViewById(R.id.user_add_new_adres_bt);
                    ArrayList<String> addressesArray = new ArrayList<>();
                    try {
                        for (int i = 0; i < addresses.length(); i++) {
                            General.userAddress = addresses.getJSONObject(i);
                            addressesArray.add(General.userAddress.getString("nm") + ", " + General.userAddress.getString("nh"));
                        }
                        userAddressesSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, addressesArray));
                    } catch (JSONException e) {
                        General.handleError("dddddd", e.toString());
                    }
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setView(view)
                            .show();
                    choose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                General.userAddress = addresses.getJSONObject(userAddressesSp.getSelectedItemPosition());

                                for (int i = 0; i < General.appStores.length(); i++) {
                                    nowStore = General.appStores.getJSONObject(i);
                                    for (int s = 0; s < nowStore.getJSONArray("ars").length(); s++) {
                                        if (General.userAddress.getString("id").matches(nowStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getString("_id"))) {
                                            General.myStore = General.appStores.getJSONObject(i);
                                            General.aavStores.put(nowStore);
                                            General.orderDist = nowStore.getJSONArray("ars").getJSONObject(s).getJSONObject("dst");
                                            General.deliveryFees = General.myStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getDouble("dFs");
                                            s = General.myStore.getJSONArray("ars").length();
                                            i = General.appStores.length();
                                        }
                                    }
                                }
                                isNewOrder = true;
                            } catch (JSONException e) {
                                General.handleError("003", e.toString());
                            }
                            alertDialog.dismiss();
                        }
                    });

                    addUserAdress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            setUserNewAddress();
                        }
                    });
                }
            } else {
                setNewUserAddress();
                // isNewOrder = true;
            }
        } else if (General.nowOp.split(",")[0].matches("sO") && General.nowOp.split(",")[1].matches("app")) {
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout layout2 = new LinearLayout(MainActivity.this);
            layout2.setOrientation(LinearLayout.HORIZONTAL);

            final Spinner callUserSp = new Spinner(MainActivity.this);
            final Spinner callUserAddressSp = new Spinner(MainActivity.this);
            final EditText mobileNo = new EditText(MainActivity.this);
            mobileNo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            final Button callUserBtn = new Button(MainActivity.this);
            callUserBtn.setText("إتصال");
            final Button searchUserBtn = new Button(MainActivity.this);
            searchUserBtn.setText("بحث");
            callUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        try {
                            callIntent.setData(Uri.parse("tel:" + callUser.getString("mo")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(callIntent);
                    } else {
                        Toast.makeText(MainActivity.this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
//                                ActivityCompat.requestPermissions((TransferActivity)context, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                    }

                }
            });
            searchUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject results = General.postUrl(General.url + "users/findCallUser", new JSONObject().put("userId", General.user.getString("_id")).put("mo", mobileNo.getText().toString().trim()));
                        if (results.length() == 0 || !results.getBoolean("state")) {
                            General.showToast(MainActivity.this, "هناك خطأ ما, راجع مديرك !!!");
                            Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                        } else {
                            General.showToast(MainActivity.this, "تم إرسال الطلب بنجاح ...");
                            if (results.getJSONObject("user").length() > 0) {
                                callList = General.creatJArraay(callList, results.getJSONObject("user"));
                                callUser = callList.getJSONObject(0);
                                callListArray.add(0, callUser.getString("nm"));
                                for (int i = 0; i < callUser.getJSONArray("adrs").length(); i++) {
                                    callUserAddress = callUser.getJSONArray("adrs").getJSONObject(i);
                                    callUserAddressArray.add(callUserAddress.getString("nm") + ", " + callUserAddress.getString("nh"));
                                }
                                mobileNo.setText(callUser.getString("mo"));
                                callUserSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, callListArray));
                                callUserAddressSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, callUserAddressArray));
                            }

                        }

                    } catch (JSONException e) {
                        General.handleError("dddddd", e.toString());
                    }

                }
            });
            callUserSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
                    try {
                        callUser = callList.getJSONObject(callUserSp.getSelectedItemPosition());
                        callUserAddressArray = new ArrayList<>();
                        for (int j = 0; j < callUser.getJSONArray("adrs").length(); j++) {
                            callUserAddress = callUser.getJSONArray("adrs").getJSONObject(j);
                            callUserAddressArray.add(callUserAddress.getString("nm") + ", " + callUserAddress.getString("nh"));
                        }
                        callUserAddressSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, callUserAddressArray));
                        mobileNo.setText(callUser.getString("mo"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
            layout2.addView(searchUserBtn);
            layout2.addView(mobileNo);
            layout.addView(layout2);
            layout.addView(callUserSp);
            layout.addView(callUserAddressSp);
            layout.addView(callUserBtn);
            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(layout)
                    .setTitle("اختر عميلا للاتصال عليه ...")
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setNeutralButton(android.R.string.unknownName, null)
                    .show();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("تنفيذ");
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
            alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setText("إلغاء");
            alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
            alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextSize(18);
            alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setText("إضافة عنوان");
            alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTypeface(null, Typeface.BOLD);
            alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTextSize(18);

            callListArray = new ArrayList<>();
            callUserAddressArray = new ArrayList<>();
            try {
                for (int i = 0; i < callList.length(); i++) {
                    callListArray.add(callList.getJSONObject(i).getString("nm"));
                }
                callUser = callList.getJSONObject(0);
                for (int i = 0; i < callUser.getJSONArray("adrs").length(); i++) {
                    callUserAddress = callUser.getJSONArray("adrs").getJSONObject(i);
                    callUserAddressArray.add(callUserAddress.getString("nm") + ", " + callUserAddress.getString("nh"));
                }
                mobileNo.setText(callUser.getString("mo"));
                callUserSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, callListArray));
                callUserAddressSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, callUserAddressArray));
            } catch (JSONException e) {
                General.handleError("dddddd", e.toString());
            }
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        callUser = callList.getJSONObject(callUserSp.getSelectedItemPosition());
                        callUserAddress = callUser.getJSONArray("adrs").getJSONObject(callUserAddressSp.getSelectedItemPosition());
                        General.userAddress = addresses.getJSONObject(callUserSp.getSelectedItemPosition());
                        for (int i = 0; i < General.appStores.length(); i++) {
                            callUserStore = General.appStores.getJSONObject(i);
                            for (int s = 0; s < callUserStore.getJSONArray("ars").length(); s++) {
                                if (callUserAddress.getString("id").matches(callUserStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getString("_id"))) {
                                    General.orderDist = callUserStore.getJSONArray("ars").getJSONObject(s).getJSONObject("dst");
                                    General.deliveryFees = callUserStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getDouble("dFs");
                                    s = callUserStore.getJSONArray("ars").length();
                                    i = General.appStores.length();
                                }
                            }
                        }
                        isNewOrder = true;
                    } catch (JSONException e) {
                        General.handleError("0gggg03", e.toString());
                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUserNewAddress();
                }
            });
        }
    }

    void setUserNewAddress() {
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

        nhsSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, General.nhsArray));
        townsSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, General.townsArray));
        gvsSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, General.gvsArray));

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(view)
                .show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addNameET.getText().toString().trim().length() < 1) {
                    addNameET.requestFocus();
                    General.showToast(MainActivity.this, "سجل اسم العنوان, مثل عمل أو منزل أو ... !!!");
                    addNameET.setError("سجل اسم العنوان, مثل عمل أو منزل أو ... !!!");
                    return;
                }
                if (addAreaET.getText().toString().trim().length() < 1) {
                    addAreaET.requestFocus();
                    General.showToast(MainActivity.this, "سجل المنطقة و هى جزء من الحى !!!");
                    addAreaET.setError("سجل المنطقة و هى جزء من الحى !!!");
                    return;
                }
                if (addDtsET.getText().toString().trim().length() < 1) {
                    addDtsET.requestFocus();
                    General.showToast(MainActivity.this, "سجل تفاصيل العنوان !!!");
                    addDtsET.setError("سجل تفاصيل العنوان !!!");
                    return;
                }
                try {
                    newAddress = new JSONObject()
                            .put("userId", General.user.getString("_id"))
//                            .put("av", true)
                            .put("id", General.neighborhoods.getJSONObject(General.nhsArray.indexOf(nhsSp.getSelectedItem().toString())).getString("_id"))
                            .put("nm", addNameET.getText().toString().trim())
                            .put("co", "مصر")
                            .put("gv", gvsSp.getSelectedItem())
                            .put("tn", townsSp.getSelectedItem())
                            .put("nh", nhsSp.getSelectedItem().toString())
                            .put("ar", addAreaET.getText().toString().trim())
                            .put("dt", addDtsET.getText().toString().trim());
                    Log.e("eeeee", newAddress.toString());
                    alertDialog.dismiss();
                    JSONObject results = General.postUrl(General.url + "users/addAddress", newAddress);
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.showToast(getApplicationContext(), "saved ... ", Toast.LENGTH_LONG);
                        General.user.getJSONArray("adrs").put(newAddress.remove("userId"));
//                        General.user = results.getJSONObject("user");
                        General.userAddress = newAddress;

                    }

                } catch (JSONException e) {
                    General.handleError("003", e.toString());
                }
                isNewOrder = true;

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

    public void calcTotals(JSONArray list) {
        General.myOrderDetails = new JSONArray();
        Double tO, tS, tA, tPrft;//, tOR, tSR, tN   ; tOR =0.0; tSR =0.0; tN = 0.0
        ttA = 0.0;
        ttO = 0.0;
        JSONObject item;
        for (int i = 0; i < list.length(); i++) {

            tS = 0.0;
            tA = 0.0;

            //    Log.e("itt", "itrate: "+ i );
            tPrft = 0.0;
            try {
                item = list.getJSONObject(i);
                if (item.get("oQ").toString().trim().length() > 0 && item.getDouble("oQ") > 0 &&
                        item.get("p").toString().trim().length() > 0 &&
                        item.getDouble("p") > 0) {
                    tO = (item.getDouble("oQ") * item.getDouble("p"));
                    tA = (item.getDouble("oQ") * item.getDouble("pA"));
                    tS = tO;


                    General.myOrderDetails.put(new JSONObject()
                            .put("itm", item.getString("_id"))
                            .put("nm", item.getString("nm"))
                            .put("unt", item.getString("u"))
                            .put("cn", item.getString("cn"))
                            .put("nts", item.getString("nts"))
                            .put("p", item.getDouble("p")) // price
                            .put("pA", item.getDouble("pA")) // price average
                            .put("p0", item.getDouble("p0")) // التسعيره الجبرية
                            .put("oQ", item.getDouble("oQ")) //order quantity
                            .put("sQ", item.getDouble("sQ")) // supplied quantity
                            .put("tO", tO) // اجمالي المطلوب
                            .put("tS", tS) // اجمال المورد
                            .put("tA", tA) // اجمالي بالتكلفه
                            .put("tPrft", tPrft) // اجمالي الربح
                    );

                    ttO += tO;
                    ttS = tO;
                    ttA += tA;

                }

            } catch (JSONException e) {
                General.handleError("mjh: : ", e.toString());
            }
        }


        mainAdapter.notifyDataSetChanged();

    }

    void startCompany() {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText adminPasswordET = new EditText(MainActivity.this);
        adminPasswordET.setHint("أدخل كلمة المرور الخاصة بك !!!");
        layout.addView(adminPasswordET);

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(layout)
                .setTitle("أدخل كلمة المرور الخاصة بك !!!")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (adminPasswordET.getText().toString().trim().length() > 0 && adminPasswordET.getText().toString().trim().matches("123")) {
                    General.showToast(MainActivity.this, "أنت الان مدير النظام, ابدأ بتسجيل بيانات التطبيق ...");
                    alertDialog.dismiss();
                    General.waitMe(MainActivity.this, "جارى تحميل البيانات");
                    try {
                        General.postUrl(General.url + "system/start", new JSONObject().put("mo", mobile).put("pw", password));
                        General.IamReady();
                        General.showToast(MainActivity.this, "تمت العملية بنجاح, و سيقوم النظام بالإغلاق و الفتح مجددا ...");
                        General.restartApp(MainActivity.this);
                    } catch (JSONException e) {
                        General.handleError("MainActivity, 0087: ", e.toString());
                    }
                }
            }
        });
    }

//    void getUserData() {
//        mobile = General.getPreference(this, "mobile");
//        password = General.getPreference(this, "password");
//        try {
//            JSONObject results = General.postUrl(General.url + "users/findUser", new JSONObject().put("mo", mobile).put("pw", password));
//            if (results.getBoolean("state")) {
//                General.system = results.getJSONObject("system");
//                if (General.system.length() == 0) {
//                    startCompany();
//                }
//                if (General.system.length() > 0 && General.system.getInt("oVersion") > General.versionNo) {
//                    General.waitMe(MainActivity.this, "نسخة غير محدثة, لا يمكن العمل بها الان !!!");
//                }
//                setData(results);
//            } else if (!results.getBoolean("state")) {
//
//                General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
//            }
//        } catch (JSONException e) {
//            General.handleError("MainActivity, 0087: ", e.toString());
//        }
//    }

    void getSystem() {
        try {
            JSONObject results = General.postUrl(General.url + "getSysInfo", new JSONObject());
            if (results.length() == 0 || !results.getBoolean("state")) {
//                General.showToast(MainActivity.this, "failed !!!");
                Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
            } else {
                General.system = results.getJSONObject("system");
                Log.e("sssssss", General.system.toString());
//                if (General.system.length() > 0 && General.system.getInt("oVersion") > General.versionNo) {
//                    General.waitMe(MainActivity.this, "نسخة غير محدثة, لا يمكن العمل بها الان !!!");
//                }
            }
        } catch (JSONException e) {
            General.handleError("MainActivity, 0087: ", e.toString());
        }
    }

    void getStores() {
        try {
            JSONObject results = General.postUrl(General.url + "stores/getStores", new JSONObject().put("userId", ""));//new JSONObject().put("order",
            General.appStores = results.getJSONArray("stores");
        } catch (JSONException e) {
            General.handleError("MyOrdersActivity, 777: ", e.toString());
        }
    }

    void populateItems() {
        try {
            catsArray = new ArrayList<>();
            catsArray.add("كل الأصناف");
            catsArray.add("تخفيضات");
            catsArray.add("المختارة");
            JSONObject newItem, oldItem;
            JSONArray units;
            for (int i = 0; i < General.itemsJson.length(); i++) {
//                Log.e("nnnnn" , General.itemsJson.getJSONObject(i).toString() );
                General.itemsJson.getJSONObject(i)
                        .put("isDisc", false)
                        .put("oQ", "")
                        .put("sQ", "")
                        .put("p", "")
                        .put("p0", "")
                        .put("pA", "")
                        .put("u", "")
                        .put("cn", "")
                        .put("nts", "");
                if (adapter != null) {
                    newItem = General.itemsJson.getJSONObject(i);
                    for (int j = 0; j < adapter.getSource().length(); j++) {
                        oldItem = adapter.getSource().getJSONObject(j);
                        if (oldItem.getString("_id").matches(newItem.getString("_id"))) {
                            General.itemsJson.getJSONObject(i)
                                    .put("oQ", oldItem.getString("oQ"))
                                    .put("sQ", oldItem.getString("sQ"))
                                    .put("p", oldItem.getString("p"))
                                    .put("p0", oldItem.getString("p0"))
                                    .put("pA", oldItem.getString("pA"))
                                    .put("u", oldItem.getString("u"))
                                    .put("cn", oldItem.getString("cn"))
//                                    .put("t", oldItem.getString("t"))
                                    .put("nts", oldItem.getString("nts"));
//                            j = adapter.getSource().length();
                            break;
                        }
                    }
                }
                for (int j = 0; j < General.itemsJson.getJSONObject(i).getJSONArray("tgs").length(); j++) {
                    if (!catsArray.contains(General.itemsJson.getJSONObject(i).getJSONArray("tgs").getString(j))) {
//                        General.cats.put(new JSONObject().put("cat", General.itemsJson.getJSONObject(i).getString("tg")));
                        General.cats.put(new JSONObject().put("cat", General.itemsJson.getJSONObject(i).getJSONArray("tgs").getString(j)));
                        catsArray.add(General.itemsJson.getJSONObject(i).getJSONArray("tgs").getString(j));
                    }
                }
                units = General.itemsJson.getJSONObject(i).getJSONArray("unts");
                for (int j = 0; j < units.length(); j++) {
                    if (units.getJSONObject(j).getDouble("p1") < units.getJSONObject(j).getDouble("lSP")) {
                        General.itemsJson.getJSONObject(i).put("isDisc", true);
                        j = units.length();
                    }
                }

            }
            mainAdapter = new MainAdapter(MainActivity.this, catsArray);
            recyclerView.setAdapter(mainAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, true));

            adapter = new listviewAdapter(MainActivity.this);
            itemsLV.setAdapter(adapter);

            switch (MainAdapter.index) {
                case 0:
                    adapter.getFilter().filter("allItems,");
                    break;
                case 1:
                    adapter.getFilter().filter("discs,");
                    break;
                case 2:
                    Log.e("mmm", catsArray.get(MainAdapter.index));
                    adapter.getFilter().filter("myItems,");
                    break;
                default:
                    adapter.getFilter().filter("cats," + catsArray.get(MainAdapter.index));
            }
        } catch (JSONException e) {
            General.handleError("gggggg", e.toString());
        }

    }

    Boolean getItems() {
        try {
            JSONObject results = General.postUrl(General.url + "items/getItems", new JSONObject().put("mo", mobile).put("pw", password));
            if (results.length() == 0 || !results.getBoolean("state")) {
//                General.showToast(MainActivity.this, "هناك خطأ ما, راجع مديرك !!!");
                Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                return false;
            } else {
                General.itemsJson = results.optJSONArray("items");
                return true;

            }

        } catch (JSONException e) {
            General.handleError("gggggg", e.toString());
            return false;
        }
    }

    void setObjEvents() {

        itemsLV.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                                       //            public void onSwipeTop() {
//                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
//            }
                                       public void onSwipeRight() {
                                           overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                           if (MainAdapter.index == catsArray.size() - 1) {
                                               MainAdapter.index = 0;
                                           } else {
                                               MainAdapter.index += 1;

                                           }
                                           recyclerView.getLayoutManager().scrollToPosition(MainAdapter.index);
                                           mainAdapter.notifyDataSetChanged();
                                           switch (MainAdapter.index) {
                                               case 0:
                                                   adapter.getFilter().filter("allItems,");
                                                   break;
                                               case 1:
                                                   adapter.getFilter().filter("discs,");
                                                   break;
                                               case 2:
                                                   adapter.getFilter().filter("myItems,");
                                                   break;
                                               default:
                                                   adapter.getFilter().filter("cats," + catsArray.get(MainAdapter.index));
                                           }
                                           overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                       }

                                       public void onSwipeLeft() {

                                           if (MainAdapter.index == 0) {
                                               MainAdapter.index = catsArray.size() - 1;
                                           } else {
                                               MainAdapter.index -= 1;

                                           }
                                           recyclerView.getLayoutManager().scrollToPosition(MainAdapter.index);
                                           mainAdapter.notifyDataSetChanged();
                                           switch (MainAdapter.index) {
                                               case 0:
                                                   adapter.getFilter().filter("allItems,");
                                                   break;
                                               case 1:
                                                   adapter.getFilter().filter("discs,");
                                                   break;
                                               case 2:
                                                   adapter.getFilter().filter("myItems,");
                                                   break;
                                               default:
                                                   adapter.getFilter().filter("cats," + catsArray.get(MainAdapter.index));
                                           }

                                       }

//            public void onSwipeBottom() {
//                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//            }

                                   }
        );

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    adapter.getFilter().filter("allItems,");
                } else {//if (searchEditText.getText().toString().trim().length() > 0)
                    adapter.getFilter().filter("search,%##" + newText);
                }

                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (getItems()) {
                    populateItems();
                    recyclerView.getLayoutManager().scrollToPosition(MainAdapter.index);
                    Toast.makeText(MainActivity.this, "تم تحديث الأصناف ...", Toast.LENGTH_SHORT).show();
                }
                if (General.countries.length() == 0) {
                    getGeoData();
                }
                if (General.system.length() == 0) {
//                    getSystem();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        gvsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
//                if (isFinished) {
//                    try {
//                        townsSp.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, General.getTowns(General.governorates.getJSONObject(gvsSp.getSelectedItemPosition()).getString("_id"))));
//                        nhsSp.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,
//                                General.getNhs(General.governorates.getJSONObject(gvsSp.getSelectedItemPosition()).getString("_id"),
//                                        townsSp.getSelectedItem().toString())));
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
//        townsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
//                if (isFinished) {
//                    try {
//                        nhsSp.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, General.getNhs(General.governorates.getJSONObject(gvsSp.getSelectedItemPosition()).getString("_id"), townsSp.getSelectedItem().toString())));
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


    }

    void getGeoData() {
        try {
            General.geos = General.postUrl(General.url + "geos/getGeosData", new JSONObject());
            General.countries = General.geos.getJSONArray("countries");
            General.governorates = General.geos.getJSONArray("governorates");
            General.towns = General.geos.getJSONArray("towns");
            General.neighborhoods = General.geos.getJSONArray("nhs");
            General.avNhs = General.geos.getJSONArray("avNhs");
            JSONObject country;
            for (int i = 0; i < General.countries.length(); i++) {
                country = General.countries.getJSONObject(i);
                General.countriesArray.add(country.getString("nm"));
            }
            JSONObject gv;
            for (int i = 0; i < General.governorates.length(); i++) {
                gv = General.governorates.getJSONObject(i);
                General.gvsArray.add(gv.getString("nm"));
            }
            JSONObject town;
            for (int i = 0; i < General.towns.length(); i++) {
                town = General.towns.getJSONObject(i);
                General.townsArray.add(town.getString("nm"));
            }
            JSONObject nh;
            for (int i = 0; i < General.neighborhoods.length(); i++) {
                nh = General.neighborhoods.getJSONObject(i);
                General.nhsArray.add(nh.getString("nm"));
            }
            JSONObject avNh;
            for (int i = 0; i < General.avNhs.length(); i++) {
                avNh = General.avNhs.getJSONObject(i);
                General.avNhsArray.add(avNh.getString("nm"));
            }

        } catch (JSONException e) {
            General.handleError("011: ", e.toString());
        }
    }

//    JSONArray stores;

    void setPurchaseOrder() {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView suTV = new TextView(MainActivity.this);
        final EditText payedET = new EditText(MainActivity.this);
        payedET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        payedET.setText(ttO + "");
        suTV.setText("قيمة الطلب: " + ttO + ", " + "اجمالى: " + ttO);
        payedET.setText(ttO + "");

        layout.addView(suTV);
        layout.addView(payedET);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(layout)
                .setTitle("راجع اجمالى الفاتورة أولا ...")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("موافق");
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setText("إلغاء");
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextSize(18);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Double payed = payedET.getText().toString().trim().length() > 0 ? Double.parseDouble(payedET.getText().toString().trim()) : 0.0;
                    if (payed < 0) {
                        payedET.requestFocus();
                        General.showToast(MainActivity.this, "مبلغ غير صالح !!!");
                        payedET.setError("مبلغ غير صالح !!!");
                        return;
                    }
                    General.myOrder
                            .put("frm", new JSONObject().put("nm", General.system.getJSONObject("dA").getString("nm")).put("id", General.system.getJSONObject("dA").getString("id")))
                            .put("to", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                            .put("adrs", new JSONObject()
//                                    .put("mo", teleET.getText())
//                                    .put("dt", adrsDtsET.getText())
//                                    .put("ar", areaET.getText())
                                            .put("nh", nhsSp.getSelectedItem())
                                            .put("tn", townsSp.getSelectedItem())
                                            .put("gv", gvsSp.getSelectedItem())
                                            .put("id", General.getNhId(nhsSp.getSelectedItem().toString() + "," + townsSp.getSelectedItem().toString() + "," + gvsSp.getSelectedItem().toString()))
                            )
//                        .put("str", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
//                            .put("dst", General.orderDist)
//                        .put("acc", new JSONObject().put("nm", General.system.getJSONObject("dA").getString("nm")).put("id", General.system.getJSONObject("dA").getString("id")))
//                        .put("tp", new JSONObject().put("nm", General.nowOp.split(",")[0]).put("mS", -1).put("bS", 1))
                            .put("op", new JSONObject().put("nm", General.nowOp.split(",")[0]).put("mS", -1).put("bS", 1))
                            .put("ops", new JSONArray()
                                    .put(0, new JSONObject()
                                            .put("no", 510)
                                            .put("nm", "فى انتظار الاستلام")
                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("to", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("lc", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("d", new Date().getTime())
                                    )
                                    .put(1, new JSONObject()
                                            .put("no", 150)
                                            .put("nm", "مطلوب")
                                            .put("frm", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("lc", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("d", new Date().getTime())
                                    )
                            ).put("dts", General.myOrderDetails)
                            .put("tts", new JSONObject()
                                    .put("dFs", 0)
                                    .put("tO", ttO)
                                    .put("tS", ttO)
                                    .put("tN", ttS)//ttN
                                    .put("tA", ttA)
                                    .put("tP", Double.parseDouble(payedET.getText().toString().trim()))
                                    .put("tPrft", 0)
                                    .put("pD", new JSONArray())
                            );
                    alertDialog.dismiss();
                    General.myOrder.put("payed", payed);
                    final JSONObject results = General.postUrl(General.url + "orders/newPurchaseOrder", new JSONObject().put("order", General.myOrder)
                            .put("userId", General.user.getString("_id"))
                            .put("userName", General.user.getString("nm"))
                    );
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.showToast(MainActivity.this, "تم إرسال الطلب بنجاح ...");
                        clearOrder();
                        if (results.getJSONObject("bal").length() > 0) {
                            General.verifyAccount(results.getJSONObject("bal"), MainActivity.this);
                        }

                    }
                } catch (JSONException e) {
                    General.handleError("dddsss", e.toString());
                }

            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    void setTransOrder() {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final Spinner storesSp = new Spinner(MainActivity.this);

        try {
            JSONObject results = General.postUrl(General.url + "stores/getStores", new JSONObject().put("userId", General.user.getString("_id")));//new JSONObject().put("order",
//            JSONObject results = new JSONObject(op);
            General.appStores = results.getJSONArray("stores");
            ArrayList<String> storesArray = new ArrayList<>();
            for (int i = 0; i < General.appStores.length(); i++) {
                storesArray.add(General.appStores.getJSONObject(i).getString("nm"));//
            }
            storesSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, storesArray));
        } catch (JSONException e) {
            General.handleError("MyOrdersActivity, 777: ", e.toString());
        }
        layout.addView(storesSp);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(layout)
                .setTitle("اختر المخزن المراد التحويل إليه ...")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
//                .setNeutralButton(android.R.string.unknownName, null)
                .show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("موافق");
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setText("إلغاء");
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextSize(18);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    General.myOrder
                            .put("frm", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                            .put("to", new JSONObject().put("nm", General.appStores.getJSONObject(storesSp.getSelectedItemPosition()).getString("nm")).put("id", General.appStores.getJSONObject(storesSp.getSelectedItemPosition()).getString("_id")))
                            .put("adrs", new JSONObject()
//                                    .put("mo", teleET.getText())
//                                    .put("dt", adrsDtsET.getText())
//                                    .put("ar", areaET.getText())
                                            .put("nh", nhsSp.getSelectedItem())
                                            .put("tn", townsSp.getSelectedItem())
                                            .put("gv", gvsSp.getSelectedItem())
                                            .put("id", General.getNhId(nhsSp.getSelectedItem().toString() + "," + townsSp.getSelectedItem().toString() + "," + gvsSp.getSelectedItem().toString()))
                            );
                    General.myOrder
//                            .put("str", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                            .put("dst", General.orderDist)
//                            .put("acc", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
//                            .put("tp", new JSONObject().put("nm", General.nowOp.split(",")[0]).put("mS", 0).put("bS", 0))
                            .put("op", new JSONObject().put("nm", General.nowOp.split(",")[0]).put("mS", 0).put("bS", 0))
//                            .put("ex", new JSONObject()
//                                    .put("frm", Integer.parseInt(fromSp.getSelectedItem().toString()))
//                                    .put("to",Integer.parseInt(toSp.getSelectedItem().toString()))
//                                    .put("d",new SimpleDateFormat("yyyy/MM/dd").parse(dateSp.getSelectedItem().toString().split(",")[1].trim()))
//                            )
                            .put("ops", new JSONArray()
                                    .put(0, new JSONObject()
                                            .put("no", 510)
                                            .put("nm", "فى انتظار الاستلام")
                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("to", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("lc", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("d", new Date().getTime())
                                    )).put("dts", General.myOrderDetails)
                            .put("tts", new JSONObject()
                                    .put("dFs", General.deliveryFees)
                                    .put("tO", ttO)
                                    .put("tS", ttO)
                                    .put("tN", ttO)//ttN
                                    .put("tA", ttA)
                                    .put("tPrft", 0)
                                    .put("pD", new JSONArray())
                            );
                    alertDialog.dismiss();
                    JSONObject results = General.postUrl(General.url + "orders/newTransOrder", new JSONObject().put("order", General.myOrder)
                            .put("userId", General.user.getString("_id"))
                            .put("userName", General.user.getString("nm")));
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.showToast(MainActivity.this, "تم إرسال الطلب بنجاح ...");
                        clearOrder();

                    }
                } catch (JSONException e) {
                    General.handleError("ddd", e.toString());
                }

            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }


    void setSalesOrder() {
//        recyclerView.findViewHolderForAdapterPosition(recyclerView.getChildAdapterPosition(recyclerView.getFocusedChild())+1).itemView.performClick();
//        if (true)return;
        Log.e("fffffff", General.myOrderDetails.toString());
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sales_order, null, false);
        final TextView suTV = view.findViewById(R.id.order_price);
        Button sendOrder = view.findViewById(R.id.send_order);
        Button cancelOrder = view.findViewById(R.id.cancel_order);
        final TextView addressTV = view.findViewById(R.id.order_address);
        final Spinner fromSp = view.findViewById(R.id.from_order);
        final Spinner toSp = view.findViewById(R.id.to_order);
        final Spinner dateSp = view.findViewById(R.id.date_order);

        switch (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                fromHours = new String[]{"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"};
                toHours = new String[]{"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                break;
            case 8:
                fromHours = new String[]{"9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"};
                toHours = new String[]{"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                break;
            case 9:
                fromHours = new String[]{"10", "11", "12", "13", "14", "15", "16", "17", "18", "19"};
                toHours = new String[]{"13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                break;
            case 10:
                fromHours = new String[]{"11", "12", "13", "14", "15", "16", "17", "18", "19"};
                toHours = new String[]{"14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                break;
            case 11:
                fromHours = new String[]{"12", "13", "14", "15", "16", "17", "18", "19"};
                toHours = new String[]{"15", "16", "17", "18", "19", "20", "21", "22", "23"};
                break;
            case 12:
                fromHours = new String[]{"13", "14", "15", "16", "17", "18", "19"};
                toHours = new String[]{"16", "17", "18", "19", "20", "21", "22", "23"};
                break;
            case 13:
                fromHours = new String[]{"14", "15", "16", "17", "18", "19"};
                toHours = new String[]{"17", "18", "19", "20", "21", "22", "23"};
                break;
            case 14:
                fromHours = new String[]{"15", "16", "17", "18", "19"};
                toHours = new String[]{"19", "20", "21", "22", "23"};
                break;
            case 15:
                fromHours = new String[]{"16", "17", "18", "19"};
                toHours = new String[]{"20", "21", "22", "23"};
                break;
            case 16:
                fromHours = new String[]{"17", "18", "19"};
                toHours = new String[]{"21", "22", "23"};
                break;
            case 17:
                fromHours = new String[]{"18", "19"};
                toHours = new String[]{"22", "23"};
                break;
            case 18:
                fromHours = new String[]{"19"};
                toHours = new String[]{"23"};
                break;
        }
        fromSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (fromSp.getSelectedItem().toString()) {
                    case "8":
                        toHours = new String[]{"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                        break;
                    case "9":
                        toHours = new String[]{"13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                        break;
                    case "10":
                        toHours = new String[]{"14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                        break;
                    case "11":
                        toHours = new String[]{"15", "16", "17", "18", "19", "20", "21", "22", "23"};
                        break;
                    case "12":
                        toHours = new String[]{"16", "17", "18", "19", "20", "21", "22", "23"};
                        break;
                    case "13":
                        toHours = new String[]{"17", "18", "19", "20", "21", "22", "23"};
                        break;
                    case "14":
                        toHours = new String[]{"18", "19", "20", "21", "22", "23"};
                        break;
                    case "15":
                        toHours = new String[]{"19", "20", "21", "22", "23"};
                        break;
                    case "16":
                        toHours = new String[]{"20", "21", "22", "23"};
                        break;
                    case "17":
                        toHours = new String[]{"21", "22", "23"};
                        break;
                    case "18":
                        toHours = new String[]{"22", "23"};
                        break;
                    case "19":
                        toHours = new String[]{"23"};
                        break;
                }
                toSp.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.single_row_myorder_spin, toHours));
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fromSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, fromHours));
        toSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, toHours));

        dateSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin,
                new String[]{new SimpleDateFormat(
                        "EEEE, yyyy/MM/dd",
                        Locale.getDefault()).format(new Date(new Date().getTime() + (5 * 60 * 60 * 1000))),
                        new SimpleDateFormat("EEEE, yyyy/MM/dd", Locale.ENGLISH)
                                .format(new Date(new Date().getTime() + (29 * 60 * 60 * 1000)))}));
//Long c = new Date(new Date().getTime() + (5 * 60 * 60 * 1000)).getTime();

        try {
            suTV.setText("قيمة الطلب: " + ttO + "\t \t \t " +
                    "قيمة التوصيل: " + General.deliveryFees + "\n " +
                    "اجمالى: " + (ttO + General.deliveryFees));

//        if(gvsSp.getParent() != null) {
//            ((ViewGroup)gvsSp.getParent()).removeAllViews(); // <- fix
//        }
            addressTV.setText("على العنوان: " + " \n " +
                    General.userAddress.getString("nm") + " " +
                    General.userAddress.getString("gv") + " " +
                    General.userAddress.getString("tn") + " \n " +
                    General.userAddress.getString("nh") + " " +
                    General.userAddress.getString("ar") + " " +
                    General.userAddress.getString("dt") + " "
            );

        } catch (JSONException e) {
            General.handleError("009: ", e.toString());
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(view)
                .show();
        alertDialog.setCanceledOnTouchOutside(false);

        addressOrder = addressTV.getText().toString();
        Log.e("kkkkkkkk", "setSalesOrder: "  + addressOrder );
        sendOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    General.myOrder = new JSONObject();
                    if (General.nowOp.split(",")[1].matches("user")) {
                        General.myOrder
                                .put("frm", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                .put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                .put("adrs", new JSONObject()
                                        .put("dst", General.orderDist)
                                        .put("mo", General.user.getString("mo"))
                                        .put("dt", General.userAddress.getString("dt"))
                                        .put("ar", General.userAddress.getString("ar"))
                                        .put("nh", General.userAddress.getString("nh"))
                                        .put("tn", General.userAddress.getString("tn"))
                                        .put("gv", General.userAddress.getString("gv"))
                                        .put("id", General.userAddress.getString("id"))

                                ).put("ops", new JSONArray()
                                .put(0, new JSONObject()
                                        .put("no", 210)
                                        .put("nm", "فى انتظار التحضير")
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                        .put("lc", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                        .put("d", new Date().getTime()))
                                .put(1, new JSONObject()
                                        .put("no", 110)
                                        .put("nm", "مطلوب")
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                        .put("lc", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                        .put("d", new Date().getTime())
                                ));
                        addressOrder = General.myOrder.getJSONObject("adrs").getString("dt");

                    } else {
                        General.myOrder
                                .put("frm", new JSONObject().put("nm", callUserStore.getString("nm")).put("id", callUserStore.getString("_id")))
                                .put("to", new JSONObject().put("nm", callUser.getString("nm")).put("id", callUser.getString("_id")))
                                .put("adrs", new JSONObject()
                                        .put("dst", General.orderDist)
                                        .put("mo", callUser.getString("mo"))
                                        .put("dt", callUserAddress.getString("dt"))
                                        .put("ar", callUserAddress.getString("ar"))
                                        .put("nh", callUserAddress.getString("nh"))
                                        .put("tn", callUserAddress.getString("tn"))
                                        .put("gv", callUserAddress.getString("gv"))
                                        .put("id", callUserAddress.getString("id"))
                                ).put("ops", new JSONArray()
                                .put(0, new JSONObject()
                                        .put("no", 210)
                                        .put("nm", "فى انتظار التحضير")
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", new JSONObject().put("nm", callUserStore.getString("nm")).put("id", callUserStore.getString("_id")))
                                        .put("lc", new JSONObject().put("nm", callUserStore.getString("nm")).put("id", callUserStore.getString("_id")))
                                        .put("d", new Date().getTime()))
                                .put(1, new JSONObject()
                                        .put("no", 110)
                                        .put("nm", "مطلوب")
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", new JSONObject().put("nm", callUserStore.getString("nm")).put("id", callUserStore.getString("_id")))
                                        .put("lc", new JSONObject().put("nm", callUserStore.getString("nm")).put("id", callUserStore.getString("_id")))
                                        .put("d", new Date().getTime())
                                ));
                    }
                    Log.e("date", "onClick: " + new Date().getTime());
                    General.myOrder
                            .put("op", new JSONObject().put("nm", General.nowOp.split(",")[0]).put("mS", 1).put("bS", -1)
                                            .put("f", Integer.parseInt(fromSp.getSelectedItem().toString()))
                                            .put("t", Integer.parseInt(toSp.getSelectedItem().toString()))

                                            .put("d",
                                                    dateSp.getSelectedItemPosition() == 0 ?
                                                            new Date(new Date().getTime() + (5 * 60 * 60 * 1000)).getTime() :
                                                            new Date(new Date().getTime() + (29 * 60 * 60 * 1000)).getTime()

//                                            (new SimpleDateFormat("yyyy/MM/dd")).parse(dateSp.getSelectedItem().toString().split(",")[1].trim()).getTime()
                                            )
                            )
                            .put("dts", General.myOrderDetails)
                            .put("tts", new JSONObject()
                                    .put("dFs", General.deliveryFees)
                                    .put("tO", ttO)
                                    .put("tS", ttO)
                                    .put("tN", (ttO + General.deliveryFees))//ttN
                                    .put("tA", ttA)
                                    .put("tPrft", 0)
                                    .put("pD", new JSONArray())
                            );
                    alertDialog.dismiss();
                    JSONObject results = General.postUrl(General.url + "orders/newSalesOrder", new JSONObject().put("order", General.myOrder)
                    );
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                        Log.e("eeeeeeeeee", "failed !!! " + results.toString());
                    } else {
                        General.showToast(MainActivity.this, "تم إرسال الطلب بنجاح ...");
                        clearOrder();
                    }
                } catch (JSONException e) {//
                    General.handleError("ffff0000", e.toString());
                }
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });
    }






    void clearOrder() {
        try {
            for (int i = 0; i < General.itemsJson.length(); i++) {
                General.itemsJson.getJSONObject(i)
                        .put("oQ", "")
                        .put("sQ", "")
                        .put("p", "")
                        .put("p0", "")
                        .put("pA", "")
                        .put("u", "")
                        .put("cn", "")
//                        .put("t", "")
                        .put("nts", "");
            }
            General.myOrder = new JSONObject();
            General.myOrderDetails = new JSONArray();
            MainAdapter.index = 0;
            mainAdapter.notifyDataSetChanged();
            adapter.getFilter().filter("allItems,");
            newAddress = new JSONObject();
            isNewOrder = false;
        } catch (JSONException e) {
            General.handleError("sss", e.toString());
        }
    }

    private void populateUser() {
        if (General.user.length() > 0) {
            try {
                General.userName = General.user.getString("nm");
                navUsername.setText(General.userName);

                if (General.user.getString("tp").matches("admin")) {
                    General.isAdmin = true;
                }
                General.logOutMI.setVisible(true);
                General.registerMI.setTitle("حسابى");
                General.registerMI.setIcon(R.drawable.ic_setting);
                General.storesMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.geoMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.itemsMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.systemMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.recruitMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.purchaseMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.transMI.setVisible(Arrays.asList(new String[]{"admin", "manager", "supervisor", "preparator", "checker", "delivery", "driver"}).contains(General.user.getString("tp")));
                General.salesMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                General.movesMI.setVisible(Arrays.asList(new String[]{"admin"}).contains(General.user.getString("tp")));
                addresses = General.user.getJSONArray("adrs");
                getStores();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void login(Boolean isLoud) {
        if (isLoud) {
            b = 0.0;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.login, null, false);

            final EditText mobileET = view.findViewById(R.id.logIn_phone);
            final EditText passwordET = view.findViewById(R.id.logIn_password);
            final CheckBox showCharsChk = view.findViewById(R.id.check);
            final Button enter = view.findViewById(R.id.logIn_enter);
            final Button creatNew = view.findViewById(R.id.logIn_new);

            showCharsChk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (showCharsChk.isChecked()) {
                        passwordET.setTransformationMethod(null);
                    } else {
                        passwordET.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }
            });

            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(view)
                    .show();
            alertDialog.setCanceledOnTouchOutside(false);

            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mobileET.getText().toString().trim().length() < 1) {
                        General.showToast(MainActivity.this, "أدخل رقم الموبايل !!!");
                        mobileET.setError("أدخل رقم الموبايل !!!");
                        return;
                    }
                    if (passwordET.getText().toString().trim().length() < 1) {
                        General.showToast(MainActivity.this, "أدخل كلمة السر !!!");
                        passwordET.setError("أدخل كلمة السر !!!");
                        return;
                    }
                    try {
                        JSONObject results = General.postUrl(General.url + "auth/login", new JSONObject().put("mo", mobileET.getText().toString().trim()).put("pw", passwordET.getText().toString().trim()));
                        if (results.length() == 0 || !results.getBoolean("state")) {
                            General.showToast(getApplicationContext(), "رقم الموبايل أو كلمة المرور غير صحيحة !!!", Toast.LENGTH_LONG);
                            Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                            General.user = new JSONObject();
                        } else {
                            alertDialog.dismiss();


                            General.user = results.getJSONObject("user");
                            General.userName = General.user.getString("nm");
                            General.showToast(MainActivity.this, "أهلا: " + General.userName + " تم تسجيل الدخول بنجاح ...");
                            General.setPreference(MainActivity.this, "mobile", mobileET.getText().toString().trim());
                            General.setPreference(MainActivity.this, "password", passwordET.getText().toString().trim());
                            General.token = results.getString("token");
                            populateUser();
                            if (General.myOrderDetails.length() > 0) {
                                if (General.myStore.length() > 0 && General.aavStores.length() > 0) {
                                    Boolean isTherAstore = false;
                                    for (int i = 0; i < General.aavStores.length(); i++) {
                                        if (General.myStore.getString("_id").matches(General.aavStores.getJSONObject(i).getString("_id"))) {
                                            General.myStore = General.aavStores.getJSONObject(i);
                                            isTherAstore = true;
                                            break;
                                        }
                                    }
                                    if (!isTherAstore) {
                                        clearOrder();
                                    }
                                } else {
                                    clearOrder();
                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
            creatNew.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    Intent RegisterIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                    RegisterIntent.putExtra("sender", "user");
                    startActivity(RegisterIntent);
                }
            });

        } else {
            mobile = General.getPreference(this, "mobile");
            password = General.getPreference(this, "password");
            if (!(mobile.matches("no") || password.matches("no"))) {//mobile.matches("no") || password.matches("no")
                try {
                    JSONObject results = General.postUrl(General.url + "auth/login", new JSONObject().put("mo", mobile).put("pw", password));
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.user = new JSONObject();
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        General.user = results.getJSONObject("user");
                        General.userName = General.user.getString("nm");
                        General.showToast(MainActivity.this, "أهلا: " + General.userName + " تم تسجيل الدخول بنجاح ...");
                        General.token = results.getString("token");
                        if (General.myOrderDetails.length() > 0) {
                            if (General.myStore.length() > 0 && General.aavStores.length() > 0) {
                                Boolean isTherAstore = false;
                                for (int i = 0; i < General.aavStores.length(); i++) {
                                    if (General.myStore.getString("_id").matches(General.aavStores.getJSONObject(i).getString("_id"))) {
                                        General.myStore = General.aavStores.getJSONObject(i);
                                        isTherAstore = true;
                                        break;
                                    }
                                }
                                if (!isTherAstore) {
                                    clearOrder();
                                }
                            } else {
                                clearOrder();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void setNewUserAddress() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        noUserAddress = inflater.inflate(R.layout.add_sub_address, null, false);
        nhsSp = noUserAddress.findViewById(R.id.new_user_adress_elhai);
        townsSp = noUserAddress.findViewById(R.id.new_user_add_adres_city);
        gvsSp = noUserAddress.findViewById(R.id.new_user_adres_country);
        Button nUserAdress = noUserAddress.findViewById(R.id.nu_add_adres);
        Button newUser = noUserAddress.findViewById(R.id.subA_newAccount);
        Button logInSub = noUserAddress.findViewById(R.id.subA_logIn);
        nhsSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, General.nhsArray));
        townsSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, General.townsArray));
        gvsSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, General.gvsArray));
        hi = new AlertDialog.Builder(MainActivity.this)
                .setView(noUserAddress)
                .show();

        hi.setCanceledOnTouchOutside(false);
        //  hi.setCancelable(false);
        nUserAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    isNewOrder = true;
                    newAddress = new JSONObject()
                            .put("id", General.getNhId(nhsSp.getSelectedItem().toString() + "," + townsSp.getSelectedItem().toString() + "," + gvsSp.getSelectedItem().toString()))
                            .put("nh", nhsSp.getSelectedItem().toString()).put("tn", townsSp.getSelectedItem()).put("gv", gvsSp.getSelectedItem());
//////////////

                    General.userAddress = newAddress;
                    for (int i = 0; i < General.appStores.length(); i++) {
                        nowStore = General.appStores.getJSONObject(i);
                        for (int s = 0; s < nowStore.getJSONArray("ars").length(); s++) {
                            if (General.userAddress.getString("id").matches(nowStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getString("_id"))) {
                                General.myStore = General.appStores.getJSONObject(i);
                                General.aavStores.put(nowStore);
                                General.orderDist = nowStore.getJSONArray("ars").getJSONObject(s).getJSONObject("dst");
                                General.deliveryFees = General.myStore.getJSONArray("ars").getJSONObject(s).getJSONObject("id").getDouble("dFs");
//                                            s = General.myStore.getJSONArray("ars").length();
//                                            i = General.appStores.length();
                            }
                        }
                    }
                    isNewOrder = true;
////////////////

                } catch (JSONException e) {
                    General.handleError("003", e.toString());
                }
                hi.dismiss();
//                getUserData();
            }
        });
        logInSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(true);
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getTitle().toString()) {

            case "مستخدم جديد":
            case "حسابى":
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                return true;

            case "طلباتى":
                if (General.user.length() == 0) {
                    login(true);
                } else {
                    startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
                }
                return true;

            case "ضبط النظام":
                startActivity(new Intent(getApplicationContext(), SystemActivity.class));
                return true;
            case "الأصناف":
                startActivity(new Intent(getApplicationContext(), ItemActivity.class));
                return true;
            case "تسجيل الدخول":
                login(true);
                return true;
            case "مخازن":
                startActivity(new Intent(getApplicationContext(), StoresActivity.class));
                return true;
            case "وظائف":
                startActivity(new Intent(getApplicationContext(), CareersActivity.class));
                return true;
            case "توظيف":
                startActivity(new Intent(getApplicationContext(), RecruitActivity.class));
                return true;
            case "تحركات":
                startActivity(new Intent(getApplicationContext(), TransferActivity.class));
                return true;
            case "جغرافيا":
                startActivity(new Intent(getApplicationContext(), GeoActivity.class));
                return true;
            case "تسجيل الخروج":
                General.user = new JSONObject();
                General.userName = "";
                navUsername.setText("تسجيل الدخول");
                General.logOutMI.setVisible(false);
                General.registerMI.setTitle("مستخدم جديد");
                General.registerMI.setIcon(R.drawable.ic_adduser);
                General.setPreference(MainActivity.this, "mobile", null);
                General.setPreference(MainActivity.this, "password", null);
                return true;
            case "اطلب":
                General.nowOp = "sO,user";
                return true;
            case "تسجيل مبيعات":
                General.nowOp = "sO,app";
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            case "تسجيل مشتروات":
                General.nowOp = "pO,app";
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            case "تحويلات":
                General.nowOp = "tO,app";
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;

            case "سياسة الخصوصية":
                startActivity(new Intent(MainActivity.this, PraivcyPolicy.class));
                return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mem);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.mem);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1888) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                listviewAdapter.im.setBackground(getDrawable(android.R.color.transparent));
            }

//            listviewAdapter.im.setImageBitmap(photo);

            resultCode = 1242;
        }
        if (resultCode == RESULT_OK) {
            try {
                mImageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(mImageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

//                listviewAdapter.im.setImageBitmap(selectedImage);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else {
        }
        if (requestCode == PIEK_REQEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            imageName = data.getData().getLastPathSegment();
        }

        if (data != null && data.getData() != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
//                imageString = getStringImage(bitmap);
                listviewAdapter.im.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                long imageLength = (stream.toByteArray()).length;

                String extension;
                //Check uri format to avoid null
                if (mImageUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    //If scheme is a content
                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    extension = mime.getExtensionFromMimeType(MainActivity.this.getContentResolver().getType(mImageUri));
                } else {
                    //If scheme is a File
                    //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                    extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(mImageUri.getPath())).toString());
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                listviewAdapter.imagePropertiesTV.setText((imageLength / 1024) + " KB, " + extension + "new " + (encodedImage.length() / 1024) + " KB, ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (listviewAdapter.im != null) {
                listviewAdapter.im.setVisibility(View.VISIBLE);
                listviewAdapter.imagePropertiesTV.setVisibility(View.VISIBLE);
                listviewAdapter.saveImage.setVisibility(View.VISIBLE);
            }
        }

    }
   /* private float x1,x2;
    static final int MIN_DISTANCE = 150;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                recyclerView.findViewHolderForAdapterPosition(recyclerView.getChildAdapterPosition(recyclerView.getFocusedChild())).itemView.performClick();

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    recyclerView.setSelected(true);//
                    Toast.makeText(this, "000000000000000", Toast.LENGTH_SHORT).show ();
                }
                else
                {
                    Toast.makeText(this, "111111111111111", Toast.LENGTH_SHORT).show ();

                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }*/
//    public String getStringImage(Bitmap bmp) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] imageByte = baos.toByteArray();
//        return Base64.encodeToString(imageByte, Base64.DEFAULT);
//    }

}


//    private void populateGeos() {
//        try {
//            JSONObject country;
//            for (int i = 0; i < General.countries.length(); i++) {
//                country = General.countries.getJSONObject(i);
//                General.countriesArray.add(country.getString("nm"));
//            }
//            JSONObject gv;
//            for (int i = 0; i < General.governorates.length(); i++) {
//                gv = General.governorates.getJSONObject(i);
//                General.gvsArray.add(gv.getString("nm"));
//            }
//            JSONObject town;
//            for (int i = 0; i < General.towns.length(); i++) {
//                town = General.towns.getJSONObject(i);
//                General.townsArray.add(town.getString("nm"));
//            }
//            JSONObject nh;
//            for (int i = 0; i < General.neighborhoods.length(); i++) {
//                nh = General.neighborhoods.getJSONObject(i);
//                General.nhsArray.add(nh.getString("nm"));
//            }
//            nhsSp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, General.nhsArray));
//            townsSp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, General.townsArray));
//            gvsSp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, General.gvsArray));
//        } catch (JSONException e) {
//            General.handleError("hhhhhh: ", e.toString());
//        }
//
//    }
