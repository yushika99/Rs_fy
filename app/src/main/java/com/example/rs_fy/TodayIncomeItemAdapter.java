package com.example.rs_fy;
import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.Context;
//import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayIncomeItemAdapter  extends  RecyclerView.Adapter<TodayIncomeItemAdapter.ViewHolder>{

    private Context mContext;
    private List<GoalData> myDataList;
    private String post_key = "";
    private String item = "";
    private String note = "";
    private int amount = 0;

    public TodayIncomeItemAdapter(Context mContext, List<GoalData> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.goal_retrieve_layout, parent,false);
        return new TodayIncomeItemAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TodayIncomeItemAdapter.ViewHolder holder, int position) {

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = data.getId();
                item = data.getItem();
                amount = data.getAmount();
                note = data.getNotes();
                updateBdata();
            }
        });

    }

    private void updateBdata() {

        AlertDialog.Builder myDialog= new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.update_goal_layout, null);

        myDialog.setView(mView);
        final  AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final  EditText mNotes = mView.findViewById(R.id.note);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        try {
            mNotes.setText(note);
            mNotes.setSelection(note.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button delBut = mView.findViewById(R.id.delBut);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = Integer.parseInt(mAmount.getText().toString());
                note = mNotes.getText().toString();


                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);

                String itemNday = item+date;
                String itemNweek = item+weeks.getWeeks();
                String itemNmonth = item+months.getMonths();

                GoalData data = new GoalData(item, date, post_key, itemNday, itemNweek, itemNmonth, amount, weeks.getWeeks(), months.getMonths(), note);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();

            }
        });

        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "Deleted  successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
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