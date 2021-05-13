package com.poraq.mobiapp;

import android.app.AlertDialog;
import android.content.Context;

import android.graphics.Typeface;

import android.text.InputType;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class OrdersExpandableListViewAdapter extends BaseExpandableListAdapter implements Filterable {
    private Context context;
    private JSONArray filteredData = new JSONArray(), orderItems;
    private JSONObject nowOrder;
    String storeId = "";
    String storeNm = "";

    JSONObject results = new JSONObject();

    public OrdersExpandableListViewAdapter(Context context) {
        this.context = context;
        this.filteredData = General.storeOrders;
        try {
            this.storeId = General.isAdmin ? General.appStores.getJSONObject(0).getString("_id") : General.user.getJSONObject("emp").getJSONObject("str").getString("id");
            this.storeNm = General.isAdmin ? General.appStores.getJSONObject(0).getString("nm") : General.user.getJSONObject("emp").getJSONObject("str").getString("nm");
            ;

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getGroupCount() {
        return filteredData.length();
    }

    @Override
    public int getChildrenCount(int i) {
        try {
            return filteredData.getJSONObject(i).getJSONArray("dts").length();
        } catch (JSONException e) {
            General.handleError("JSONException111", e.toString());
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        try {
            return filteredData.getJSONObject(i);
        } catch (JSONException e) {
            General.handleError("JSONException222", e.toString());
            return null;
        }
    }

    @Override
    public Object getChild(int i, int i1) {
        try {
            return filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1);
        } catch (JSONException e) {
            General.handleError("oovv", e.toString());
            return null;
        }
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
//        try {
//            return General.storeOrders.getJSONObject(i).getJSONArray("dts").getLong(i1);
//        }catch (JSONException e){
//            return 0;
//        }
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int pos, boolean b, View view, ViewGroup viewGroup) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ex = inflater.inflate(R.layout.expand_main_list, null, false);



     /*   LinearLayout listLayout = new LinearLayout(context);
        listLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));*/

        final Button holdOrderBtnBtn =  ex.findViewById(R.id.ex_b);
        holdOrderBtnBtn.setFocusable(false);
        final TextView orderHeaderTV = ex.findViewById(R.id.ex_tv);
        orderHeaderTV.setTag("123" + pos);

     /*   orderHeaderTV.setTypeface(null, Typeface.BOLD);
        orderHeaderTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        orderHeaderTV.setTextColor(Color.BLACK);
        orderHeaderTV.setLines(4);
        orderHeaderTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
      */
      try {
            nowOrder = filteredData.getJSONObject(pos);
            String headerText = "";
            switch (nowOrder.getJSONArray("ops").getJSONObject(0).getString("nm")) {
                case "فى انتظار التحضير":// محضر
                    headerText = nowOrder.getString("no")
                            + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                            + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                            + ", " + nowOrder.getJSONObject("op").getString("f")
                            + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("حجز للتحضير");
                    break;
                case "فى التحضير":// محضر
                    headerText = nowOrder.getString("no")
                            + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                            + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                            + ", " + nowOrder.getJSONObject("op").getString("f")
                            + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("تحضير");
                    break;
                case "فى انتظار المراجعة":// سائق أو ديليفرى
                    Log.e("yyyyyyy", nowOrder.getJSONArray("ops").getJSONObject(0).toString());
                    headerText = nowOrder.getString("no")
                            + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                            + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                            + ", " + nowOrder.getJSONObject("op").getString("f")
                            + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("مراجعة");
                    break;
                case "فى انتظار النقل":// ديليفرى
                    headerText = nowOrder.getString("no")
                            + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                            + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                            + ", " + nowOrder.getJSONObject("op").getString("f")
                            + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("نقل");
                    break;
                case "فى انتظار الاستلام":// ديليفرى
                    switch (nowOrder.getJSONObject("op").getString("nm")) {
                        case "sO":
                        case "sI":
                            headerText = nowOrder.getString("no")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                                    + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                                    + ", " + nowOrder.getJSONObject("op").getString("f")
                                    + ", " + nowOrder.getJSONObject("op").getString("t");
                            break;
                        case "pO":
                        case "pI":
                            headerText = nowOrder.getString("no") + ", مشتروات";
                            break;
                        case "tO":
                        case "tI":
                            headerText = nowOrder.getString("no") + ", تحويلات";
                            break;
                    }
                    holdOrderBtnBtn.setText("استلام");
                    break;
                case "فى انتظار التوزيع":// ديليفرى
                    headerText = nowOrder.getString("no")
                            + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                            + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                            + ", " + nowOrder.getJSONObject("op").getString("f")
                            + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("حجز للتوزيع");
                    break;
                case "فى التوزيع":// ديليفرى
                    headerText =
                            nowOrder.getString("no")
                                    + ", " + nowOrder.getJSONObject("tts").getString("tN")
                                    + ", " + nowOrder.getJSONObject("tts").getString("dFs")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("ar")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("dt")
                                    + ", " + nowOrder.getJSONObject("frm").getString("nm")
                                    + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                                    + ", " + nowOrder.getJSONObject("op").getString("f")
                                    + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("توزيع");
                    break;
                case "فى انتظار التقفيل":// محاسب
                    headerText =
                            nowOrder.getString("no")
                                    + ", " + nowOrder.getJSONObject("tts").getString("tN")
                                    + ", " + nowOrder.getJSONObject("tts").getString("dFs")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("ar")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("dt")
                                    + ", " + nowOrder.getJSONObject("frm").getString("nm")
                                    + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                                    + ", " + nowOrder.getJSONObject("op").getString("f")
                                    + ", " + nowOrder.getJSONObject("op").getString("t");
                    holdOrderBtnBtn.setText("تقفيل");
                    break;
                case "تحويل أموال":// محاسب
                    headerText =
                            nowOrder.getString("no")
                                    + ", " + nowOrder.getJSONArray("ops").getJSONObject(0).getString("nm")
                                    + " مبلغ " + nowOrder.getJSONObject("tts").getInt("tN")
                                    + " من " + nowOrder.getJSONArray("ops").getJSONObject(0).getJSONObject("frm").getString("nm");
                    holdOrderBtnBtn.setText("استلام");
                    break;
                case "إعلام بالخصم":// محاسب
                    headerText =
                            nowOrder.getString("no")
                                    + ", " + nowOrder.getJSONObject("adrs").getString("nh")
                                    + ", " + nowOrder.getJSONObject("op").getString("d").split("T")[0]
                                    + ", " + nowOrder.getJSONObject("op").getString("f")
                                    + ", " + nowOrder.getJSONObject("op").getString("t")
                                    + ", " + nowOrder.getJSONArray("ops").getJSONObject(0).getString("nts");
                    holdOrderBtnBtn.setText("علم");
                    break;
            }
            orderHeaderTV.setText(headerText);
        } catch (JSONException e) {
            General.handleError("uuuuuu", e.toString());
        }

        holdOrderBtnBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
//                if (!(General.isAdmin
//                        || (General.user.getJSONObject("emp").getBoolean("av")&& General.user.getJSONObject("emp").getString("rls").contains(holdOrderBtnBtn.getText().toString())))
//                    ){
//                    General.showToast(context, "Access denied !!!");
//                    return;
//                }
                    Log.e("111111111111", "11111111111");

                    nowOrder = filteredData.getJSONObject(pos);
                    switch (holdOrderBtnBtn.getText().toString()) {
                        case "حجز للتحضير":
                            Log.e("0000000", "11111111111");
                            nowOrder.getJSONArray("ops").getJSONObject(0)
                                    .put("nm", "فى التحضير")
                                    .put("no", 220).put("fD", new Date())
                                    .put("to", new JSONObject().put("nm", General.user.getString("nm"))
                                            .put("id", General.user.getString("_id")));// for search
                            results = General.postUrl(General.url + "orders/saveOrder", new JSONObject()
                                    .put("order", nowOrder)
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("userId", General.user.getString("_id"))
                                    .put("storeId", General.isAdmin ? General.appStores.getJSONObject(0).getString("_id") : General.user.getJSONObject("emp").getJSONObject("str").getString("id")));
                            break;
                        case "تحضير":
                            nowOrder.getJSONArray("ops").getJSONObject(0)
                                    .put("nm", "محضر").put("no", 230)
                                    .put("fD", new Date());
                            nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                    .put("nm", "فى انتظار المراجعة")
                                    .put("no", 310)
                                    .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                    .put("to", new JSONObject().put("nm", storeNm).put("id", storeId))
                                    .put("lc", new JSONObject().put("nm", storeNm).put("id", storeId))
                            ));
                            results = General.postUrl(General.url + "orders/saveOrder", new JSONObject()
                                    .put("order", nowOrder)
                                    .put("userId", General.user.getString("_id"))
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("storeId", storeId));
                            break;
                        case "مراجعة":
                            nowOrder.getJSONArray("ops").getJSONObject(0)
                                    .put("nm", "تمت المراجعة")
                                    .put("no", 320).put("fD", new Date());
                            if (nowOrder.getJSONObject("adrs").getJSONObject("dst").getString("id").matches(General.office.getString("_id"))) {
                                nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"),
                                        new JSONObject()
                                                .put("nm", "فى انتظار التوزيع")
                                                .put("no", 610)
                                                .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                                .put("to", new JSONObject().put("nm", storeNm).put("id", storeId))
                                                .put("lc", new JSONObject().put("nm", storeNm).put("id", storeId))
                                ));
                            } else {
                                nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                        .put("nm", "فى انتظار النقل")
                                        .put("no", 410)
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", new JSONObject().put("nm", storeNm).put("id", storeId))
                                        .put("lc", new JSONObject().put("nm", storeNm).put("id", storeId))
                                ));
                            }
                            nowOrder.getJSONObject("op").put("nm", "sI").put("lNm", "فاتورة بيع");
                            results = General.postUrl(General.url + "orders/reviseSalesInv", new JSONObject()
                                    .put("order", General.calcTotals(nowOrder))
                                    .put("userId", General.user.getString("_id"))
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("storeId", storeId));
                            break;
                        case "نقل":
                            nowOrder.getJSONArray("ops").getJSONObject(0).put("fD", new Date())
                                    .put("to", new JSONObject().put("nm", General.user.getString("nm"))
                                            .put("id", General.user.getString("_id")));
                            nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                    .put("nm", "فى انتظار الاستلام")
                                    .put("no", 510)
                                    .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                    .put("to", nowOrder.getJSONObject("adrs").getJSONObject("dst"))
                                    .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"))));
                            results = General.postUrl(General.url + "orders/saveOrder", new JSONObject()
                                    .put("order", nowOrder)
                                    .put("userId", General.user.getString("_id"))
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("storeId", storeId)
                            );
                            break;
                        case "استلام":
                            switch (nowOrder.getJSONObject("op").getString("nm")) {
                                case "sO":
                                case "sI":
                                    nowOrder.getJSONArray("ops").getJSONObject(0).put("fD", new Date()).put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
//                                nowOrder.getJSONArray("ops").put(0,new JSONObject()
                                    nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                            .put("nm", "فى انتظار التوزيع")
                                            .put("no", 610)
                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("to", nowOrder.getJSONObject("adrs").getJSONObject("dst"))
                                            .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"))));
                                    results = General.postUrl(General.url + "orders/saveOrder", new JSONObject()
                                            .put("order", nowOrder)
                                            .put("userId", General.user.getString("_id"))
                                            .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                            .put("storeId", storeId)
                                    );
                                    break;
                                case "pO":
                                case "pI":
                                    Boolean isReturns = false;
                                    Double gap = 0.0;
                                    for (int i = 0; i < nowOrder.getJSONArray("dts").length(); i++) {
                                        if (nowOrder.getJSONArray("dts").getJSONObject(i).getDouble("sQ") < 0) {
                                            gap += (nowOrder.getJSONArray("dts").getJSONObject(i).getDouble("sQ") * nowOrder.getJSONArray("dts").getJSONObject(i).getDouble("p"));
                                            isReturns = true;
                                        }
                                    }

                                    nowOrder.put("op", new JSONObject().put("aS", 2).put("uS", 1).put("nm", "pI").put("lNm", "فاتورة شراء"));
                                    nowOrder.getJSONArray("ops").getJSONObject(0).put("fD", new Date()).put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
                                    nowOrder.getJSONArray("ops").getJSONObject(0)
                                            .put("no", 550)
                                            .put("nm", "تم التقفيل")
                                            .put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("lc", new JSONObject().put("nm", General.myStore.getString("nm")).put("id", General.myStore.getString("_id")))
                                            .put("dt", new Date().getTime());
                                    results = General.postUrl(General.url + "orders/finishPurchase", new JSONObject()
                                            .put("order", nowOrder)
                                            .put("gap", Math.floor(gap))
                                            .put("userId", General.user.getString("_id"))
                                            .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                            .put("storeId", storeId)
                                    );
                                    break;
                                case "tO":
                                case "tI":
                                    nowOrder.put("op", new JSONObject().put("nm", "tI").put("lNm", "فاتورة تحويل بضاعة"));
                                    nowOrder.getJSONArray("ops").getJSONObject(0).put("fD", new Date()).put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
                                    nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                            .put("nm", "تم التقفيل")
                                            .put("no", 720)
                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("to", nowOrder.getJSONObject("adrs").getJSONObject("dst"))
                                            .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"))));
                                    results = General.postUrl(General.url + "orders/finishTrans", new JSONObject()
                                            .put("order", nowOrder)
                                            .put("userId", General.user.getString("_id"))
                                            .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                            .put("storeId", storeId)
                                    );
                                    break;
                                case "mTO":
                                    nowOrder.put("op", new JSONObject().put("nm", "mTI").put("lNm", "فاتورة تحويل أموال"));
                                    nowOrder.getJSONArray("ops").getJSONObject(0).put("nm", "تم التحويل").put("no", 820)
                                            .put("fD", new Date()).put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
                                    results = General.postUrl(General.url + "orders/getMoney", new JSONObject()
                                            .put("orderId", nowOrder.getString("_id"))
                                            .put("userId", General.user.getString("_id"))
                                            .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                            .put("storeId", storeId)
                                    );
                                    break;
                            }
                            break;
                        case "حجز للتوزيع":
                            nowOrder.getJSONArray("ops").getJSONObject(0)
                                    .put("no", 620)
                                    .put("nm", "فى التوزيع").put("fD", new Date())
                                    .put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
                            results = General.postUrl(General.url + "orders/saveOrder", new JSONObject()
                                    .put("order", nowOrder)
                                    .put("userId", General.user.getString("_id"))
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("storeId", storeId)
                            );
                            break;
                        case "توزيع":
                            Boolean isReturns = false;
                            Double gap = 0.0;
                            for (int i = 0; i < nowOrder.getJSONArray("dts").length(); i++) {
                                if (nowOrder.getJSONArray("dts").getJSONObject(i).getDouble("sQ") < 0) {
                                    gap += (nowOrder.getJSONArray("dts").getJSONObject(i).getDouble("sQ") * nowOrder.getJSONArray("dts").getJSONObject(i).getDouble("p"));
                                    isReturns = true;
                                }
                            }
                            nowOrder.getJSONArray("ops").getJSONObject(0).put("fD", new Date())
                                    .put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
                            nowOrder.getJSONObject("tts").put("tP", nowOrder.getJSONObject("tts").getDouble("tN"));
                            if (isReturns) {
                                nowOrder.put("gap", Math.abs(gap));
                                nowOrder.getJSONObject("op").put("aS", 1).put("uS", 1);
                                nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                        .put("nm", "فى انتظار التقفيل")
                                        .put("no", 720)
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", nowOrder.getJSONObject("adrs").getJSONObject("dst"))
                                        .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"))));
                            } else {
                                nowOrder.getJSONObject("op").put("aS", 2).put("uS", 1);
//                            nowOrder.getJSONArray("ops").put(0,new JSONObject()
                                nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                        .put("nm", "تم التوزيع")
                                        .put("no", 630)
                                        .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                        .put("to", nowOrder.getJSONObject("adrs").getJSONObject("dst"))
                                        .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"))));
                            }

                            results = General.postUrl(General.url + "orders/deliverOrder", new JSONObject()
                                    .put("order", nowOrder)
                                    .put("userId", General.user.getString("_id"))
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("storeId", storeId)
                            );
                            break;
                        case "تقفيل":
                            try {
                                nowOrder.getJSONArray("ops").getJSONObject(0)
                                        .put("fD", new Date())
                                        .put("nm", "تم التقفيل")
                                        .put("no", 720)
                                        .put("to", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")));
                                if (nowOrder.getJSONObject("tts").getDouble("tN") < nowOrder.getJSONObject("tts").getDouble("tP")) {
//                                nowOrder.getJSONArray("ops").put(0,new JSONObject()
                                    nowOrder.put("ops", General.creatJArraay(nowOrder.getJSONArray("ops"), new JSONObject()
                                            .put("nm", "إعلام بالخصم")
                                            .put("no", 910)
                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
                                            .put("to", nowOrder.getJSONArray("op").getJSONObject(0).getJSONObject("frm"))
                                            .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"))));
                                } else {
                                    nowOrder.getJSONObject("op").put("aS", 2).put("uS", 1);
                                }
                                results = General.postUrl(General.url + "orders/finishSalesOrder", new JSONObject()
                                        .put("order", nowOrder)
                                        .put("userId", General.user.getString("_id"))
                                        .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                        .put("storeId", storeId)
                                );
                            } catch (JSONException e) {
                                General.handleError("EXP, yyy:", e.toString());
                            }
                            break;
                        case "علم":
                            nowOrder.getJSONObject("op").put("aS", 2);
                            nowOrder.getJSONArray("ops").getJSONObject(0)
                                    .put("nm", "تم العلم")
                                    .put("no", 920)
//                                .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
//                                .put("to", nowOrder.getJSONObject("adrs").getJSONObject("dst"))
                                    .put("lc", nowOrder.getJSONObject("adrs").getJSONObject("dst"));
//                        );
                            results = General.postUrl(General.url + "orders/saveOrder", new JSONObject()
                                    .put("orderId", nowOrder.getString("_id"))
                                    .put("userId", General.user.getString("_id"))
                                    .put("ops", ((TransferActivity) context).getOpNo().getJSONArray("ops"))
                                    .put("storeId", storeId)
                            );
                            break;
                    }
                    if (results.length() == 0 || !results.getBoolean("state")) {
                        General.showToast(context, "هناك خطأ ما, راجع مديرك !!!");
                        Log.e("eeeeeeeeee", "failed !!! " + results.toString());
                    } else {
                        General.showToast(context, "تم بنجاح");
                        General.storeOrders = results.getJSONArray("orders");
                        General.user.put("emp", results.getJSONObject("emp"));
                        filteredData = General.storeOrders;
                        notifyDataSetChanged();
                        if (results.getJSONObject("bal").length() > 0) {
                            General.verifyAccount(results.getJSONObject("bal"), context);
                        }
                    }
                } catch (JSONException e) {
                    General.handleError("cccccccccc", e.toString());
                }
            }
        });
        return ex;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, final ViewGroup viewGroup) {
       /* LinearLayout listLayout = new LinearLayout(context);
        listLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
*/
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View exChild = inflater.inflate(R.layout.expand_chiled_list, null, false);


        TextView itemNameTV = exChild.findViewById(R.id.tv3);

       /* itemNameTV.setTypeface(null, Typeface.BOLD);
        itemNameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        itemNameTV.setTextColor(Color.BLACK);*/

        TextView oQTV = exChild.findViewById(R.id.tv1);

       /* oQTV.setTypeface(null, Typeface.BOLD);
        oQTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        oQTV.setTextColor(Color.RED);*/

        TextView unitTV = exChild.findViewById(R.id.tv2);

        /*unitTV.setTypeface(null, Typeface.BOLD);
        unitTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        unitTV.setTextColor(Color.RED);*/

        TextView priceTV = exChild.findViewById(R.id.tv4);
        priceTV.setVisibility(View.GONE);

        final EditText sQET = exChild.findViewById(R.id.et1);
        sQET.setSelectAllOnFocus(true);
        sQET.setEnabled(false);

        /*sQET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        sQET.setSelectAllOnFocus(true);//sQET.setHint("كمية");
        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f);

        itemNameTV.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f));
        oQTV.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.04f));
        unitTV.setLayoutParams(childParam);
        priceTV.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.04f));
        sQET.setLayoutParams(childParam);
        sQET.setEnabled(false);*/

        itemNameTV.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                try {
                    nowOrder = filteredData.getJSONObject(i);
                    Log.e("child", nowOrder.getJSONArray("ops").getJSONObject(0).getString("no"));
                    if (
                            nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no") == 220
                                    || nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no") == 310
                                    || nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no") == 620
                                    || (nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no") == 710 && filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).getDouble("sQ") < 0)
                    ) {
                        LinearLayout layout = new LinearLayout(context);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        TextView labelTV = new TextView(context);
                        labelTV.setText("الكمية الفعلية");
                        Spinner reasonsSp = new Spinner(context);
                        reasonsSp.setAdapter(new ArrayAdapter<>(context, R.layout.single_row_myorder_spin, new String[]{"بضاعة غير صالحة", "طلب بالخطأ", "تحضير بالخطأ", "العميل غير رأيه", "أخرى"}));
                        final EditText quantityET = new EditText(context);
                        quantityET.setHint("الكمية");
                        quantityET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        layout.addView(labelTV);
                        layout.addView(quantityET);
                        layout.addView(reasonsSp);
                        final AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                                .setView(layout)
                                .setTitle("كميات ...")
                                .setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                        alertDialog1.getButton(alertDialog1.BUTTON_POSITIVE).setText("موافق");
                        alertDialog1.getButton(alertDialog1.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
                        alertDialog1.getButton(alertDialog1.BUTTON_POSITIVE).setTextSize(18);
                        alertDialog1.getButton(alertDialog1.BUTTON_NEGATIVE).setText("إلغاء");
                        alertDialog1.getButton(alertDialog1.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
                        alertDialog1.getButton(alertDialog1.BUTTON_NEGATIVE).setTextSize(18);

                        alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Double quantity = quantityET.getText().toString().trim().length() > 0 ? Double.parseDouble(quantityET.getText().toString()) : 0.0;
                                if (quantity <= 0) {
                                    quantityET.setError("سجل كمية صالحة !!!");
                                    quantityET.requestFocus();
                                    General.showToast(context, "سجل كمية صالحة !!!", Toast.LENGTH_LONG);
                                    return;
                                }
                                try {
                                    JSONObject newItem;
                                    nowOrder = filteredData.getJSONObject(i);
                                    orderItems = filteredData.getJSONObject(i).getJSONArray("dts");
                                    JSONObject item;
                                    switch (nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no")) {
                                        case 220:
                                        case 310:
                                            filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).put("sQ", quantity);
                                            for (int j = 0; j < General.storeOrders.length(); j++) {
                                                if (nowOrder.getString("_id").matches(General.storeOrders.getJSONObject(j).getString("_id"))) {
                                                    General.storeOrders.getJSONObject(j).getJSONArray("dts").getJSONObject(i1).put("sQ", quantity);
                                                }
                                            }
                                            break;
                                        case 620:// توزيع
                                            item = filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1);
                                            newItem = new JSONObject().put("img", null)
                                                    .put("oQ", -quantity)
                                                    .put("sQ", -quantity)
                                                    .put("nm", item.getString("nm"))
                                                    .put("ev", item.getString("ev"))
                                                    .put("p", item.getString("p"))
                                                    .put("p0", item.getString("p0"))
                                                    .put("pA", item.getString("pA"))
                                                    .put("unt", item.getString("unt"))
                                                    .put("cn", item.getString("cn"))
                                                    .put("tO", item.getString("tO"))
                                                    .put("tS", item.getString("tS"))
                                                    .put("tA", item.getString("tA"))
                                                    .put("tPrft", item.getString("tPrft"))
                                                    .put("itm", item.getString("itm"))
                                                    .put("cn", item.getString("cn"))
                                                    .put("nts", "")
                                                    .put("nts", "");
                                            nowOrder.getJSONArray("dts").put(newItem);
                                            nowOrder = General.calcTotals(nowOrder);
                                            filteredData.put(i, nowOrder);
                                            for (int j = 0; j < General.storeOrders.length(); j++) {
                                                if (nowOrder.getString("_id").matches(General.storeOrders.getJSONObject(j).getString("_id"))) {
                                                    General.storeOrders.put(j, nowOrder);
                                                    j = General.storeOrders.length();
                                                }
                                            }
                                            break;
                                        case 710://تقفيل
                                            if (quantity < 0 || quantity > Math.abs(filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).getDouble("sQ"))) {
                                                General.showToast(context, "كمية غير صالحة");
                                                return;
                                            }
                                            filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).put("sQ", -quantity);
                                            for (int j = 0; j < General.storeOrders.length(); j++) {
                                                if (nowOrder.getString("_id").matches(General.storeOrders.getJSONObject(j).getString("_id"))) {
                                                    General.storeOrders.getJSONObject(j).getJSONArray("dts").getJSONObject(i1).put("sQ", -quantity);
                                                }
                                            }
                                            break;
                                    }
                                    nowOrder = filteredData.getJSONObject(i);
                                    nowOrder = General.calcTotals(nowOrder);
                                    notifyDataSetChanged();
//                                ((TextView)viewGroup.findViewWithTag("123"+i)).setText(
//                                        nowOrder.getJSONObject("tts").getString("tO")
//                                                +", "+nowOrder.getJSONObject("tts").getString("tS")
//                                                +", "+nowOrder.getJSONObject("tts").getString("tN")
//                                                +"*"+nowOrder.getString("no")+"*"
//                                                +""+nowOrder.getJSONObject("adrs").getString("nh")
//                                );
                                } catch (JSONException e) {
                                    General.handleError("JSONExceptionsss: ", e.toString());
                                }
                                alertDialog1.dismiss();
                            }
                        });
                        alertDialog1.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog1.dismiss();
                            }
                        });
                    }
                } catch (JSONException e) {
                    General.handleError("EXP,006: ", e.toString());
                }
                return false;
            }
        });
        try {
            nowOrder = filteredData.getJSONObject(i);
            orderItems = nowOrder.getJSONArray("dts");
            sQET.setText(orderItems.getJSONObject(i1).getString("sQ"));
//            if (nowOrder.getJSONArray("ops").getJSONObject(0).getString("nm").matches("فى التحضير") ||
//                    nowOrder.getJSONArray("ops").getJSONObject(0).getString("nm").matches("فى انتظار المراجعة") ||
//                    nowOrder.getJSONArray("ops").getJSONObject(0).getString("nm").matches("فى التوزيع")){
//                sQET.setEnabled(true);
//            }else {
//                sQET.setEnabled(false);
//            }
            itemNameTV.setText(orderItems.getJSONObject(i1).getString("nm") + (orderItems.getJSONObject(i1).getString("nts").matches("") ? "" : ", " + orderItems.getJSONObject(i1).getString("nts")));
            unitTV.setText(orderItems.getJSONObject(i1).getString("unt"));
            priceTV.setText(orderItems.getJSONObject(i1).getString("p"));
            oQTV.setText(orderItems.getJSONObject(i1).getString("oQ"));
         /*   listLayout.addView(sQET);
            listLayout.addView(unitTV);
            listLayout.addView(oQTV);*/
            if (nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no") == 620 || nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no") == 710) {
              //  listLayout.addView(priceTV);
                priceTV.setVisibility(View.VISIBLE);
            }
         //   listLayout.addView(itemNameTV);
        } catch (JSONException e) {
            General.handleError("OrdersExpandableListViewAdapter, 333: ", e.toString());
        }
