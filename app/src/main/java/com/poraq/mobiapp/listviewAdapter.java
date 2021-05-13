package com.poraq.mobiapp;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;


import com.squareup.picasso.Callback;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;


public class listviewAdapter extends BaseAdapter implements Filterable {
    static ImageView im;
    static Button saveImage;
    static TextView imagePropertiesTV;
    List<String> unitsList = new ArrayList<>();
    JSONArray unitsJson = new JSONArray();
    Boolean isFiltered; // = false;
    private Context context;
    private JSONArray filteredData = new JSONArray();
    public listviewAdapter(Context context) {
        super();

        switch (MainAdapter.index) {
            case 0:
                getFilter().filter("allItems,");
                break;
            case 1:
                getFilter().filter("discs,");
                break;
            case 2:
                getFilter().filter("myItems,");
                break;
            default:
                getFilter().filter("cats," + ((MainActivity) context).catsArray.get(MainAdapter.index));
        }

        this.context = context;
    }

    public JSONArray getSource() {
        return filteredData;
    }

    @Override
    public int getCount() {
        return filteredData.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return filteredData.getJSONObject(position);

        } catch (JSONException e) {
            General.handleError("33333", e.toString());
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (General.nowOp.split(",")[0].matches("pO") || General.nowOp.split(",")[0].matches("tO")) {
//            convertView = layoutInflater.inflate(R.layout.purchase_single_row, parent, false);
//        }

        convertView = layoutInflater.inflate(R.layout.single_row_main_item, parent, false);
        final TextView itemNameText = convertView.findViewById(R.id.tv_name_of_item);
        final TextView qtyText = convertView.findViewById(R.id.tv_number_of_item);
        final EditText priceET = new EditText(context);
        priceET.setHint("سعر");//
        final Spinner unisSpinner = convertView.findViewById(R.id.spinner_amount);
        final ImageView iv = convertView.findViewById(R.id.iv_image_of_item);
        Button plusBtn = convertView.findViewById(R.id.b_countUp_item);
        Button minusBtn = convertView.findViewById(R.id.b_countDown_item);
      /*  if (isFiltered)
        {
            Log.e("dddddddd", "ggggggggggggg");
            Animation anim = AnimationUtils.loadAnimation(context , R.anim.grid);
            anim.setStartOffset(position*500);
            convertView.setAnimation(anim);
            anim.start();
//            isFiltered = false;
        }*/

        try {
            final JSONObject item = filteredData.getJSONObject(position);
            if (General.nowOp.split(",")[0].matches("pO")) {
                itemNameText.setText(
                        item.get("tB") +
                                System.getProperty("line.separator") +
                                item.get("sB") +
                                System.getProperty("line.separator") +
                                item.get("nm") + "مطلوب: " +
                                item.getJSONObject("tB").getString("oQ") + "بمعدل: " +
                                item.getJSONObject("tB").getString("oQ") + "مقترح: " + ""
                );
                qtyText.setText(item.get("oQ").toString());
                priceET.setText(item.get("p").toString());
            } else {
                itemNameText.setText(item.getString("nm"));
                /* +(item.getString("nts").length() > 0? ", "+item.getString("nts"):""));*/
                qtyText.setText(item.get("oQ").toString());
            }
            unitsJson = item.getJSONArray("unts");
            if (General.nowOp.split(",")[0].matches("sO")) {
                item.put("p0", unitsJson.getJSONObject(0).getDouble("p0")).put("p", unitsJson.getJSONObject(0).getDouble("p1")).put("pA", unitsJson.getJSONObject(0).getDouble("pA"));
                General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("p0", unitsJson.getJSONObject(0).getDouble("p0")).put("p", unitsJson.getJSONObject(0).getDouble("p1")).put("pA", unitsJson.getJSONObject(0).getDouble("pA"));

            } else if (General.nowOp.split(",")[0].matches("pO")) {
                item.put("p0", unitsJson.getJSONObject(0).getDouble("p0")).put("p", 0).put("pA", unitsJson.getJSONObject(0).getDouble("pA"));
                General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("p0", unitsJson.getJSONObject(0).getDouble("p0")).put("p", 0).put("pA", unitsJson.getJSONObject(0).getDouble("pA"));
            } else if (General.nowOp.split(",")[0].matches("tO")) {
                item.put("p0", unitsJson.getJSONObject(0).getDouble("p0")).put("p", unitsJson.getJSONObject(0).getDouble("pA")).put("pA", unitsJson.getJSONObject(0).getDouble("pA"));
                General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("p0", unitsJson.getJSONObject(0).getDouble("p0")).put("p", unitsJson.getJSONObject(0).getDouble("pA")).put("pA", unitsJson.getJSONObject(0).getDouble("pA"));
            }
            item.put("u", unitsJson.getJSONObject(0).getString("nm")).put("cn", unitsJson.getJSONObject(0).getDouble("cn"));
            General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("u", unitsJson.getJSONObject(0).getString("nm")).put("cn", unitsJson.getJSONObject(0).getDouble("cn"));


            unitsList = new ArrayList<>();
            for (int i = 0; i < unitsJson.length(); i++) {
                unitsList.add(unitsJson.getJSONObject(i).getDouble("p1") + " : " + unitsJson.getJSONObject(i).getString("nm"));
            }
            unisSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.single_row_main_spin, unitsList));
            String imageUrl = "";
            try {
                imageUrl = filteredData.getJSONObject(position).has("img") ? filteredData.getJSONObject(position).getString("img") : "";
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (imageUrl.length() > 0) {
//.placeholder(context.getResources().getDrawable(R.drawable.default_person_image)).error(context.getResources().getDrawable(R.drawable.default_person_image))
                Picasso.get().load(General.url + "images/" + imageUrl)
//                        .placeholder(R.drawable.ic_launcher)
                        .into(iv, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("ggggggg", e.toString());
                            }
                        });
            }
            itemNameText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View itemNote = inflater.inflate(R.layout.item_note, null, false);

