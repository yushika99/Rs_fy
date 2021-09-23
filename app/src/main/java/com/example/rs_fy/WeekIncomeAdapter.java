package com.example.rs_fy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekIncomeAdapter extends RecyclerView.Adapter<WeekIncomeAdapter.ViewHolder> {

    private Context mContext;
    private List<GoalData> myDataList;

    public WeekIncomeAdapter(Context mContext, List<GoalData> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.goal_retrieve_layout, parent,false);
        return new WeekIncomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final  GoalData data = myDataList.get(position);

        holder.item.setText("Item: "+ data.getItem());
        holder.amount.setText("Amount: "+ data.getAmount());
        holder.date.setText("On "+data.getDate());
        holder.notes.setText("Note: "+data.getNotes());


        switch (data.getItem()) {
            case "Salary":
                holder.imageView.setImageResource(R.drawable.incomesalary);
                break;
            case "Grants":
                holder.imageView.setImageResource(R.drawable.incomegrantt);
                break;
            case "Rental":
                holder.imageView.setImageResource(R.drawable.incomerental);
                break;
            case "Invesment":
                holder.imageView.setImageResource(R.drawable.incomeinvest);
                break;
            case "Wages":
                holder.imageView.setImageResource(R.drawable.incomewage);
                break;
            case "side business":
                holder.imageView.setImageResource(R.drawable.incomeside);
                break;
            case "Dividend":
                holder.imageView.setImageResource(R.drawable.incomedevidence);
                break;
            case "Pension":
                holder.imageView.setImageResource(R.drawable.incomepension);
                break;
            case "Other":
                holder.imageView.setImageResource(R.drawable.incomeother);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item, amount, date, notes;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            notes = itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
