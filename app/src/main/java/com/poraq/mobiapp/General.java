package com.poraq.mobiapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Typeface;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.concurrent.ExecutionException;

public class General extends android.app.Application {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final static Integer versionNo = 1;
    public static String url = "http://192.241.156.208:8080/", nowOp = "sO,user", userName, token = "";//192.168.8.100 192.168.1.10 , 192.168.43.138  192.168.8.101///68.183.86.25
    public static JSONArray appStores = new JSONArray(),aavStores = new JSONArray(), cats = new JSONArray(), cars, partners, mates, mySuspendedOrders, itemsJson = new JSONArray(), myOrderDetails = new JSONArray(), storeOrders = new JSONArray(), countries = null, governorates = null, towns = null, neighborhoods = null, avNhs = null;
    public static JSONObject translator = new JSONObject(), userAddress, orderDist, myStore = new JSONObject(), system = new JSONObject(), myOrder = new JSONObject(), user = new JSONObject(), geos = new JSONObject(), office = null;
    public static Boolean isExceptionalLogin = false, isAdmin = false;
    public static String[] jobsArray = new String[]{"ديليفرى بموتوسيكل", "ديليفرى بسيارة", "محضر طلبيات", "أمين مخزن"};
    public static Context context;
    public static ArrayList<String> nhsArray = new ArrayList<>(), avNhsArray = new ArrayList<>(), townsArray = new ArrayList<>(), gvsArray = new ArrayList<>(), countriesArray = new ArrayList<>();
    public static Double deliveryFees = 10.0;
    public static MenuItem transMI, systemMI, salesMI, itemsMI, logOutMI, registerMI, careersMI, myOrdersMI, recruitMI, movesMI, purchaseMI, storesMI, geoMI;
    public static int state;
    static SharedPreferences preferences;
    static ProgressDialog dialog;


