package com.poraq.mobiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    List<OrderModel> list;
    Context context;

    public OrderAdapter(List<OrderModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_row_myorder, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel model = list.get(position);
        holder.type.setText(model.getType());
        holder.sent.setText(model.getSent());
        holder.price.setText(model.getPrice());
        holder.qu.setText(model.getQu());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView type, qu, price, sent;
        Context context;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type_order);
            qu = itemView.findViewById(R.id.qu);
            price = itemView.findViewById(R.id.price_order);
            sent = itemView.findViewById(R.id.sent_qu);


        }
    }

}
