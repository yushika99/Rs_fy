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
    private DatabaseReference goalref;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    //updating budget
    private String ipost_key="";
    private String goalItem="";
    private int goalamount=0;

    public IncomeGoalActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        //initializing
        mAuth = FirebaseAuth.getInstance();
        goalref = FirebaseDatabase.getInstance().getReference().child("goal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        TotalGoalAmountTextview=findViewById(R.id.TotalGoalAmountTextview);
        recyclerView=findViewById(R.id.goalrecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        goalref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                int totalAmountGoal =0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    GoalData data = snap.getValue(GoalData.class);
                    totalAmountGoal+=data.getGoalamount();
                    String gTotal =String.valueOf("Month Goal: Rs. "+totalAmountGoal);
                    TotalGoalAmountTextview.setText(gTotal);
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


        final Spinner inputgoalspinner = myView.findViewById(R.id.inputgoalspinner);
        final EditText inputgoalamount = myView.findViewById(R.id.inputgoalamount);
        final Button inputgoalcancel =myView.findViewById(R.id.inputgoalcancel);
        final Button inputgoalsave =myView.findViewById(R.id.inputgoalsave);

        inputgoalsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validation
                String goalAmount = inputgoalamount.getText().toString();
                String goalItem = inputgoalspinner.getSelectedItem().toString();
                if(TextUtils.isEmpty(goalAmount)){
                    inputgoalamount.setError("Amount is Required!");
                    return;
                }
                if (goalAmount.equals("Select Item")){
                    Toast.makeText(IncomeGoalActivity.this,"Select a valid Item",Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("Adding a Goal");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id=goalref.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal =Calendar.getInstance();
                    String date= dateFormat.format(cal.getTime());

                    MutableDateTime epoch =new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now =new DateTime();
                    Weeks weeks= Weeks.weeksBetween(epoch,now);
                    Months months = Months.monthsBetween(epoch,now);


                    //pass parameters according to the paramiterlized constucter
                    GoalData data = new GoalData(goalItem,goaldate,goalid,null,Integer.parseInt(goalAmount), goalmonth.getMonths(),goalweeks.getWeeks());
                    goalref.child(goalid).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        inputgoalcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void OnStart(){
        super.onStart();

        FirebaseRecyclerOptions<GoalData> options =new FirebaseRecyclerOptions.Builder<GoalData>()
                .setQuery(goalref,GoalData.class)
                .build();

        FirebaseRecyclerAdapter<GoalData, MyViewHolder> adapter = new FirebaseRecyclerAdapter<GoalData, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull  GoalData model) {
                holder.setItemAmount("Allocated amount: Rs" +model.getGoalamount());
                holder.setDate("On : "+model.getGoaldate());
                holder.setItemName("Goal Item : "+model.getGoalItem());
                holder.goalnotes.setVisibility(View.GONE);
                switch (model.getGoalItem()){





                    case "Salary":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomesalary);
                        break;
                    case "Grants":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomegrantt);
                        break;
                    case "Rental":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomerental);
                        break;
                    case "Invesment":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomeinvest);
                        break;
                    case "Wages":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomewage);
                        break;
                    case "side business":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomeside);
                        break;
                    case "Dividend":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomedevidence);
                        break;
                    case "Pension":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomepension);
                        break;
                    case "Other":
                        holder.retrivegoalImageview.setImageResource(R.drawable.incomeother);
                        break;

                }
                //update goal
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ipost_key=getgoalRef(position).getKey();
                        goalItem=model.getGoalItem();
                        goalamount = model.getGoalamount();
                        updateIdata();
                    }
                });
            }

            @NonNull

            @Override
            public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_retrieve_layout,parent,false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notify();

    }

    public class MyViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        View mView;
        public ImageView retrivegoalImageview;
        public TextView retrivegoalnote,retrivegoaldate;

        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);
            mView=itemView;
            retrivegoalImageview =itemView.findViewById(R.id.retrivegoalImageview);
            retrivegoalnote=itemView.findViewById(R.id.retrivegoalnote);
            retrivegoaldate=itemView.findViewById(R.id.retrivegoaldate);

        }

        public void setItemName(String itemName){
            TextView retrivegoalitem= mView.findViewById(R.id.retrivegoalitem);
            retrivegoalitem.setText(itemName);
        }

        public void setItemAmount(String itemAmount){
            TextView retrivegoalamount= mView.findViewById(R.id.retrivegoalamount);
            retrivegoalamount.setText(itemAmount);
        }

        public void setDate(String itemDate){
            TextView retrivegoaldate= mView.findViewById(R.id.retrivegoaldate);
            retrivegoaldate.setText(itemDate);
        }
    }
    private void updateIdata(){

        AlertDialog.Builder myDialog= new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View gView = inflater.inflate(R.layout.update_goal_layout,null);

        myDialog.setView(gView);
        final AlertDialog dialog=myDialog.create();

        final TextView gItem=gView.findViewById(R.id.retrivegoalitem);
        final EditText gAmount=gView.findViewById(R.id.retrivegoalamount);
        final EditText gNotes=gView.findViewById(R.id.retrivegoalnote);

        gNotes.setVisibility(View.GONE);

        gItem.setText(goalItem);

        gAmount.setText(String.valueOf(goalamount));
        gAmount.setSelection(String.valueOf(goalamount).length());

        Button deleteBtn=gView.findViewById(R.id.updategoaldelete);
        Button btnUpdate=gView.findViewById(R.id.updategoalupdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalamount=Integer.parseInt(gAmount.getText().toString());
                //take month

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal =Calendar.getInstance();
                String date= dateFormat.format(cal.getTime());

                MutableDateTime epoch =new MutableDateTime();
                epoch.setDate(0);
                DateTime now =new DateTime();
                Weeks weeks= Weeks.weeksBetween(epoch,now);
                Months months = Months.monthsBetween(epoch,now);

//ipost_key=getgoalRef(position).getKey();
//                        goalItem=model.getGoalItem();
//                        goalamount = model.getGoalamount();
//                        updateIdata();
                
                
                GoalData data = new GoalData(goalItem,goaldate,ipost_key,null,goalamount, goalmonth.getGoalmonth(),goalweeks.getWeeks());
                goalref.child(ipost_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(IncomeGoalActivity.this," Goal Updated Sccessfuly ",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(IncomeGoalActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();
            }
        });

        //delete item
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalref.child(ipost_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(IncomeGoalActivity.this,"  Goal Deleted  Sccessfuly ",Toast.LENGTH_SHORT).show();
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