    public static JSONArray creatJArraay(JSONArray jARR, JSONObject jObj) {
        JSONArray newJArr = new JSONArray();
        try {
            newJArr.put(0, jObj);
            for (int i = 0; i < jARR.length(); i++) {
                newJArr.put(i + 1, jARR.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newJArr;
    }

    public static void verifyAccount(final JSONObject bal, final Context context) {
        try {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            TextView suTV = new TextView(context);
            String su = "الرصيد السابق: " + bal.getString("bB")
                    + System.getProperty("line.separator")
                    + "قيمة العملية: " + bal.getString("op")
                    + System.getProperty("line.separator")
                    + "الرصيد الحالى: " + bal.getString("bA");
            suTV.setText(su);
            layout.addView(suTV);

            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setView(layout)
                    .setTitle("مطابقة حسابات ...")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("مطابق");
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        } catch (JSONException e) {
            handleError("General 006: ", e.toString());
        }
    }

    public static ArrayList<String> getTowns(String id) {
        ArrayList<String> towns = new ArrayList<String>();
        for (int i = 0; i < General.towns.length(); i++) {
            try {
                if (General.towns.getJSONObject(i).getJSONObject("gv").getString("id").matches(id)) {
                    towns.add(General.towns.getJSONObject(i).getString("nm"));
                }
            } catch (JSONException e) {
                handleError("General", e.toString());
            }
        }
        return towns;
    }

    public static ArrayList<String> getNhs(String id, String town) {
        ArrayList<String> nhs = new ArrayList<String>();
        for (int i = 0; i < General.towns.length(); i++) {
            try {
                if (General.neighborhoods.getJSONObject(i).getJSONObject("gv").getString("id").matches(id) && General.neighborhoods.getJSONObject(i).getJSONObject("tn").getString("nm").matches(town)) {
                    nhs.add(General.neighborhoods.getJSONObject(i).getString("nm"));
                }
            } catch (JSONException e) {
                handleError("General", e.toString());
            }
        }
        return nhs;
    }

    public static String getNhId(String adrs) {
        try {
            for (int i = 0; i < neighborhoods.length(); i++) {
                Log.e("eeeee", neighborhoods.getJSONObject(i).toString());
                if (
                        adrs.split(",")[0].matches(neighborhoods.getJSONObject(i).getString("nm"))
                                && adrs.split(",")[1].matches(neighborhoods.getJSONObject(i).getJSONObject("tn").getString("nm"))
                                && adrs.split(",")[2].matches(neighborhoods.getJSONObject(i).getJSONObject("gv").getString("nm"))
                ) {
                    return neighborhoods.getJSONObject(i).getString("_id");
                }
            }
        } catch (JSONException e) {
            handleError("General, 001: ", e.toString());
            return "-1";
        }
        return "-1";
    }

    public static JSONObject calcTotals(JSONObject order) {
        JSONObject item;

        try {
            Double tO, tS, tA, tPrft, ttO = 0.0, ttS = 0.0, ttN = order.getJSONObject("tts").getDouble("dFs"), ttA = 0.0, ttPrft = 0.0;
            for (int i = 0; i < order.getJSONArray("dts").length(); i++) {
                item = order.getJSONArray("dts").getJSONObject(i);
                tO = 0.0;
                tS = 0.0;
                tA = 0.0;
                tPrft = 0.0;
                tO = (item.getDouble("oQ") * item.getDouble("p"));
                tS = (item.getDouble("sQ") * item.getDouble("p"));
                tA = (item.getDouble("sQ") * item.getDouble("pA"));
                tPrft = (item.getDouble("sQ") * (item.getDouble("p") - item.getDouble("pA")));
                order.getJSONArray("dts").getJSONObject(i)
                        .put("tO", tO)
                        .put("tS", tS)
//                        .put("tOR", tOR)
//                        .put("tSR", tSR)
//                        .put("tN", tN)// diff between tS - tR, user cost
                        .put("tA", tA)
                        .put("tPrft", tPrft);
                //ttOR += tOR; ttSR += tSR;
                ttO += tO;
                ttS += tS;
                ttN += tO;
                ttA += tA;
                ttPrft += tPrft;
            }
            order.getJSONObject("tts")
                    .put("tO", ttO)
                    .put("tS", ttS)
//                    .put("tOR", ttOR)
//                    .put("tSR", ttSR)
                    .put("tN", ttN)// diff between tS - tR, user cost
                    .put("tA", ttA)
                    .put("tPrft", ttPrft);
        } catch (JSONException e) {
            General.handleError("exp", e.toString());
        }

        return order;
    }

    public static void waitMe(Context context, String msg) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void IamReady() {
        dialog.dismiss();
    }

    public static void restartApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    public static String getPreference(Context context, String key) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "no");
    }

    public static void setPreference(Context context, String key, String value) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();//.putString(key,value);
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static void showToast(Context context, String msg, Integer duration) {
        Toast.makeText(context, msg, duration).show();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void handleError(String pos, String err) {
        Log.e(pos, err);
    }

    public static JSONObject postUrl(String url, JSONObject data) {
        state = 0;
        if (!NetworkChangeReceive.isOnline(context) || !NetworkChangeReceive.isOnline()) {
            General.showToast(context, "Please check the Internet");
            return new JSONObject();
        }

        final String uri = url;
        final JSONObject parameters = data;
        AsyncTask xx = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
              //  OkHttpClient client = new OkHttpClient();


                RequestBody body = RequestBody.create(General.JSON, parameters.toString());

                Request request = new Request.Builder()
                        .url(uri)
                        .header( "Authorization", "Bearer " + token)
                        .post(body)
                        .build();
                try {

                    Response response = new OkHttpClient().newCall(request).execute();
                    state = response.code();
                    return response.body().string();//response.body().toString();


                } catch (IOException e) {
                    General.handleError("00000000", e.toString());
                    try {
                        return new JSONObject().put("err", e.toString()).put("responseCode", state);
                    } catch (JSONException ex) {
                        General.handleError("err", ex.toString());
                        return new JSONObject();
                    }
                }
            }
        }.execute();
        try {
            switch (state) {
                case 401:
                    General.showToast(context, "    00000000000000000");
                    ((MainActivity) context).login(false);
                    if (General.user.length() > 0) {
                        return postUrl(General.url, data);
                    } else {
                        ((MainActivity) context).login(true);
                        Toast.makeText(context, "برجاء اعادة تسجيل الدخول مره اخري", Toast.LENGTH_SHORT).show();
                        return new JSONObject();
                    }
                case 405:
                    Toast.makeText(context, "غير مسموح, راجع مديرك !!!", Toast.LENGTH_SHORT).show();
                    return new JSONObject().put("state", false).put("st", state);
            }
            return new JSONObject(xx.get().toString()).put("st", state).put("state", (Arrays.asList(new Integer[]{200, 201, 204}).contains(state) ? true : false));
        } catch (JSONException | ExecutionException | InterruptedException e) {
            Log.e("error1111111111: ", e.toString() + state);
            try {
                return new JSONObject().put("err", e).put("st", state).put("state", (Arrays.asList(new Integer[]{200, 201, 204}).contains(state) ? true : false));
            } catch (JSONException ex) {
                General.handleError("ssssss", e.toString());
                return new JSONObject();
            }
        }
    }


}

