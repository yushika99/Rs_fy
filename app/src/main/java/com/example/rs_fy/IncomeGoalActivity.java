package com.example.rs_fy;

import  androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class IncomeGoalActivity extends AppCompatActivity {

    private TextView TotalGoalAmountTextview;
    private RecyclerView recyclerView;


    private FloatingActionButton fab;

    private DatabaseReference goalRef, personalRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    //updating budget
    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        //initializing
        mAuth = FirebaseAuth.getInstance();
        goalRef = FirebaseDatabase.getInstance().getReference().child("goal").child(mAuth.getCurrentUser().getUid());
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        TotalGoalAmountTextview = findViewById(R.id.TotalGoalAmountTextview);
        recyclerView = findViewById(R.id.recyclerView);

        fab = findViewById(R.id.fab);
//        fab.setOnClickListener((View.OnClickListener) this);

        getData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        goalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    GoalData data = snap.getValue(GoalData.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Month Goal: Rs. " + totalAmount);
                    TotalGoalAmountTextview.setText(sTotal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//floating action button to add goals


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });

        goalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalammount = 0;

                    for (DataSnapshot snap : snapshot.getChildren()) {

                        GoalData data = snap.getValue(GoalData.class);

                        totalammount += data.getAmount();

                        String sttotal = String.valueOf("Month Goal: " + totalammount);

                        TotalGoalAmountTextview.setText(sttotal);

                    }
                    int weeklyGoal = totalammount / 4;
                    int dailyGoal = totalammount / 30;
                    personalRef.child("goal").setValue(totalammount);
                    personalRef.child("weeklyGoal").setValue(weeklyGoal);
                    personalRef.child("dailyGoal").setValue(dailyGoal);

                } else {
                    personalRef.child("goal").setValue(0);
                    personalRef.child("weeklyGoal").setValue(0);
                    personalRef.child("dailyGoal").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getMonthSalaryIncomeRatios();
        getMonthGrantsIncomeRatios();
        getMonthRentalIncomeRatios();
        getMonthInvesmentIncomeRatios();
        getMonthWagesIncomeRatios();
        getMonthSide_businessIncomeRatios();
        getMonthDividendIncomeRatios();
        getMonthPensionIncomeRatios();
        getMonthOtherIncomeRatios();


    }


    private void additem() {
        AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_goal_layout, null);
        myDiolag.setView(myView);

        final AlertDialog dialog = myDiolag.create();
        dialog.setCancelable(false);


        final Spinner itemSpinner = myView.findViewById(R.id.itemspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validation
                String goalAmount = amount.getText().toString();
                String goalItem = itemSpinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(goalAmount)) {
                    amount.setError("Amount is Required!");
                    return;
                }
                if (goalItem.equals("Select Item")) {
                    Toast.makeText(IncomeGoalActivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("Adding a Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = goalRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    String itemNday = goalItem + date;
                    String itemNweek = goalItem + weeks.getWeeks();
                    String itemNmonth = goalItem + months.getMonths();


                    //pass parameters according to the paramiterlized constucter
                    GoalData data = new GoalData(goalItem, date, id, itemNday, itemNweek, itemNmonth, Integer.parseInt(goalAmount), weeks.getWeeks(), months.getMonths(), null);
                    goalRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(IncomeGoalActivity.this, "Goal Item Added Sccessfuly", Toast.LENGTH_SHORT).show();
                                getData();
                            } else {
                                Toast.makeText(IncomeGoalActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

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

    public void getData(){


        FirebaseRecyclerOptions<GoalData> options = new FirebaseRecyclerOptions.Builder<GoalData>()
                .setQuery(goalRef,GoalData.class)
                .build();

//        Log.d(TAG, "getData1: "+goalRef.child("MkSrlUoFiSAa6LLiWqF"));
//        Log.d(TAG, "getOption: "+options);

        FirebaseRecyclerAdapter<GoalData, MyViewHolder> adapter = new FirebaseRecyclerAdapter<GoalData, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull GoalData model) {
                holder.setItemAmount("Allocated amount: Rs" + model.getAmount());
                holder.setDate("On : " + model.getDate());
                holder.setItemName("Goal Item : " + model.getItem());

                holder.notes.setVisibility(View.GONE);


                switch (model.getItem()) {
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
                //update goal
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        updateBdata();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView imageView;
        public TextView notes, date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);

        }

        public void setItemName(String itemName) {
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount(String itemAmount) {
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setDate(String itemDate) {
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }

    private void updateBdata() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_goal_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button deleteBtn = mView.findViewById(R.id.delBut);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(mAmount.getText().toString());
                //take month

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);

                String itemNday = item + date;
                String itemNweek = item + weeks.getWeeks();
                String itemNmonth = item + months.getMonths();


                GoalData data = new GoalData(item, date, post_key, itemNday, itemNweek, itemNmonth, amount, weeks.getWeeks(), months.getMonths(), null);
                goalRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(IncomeGoalActivity.this, "Goal updated Sccessfuly", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(IncomeGoalActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();
                    }
                });

                dialog.dismiss();
            }
        });

        //delete item
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(IncomeGoalActivity.this, "  Goal Deleted  Sccessfuly ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(IncomeGoalActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    private void getMonthSalaryIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Salary");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int daySalRatio = pTotal / 30;
                    int weekSalRatio = pTotal / 4;
                    int monthSalRatio = pTotal;

                    personalRef.child("daySalRatio").setValue(daySalRatio);
                    personalRef.child("weekSalRatio").setValue(weekSalRatio);
                    personalRef.child("monthSalRatio").setValue(monthSalRatio);

                } else {
                    personalRef.child("daySalRatio").setValue(0);
                    personalRef.child("weekSalRatio").setValue(0);
                    personalRef.child("monthSalRatio").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthGrantsIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayGrantsRatio = pTotal / 30;
                    int weekGrantsRatio = pTotal / 4;
                    int monthGrantsRatio = pTotal;

                    personalRef.child("dayGrantsRatio").setValue(dayGrantsRatio);
                    personalRef.child("weekGrantsRatio").setValue(weekGrantsRatio);
                    personalRef.child("monthGrantsRatio").setValue(monthGrantsRatio);

                } else {
                    personalRef.child("dayGrantsRatio").setValue(0);
                    personalRef.child("weekGrantsRatio").setValue(0);
                    personalRef.child("monthGrantsRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthRentalIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Rental Income");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayRentRatio = pTotal / 30;
                    int weekRentRatio = pTotal / 4;
                    int monthRentRatio = pTotal;

                    personalRef.child("dayRentRatio").setValue(dayRentRatio);
                    personalRef.child("weekRentRatio").setValue(weekRentRatio);
                    personalRef.child("monthRentRatio").setValue(monthRentRatio);

                } else {
                    personalRef.child("dayRentRatio").setValue(0);
                    personalRef.child("weekRentRatio").setValue(0);
                    personalRef.child("monthRentRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthInvesmentIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Investmant");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayInvestRatio = pTotal / 30;
                    int weekInvestRatio = pTotal / 4;
                    int monthInvestRatio = pTotal;

                    personalRef.child("dayInvestRatio").setValue(dayInvestRatio);
                    personalRef.child("weekInvestRatio").setValue(weekInvestRatio);
                    personalRef.child("monthInvestRatio").setValue(monthInvestRatio);

                } else {
                    personalRef.child("dayInvestRatio").setValue(0);
                    personalRef.child("weekInvestRatio").setValue(0);
                    personalRef.child("monthInvestRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthWagesIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("wage");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayWageRatio = pTotal / 30;
                    int weekWageRatio = pTotal / 4;
                    int monthWageRatio = pTotal;

                    personalRef.child("dayWageRatio").setValue(dayWageRatio);
                    personalRef.child("weekWageRatio").setValue(weekWageRatio);
                    personalRef.child("monthWageRatio").setValue(monthWageRatio);

                } else {
                    personalRef.child("dayWageRatio").setValue(0);
                    personalRef.child("weekWageRatio").setValue(0);
                    personalRef.child("monthWageRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthSide_businessIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Charity");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int daySbRatio = pTotal / 30;
                    int weekSbRatio = pTotal / 4;
                    int monthSbRatio = pTotal;

                    personalRef.child("daySbRatio").setValue(daySbRatio);
                    personalRef.child("weekSbRatio").setValue(weekSbRatio);
                    personalRef.child("monthSbRatio").setValue(monthSbRatio);

                } else {
                    personalRef.child("daySbRatio").setValue(0);
                    personalRef.child("weekSbRatio").setValue(0);
                    personalRef.child("monthSbRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthDividendIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Dividend");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayDividendRatio = pTotal / 30;
                    int weekDividendRatio = pTotal / 4;
                    int monthDividendRatio = pTotal;

                    personalRef.child("dayDividendRatio").setValue(dayDividendRatio);
                    personalRef.child("weekDividendRatio").setValue(weekDividendRatio);
                    personalRef.child("monthDividendRatio").setValue(monthDividendRatio);

                } else {
                    personalRef.child("dayDividendRatio").setValue(0);
                    personalRef.child("weekDividendRatio").setValue(0);
                    personalRef.child("monthDividendRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthPensionIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Pension");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayPensionRatio = pTotal / 30;
                    int weekPensionRatio = pTotal / 4;
                    int monthPensionRatio = pTotal;

                    personalRef.child("dayPensionRatio").setValue(dayPensionRatio);
                    personalRef.child("weekPensionRatio").setValue(weekPensionRatio);
                    personalRef.child("monthPensionRatio").setValue(monthPensionRatio);

                } else {
                    personalRef.child("dayPensionRatio").setValue(0);
                    personalRef.child("weekPensionRatio").setValue(0);
                    personalRef.child("monthPensionRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthOtherIncomeRatios() {
        Query query = goalRef.orderByChild("item").equalTo("Other");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayOtherRatio = pTotal / 30;
                    int weekOtherRatio = pTotal / 4;
                    int monthOtherRatio = pTotal;

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio);

                } else {
                    personalRef.child("dayOtherRatio").setValue(0);
                    personalRef.child("weekOtherRatio").setValue(0);
                    personalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}