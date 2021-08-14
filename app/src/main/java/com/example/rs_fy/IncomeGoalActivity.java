package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IncomeGoalActivity extends AppCompatActivity {

    private TextView TotalGoalAmountTextview;
    private androidx.recyclerview.widget.RecyclerView recyclerView;



    private FloatingActionButton gfab;
    private DatabaseReference incomeRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    //updating budget
    private String post_key="";
    private String item="";
    private int amount=0;

    public IncomeGoalActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        //initializing
        mAuth = FirebaseAuth.getInstance();
        incomeRef = FirebaseDatabase.getInstance().getReference().child("goal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        TotalGoalAmountTextview=findViewById(R.id.TotalGoalAmountTextview);
        recyclerView=findViewById(R.id.goalrecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                int totalAmount =0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount+=data.getAmount();
                    String sTotal =String.valueOf("Month Goal: Rs. "+totalAmount);
                    TotalGoalAmountTextview.setText(sTotal);
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });



        gfab = findViewById(R.id.gfab);
        gfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });
    }

    private void additem() {
        AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_goal_layout, null);
        myDiolag.setView(myView);

        final AlertDialog dialog=myDiolag.create();
        dialog.setCancelable(false);

        final Spinner goalSpinner = myView.findViewById(R.id.goalSpinner);
        final EditText amount = myView.findViewById(R.id.goalamount);
        final Button cancel =myView.findViewById(R.id.goalcancel);
        final Button save =myView.findViewById(R.id.goalsave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validation
                String goalAmount = amount.getText().toString();
                String goalAmount = goalSpinner.getSelectedItem().toString();
                if(TextUtils.isEmpty(goalAmount)){
                    amount.setError("Amount is Required!");
                    return;
                }
                if (goalAmount.equals("Select Item")){
                    Toast.makeText(IncomeGoalActivity.this,"Select a valid Item",Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("Adding a Goal");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id=incomeRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal =Calendar.getInstance();
                    String date= dateFormat.format(cal.getTime());

                    MutableDateTime epoch =new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now =new DateTime();
                    Weeks weeks= Weeks.weeksBetween(epoch,now);
                    Months months = Months.monthsBetween(epoch,now);


                    //pass parameters according to the paramiterlized constucter
                    Data data = new Data(budgetItem,date,id,null,Integer.parseInt(goalAmount), months.getMonths(),weeks.getWeeks());
                    incomeRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(IncomeGoalActivity.this,"Goal Item Added Sccessfuly",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(IncomeGoalActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void OnStart(){
        super.onStart();

        FirebaseRecyclerOptions<Data> options =new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(incomeRef,Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull  Data model) {
                holder.setItemAmount("Allocated amount: Rs" +model.getAmount());
                holder.setDate("On : "+model.getDate());
                holder.setItemName("Goal Item : "+model.getItem());
                holder.notes.setVisibility(View.GONE);
                switch (model.getItem()){





                    case "Salary":
                        holder.imageView.setImageResource(R.drawable.iconsetransport);
                        break;
                    case "Grants":
                        holder.imageView.setImageResource(R.drawable.iconsefood);
                        break;
                    case "Rental":
                        holder.imageView.setImageResource(R.drawable.iconsehome);
                        break;
                    case "Invesment":
                        holder.imageView.setImageResource(R.drawable.iconseentertainment);
                        break;
                    case "Wages":
                        holder.imageView.setImageResource(R.drawable.iconseeducation);
                        break;
                    case "side business":
                        holder.imageView.setImageResource(R.drawable.iconsecharity);
                        break;
                    case "Dividend":
                        holder.imageView.setImageResource(R.drawable.iconsehealth);
                        break;
                    case "Pension":
                        holder.imageView.setImageResource(R.drawable.iconsepersonal);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.iconseother);
                        break;

                }
                //update budget
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(position).getKey();
                        item=model.getItem();
                        amount = model.getAmount();
                        updateBdata();
                    }
                });
            }

            @NonNull

            @Override
            public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notify();
        // check this error
    }

    public class MyViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView notes,date;

        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);
            mView=itemView;
            imageView =itemView.findViewById(R.id.imageView);
            notes=itemView.findViewById(R.id.note);
            date=itemView.findViewById(R.id.date);

        }

        public void setItemName(String itemName){
            TextView item= mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount){
            TextView amount= mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setDate(String itemDate){
            TextView date= mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }
    private void updateBdata(){

        AlertDialog.Builder myDialog= new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_budget_layout,null);

        myDialog.setView(mView);
        final AlertDialog dialog=myDialog.create();

        final TextView mItem=mView.findViewById(R.id.itemName);
        final EditText mAmount=mView.findViewById(R.id.amount);
        final EditText mNotes=mView.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button delBtn=mView.findViewById(R.id.btnDelete);
        Button btnUpdate=mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount=Integer.parseInt(mAmount.getText().toString());
                //take month

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal =Calendar.getInstance();
                String date= dateFormat.format(cal.getTime());

                MutableDateTime epoch =new MutableDateTime();
                epoch.setDate(0);
                DateTime now =new DateTime();
                Weeks weeks= Weeks.weeksBetween(epoch,now);
                Months months = Months.monthsBetween(epoch,now);


                Data data = new Data(item,date,post_key,null,amount, months.getMonths(),weeks.getWeeks());
                incomeRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(IncomeGoalActivity.this," Item Updated Sccessfuly ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(IncomeGoalActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                incomeRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(IncomeGoalActivity.this,"  Deleted  Sccessfuly ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(IncomeGoalActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();

            }
        });
        dialog.show();

    }
}