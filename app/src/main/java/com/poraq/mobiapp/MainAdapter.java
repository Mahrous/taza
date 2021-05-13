package com.poraq.mobiapp;

import android.content.Context;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    Context context;
    List<String> list;
   static int index ;
    double ttot ;

    public MainAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.singe_row, parent, false);
        ViewHolder holder = new ViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (position == 2) {
            holder.textView.setText(list.get(position) + " " +((MainActivity)context).ttO);
            Log.e("beeb", "onBindViewHolder: "+((MainActivity)context).ttO );
        } else {
            holder.textView.setText(list.get(position));

        }


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("position", position + "");
                notifyDataSetChanged();
                index = position;
                switch (list.get(position)) {
                    case "كل الأصناف":

                        ((MainActivity) context).adapter.getFilter().filter("allItems,");
                        break;
                    case "المختارة":
                        ((MainActivity) context).adapter.getFilter().filter("myItems,");


                        break;
                    case "تخفيضات":
                        ((MainActivity) context).adapter.getFilter().filter("discs,");

                        break;
                    default:
                        Log.e("lop", "onClick: " +    ((MainActivity) context).ttO);
                        ((MainActivity) context).adapter.getFilter().filter("cats," + list.get(position));

                       // holder.textView.setText(list.get(2) + " " +((MainActivity) context).ttO);


                        Log.e("lop", "onClick: " +    ((MainActivity) context).ttO);

                }

            }
        });
        if (index == position) {

            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.textView.setTextColor(Color.parseColor("#CF3423"));

        } else {
            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.textView.setTextColor(Color.parseColor("#000000"));

        }
    }
//    public  int getSelectedItem(){
//      return 0;
//    };

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layout;
        TextView textView;
//        Context context;


        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            textView = itemView.findViewById(R.id.t1);
            //   itemView.setSelected(true);
            layout = itemView.findViewById(R.id.lin_ot);

           /* textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (textView.getText().toString()) {
                        case "كل الأصناف":
                            ((MainActivity) context).adapter.getFilter().filter("allItems,");
                            break;
                        case "المختارة":
                            ((MainActivity) context).adapter.getFilter().filter("myItems,");
                            break;
                        default:
                            ((MainActivity) context).adapter.getFilter().filter("cats," + textView.getText().toString());
                    }


                }
            });
*/

        }

        @Override
        public void onClick(View view) {

        }
    }

}
