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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class IncomeGoalActivity extends AppCompatActivity {

            private TextView TotalBudgetAmountTextview;
            private RecyclerView recyclerView;



            private FloatingActionButton fab;
            private DatabaseReference budgetRef;
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
                budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
                loader = new ProgressDialog(this);

                TotalBudgetAmountTextview=findViewById(R.id.TotalBudgetAmountTextview);
                recyclerView=findViewById(R.id.recyclerView);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setStackFromEnd(true);
                linearLayoutManager.setReverseLayout(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        additem();
                    }
                });
            }

            private void additem() {
                AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                View myView = inflater.inflate(R.layout.input_layout, null);
                myDiolag.setView(myView);

                final AlertDialog dialog=myDiolag.create();
                dialog.setCancelable(false);
                dialog.show();

                final Spinner itemsSpinner = myView.findViewById(R.id.itemsSpinner);
                final EditText amount = myView.findViewById(R.id.amount);
                final Button cancel =myView.findViewById(R.id.cancel);
                final Button save =myView.findViewById(R.id.save);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // validation
                        String budgetAmount = amount.getText().toString();
                        String budgetItem = itemsSpinner.getSelectedItem().toString();
                        if(TextUtils.isEmpty(budgetAmount)){
                            amount.setError("Amount is Required!");
                            return;
                        }
                        if (budgetItem.equals("Select Item")){
                            Toast.makeText(com.example.rs_fy.BudgetActivity.this,"Select a valid Item",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            loader.setMessage("Adding a Budget Item");
                            loader.setCanceledOnTouchOutside(false);
                            loader.show();

                            String id=budgetRef.push().getKey();
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Calendar cal =Calendar.getInstance();
                            String date= dateFormat.format(cal.getTime());

                            MutableDateTime epoch =new MutableDateTime();
                            epoch.setDate(0);
                            DateTime now =new DateTime();
                            Months months = Months.monthsBetween(epoch,now);
                            //pass parameters according to the paramiterlized constucter
                            Data data = new Data(budgetItem,date,id,null,Integer.parseInt(budgetAmount), months.getMonths());
                            budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(com.example.rs_fy.BudgetActivity.this,"Budget Item Added Sccessfuly",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(com.example.rs_fy.BudgetActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                        .setQuery(budgetRef,Data.class)
                        .build();

                FirebaseRecyclerAdapter<Data, com.example.rs_fy.BudgetActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, com.example.rs_fy.BudgetActivity.MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull com.example.rs_fy.BudgetActivity.MyViewHolder holder, int position, @NonNull  Data model) {
                        holder.setItemAmount("Allocated amount: Rs" +model.getAmount());
                        holder.setDate("On : "+model.getDate());
                        holder.setItemName("Budget Item : "+model.getItem());
                        holder.notes.setVisibility(View.GONE);
                        switch (model.getItem()){

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

                    @NonNull

                    @Override
                    public com.example.rs_fy.BudgetActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);
                        return new com.example.rs_fy.BudgetActivity.MyViewHolder(view);
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
                public TextView notes;

                public MyViewHolder(@NonNull  View itemView) {
                    super(itemView);
                    mView=itemView;
                    imageView =itemView.findViewById(R.id.imageView);
                    notes=itemView.findViewById(R.id.note);

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
        }
    }
}