                    final EditText itemNotesText = itemNote.findViewById(R.id.order_custom_et);
                    Button submitCh = itemNote.findViewById(R.id.submit_edit_order);
                    Button cancelCh = itemNote.findViewById(R.id.cancel_edit_order);

                    try {
                        itemNotesText.setText(filteredData.getJSONObject(position).getString("nts"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setView(itemNote)
                            .show();
                    submitCh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                filteredData.getJSONObject(position).put("nts", itemNotesText.getText().toString().trim());
                                General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("nts", itemNotesText.getText().toString().trim());
                                alertDialog.dismiss();
                            } catch (JSONException e) {
                                General.handleError("Listviewadapter, 090909: ", e.toString());
                            }
                        }
                    });
                    cancelCh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    return true;
                }


            });
            iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    try {
                        if (Arrays.asList("admin").contains(General.user.getString("tp"))) {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View editeImage = inflater.inflate(R.layout.edit_image, null, false);

                            im = editeImage.findViewById(R.id.iv_item);
                            im.setVisibility(View.GONE);
                            Button button = editeImage.findViewById(R.id.addImagee);
                            imagePropertiesTV = editeImage.findViewById(R.id.imagePropertiesTV);
                            imagePropertiesTV.setVisibility(View.GONE);

                            Button save = editeImage.findViewById(R.id.save_item_changes);
                            Button cancel = editeImage.findViewById(R.id.cancel_item_changes);
                            saveImage = editeImage.findViewById(R.id.save_image);
                            saveImage.setVisibility(View.GONE);

                            final Spinner unitsSP = editeImage.findViewById(R.id.unitsSP);
                            final EditText unitPriceET = editeImage.findViewById(R.id.unitPriceET);
                            unitPriceET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            final JSONArray newUnits = new JSONArray();//, myUnit = filteredData.getJSONObject(position).getJSONArray("unts");
                            List<String> muYnitsList = new ArrayList<>();
                            unitsJson = filteredData.getJSONObject(position).getJSONArray("unts");
                            for (int i = 0; i < unitsJson.length(); i++) {
                                muYnitsList.add(unitsJson.getJSONObject(i).getDouble("p1") + " : " + unitsJson.getJSONObject(i).getString("nm"));
                            }
                            unitsSP.setAdapter(new ArrayAdapter<>(context, R.layout.single_row_main_spin, muYnitsList));

                            unitPriceET.setText(unitsSP.getSelectedItem().toString().split(":")[0].trim());

                            unitPriceET.requestFocus();
                            unitPriceET.selectAll();

                            for (int i = 0; i < unitsJson.length(); i++) {
                                newUnits.put(new JSONObject()
                                        .put("_id", unitsJson.getJSONObject(i).getString("_id"))
                                        .put("nm", unitsJson.getJSONObject(i).getString("nm"))
                                        .put("av", unitsJson.getJSONObject(i).getJSONObject("av"))
                                        .put("cn", unitsJson.getJSONObject(i).getString("cn"))
                                        .put("p1", unitsJson.getJSONObject(i).getString("p1"))
                                        .put("lSP", unitsJson.getJSONObject(i).getString("lSP"))
                                );
                            }
                            unitsSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
                                    unitPriceET.setText(unitsSP.getSelectedItem().toString().split(":")[0].trim());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }
                            });
                            unitPriceET.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    try {
                                        Double price = (unitPriceET.getText().toString().trim().length() > 0 ? Double.parseDouble(unitPriceET.getText().toString()) : 0.0);
                                        if (price <= 0) {
                                            unitPriceET.setError("سعر غير صالح !!!");
                                            unitPriceET.requestFocus();
                                            General.showToast(context, "سعر غير صالح !!!", Toast.LENGTH_LONG);
                                            return;
                                        }
                                        newUnits.getJSONObject(unitsSP.getSelectedItemPosition()).put("p1", price);
                                        newUnits.getJSONObject(unitsSP.getSelectedItemPosition()).put("lSP", unitsSP.getSelectedItem().toString().split(":")[0].trim());
                                    } catch (JSONException e) {
                                        General.handleError("JSONException, gffdd: ", e.toString());
                                    }
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int RESULT_LOAD_IMG = 1;
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    ((MainActivity) context).startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                                }
                            });
                            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setView(editeImage)
                                    .show();
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        JSONObject results = General.postUrl(General.url + "items/updateItemCost", new JSONObject()
                                                .put("_id", filteredData.getJSONObject(position).getString("_id"))
                                                .put("unts", newUnits)
                                        );
                                        if (results.length() == 0 || !results.getBoolean("state")) {
                                            General.showToast(context, "failed !!!");
                                            Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                                        } else {
                                            General.showToast(context, "succeeded ...");
                                            for (int i = 0; i < newUnits.length(); i++) {
                                                unitsJson.put(new JSONObject()
                                                        .put("_id", newUnits.getJSONObject(i).getString("_id"))
                                                        .put("nm", newUnits.getJSONObject(i).getString("nm"))
                                                        .put("av", newUnits.getJSONObject(i).getJSONObject("av"))
                                                        .put("cn", newUnits.getJSONObject(i).getString("cn"))
                                                        .put("p1", newUnits.getJSONObject(i).getString("p1"))
                                                        .put("lSP", newUnits.getJSONObject(i).getString("lSP"))
                                                );
                                            }
                                            filteredData.getJSONObject(position).put("unts", unitsJson);
                                            General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("unts", unitsJson);

                                            notifyDataSetChanged();
                                            Log.e("boob", "publishResults: " + ((MainActivity) context).ttO);
                                            ((MainActivity) context).mainAdapter.notifyDataSetChanged();
                                        }
                                    } catch (JSONException e) {
                                        General.handleError("JSONException, mmmnnbb: ", e.toString());
                                    }
                                    alertDialog.dismiss();
                                }
                            });

                            saveImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    alertDialog.dismiss();
                                    Bitmap bitmap = ((BitmapDrawable) im.getDrawable()).getBitmap();
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                                    if ((encodedImage.length() / 1024) > 100) {
                                        General.showToast(context, "too much image to be saved !!!");
                                        return;
                                    }
                                    try {
                                        JSONObject results = General.postUrl(General.url + "items/addItemImg",
                                                new JSONObject().put("_id", filteredData.getJSONObject(position).getString("_id")).put("img", encodedImage));
                                        if (results.length() == 0 || !results.getBoolean("state")) {
                                            General.showToast(context, "failed !!!");
                                            Log.e("eeeeeeeeee", "failed !!!" + (results.has("err") ? results.getString("err") : ""));
                                        } else {
                                            iv.setImageBitmap(bitmap);
                                            General.showToast(context, "succeeded ...");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }

            });


            plusBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (((MainActivity) context).isNewOrder == false) {
                            if (((MainActivity) context).addresses.length() == 1) {
                                ((MainActivity) context).checkIsNewInv();
                            } else {
                                ((MainActivity) context).checkIsNewInv();
                                return;
                            }
                        }

                        Double quantity = qtyText.getText().toString().trim().length() > 0
                                ? Double.parseDouble(qtyText.getText().toString()) + 1.0 : 1.0;
                        qtyText.setText(quantity + "");
                        filteredData.getJSONObject(position).put("oQ", quantity).put("sQ", quantity);
                        General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position)
                                .getString("_id"))).put("oQ", quantity).put("sQ", quantity);
                        ((MainActivity) context).calcTotals(General.itemsJson);
                    } catch (JSONException e) {
                        General.handleError("JSONException, yuuyu: ", e.toString());
                    }
                }
            });
            plusBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        if (((MainActivity) context).isNewOrder == false) {
                            if (((MainActivity) context).addresses.length() == 1) {
                                ((MainActivity) context).checkIsNewInv();
                            } else {
                                ((MainActivity) context).checkIsNewInv();
                                return true;
                            }
                        }
                        Double quantity = qtyText.getText().toString().trim().length() > 0 ? Double.parseDouble(qtyText.getText().toString()) + 10.0 : 10.0;
                        qtyText.setText(quantity + "");
                        filteredData.getJSONObject(position).put("oQ", quantity).put("sQ", quantity);
                        General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("oQ", quantity).put("sQ", quantity);
                        ((MainActivity) context).calcTotals(General.itemsJson);
                    } catch (JSONException e) {
                        General.handleError("JSONException, gfggg: ", e.toString());
                    }
                    return true;
                }
            });
            minusBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (((MainActivity) context).isNewOrder == false) {
                            if (((MainActivity) context).addresses.length() == 1) {
                                ((MainActivity) context).checkIsNewInv();
                            } else {
                                ((MainActivity) context).checkIsNewInv();
                                return;
                            }
                        }
                        Double quantity = qtyText.getText().toString().trim().length() > 0 ? (Double.parseDouble(qtyText.getText().toString()) - 1.0) : 0.0;
                        qtyText.setText(quantity == 0 ? "" : (quantity + ""));
                        filteredData.getJSONObject(position).put("oQ", quantity == 0 ? "" : quantity).put("sQ", quantity == 0 ? "" : quantity);
                        General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("oQ", quantity == 0 ? "" : quantity).put("sQ", quantity == 0 ? "" : quantity);
                        ((MainActivity) context).calcTotals(General.itemsJson);
                    } catch (JSONException e) {
                        General.handleError("JSONExceptionsss: ", e.toString());
                    }
                }
            });
            unisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int i, long id) {
                    try {
                        if (General.nowOp.split(",")[0].matches("sO")) {
                            filteredData.getJSONObject(position).put("p", Double.parseDouble(parentView.getItemAtPosition(i).toString().split(" : ")[0]));
                            General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("p", Double.parseDouble(parentView.getItemAtPosition(i).toString().split(" : ")[0]));
                        }
                        filteredData.getJSONObject(position).put("unt", parentView.getItemAtPosition(i).toString().split(" : ")[1]);
                        General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("unt", parentView.getItemAtPosition(i).toString().split(" : ")[1]);
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).calcTotals(General.itemsJson);
                        }
                    } catch (JSONException e) {
                        General.handleError("listviewAdapter, 676: ", e.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        } catch (JSONException e) {
            General.handleError("JSONExceptioneee", e.toString());
        }

//        priceET.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//                try {
//                    Double price = (priceET.getText().toString().trim().length() > 0 ? Double.parseDouble(priceET.getText().toString()) : 0.0);
//                    filteredData.getJSONObject(position).put("p", (price > 0 ? price : ""));
//                    General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("p", (price > 0 ? price : ""));
//                    ((MainActivity) context).calcTotals(General.itemsJson);
//                } catch (JSONException e) {
//                    General.handleError("JSONException, sss: ", e.toString());
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });

