package com.example.rs_fy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodayItemsAdapter extends RecyclerView.Adapter<TodayItemsAdapter.ViewHolder> {
    private Context mContext;
    private List<Data> myDataList;
    private String post_key="";
    private String item="";
    private String note="";
    private int amount=0;

    public TodayItemsAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,parent,false);
        return new TodayItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  TodayItemsAdapter.ViewHolder holder, int position) {
    final Data data = myDataList.get(position);
    holder.item.setText("Item: "+data.getItem());
    holder.amount.setText("Amount: "+data.getAmount());
    holder.date.setText("On: "+data.getItem());
    holder.item.setText("Item: "+data.getItem());
    holder.item.setText("Item: "+data.getItem());

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        //initialize variables

        public TextView item,amount,date,notes;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            item=itemView.findViewById(R.id.item);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
            notes=itemView.findViewById(R.id.note);
            imageView=itemView.findViewById(R.id.Imageview);
        }
    }
}