//        sQET.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//                Double qty = sQET.getText().toString().trim().length() > 0 ? Double.parseDouble(sQET.getText().toString()) : 0.0;
//                try {
//                    nowOrder = filteredData.getJSONObject(i);
//                    orderItems = filteredData.getJSONObject(i).getJSONArray("dts");
//                    switch (nowOrder.getJSONArray("ops").getJSONObject(0).getInt("no")){
//                        case 220:
//                        case 310:
//                            filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).put("sQ", qty);
//                            for (int j=0;j<General.storeOrders.length();j++){
//                                if (nowOrder.getString("_id").matches(General.storeOrders.getJSONObject(j).getString("_id"))){
//                                    General.storeOrders.getJSONObject(j).getJSONArray("dts").getJSONObject(i1).put("sQ", qty);
//                                }
//                            }
//                            break;
//                        case 620://جاهز للتوزيع
//                            filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).put("oRQ",(nowOrder.getJSONArray("dts").getJSONObject(i1).getDouble("sQ")- qty));
//                            for (int j=0;j<General.storeOrders.length();j++){
//                                if (nowOrder.getString("_id").matches(General.storeOrders.getJSONObject(j).getString("_id"))){
//                                    General.storeOrders.getJSONObject(j).getJSONArray("dts").getJSONObject(i1).put("sQ", (nowOrder.getJSONArray("dts").getJSONObject(i1).getDouble("sQ")-qty));
//                                }
//                            }
//                            break;
//                        case 710://تقفيل
//                            filteredData.getJSONObject(i).getJSONArray("dts").getJSONObject(i1).put("sRQ",qty);
//                            for (int j=0;j<General.storeOrders.length();j++){
//                                if (nowOrder.getString("_id").matches(General.storeOrders.getJSONObject(j).getString("_id"))){
//                                    General.storeOrders.getJSONObject(j).getJSONArray("dts").getJSONObject(i1).put("sRQ", qty);
//                                }
//                            }
//                            break;
//                    }
//                    nowOrder = General.calcTotals(nowOrder);
//                    ((TextView)viewGroup.findViewWithTag("123"+i)).setText(
//                                nowOrder.getJSONObject("tts").getString("tO")
//                            +", "+nowOrder.getJSONObject("tts").getString("tS")
//                            +", "+nowOrder.getJSONObject("tts").getString("tN")
//                            +"*"+nowOrder.getString("no")+"*"
//                            +""+nowOrder.getJSONObject("adrs").getString("nh")
////                            +""+nowOrder.getJSONObject("op").getString("d").split("T")[0]
////                            +" "+nowOrder.getJSONObject("op").getString("frm")
////                            +", "+nowOrder.getJSONObject("op").getString("to")
////                            +" "+nowOrder.getJSONArray("ops").getJSONObject(0).getJSONObject("frm").getString("nm")
//                    );
//                } catch (JSONException e) {
//                    General.handleError("JSONExceptionsss: ", e.toString());
//                }
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//        });


        return exChild;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (JSONArray) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                JSONArray nlist = new JSONArray();
                JSONObject order;
                try {
                    for (int i = 0; i < General.storeOrders.length(); i++) {
                        order = General.storeOrders.getJSONObject(i);
                        if (Integer.parseInt(constraint.toString()) == 10 || order.getJSONArray("ops").getJSONObject(0).getString("no").equals(Integer.parseInt(constraint.toString()))
                        ) {
                            nlist.put(order);
                        }
                    }
                } catch (JSONException e) {
                    General.handleError("protected: ", e.toString());
                }
                results.values = nlist;
                results.count = nlist.length();
                return results;
            }
        };
        return filter;
    }
}