//        qtyText.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//                try {
//                    Double qty = (qtyText.getText().toString().trim().length() > 0 ? Double.parseDouble(qtyText.getText().toString()) : 0.0);
//                    filteredData.getJSONObject(position).put("oQ", (qty > 0 ? qty : "")).put("sQ", (qty > 0 ? qty : ""));
//                    General.itemsJson.getJSONObject(getItemIndex(filteredData.getJSONObject(position).getString("_id"))).put("oQ", (qty > 0 ? qty : "")).put("sQ", (qty > 0 ? qty : ""));
//                    ((MainActivity) context).calcTotals(filteredData);
//                } catch (JSONException e) {
//                    General.handleError("JSONExceptionsss: ", e.toString());
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });


        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {

                    filteredData = new JSONArray(results.values.toString());
                } catch (JSONException e) {
                    General.handleError("hhhjjhhj", e.toString());
                }
                ((MainActivity)context).itemsLV.setLayoutAnimation(((MainActivity)context).controller);

                notifyDataSetChanged();

               // ((MainActivity)context).itemsLV.setLayoutAnimation(((MainActivity)context).controller);
//                isFiltered = false;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                //TODO
                FilterResults results = new FilterResults();
                JSONArray nlist = new JSONArray();
                switch (constraint.toString().split(",")[0]) {
                    case "allItems":
                        nlist = General.itemsJson;

                        break;
                    case "myItems":
                        for (int i = 0; i < General.itemsJson.length(); i++) {
                            try {
                                JSONObject item = General.itemsJson.getJSONObject(i);
                                if (!item.get("oQ").toString().trim().matches("") && item.getInt("oQ") > 0) {
                                    nlist.put(item);
                                }
                            } catch (JSONException e) {
                                General.handleError("protected: ", e.toString());
                            }
                        }
                        break;
                    case "discs":
                        for (int i = 0; i < General.itemsJson.length(); i++) {
                            try {
                                JSONObject item = General.itemsJson.getJSONObject(i);
                                if (item.getBoolean("isDisc")) {
                                    nlist.put(item);
                                }
                            } catch (JSONException e) {
                                General.handleError("protectedwwwww: ", e.toString());
                            }
                        }

                        break;
                    case "search":
                        for (int i = 0; i < General.itemsJson.length(); i++) {

                            try {
                                JSONObject item = General.itemsJson.getJSONObject(i);
                                if (item.get("nm").toString().contains(constraint.toString().split(",%##")[1])) {
                                    nlist.put(item);
                                }
                            } catch (JSONException e) {
                                General.handleError("protectedwwwww: ", e.toString());
                            }
                        }
                        break;
                    case "cats":
                        for (int i = 0; i < General.itemsJson.length(); i++) {
                            try {
                                JSONObject item = General.itemsJson.getJSONObject(i);
                                if (item.getString("tgs").contains(constraint.toString().split(",")[1])) {
                                    nlist.put(item);
                                }
                            } catch (JSONException e) {
                                General.handleError("protectedwwwww: ", e.toString());
                            }
                        }
                        break;
                }
                results.values = nlist;
                results.count = nlist.length();
                return results;
            }
        };
        return filter;
    }

    Integer getItemIndex(String id) {
        for (int i = 0; i < General.itemsJson.length(); i++) {
            try {
                JSONObject item = General.itemsJson.getJSONObject(i);
                if (item.getString("_id").matches(id)) {
                    return i;
                }
            } catch (JSONException e) {
                General.handleError("protected: ", e.toString());
                return -1;
            }
        }
        return -1;
    }
}
