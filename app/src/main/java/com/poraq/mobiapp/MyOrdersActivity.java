package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyOrdersActivity extends AppCompatActivity {
    Spinner myOrdersSp, deliveryEvSp, pricesEvSp, qualityEvSp;
    RecyclerView ordersLV;
    Button evOrderBtn;
    Button printOrderBtn;
    JSONObject nowOrder;
    OrderAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> ordersArray;
    ArrayList<OrderModel> orderItemsArray;

    TextView date, value, deliver, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        printOrderBtn = findViewById(R.id.printOrderBtn);
        printOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
            }
        });
        myOrdersSp = findViewById(R.id.myOrdersSp);
        deliveryEvSp = findViewById(R.id.deliveryEvSp);
        pricesEvSp = findViewById(R.id.pricesEvSp);
        qualityEvSp = findViewById(R.id.qualityEvSp);
        ordersLV = findViewById(R.id.ordersLV);
        evOrderBtn = findViewById(R.id.evOrderBtn);

        date = findViewById(R.id.order_received_date);
        value = findViewById(R.id.order_value);
        deliver = findViewById(R.id.order_delivery);
        total = findViewById(R.id.order_total);

        swipeRefreshLayout = findViewById(R.id.orderRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Toast.makeText(MyOrdersActivity.this, "I am refreshed", Toast.LENGTH_SHORT).show();
                getMyOrders();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        final String[] degrees = new String[]{"10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0"};
        deliveryEvSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, degrees));
        qualityEvSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, degrees));
        pricesEvSp.setAdapter(new ArrayAdapter<>(this, R.layout.single_row_myorder_spin, degrees));

        getMyOrders();

        evOrderBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (myOrdersSp.getCount() == 0) {
                    General.showToast(MyOrdersActivity.this, "اختر طلب أولا للتقييم !!!");
                    return;
                }
                try {

                    nowOrder.getJSONObject("op")
                            .put("dE", deliveryEvSp.getSelectedItem().toString())
                            .put("qE", qualityEvSp.getSelectedItem().toString())
                            .put("pE", pricesEvSp.getSelectedItem().toString())
                            .put("uS", nowOrder.getJSONObject("op").getInt("uS") < 1 ? 1 : 2);
                    Log.e("yyyyyyyy", nowOrder.toString());

                    JSONObject results = General.postUrl(General.url + "orders/evaluateOrder"
                            , new JSONObject().put("order", nowOrder));
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(MyOrdersActivity.this, "هناك خطأ ما !!!");
                        Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                    } else {
                        if (nowOrder.getJSONObject("op").getInt("uS") < 1) {
                            General.showToast(getApplicationContext(), "تم التقييم بنجاح, و سيظل الطلب ظاهر لحين التسليم, و بالإمكان إعادة التقييم مرة أخرى ...", Toast.LENGTH_LONG);
                        } else {
                            General.showToast(getApplicationContext(), "تم التقييم بنجاح ...", Toast.LENGTH_LONG);
                            General.mySuspendedOrders.remove(myOrdersSp.getSelectedItemPosition());

                            ordersArray.remove(myOrdersSp.getSelectedItemPosition());
                            myOrdersSp.setAdapter(new ArrayAdapter<>
                                    (MyOrdersActivity.this, R.layout.single_row_myorder_spin, ordersArray));
                            if (ordersArray.size() == 0) {
                                orderItemsArray.clear();
                                adapter.notifyDataSetChanged();
                                value.setText("");
                                total.setText("");
                                date.setText("");
                                deliver.setText("");
                                //evacuate order
                            }
                        }
                    }
                } catch (JSONException e) {
                    General.handleError("JSONException 003: ", e.toString());
                }
            }
        });

        myOrdersSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderItemsArray = new ArrayList<>();
                try {
                    nowOrder = General.mySuspendedOrders.getJSONObject(position);
                    Log.e("bbbbbb", nowOrder.toString());
                    deliveryEvSp.setSelection(Arrays.asList(degrees).indexOf(nowOrder.getJSONObject("op").getString("dE")));
                    qualityEvSp.setSelection(Arrays.asList(degrees).indexOf(nowOrder.getJSONObject("op").getString("qE")));
                    pricesEvSp.setSelection(Arrays.asList(degrees).indexOf(nowOrder.getJSONObject("op").getString("pE")));

                    deliver.setText(nowOrder.getJSONObject("tts").getString("dFs"));
                    date.setText(nowOrder.getJSONObject("op").getString("d").split("T")[0] + "  من " + nowOrder.getJSONObject("op").getString("f") +
                            "الى " + nowOrder.getJSONObject("op").getString("t"));

                    value.setText(nowOrder.getJSONObject("tts").getString("tS"));
                    total.setText(nowOrder.getJSONObject("tts").getString("tN"));

                    JSONArray orderItems = nowOrder.getJSONArray("dts");
                    for (int i = 0; i < orderItems.length(); i++) {
                        JSONObject item = orderItems.getJSONObject(i);//+","+order.getJSONObject("tts").getString("ta")

                        orderItemsArray.add(
                                new OrderModel(
                                        item.getString("nm"),
                                        item.getString("oQ") + "  " +
                                                item.getString("unt"),
                                        item.getString("sQ"),
                                        item.getString("p")));

                    }
                } catch (JSONException e) {
                    General.handleError("MyOrdersActivity, 777: ", e.toString());
                }
                ordersLV.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this, LinearLayoutManager.VERTICAL, false));
                adapter = new OrderAdapter(orderItemsArray, MyOrdersActivity.this);
                ordersLV.setAdapter(adapter);
                // ordersLV.setAdapter(new ArrayAdapter<>(MyOrdersActivity.this,android.R.layout.simple_list_item_1, orderItemsArray));
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void populateOrder() {


    }


    private void createPdf() {

        Log.e("pmpm", "createPdf: ");
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 4000, 2).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.print_my_orders, null);
        RecyclerView recyclerView = content.findViewById(R.id.ordersRV);
        recyclerView.setAdapter(ordersLV.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this , RecyclerView.VERTICAL , false));

        TextView orderNum = content.findViewById(R.id.p_myOrdersTV);
        orderNum.setText(myOrdersSp.getSelectedItem().toString());
        TextView pDate = content.findViewById(R.id.P_order_received_date);
        pDate.setText(date.getText());

        TextView pValue = content.findViewById(R.id.p_order_value);
        pValue.setText(value.getText());
        TextView pDeliver = content.findViewById(R.id.p_order_delivery);
        pDeliver.setText(deliver.getText());
        TextView pTotal = content.findViewById(R.id.p_order_total);
        pTotal.setText(total.getText());
        TextView userName = content.findViewById(R.id.p_user_name);
        userName.setText(((MainActivity)General.context).navUsername.getText());
        TextView userAdress = content.findViewById(R.id.p_user_address);
        userAdress.setText(MainActivity.addressOrder);

        Log.e("looolgg", "createPdf: " );


        int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);
        content.measure(measureWidth, measuredHeight);
        content.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);
        // add more pages
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + "test-2.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();

    }


    void getMyOrders() {
        try {
            JSONObject results = General.postUrl(General.url + "orders/findMySuspendedOrders", new JSONObject()
                    .put("id", General.user.get("_id")));
            Log.e("dddddddd", results.toString());
            if (results.length() == 0 || !results.getBoolean("state")) {
                General.showToast(MyOrdersActivity.this, "هناك خطأ ما !!!");
                Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
            } else {
//                General.showToast(MyOrdersActivity.this, "تم التسجيل بنجاح");
                General.mySuspendedOrders = results.getJSONArray("orders");
                ordersArray = new ArrayList<>();
                JSONObject order;
                for (int i = 0; i < General.mySuspendedOrders.length(); i++) {
                    order = General.mySuspendedOrders.getJSONObject(i);//+","+order.getJSONObject("tts").getString("ta")
                    ordersArray.add("طلب رقم : " + order.getString("no") + "\t\t بتاريخ : " + order.getJSONObject("op").getString("d").split("T")[0] + " ");
                }
                myOrdersSp.setAdapter(new ArrayAdapter<>(MyOrdersActivity.this, R.layout.single_row_myorder_spin, ordersArray));
            }
        } catch (JSONException e) {
            General.handleError("MyOrder000df", e.toString());
        }
    }

}
//                new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//
//                        OkHttpClient client = new OkHttpClient();
//                        client.setConnectTimeout(30, TimeUnit.SECONDS);
//                        client.setReadTimeout(30, TimeUnit.SECONDS);
//                        client.setWriteTimeout(30, TimeUnit.SECONDS);
//                        RequestBody body = RequestBody.create(General.JSON, PayOrdersData.toString());
//                        Request request = new Request.Builder()
//                                .url(General.url+"orders/userPayOrder")
//                                .post(body)
//                                .build();
//                        Response response = null;
//                        try {
//                            response = new OkHttpClient().newCall(request).execute();
//                            return  response.body().string();
//                        }catch(IOException e){
//                            Log.e("0000000000000000: ", e.toString());
//                            return ("failed: "+e.toString());
//                        }
//                    }
//                    @Override
//                    protected void onPostExecute(Object result) {
//                        Log.e("111111111111111111111", result.toString());
//                        try {
//                            JSONObject results = new JSONObject(result.toString());
//                            if (results.getString("st").matches("ok")){
//                                General.mySuspendedOrders = results.getJSONArray("orders");
//
//                                ArrayList<String> ordersArray = new ArrayList<String>();
//                                JSONObject order = new JSONObject();
//                                for (int i=0;i<General.mySuspendedOrders.length();i++){
//                                    order = General.mySuspendedOrders.getJSONObject(i);//+","+order.getJSONObject("tts").getString("ta")
//                                    ordersArray.add(order.getString("cd").split("T")[0]+", "+order.getJSONObject("tts").getString("ta"));
//                                }
//                                myOrdersSp.setAdapter(new ArrayAdapter<String>(MyOrdersActivity.this,android.R.layout.simple_spinner_item, ordersArray));
//
//
//                            }else if (results.getString("st").matches("no")){
//                                General.showToast(getApplicationContext(),"Incorrect data: ", Toast.LENGTH_LONG);
//                            }
//                        }catch (JSONException e){
//                            General.handleError("JSONException 003: ",e.toString());
//                        }
//                    }
//                }.execute() ;
//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                JSONObject ordersData = new JSONObject();
//                try {
//                    ordersData.put("id", General.user.get("_id"));
//                }catch (JSONException e){
//
//                }
//                OkHttpClient client = new OkHttpClient();
//                client.setConnectTimeout(30, TimeUnit.SECONDS);
//                client.setReadTimeout(30, TimeUnit.SECONDS);
//                client.setWriteTimeout(30, TimeUnit.SECONDS);
//                RequestBody body = RequestBody.create(General.JSON, ordersData.toString());
//                Request request = new Request.Builder()
//                        .url(General.url+"orders/findMySuspendedOrders")//findMySuspendedOrders
//                        .post(body)
//                        .build();
//                Response response = null;
//                try {
//                    response = new OkHttpClient().newCall(request).execute();
//                    return  response.body().string();
//                }catch(IOException e){
//                    Log.e("my orders 001: ", e.toString());
//                    return ("failed: "+e.toString());
//                }
//            }
//            @Override
//            protected void onPostExecute(Object result) {
//                Log.e("my orders 002: ", result.toString());
//                try {
//                    JSONObject results = new JSONObject(result.toString());
//                    if (results.getString("st").matches("ok")){
//                        General.mySuspendedOrders = results.getJSONArray("orders");
//                        ordersArray = new ArrayList<String>();
//                        JSONObject order = new JSONObject();
//
//                        for (int i=0;i<General.mySuspendedOrders.length();i++){
//
//                            order = General.mySuspendedOrders.getJSONObject(i);//+","+order.getJSONObject("tts").getString("ta")
//                            Log.e("sssssssssss", order.getJSONObject("ex").getString("d").split("T")[0]);
//                            ordersArray.add("رقم: "+order.getString("no")+", "+order.getJSONObject("ex").getString("d").split("T")[0]+" "+order.getJSONObject("ex").getString("frm")+":"+order.getJSONObject("ex").getString("frm")+", مطلوب: "+order.getJSONObject("tts").getString("tO")+", مورد: "+order.getJSONObject("tts").getString("tS"));
//                        }
//                        myOrdersSp.setAdapter(new ArrayAdapter<String>(MyOrdersActivity.this,android.R.layout.simple_spinner_item, ordersArray));
//
//
//                    }else if (results.getString("st").matches("no")){
//                        General.showToast(getApplicationContext(),"Incorrect data: ", Toast.LENGTH_LONG);
//                    }
//                }catch (JSONException e){
//                    General.handleError("JSONException 003: ",e.toString());
//                }
//            }
//        }.execute() ;