//        orderHeaderTV.setOnLongClickListener(new View.OnLongClickListener() {
//            public boolean onLongClick(View v)
//            {
//                Log.e("aaaaaaaa", "aaaaaaaaaa");
//                return false;
//            }
//        });
//                        LinearLayout layout = new LinearLayout(context);  layout.setOrientation(LinearLayout.VERTICAL);
//                        TextView suTV = new TextView(context);
//                        String su =  "الرصيد السابق: "+results.getJSONObject("bal").getString("bB")
//                                +System.getProperty("line.separator")
//                                +"قيمة العملية: "+results.getJSONObject("bal").getString("op")
//                                +System.getProperty("line.separator")
//                                +"الرصيد الحالى: "+results.getJSONObject("bal").getString("bA");
//                        suTV.setText(su);
//                        layout.addView(suTV);
//
//                        final AlertDialog alertDialog = new AlertDialog.Builder(context)
//                            .setView(layout)
//                            .setTitle("مطابقة حسابات ...")
//                            .setPositiveButton(android.R.string.ok, null)
////                            .setNegativeButton(android.R.string.cancel, null)
//                            .setNeutralButton(android.R.string.unknownName, null)
//                            .show();
//                        alertDialog.setCanceledOnTouchOutside(false);
//                        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("مطابق");
//                        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD); alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
////                        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setText("غير مطابق");
////                        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD); alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextSize(18);;
//                        alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setText("غير مطابق");
//                        alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTypeface(null, Typeface.BOLD); alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTextSize(18);
//                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                            filteredData = new JSONArray(); notifyDataSetChanged();
//                                try {
//                                    String ooo = General.postUrl(General.url+"users/empTempFire", new JSONObject()
//                                            .put("userId", General.user.getString("_id"))
//                                            .put("userName", General.user.getString("nm"))
//                                            .put("orderId", results.getJSONObject("bal").getString("orderId"))
//                                            .put("empOps", new JSONObject().put("av", false).put("nm", "وقف مالى").put("nts", "وقف مالى مؤقت لمطابقة الحسابات"))
//                                            .put("orderOps", new JSONObject()
//                                                            .put("nm", "فى انتظار المطابقة")
//                                                            .put("no", 1010)
//                                                            .put("frm", new JSONObject().put("nm", General.user.getString("nm")).put("id", General.user.getString("_id")))
//                                                            .put("to", new JSONObject().put("nm", General.user.getJSONObject("emp").getJSONObject("str").getString("nm")).put("id", General.office.getString("_id")))
//                                                            .put("lc", new JSONObject().put("nm", General.user.getJSONObject("emp").getJSONObject("str").getString("nm")).put("id", General.office.getString("_id"))))
//                                    );
//                                    General.user.getJSONObject("emp").getJSONArray("ops").put(0,  new JSONObject()
//                                            .put("av", false).put("nm", "وقف مالى")
//                                            .put("nts", "وقف مالى مؤقت لمطابقة الحسابات"));
//                                    if (ooo.contains("ok")){
//                                        General.showToast(context, "تم و قفك مؤقتا من العمليات المالية لحين مراجعة الحساب, راجع مديرك !!!");
//                                    }
//                                } catch (JSONException e) {
//                                    General.handleError("vvvvvvv", e.toString());
//                                }
//                                alertDialog.dismiss();
//                            }
//                        });
