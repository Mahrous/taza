package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransferActivity extends AppCompatActivity {
    Toolbar toolbar;
    ExpandableListView ordersListView;
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner orderStatesSp;
    OrdersExpandableListViewAdapter adapter;
    ArrayList<String> partnersArray = new ArrayList<>(), matesArray = new ArrayList<>();
    Integer opNo;
    Boolean finished = false;
    ArrayList<String> myRolesArray = new ArrayList<>();
    JSONArray mateRoles = new JSONArray();
    JSONObject ops = new JSONObject(), results = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        toolbar = findViewById(R.id.tool_bar_transfer);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.transfer_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        ordersListView = findViewById(R.id.ordersExpandableListView);
//        transferOrdersBtn = findViewById(R.id.transferOrdersBtn);
        orderStatesSp = findViewById(R.id.orderStatesSp);

        orderStatesSp.setAdapter(new ArrayAdapter<>(TransferActivity.this, R.layout.single_row_myorder_spin,
                new String[]{"مطلوب", "تحضير", "مراجعة", "نقل", "استلام", "جاهز للتوزيع", "توزيع", "تقفيل", "تحويل أموال", "كل العمليات"}));

        setObjectsEvents();
        getOfficeData();


        adapter = new OrdersExpandableListViewAdapter(this);
        ordersListView.setAdapter(adapter);
        finished = true;
    }

    void getOfficeData() {
        try {
            JSONObject results = new JSONObject();
            if (General.isAdmin) {
                results = General.postUrl(General.url + "careers/getOfficeData",
                        new JSONObject().put("storeId", General.appStores.getJSONObject(0).getString("_id")));
                //TODO admin choose store to work with

            } else {
                results = General.postUrl(General.url + "careers/getOfficeData",
                        new JSONObject().put("storeId", General.user.getJSONObject("emp").getJSONObject("str").getString("id")));

            }
            if (results.length() == 0 || !results.getBoolean("state")) {
                General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                Log.e("eeeeeeeeee", "failed !!! " + results.toString());
            } else {
//                General.showToast(TransferActivity.this, "تم إرسال الطلب بنجاح ...");
                General.mates = results.getJSONArray("mates");
                General.office = results.getJSONObject("office");
                General.partners = results.getJSONArray("partners");
//                General.cars = results.getJSONArray("cars");
            }
        } catch (JSONException e) {
            General.handleError("JSONException qqq: ", e.toString());
        }
    }

    JSONObject getOpNo() {
        opNo = 0;
        try {
            if (orderStatesSp.getSelectedItem().toString().matches("كل العمليات")) {
                if (General.isAdmin || General.user.getJSONObject("emp").getJSONArray("ops").getJSONObject(0).getBoolean("av")) {
                    ops.put("nowOp", 10).put("ops", new JSONArray().put(210).put(220).put(310).put(410).put(510).put(610).put(620).put(710).put(810).put(910).put(1010));
                } else {
                    ops.put("nowOp", 10).put("ops", new JSONArray().put(210).put(220).put(310).put(410).put(620).put(910));
                }
            } else {
                switch (orderStatesSp.getSelectedItem().toString()) {
                    case "مطلوب":
                        opNo = 210;
                        break;
                    case "تحضير":
                        opNo = 220;
                        break;
                    case "مراجعة":
                        opNo = 310;
                        break;
                    case "نقل":
                        opNo = 410;
                        break;
                    case "استلام":
                        opNo = 510;
                        break;
                    case "جاهز توزيع":
                        opNo = 610;
                        break;
                    case "توزيع":
                        opNo = 620;
                        break;
                    case "تقفيل":
                        opNo = 710;
                        break;
                    case "تحويل أموال":
                        opNo = 810;
                        break;
                    case "إعلام بالخصم":
                        opNo = 910;
                        break;
                    case "مطابقة":
                        opNo = 1010;
                        break;

                }
                if (General.isAdmin || General.user.getJSONObject("emp").getJSONArray("ops").getJSONObject(0).getBoolean("av")) {
                    ops.put("nowOp", opNo).put("ops", new JSONArray().put(opNo));
                } else {
                    if (opNo == 610 || opNo == 620 || opNo == 810 || opNo == 1010) {
                        ops.put("nowOp", opNo).put("ops", new JSONArray().put(0));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return ops;
    }

    void getOrders() {
        try {
//            results = General.postUrl(General.url + "careers/getOfficeData",
//                    new JSONObject().put("storeId", General.appStores.getJSONObject(0).getString("_id")));
//            //TODO admin choose store to work with
            if (General.isAdmin) {
                results = General.postUrl(General.url + "orders/findOrders",
                        new JSONObject()
//                                .put("jb", General.user.getJSONObject("emp").getString("jb"))
                                .put("ops", getOpNo().getJSONArray("ops"))
                                .put("userId", General.user.getString("_id"))
                                .put("storeId", General.appStores.getJSONObject(0).getString("_id"))
                );

            } else {
                results = General.postUrl(General.url + "orders/findOrders",
                        new JSONObject()
                                .put("jb", General.user.getJSONObject("emp").getString("jb"))
                                .put("opNo", getOpNo().getJSONArray("ops"))
                                .put("userId", General.user.getString("_id"))
                                .put("storeId", General.user.getJSONObject("emp").getJSONObject("str").getString("id"))
                );
            }
            if (results.length() == 0 || !results.getBoolean("state")) {
                General.showToast(TransferActivity.this, "Incorrect data: ", Toast.LENGTH_LONG);
                Log.e("eeeeeeeeee", "failed !!! " + results.toString());
            } else {
                General.user.put("emp", results.getJSONObject("emp"));
                General.storeOrders = results.getJSONArray("orders");
                adapter = new OrdersExpandableListViewAdapter(this);
                ordersListView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            General.handleError("JSONException www: ", e.toString());
        }
    }

    void setObjectsEvents() {

        orderStatesSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
                if (finished) {
                    getOpNo();
                    adapter.getFilter().filter(opNo.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        ordersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
//                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
//                    int childPosition = ExpandableListView.getPackedPositionChild(id);
//                    // You now have everything that you would as if this was an OnChildClickListener()
//                    // Add your logic here.
//
//                    // Return true as we are handling the event.
//                    return true;
//                }
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {//if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
//            try {
//                JSONObject cc = new JSONObject(parent.getAdapter().getItem(position).toString());
//                callIntent.setData(Uri.parse("tel:"+cc.getJSONObject("adrs").getString("mo")));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    LinearLayout layout = new LinearLayout(TransferActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final Button taskBtn = new Button(TransferActivity.this);
                    taskBtn.setText("مرتجع بالكامل");
                    final Button callClientBtn = new Button(TransferActivity.this);
                    callClientBtn.setText("اتصال بالعميل");
                    layout.addView(taskBtn);
                    layout.addView(callClientBtn);
                    final AlertDialog alertDialog = new AlertDialog.Builder(TransferActivity.this)
                            .setView(layout)
                            .setTitle("عمليات خاصة ...")
                            .show();
                    taskBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    callClientBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (ActivityCompat.checkSelfPermission(TransferActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                try {
                                    JSONObject cc = new JSONObject(parent.getAdapter().getItem(position).toString());
                                    callIntent.setData(Uri.parse("tel:" + cc.getJSONObject("adrs").getString("mo")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                TransferActivity.this.startActivity(callIntent);
                            } else {
                                Toast.makeText(TransferActivity.this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
//                                ActivityCompat.requestPermissions((TransferActivity)context, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
                                ActivityCompat.requestPermissions(TransferActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                            }
                            alertDialog.dismiss();
                        }
                    });

                    return true;
                }
//                else {
//                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
//                    int childPosition = ExpandableListView.getPackedPositionChild(id);
//                    // You now have everything that you would as if this was an OnChildClickListener()
//                    // Add your logic here.
//
//                    // Return true as we are handling the event.
//                    return true;
//                }

                return false;
            }
        });

//        ordersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(final AdapterView<?> arg0, View arg1, final int pos, long id) {
//
//            LinearLayout layout = new LinearLayout(TransferActivity.this);
//            layout.setOrientation(LinearLayout.VERTICAL);
//            final Button taskBtn = new Button(TransferActivity.this);taskBtn.setText("مرتجع بالكامل");
//            final Button callClientBtn = new Button(TransferActivity.this);callClientBtn.setText("اتصال بالعميل");
//            layout.addView(taskBtn);
//            layout.addView(callClientBtn);
//            final AlertDialog alertDialog = new AlertDialog.Builder(TransferActivity.this)
//                    .setView(layout)
//                    .setTitle("عمليات خاصة ...")
//                    .show();
//            taskBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//            callClientBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                if (ActivityCompat.checkSelfPermission(TransferActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    try {
//                        JSONObject cc = new JSONObject(arg0.getAdapter().getItem(pos).toString());
//                        callIntent.setData(Uri.parse("tel:"+cc.getJSONObject("adrs").getString("mo")));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    TransferActivity.this.startActivity(callIntent);
//                }else{
//                    Toast.makeText(TransferActivity.this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
////                                ActivityCompat.requestPermissions((TransferActivity)context, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
//                    ActivityCompat.requestPermissions(TransferActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
//                }
//                alertDialog.dismiss();
//                }
//            });
//
//            return true;
//            }
//        });
    }

    private static final int MENU_ITEM_ITEM1 = 1;
    private static final int MENU_ITEM_ITEM2 = 2;
    private static final int MENU_ITEM_ITEM3 = 3;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "تحويل أموال");
        menu.add(Menu.NONE, MENU_ITEM_ITEM2, Menu.NONE, "عهد");
        menu.add(menu.NONE, MENU_ITEM_ITEM3, Menu.NONE, "صلاحيات");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_ITEM1:
                Double debt = 0.0;
                LinearLayout layout = new LinearLayout(TransferActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView deptTV = new TextView(TransferActivity.this);
                final EditText amountET = new EditText(TransferActivity.this);
                amountET.setHint("المبلغ المحول");
                amountET.setSelectAllOnFocus(true);
                try {
                    deptTV.setHint("عهدتك الحالية هى: " + General.user.getString("wlt"));
                    amountET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    amountET.setText(Math.abs(General.user.getDouble("wlt")) + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final TextView toTV = new TextView(TransferActivity.this);
                toTV.setHint("التحويل إلى");
                final Spinner toSp = new Spinner(TransferActivity.this);
                try {
                    partnersArray = new ArrayList<>();
                    for (int j = 0; j < General.partners.length(); j++) {
                        partnersArray.add(General.partners.getJSONObject(j).getString("nm"));
                    }
                    General.postUrl(General.url + "finances/getUserDebt", new JSONObject().put("userId", General.user.getString("_id")));
                    deptTV.setHint("عهدتك الحالية هى: " + debt);
                    amountET.setText((int) Math.ceil(debt) + "");
                } catch (JSONException e) {
                    General.handleError("fffffff: ", e.toString());
                }
                toSp.setAdapter(new ArrayAdapter<>(TransferActivity.this, R.layout.single_row_myorder_spin, partnersArray));

                layout.addView(deptTV);
                layout.addView(amountET);
                layout.addView(toTV);
                layout.addView(toSp);
                final AlertDialog alertDialog = new AlertDialog.Builder(TransferActivity.this)
                        .setView(layout)
                        .setTitle("بيانات الإستلام و التسليم ...")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (amountET.getText().toString().length() == 0 || Integer.parseInt(amountET.getText().toString()) < 1) {
                            General.showToast(TransferActivity.this, "سجل مبلغ أكبر من صفر !!!");
                            return;
                        }
                        try {
                            JSONObject results = General.postUrl(General.url + "orders/saveNewOrder", new JSONObject().put("order", new JSONObject()
                                    .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                    .put("to", new JSONObject().put("nm", General.partners.getJSONObject(toSp.getSelectedItemPosition()).getString("nm")).put("id", General.partners.getJSONObject(toSp.getSelectedItemPosition()).getString("_id")))
                                    .put("tts", new JSONObject().put("tN", Integer.parseInt(amountET.getText().toString())))
                                    .put("op", new JSONObject().put("nm", "mTO").put("lNm", "تحويل أموال"))
                                    .put("st", 0)
                                    .put("ops", new JSONObject()
                                            .put("nm", "تحويل أموال")// money transfer order
                                            .put("no", 810)// money transfer order
                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("to", new JSONObject().put("nm", General.partners.getJSONObject(toSp.getSelectedItemPosition()).getString("nm")).put("id", General.partners.getJSONObject(toSp.getSelectedItemPosition()).getString("_id")))
                                    )
                            ));
                            if (results.toString().contains("ok")) {
                                alertDialog.dismiss();
                                General.showToast(TransferActivity.this, "تمت العملية بنجاح !!!");

                            }
                        } catch (JSONException e) {
                            General.handleError("Transfer, 980: ", e.toString());
                        }
                    }
                });
                return true;
            case MENU_ITEM_ITEM2:
                LinearLayout layout2 = new LinearLayout(TransferActivity.this);
                layout2.setOrientation(LinearLayout.VERTICAL);
                final ListView debtsLV = new ListView(TransferActivity.this);
                try {
                    JSONObject results = General.postUrl(General.url + "finances/getUsersDebts", new JSONObject().put("storeId", General.myStore.getString("_id")));
                    JSONArray debts = results.getJSONArray("dbts");
                    ArrayList<String> debtsArray = new ArrayList<>();
                    for (int i = 0; i < debts.length(); i++) {
                        debtsArray.add(debts.getJSONObject(i).getString("nm") + ",  " + debts.getJSONObject(i).getString("wlt"));//debts.getJSONObject(j).getJSONObject("emp").getJSONObject("wlt").getString("dbt")
                    }
                    debtsLV.setAdapter(new ArrayAdapter<>(TransferActivity.this,R.layout.single_row_myorder_spin, debtsArray));

                } catch (JSONException e) {
                    General.handleError("fffffff: ", e.toString());
                }
//                addressesLV.setAdapter(new ArrayAdapter<>(RegisterActivity.this,android.R.layout.simple_spinner_item, addressesArray));

                layout2.addView(debtsLV);
                final AlertDialog alertDialog2 = new AlertDialog.Builder(TransferActivity.this)
                        .setView(layout2)
                        .setTitle("العهد ...")
                        .setPositiveButton(android.R.string.ok, null)
//                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog2.dismiss();
                    }
                });

                return true;

            case MENU_ITEM_ITEM3:
                LinearLayout layout3 = new LinearLayout(TransferActivity.this);
                layout3.setOrientation(LinearLayout.VERTICAL);
                final TextView matesTV = new TextView(TransferActivity.this);
                matesTV.setHint("الزميل: ");
                final Spinner matesSp = new Spinner(TransferActivity.this);
                final ListView rolesLV = new ListView(TransferActivity.this);
                rolesLV.setItemsCanFocus(false);
                rolesLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                try {
                    if (!(General.isAdmin || General.user.getJSONObject("emp").getJSONArray("rls").toString().contains("صلاحيات"))) {
                        General.showToast(TransferActivity.this, "Access denied !!!");
                        return true;
                    }
                    if (General.isAdmin) {
                        for (int i = 0; i < General.user.getJSONObject("emp").getJSONArray("rls").length(); i++) {
                            myRolesArray.add(General.user.getJSONObject("emp").getJSONArray("rls").getString(i));
                        }
                        int x = 5;
//                        EditText dd = new EditText(TransferActivity.this);
//                        dd.setText(x+"");
//                        Log.e("fffff", myRolesArray.toString());
                    } else {
                        for (int i = 0; i < General.user.getJSONObject("emp").getJSONArray("rls").length(); i++) {
                            myRolesArray.add(General.user.getJSONObject("emp").getJSONArray("rls").getString(i));
                        }
                    }
                    rolesLV.setAdapter(new ArrayAdapter<>(TransferActivity.this, android.R.layout.simple_list_item_multiple_choice, myRolesArray));
                    matesArray = new ArrayList<>();
                    for (int j = 0; j < General.mates.length(); j++) {
                        matesArray.add(General.mates.getJSONObject(j).getString("nm"));
                    }
                    matesSp.setAdapter(new ArrayAdapter<>(TransferActivity.this,R.layout.single_row_myorder_spin, matesArray));
                    mateRoles = General.mates.getJSONObject(matesSp.getSelectedItemPosition()).getJSONObject("emp").getJSONArray("rls");

                } catch (JSONException e) {
                    General.handleError("fgfg: ", e.toString());
                }
                matesSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
                        try {
                            mateRoles = General.mates.getJSONObject(matesSp.getSelectedItemPosition()).getJSONObject("emp").getJSONArray("rls");
                            rolesLV.clearChoices();
                            for (int j = 0; j < mateRoles.length(); j++) {
                                rolesLV.setItemChecked(myRolesArray.indexOf(mateRoles.getString(j)), true);
                            }
                        } catch (JSONException e) {
                            General.handleError("trans", e.toString());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });
                layout3.addView(matesTV);
                layout3.addView(matesSp);
                layout3.addView(rolesLV);
                final AlertDialog alertDialog3 = new AlertDialog.Builder(TransferActivity.this)
                        .setView(layout3)
                        .setTitle("ضبط صلاحيات الموظفين ...")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                alertDialog3.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (int i = 0; i < rolesLV.getCount(); i++) {
                                if (rolesLV.isItemChecked(i)) {//&& !mateRoles.toString().contains(myRolesArray.get(i))//checked.get(i)
                                    if (!mateRoles.toString().contains(myRolesArray.get(i))) {
                                        mateRoles.put(myRolesArray.get(i));/* do whatever you want with the checked item */
                                    }
                                } else {
                                    for (int j = 0; j < mateRoles.length(); j++) {
                                        if (mateRoles.getString(j).matches(myRolesArray.get(i))) {
                                            mateRoles.remove(j);
                                        }
                                    }
                                }
                            }
                            JSONObject results = General.postUrl(General.url + "careers/editRoles", new JSONObject()
                                    .put("empId", General.mates.getJSONObject(matesSp.getSelectedItemPosition()).getString("_id"))
                                    .put("rls", mateRoles)
                            );
//                    JSONObject results = new JSONObject(op);
                            if (results.getString("st").matches("ok")) {
                                General.showToast(TransferActivity.this, "succeeded !");
                            } else if (results.getString("st").matches("no")) {
                                General.showToast(getApplicationContext(), "Incorrect data: ", Toast.LENGTH_LONG);
                            }
                        } catch (JSONException e) {
                            General.handleError("Trans, 666: ", e.toString());
                        }
                    }
                });
                return true;

            default:
                return false;
        }
    }
}
