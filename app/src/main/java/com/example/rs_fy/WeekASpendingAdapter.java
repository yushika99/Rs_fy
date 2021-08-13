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

public class WeekASpendingAdapter extends RecyclerView.Adapter<WeekASpendingAdapter.ViewHolder> {

    private Context mContext;
    private List<Data> myDataList;

    public WeekASpendingAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,parent,false);
        return new WeekASpendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  WeekASpendingAdapter.ViewHolder holder, int position) {

            final Data data = myDataList.get(position);
            holder.item.setText("Item: "+data.getItem());
            holder.amount.setText("Amount: "+data.getAmount());
            holder.date.setText("On: "+data.getDate());
            holder.notes.setText("Item: "+data.getNotes());

            switch (data.getItem()){

                case "Transport":
                    holder.imageView.setImageResource(R.drawable.iconsetransport);
                    break;
                case "Food":
                    holder.imageView.setImageResource(R.drawable.iconsefood);
                    break;
                case "House":
                    holder.imageView.setImageResource(R.drawable.iconsehome);
                    break;
                case "Entertainment":
                    holder.imageView.setImageResource(R.drawable.iconseentertainment);
                    break;
                case "Education":
                    holder.imageView.setImageResource(R.drawable.iconseeducation);
                    break;
                case "Charity":
                    holder.imageView.setImageResource(R.drawable.iconsecharity);
                    break;
                case "Health":
                    holder.imageView.setImageResource(R.drawable.iconsehealth);
                    break;
                case "Personal":
                    holder.imageView.setImageResource(R.drawable.iconsepersonal);
                    break;
                case "Other":
                    holder.imageView.setImageResource(R.drawable.iconseother);
                    break;

            }

    }

    @Override
    public int getItemCount() {
        return myDataList.size();
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
