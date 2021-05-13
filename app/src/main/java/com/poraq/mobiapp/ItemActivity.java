package com.poraq.mobiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Bundle;

import android.text.InputType;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    Button newItemBtn, newUnitBtn, saveNewItemBtn;
    AutoCompleteTextView itemsACTV;
    EditText itemNameET, itemAgeET, itemCatET;//, unitContentET, unitPriceET, unitNameET
    ListView unitsLV;
    JSONObject nowItem = new JSONObject()  ;
    ArrayList<String> itemUnitsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        newItemBtn =  findViewById(R.id.newItemBtn);
        unitsLV =  findViewById(R.id.unitsLV);
        newUnitBtn =  findViewById(R.id.newUnitBtn);
        saveNewItemBtn =  findViewById(R.id.saveNewItemBtn);
        itemsACTV =  findViewById(R.id.itemsACTV);
        itemNameET =  findViewById(R.id.itemNameET);
        itemAgeET =  findViewById(R.id.itemAgeET);
        itemCatET =  findViewById(R.id.itemCatET);
        itemsACTV.setThreshold(1);//will start working from first character
        itemsACTV.setTextColor(Color.RED);


        fillAutoCompleteItems();

        itemsACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            try {
                for (int i=0;i<General.itemsJson.length();i++){
                    nowItem = new JSONObject();
                    if (General.itemsJson.getJSONObject(i).getString("nm").matches(itemsACTV.getAdapter().getItem(position).toString())){
                        nowItem.put("_id", General.itemsJson.getJSONObject(i).getString("_id"));
                        nowItem.put("nm", General.itemsJson.getJSONObject(i).getString("nm"));
                        nowItem.put("age",General.itemsJson.getJSONObject(i).getString("age"));
                        nowItem.put("tg",General.itemsJson.getJSONObject(i).getString("tg"));
                        nowItem.put("tgs",General.itemsJson.getJSONObject(i).getJSONArray("tgs"));
                        nowItem.put("unts",new JSONArray());//General.itemsJson.getJSONObject(i).getJSONArray("unts")
                        for (int j = 0; j < General.itemsJson.getJSONObject(i).getJSONArray("unts").length(); j++) {
                            nowItem.getJSONArray("unts").put(new JSONObject()
//                                    .put("av", true)
                                    .put("nm", General.itemsJson.getJSONObject(i).getJSONArray("unts").getJSONObject(j).getString("nm"))
                                    .put("p0",General.itemsJson.getJSONObject(i).getJSONArray("unts").getJSONObject(j).getString("p0"))
                                    .put("p1",General.itemsJson.getJSONObject(i).getJSONArray("unts").getJSONObject(j).getString("p1"))
                                    .put("lSP",General.itemsJson.getJSONObject(i).getJSONArray("unts").getJSONObject(j).getString("p1"))
                                    .put("cn",General.itemsJson.getJSONObject(i).getJSONArray("unts").getJSONObject(j).getString("cn"))
                            );
                        }
                        populateItem(false);
                        i = General.itemsJson.length();
                    }
                }
            }catch (JSONException e){
                General.handleError("ItemActivity, 000: ", e.toString());
            }
            }
        });

        saveNewItemBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (itemNameET.getText().toString().trim().matches("")){
                itemNameET.setError("برجاء تسجيل اسم الصنف" );
                General.showToast(ItemActivity.this, "سجل اسم الصنف !!!");
                return;
            }
            if (itemAgeET.getText().toString().trim().matches("")){
                itemAgeET.setError("برجاء تسجيل عمر الصنف" );
                General.showToast(ItemActivity.this, "سجل اسم الصنف !!!");
                return;
            }
            if (itemCatET.getText().toString().trim().matches("")){
                itemCatET.setError("برجاء تسجيل فئة الصنف" );
                General.showToast(ItemActivity.this, "سجل غئة الصنف الصنف !!!");
                return;
            }
            try {
                if (!nowItem.has("unts")|| nowItem.getJSONArray("unts").length() == 0){
                    General.showToast(ItemActivity.this, "سجل وحدة للصنف !!!");
                    setNewUnit(-1);
                    return;

                }
                nowItem.put("nm",itemNameET.getText().toString().trim());
                nowItem.put("age",itemAgeET.getText().toString().trim());
                nowItem.put("tg",itemCatET.getText().toString().trim());
                nowItem.put("tgs", new JSONArray().put(itemCatET.getText().toString().trim()));
                JSONObject results = General.postUrl(nowItem.has("_id") ?General.url+"items/edit":General.url+"items/new", nowItem);
                if (results.length() == 0 || !results.getBoolean("state")){
                    General.showToast(ItemActivity.this, "failed !!!");
                    Log.e("eeeeeeeeee", "failed !!!"+ (results.has("err")?results.getString("err"):""));
                }else {
                    General.showToast(ItemActivity.this, "تمت بنجاح ...");
                }
            }catch (JSONException e){
                General.handleError("Itemnmmn", e.toString());
            }
            }
        });

        newItemBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            nowItem = new JSONObject();
            itemsACTV.setText("");
            populateItem(true);
            }
        });
        newUnitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setNewUnit(-1);
            }
        });
        unitsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setNewUnit(position);
            }
        });
        unitsLV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LinearLayout layout = new LinearLayout(ItemActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                TextView suTV = new TextView(ItemActivity.this);
                String su = "تأكيد مسح الوحدة المختارة";
                suTV.setText(su);
                layout.addView(suTV);

                final AlertDialog alertDialog = new AlertDialog.Builder(ItemActivity.this)
                        .setView(layout)
                        .setTitle("مطابقة حسابات ...")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("موافق");
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD);
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setText("إلغاء");
                alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTypeface(null, Typeface.BOLD);
                alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTextSize(18);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            nowItem.getJSONArray("unts").remove(unitsLV.getSelectedItemPosition());
                        } catch (JSONException e) {
                            General.handleError("cdcdcd", e.toString());
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                return false;
            }
        });

    }

    void fillAutoCompleteItems(){
        ArrayList<String> itemsArray = new ArrayList<>();
        JSONObject item;
        try {
            for (int i=0;i<General.itemsJson.length();i++){
                item = General.itemsJson.getJSONObject(i);
                itemsArray.add(item.getString("nm"));

            }
        }catch (JSONException e){
            General.handleError("ItemActivity, 000: ", e.toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, itemsArray);
        itemsACTV.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

    }

    void setNewUnit(final int index){
        LinearLayout layout = new LinearLayout(ItemActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText unitNameET = new EditText(ItemActivity.this);unitNameET.setHint("اسم الوحدة");unitNameET.setSelectAllOnFocus(true);
        final EditText unitPriceET = new EditText(ItemActivity.this);unitPriceET.setHint("سعر البيع");unitPriceET.setSelectAllOnFocus(true);        unitPriceET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        final EditText unitOfficialPriceET = new EditText(ItemActivity.this);unitOfficialPriceET.setHint("التسعيرة الجبرية");unitOfficialPriceET.setSelectAllOnFocus(true);        unitOfficialPriceET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        final EditText unitContentET = new EditText(ItemActivity.this);unitContentET.setHint("محتوى الوحدة");unitContentET.setSelectAllOnFocus(true);        unitContentET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        final CheckBox iSChk = new CheckBox(ItemActivity.this);iSChk.setText("متاح للبيع العام");iSChk.setChecked(true);
        final CheckBox pSChk = new CheckBox(ItemActivity.this);pSChk.setText("متاح للبيع الداخلى");pSChk.setChecked(true);
        final CheckBox pChk = new CheckBox(ItemActivity.this);pChk.setText("متاح للشراء");pChk.setChecked(true);
        if (index > -1){
            try {
                unitNameET.setText(nowItem.getJSONArray("unts").getJSONObject(index).getString("nm"));
                unitPriceET.setText(nowItem.getJSONArray("unts").getJSONObject(index).getString("p1"));
                unitOfficialPriceET.setText(nowItem.getJSONArray("unts").getJSONObject(index).getString("p0"));
                unitContentET.setText(nowItem.getJSONArray("unts").getJSONObject(index).getString("cn"));
                pSChk.setChecked(nowItem.getJSONArray("unts").getJSONObject(index).getJSONObject("av").getBoolean("pS"));
                iSChk.setChecked(nowItem.getJSONArray("unts").getJSONObject(index).getJSONObject("av").getBoolean("iS"));
                pChk.setChecked(nowItem.getJSONArray("unts").getJSONObject(index).getJSONObject("av").getBoolean("p"));
            }
            catch (JSONException e) {
                General.handleError("Item, 001: ", e.toString());
            }
        }
        layout.addView(unitNameET);
        layout.addView(unitPriceET);
        layout.addView(unitOfficialPriceET);
        layout.addView(unitContentET);
        layout.addView(iSChk);
        layout.addView(pSChk);
        layout.addView(pChk);
        final AlertDialog alertDialog = new AlertDialog.Builder(ItemActivity.this)
                .setView(layout)
                .setTitle("الوحدات ...")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(android.R.string.cancel, null)
                .show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("موافق");
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTypeface(null, Typeface.BOLD); alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextSize(18);
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setText("إلغاء");
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD); alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextSize(18);
        alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setText("حذف");
        alertDialog.getButton(alertDialog.BUTTON_NEUTRAL).setTypeface(null, Typeface.BOLD); alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextSize(18);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unitNameET.getText().toString().trim().matches("")){
                    unitNameET.setError("ادخل اسم الوحدة !!!");
                    unitNameET.requestFocus();
                    General.showToast(ItemActivity.this,"ادخل اسم الوحدة !!!");
                    return;
                }
                if (unitPriceET.getText().toString().trim().matches("")){
                    unitPriceET.setError("ادخل سعر بيع الوحدة !!!");
                    unitPriceET.requestFocus();
                    General.showToast(ItemActivity.this,"ادخل سعر بيع الوحدة !!!");
                    return;
                }
                if (unitOfficialPriceET.getText().toString().trim().matches("")){
                    unitOfficialPriceET.setError("ادخل سعر الوحدة الرسمى !!!");
                    unitOfficialPriceET.requestFocus();
                    General.showToast(ItemActivity.this,"ادخل سعر الوحدة الرسمى !!!");
                    return;
                }
                if (unitContentET.getText().toString().trim().matches("") ){
                    unitContentET.setError("ادخل محتوى الوحدة !!!");
                    unitContentET.requestFocus();
                    General.showToast(ItemActivity.this,"ادخل محتوى الوحدة !!!");
                    return;
                }
                try {
                    if (!nowItem.has("unts")){
                        nowItem.put("unts", new JSONArray());
                    }
                    if (index == -1){
                        nowItem.getJSONArray("unts").put(new JSONObject()
                                .put("nm",unitNameET.getText().toString().trim())
                                .put("p0",Double.parseDouble(unitOfficialPriceET.getText().toString().trim()))
                                .put("p1",Double.parseDouble(unitPriceET.getText().toString().trim()))
                                .put("lSP",Double.parseDouble(unitPriceET.getText().toString().trim()))
                                .put("cn",Double.parseDouble(unitContentET.getText().toString().trim()))
                        );
                    }else {
                        nowItem.getJSONArray("unts").put(index, new JSONObject()
                                .put("nm",unitNameET.getText().toString().trim())
                                .put("p0",Double.parseDouble(unitOfficialPriceET.getText().toString().trim()))
                                .put("p1",Double.parseDouble(unitPriceET.getText().toString().trim()))
                                .put("lSP",Double.parseDouble(unitPriceET.getText().toString().trim()))
                                .put("cn",Double.parseDouble(unitContentET.getText().toString().trim()))
                        );
                    }
                    JSONObject unit;itemUnitsArray = new ArrayList<>();
                    for (int j=0;j<nowItem.getJSONArray("unts").length();j++){
                        unit = nowItem.getJSONArray("unts").getJSONObject(j);
                        itemUnitsArray.add(unit.getString("nm")+", "+unit.getString("p0"));
                    }
                    unitsLV.setAdapter(new ArrayAdapter<>(ItemActivity.this,android.R.layout.simple_list_item_checked, itemUnitsArray));
                }catch (JSONException e){
                    General.handleError("setPositiveButton, 987: ",e.toString());
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(ItemActivity.this);
                layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        return false;
                    }
                });

                try {
                    Log.e("vvvvvv", unitsLV.getSelectedItemPosition()+"");
                    itemUnitsArray.remove(index);
                    unitsLV.setAdapter(new ArrayAdapter<>(ItemActivity.this,android.R.layout.simple_list_item_checked, itemUnitsArray));
//                    unitsLV.setAdapter(new ArrayAdapter<>(ItemActivity.this,android.R.layout.simple_spinner_item, itemUnitsArray));
//                    unitsLV.getAdapter().n
                    nowItem.getJSONArray("unts").remove(index);
                } catch (JSONException e) {
                    General.handleError("hhhhhh", e.toString());
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
    }

    void populateItem(Boolean isNew){
        if (isNew){
            try {
                nowItem.put("nm","").put("tg","").put("unts", new JSONArray());
            }catch (JSONException e){
                General.handleError("", e.toString());
            }
            itemNameET.setText("");
            itemAgeET.setText("");
            itemCatET.setText("");
            itemUnitsArray = new ArrayList<>();
            unitsLV.setAdapter(new ArrayAdapter<>(ItemActivity.this,R.layout.single_row_myorder_spin, itemUnitsArray));
        }else {
            try {
                itemNameET.setText(nowItem.getString("nm"));
                itemAgeET.setText(nowItem.getString("age"));
                itemCatET.setText(nowItem.getString("tg"));
                itemUnitsArray = new ArrayList<>();
                JSONObject units;itemUnitsArray = new ArrayList<>();
                Log.e("gggggggggg", nowItem.getJSONArray("unts").toString());
                for (int j=0;j<nowItem.getJSONArray("unts").length();j++){
                    units = nowItem.getJSONArray("unts").getJSONObject(j);
                    itemUnitsArray.add(units.getString("nm")+", "+units.getString("p0"));
                }
                unitsLV.setAdapter(new ArrayAdapter<>(ItemActivity.this,android.R.layout.simple_list_item_checked, itemUnitsArray));
            }catch (JSONException e){
                General.handleError("Item 777000", e.toString());
            }
        }
    }
}