// اعدد ان المستخدم لايتاح له التطبيق الا فى الاطار الجغرافى لمكان عمله
// claim this my mo. no. some one else use it
//down tool bar

//    make admin account
//        حجز user names and urls
//
//
//        format rate in items, date in moves
//        set patients with no identiffiers local to creator
//        minify mshafy.com in p.
//        remove updates from presc when save
//        edit drUsr
//        typeahead open popup on focus
//        items typeahead-template-url ng-hide not work
//        test change password on server
//        add cell text allignment to tinymce top bottom right left
//        hide popup onBlur when no selection templates in presc
//        setItemData when change item by barcode in pharmacy, how to set barcode
//        default message
///////////////////////////////////////////////
//        change project icon
//        set default add in now presc area
//        face book page & group
//        set templates gallery
//        remember password
//        nodeMailer send congratulation no reply mail
///////////////////////////////////
//        export LC_ALL=C
//        to creat admin user: MONGO_SEED=true grunt --force
//        Database Seeding:			Local user added with password set to CF54QwDKhsVnpBCFwyVksDVvCCve9qEHhjNmN6
//        Database Seeding:			Local admin added with password set to VhAexfVdjK7xx6gV6uBJ1m4FYs
///////////////////////
//        Push to repo
//        First time
//        git init
//        git remote add origin https://M_Abdulah@bitbucket.org/mshafy/mshafy.git
//        git remote add origin https://M_Abdulah@bitbucket.org/M_Abdulah/poraqserver.git
//        git remote add origin https://M_Abdulah@bitbucket.org/M_Abdulah/poraqmobiapp.git
//        git remote add origin https://M_Abdulah:mohamad.at.c9atgmail.com@bitbucket.org/mshafy/mshafy.git (if you need to provide password within your command)
//        git remote add origin https://M_Abdulah:mohamad.a.c9atgmail.com@bitbucket.org/M_Abdulah/poraqserver.git (if you need to provide password within your command)
//        Update
//        Push your work to bitbucket
//        git add . //or specific path for file or folder
//        git commit -m "....."
//        git push origin master
//
//        pull from repo to update server
//        connect to your server using ssh (linux)
//        sudo ssh 165.22.208.137
//        sudo ssh 46.101.142.93
//        root password  mohamad.a.c9atgmail.com
//        Fist time
//        browse to your folder
//        git clone https://M_Abdulah:mohamad.a.c9atgmail.com@bitbucket.org/mshafy/mshafy.git
//        update
//        cd /opt/mshafy
//        git pull
//
//
//        ---full access directory command: chmod  777 . -R
//        --- solve atom permission denied: sudo chown -R `whoami` ~/.atom
//        qtyText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

//    public void sendSMS(String phoneNo, String msg) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//            Toast.makeText(getApplicationContext(), "Message Sent",
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception ex) {
//            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
//        }
//    }
//    public static String getUrl(String url) {
//        final String uri = url; //JSONObject parameters = body;
//        AsyncTask xx = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                OkHttpClient client = new OkHttpClient();
//                client.setConnectTimeout(30, TimeUnit.SECONDS);
//                client.setReadTimeout(30, TimeUnit.SECONDS);
//                client.setWriteTimeout(30, TimeUnit.SECONDS);
//                Request request = new Request.Builder()
//                        .url(uri)
//                        .build();
//                try {
//                    Response response = new OkHttpClient().newCall(request).execute();
//                    Log.e("00000", response.code() + "");
//                    return response.body().string();
//                } catch (IOException e) {
//                    Log.e("error0000000: " + uri, e.toString());
//                    return ("failed: " + e.toString());
//                }
//            }
//        }.execute();
//        try {
//            return xx.get().toString();
//        } catch (ExecutionException e) {
//            Log.e("error1111111111: ", e.toString());
//            return "failed";
//        } catch (InterruptedException e) {
//            Log.e("error2222222222: ", e.toString());
//            return "failed";
//        }
//    }
