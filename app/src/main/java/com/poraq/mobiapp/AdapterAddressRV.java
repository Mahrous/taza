package com.poraq.mobiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterAddressRV extends RecyclerView.Adapter<AdapterAddressRV.ViewHolder> {
    Context context;
    List<String> list ;

    public AdapterAddressRV(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.singe_row_list_address , parent , false );
        ViewHolder holder = new ViewHolder(view , context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        Context context;


        public ViewHolder(@NonNull View itemView , final Context context) {
            super(itemView);
            textView = itemView.findViewById(R.id.t1);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                }
            });


        }
    }

}
