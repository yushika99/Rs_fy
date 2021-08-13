package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class IncomeGoalActivity extends AppCompatActivity {

            private TextView TotalIncomeGoalTextview;
            private RecyclerView recyclerView;



            private FloatingActionButton goalfab;
            private DatabaseReference goalRef;
            private FirebaseAuth mAuth;
            private ProgressDialog loader;

            public IncomeGoalActivity() {
            }


                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.activity_income_goal);

                //initializing
                mAuth = FirebaseAuth.getInstance();
                goalRef = FirebaseDatabase.getInstance().getReference().child("Income Goal").child(mAuth.getCurrentUser().getUid());
                loader = new ProgressDialog(this);

                TotalIncomeGoalTextview=findViewById(R.id.TotalIncomeGoalTextview);
                recyclerView=findViewById(R.id.recyclerView);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setStackFromEnd(true);
                linearLayoutManager.setReverseLayout(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);

                goalRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                        int totalGoal=0;

                        for(DataSnapshot snap:snapshot.getChildren()){
                            goalData data=snap.getValue(goalData.class);
                            totalGoal+=data.getGoal();
                            String sTotal=String.valueOf("Month Goal:Rs"+totalGoal);
                            TotalIncomeGoalTextview.setText(sTotal);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                    }
                })
                        goalfab = findViewById(R.id.goalfab);
                        goalfab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addgoal();
                    }
                });
            }

            private void addgoal() {
                AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                View myView = inflater.inflate(R.layout.input_goal_layout, null);
                myDiolag.setView(myView);

                final AlertDialog dialog=myDiolag.create();
                dialog.setCancelable(false);
                dialog.show();

                final Spinner goalspinner = myView.findViewById(R.id.goalspinner);
                final EditText goalAmount = myView.findViewById(R.id.goalamount);
                final Button goalcancel =myView.findViewById(R.id.goalcancel);
                final Button goalsave =myView.findViewById(R.id.goalsave);

                goalsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // validation
                        String goalAmount = goalAmount.getText().toString();
                        String goalItem = goalspinner.getSelectedItem().toString();
                        if(TextUtils.isEmpty(goalAmount)){
                            goalAmount.setError("Amount is Required!");
                            return;
                        }
                        if (goalItem.equals("Select Goal")){
                            Toast.makeText(com.example.rs_fy.IncomeGoalActivity.this,"Select a valid Item",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            loader.setMessage("Adding an Income Goal");
                            loader.setCanceledOnTouchOutside(false);
                            loader.show();

                            String id=goalRef.push().getKey();
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Calendar cal =Calendar.getInstance();
                            String date= dateFormat.format(cal.getTime());

                            MutableDateTime epoch =new MutableDateTime();
                            epoch.setDate(0);
                            DateTime now =new DateTime();
                            Months months = Months.monthsBetween(epoch,now);
                            //pass parameters according to the paramiterlized constucter
                            goalData data = new goalData(goalItem,goaldate,goalid,null,Integer.parseInt(goalAmount), goalmonth.getMonths());
                            goalRef.child(goalid).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(com.example.rs_fy.IncomeGoalActivity.this,"Goal Item Added Sccessfuly",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(com.example.rs_fy.IncomeGoalActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    loader.dismiss();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });
                goalcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            public void OnStart(){
                super.onStart();

                FirebaseRecyclerOptions<goalData> goaloptions =new FirebaseRecyclerOptions.Builder<goalData>()
                        .setQuery(goalRef,goalData.class)
                        .build();

                FirebaseRecyclerAdapter<goalData, com.example.rs_fy.IncomeGoalActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<goalData, com.example.rs_fy.IncomeGoalActivity.MyViewHolder>(goaloptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull com.example.rs_fy.IncomeGoalActivity.MyViewHolder holder, int position, @NonNull  Data model) {
                        holder.setItemAmount("Allocated amount: Rs" +model.getAmount());
                        holder.setDate("On : "+model.getDate());
                        holder.setItemName("Goal : "+model.getGoal());
                        holder.notes.setVisibility(View.GONE);

                        switch (model.getGoal()){

                            case "Salary":
                                holder.imageView.setImageResource(R.drawable.incomeSalary);
                                break;
                            case "Grants":
                                holder.imageView.setImageResource(R.drawable.incomeGrants);
                                break;
                            case "Rental":
                                holder.imageView.setImageResource(R.drawable.incomeRental);
                                break;
                            case "Invesment":
                                holder.imageView.setImageResource(R.drawable.incomeInvestments);
                                break;
                            case "Wages":
                                holder.imageView.setImageResource(R.drawable.incomewage);
                                break;
                            case "side business":
                                holder.imageView.setImageResource(R.drawable.incomeside);
                                break;
                            case "Dividend":
                                holder.imageView.setImageResource(R.drawable.incomeDevidends);
                                break;
                            case "Pension":
                                holder.imageView.setImageResource(R.drawable.incomePension);
                                break;
                            case "Other":
                                holder.imageView.setImageResource(R.drawable.incomeOther);
                                break;

                        }
                    }

                    @NonNull

                    @Override
                    public com.example.rs_fy.IncomeGoalActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_retrieve_layout,parent,false);
                        return new com.example.rs_fy.IncomeGoalActivity().MyViewHolder(view);
                    }
                };

                recyclerView.setAdapter(adapter);
                adapter.startListening();
                adapter.notify();
                // check this error
            }

            public class MyViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
                View mView;
                public ImageView goalImageview;
                public TextView goalnote;

                public MyViewHolder(@NonNull  View itemView) {
                    super(itemView);
                    mView=itemView;
                    goalImageview =itemView.findViewById(R.id.goalImageview);
                    goalnote=itemView.findViewById(R.id.goalnote);

                }

                public void setGoal(String goalitem){
                    TextView goalitem= mView.findViewById(R.id.goalitem);
                    goalitem.setText(goalitem);
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
        }
    }
}