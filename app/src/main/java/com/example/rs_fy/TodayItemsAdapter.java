package com.example.rs_fy;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key= data.getId();
                item= data.getItem();
                amount = data.getAmount();
                note= data.getNotes();
                updateBdata();
            }
        });
    }

    private void updateBdata() {

            AlertDialog.Builder myDialog= new AlertDialog.Builder(mContext);
            LayoutInflater inflater= LayoutInflater.from(mContext);
            View mView = inflater.inflate(R.layout.update_budget_layout,null);

            myDialog.setView(mView);
            final AlertDialog dialog=myDialog.create();

            final TextView mItem=mView.findViewById(R.id.itemName);
            final EditText mAmount=mView.findViewById(R.id.amount);
            final EditText mNotes=mView.findViewById(R.id.note);


            mItem.setText(item);

            mAmount.setText(String.valueOf(amount));
            mAmount.setSelection(String.valueOf(amount).length());

            mNotes.setText(note);
            mNotes.setSelection(note.length());

            Button delBtn=mView.findViewById(R.id.btnDelete);
            Button btnUpdate=mView.findViewById(R.id.btnUpdate);

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    amount=Integer.parseInt(mAmount.getText().toString());
                    note=mNotes.getText().toString();

                    //take month
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal =Calendar.getInstance();
                    String date= dateFormat.format(cal.getTime());

                    MutableDateTime epoch =new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now =new DateTime();
                    Weeks weeks= Weeks.weeksBetween(epoch,now);
                    Months months = Months.monthsBetween(epoch,now);


                    Data data = new Data(item,date,post_key,note,amount, months.getMonths(),weeks.getWeeks());
                    //databse refrence
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext," Item Updated Sccessfuly ",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    dialog.dismiss();
                }
            });

            //delete item
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext,"  Deleted  Sccessfuly ",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,task.getException().toString(), Toast.LENGTH_SHORT).show();
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
