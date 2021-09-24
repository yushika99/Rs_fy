package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

public class expensesActivity extends AppCompatActivity {

private Toolbar toolbar;
//private ImageView budgetEBtnImageView,todayEBtnImageView,weekEBtnImageView,monthEBtnImageView,analysisEBtnImageView;
private TextView budgetTv,todayTv,weekTv,monthTv,inpocketTv;
private CardView budgetcardViewbtn,todaycardViewbtn,weekcardViewbtn,monthcardViewbtn,anlyticscardViewbtn,historycardViewbtn;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef, expensesRef, personalRef;
    private String onlineUserID = "";

    private int totalAmountMonth = 0;
    private int totalAmountBudget = 0;
    private  int totalAmountBudgetB = 0;
    private  int totalAmountBudgetC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My expenses");

//        budgetEBtnImageView =findViewById(R.id.budgetEBtnImageView);
//        todayEBtnImageView =findViewById(R.id.todayEBtnImageView);
//        weekEBtnImageView =findViewById(R.id.weekEBtnImageView);
//        monthEBtnImageView =findViewById(R.id.monthEBtnImageView);
//        analysisEBtnImageView =findViewById(R.id.analysisEBtnImageView);

        budgetcardViewbtn =findViewById(R.id.budgetcardViewbtn);
        todaycardViewbtn =findViewById(R.id.todaycardViewbtn);
        weekcardViewbtn =findViewById(R.id.weekcardViewbtn);
        monthcardViewbtn =findViewById(R.id.monthcardViewbtn);
        anlyticscardViewbtn =findViewById(R.id.anlyticscardViewbtn);
        historycardViewbtn =findViewById(R.id.historycardViewbtn);



        //table
        budgetTv =findViewById(R.id.budgetTv);
        todayTv =findViewById(R.id.todayTv);
        weekTv =findViewById(R.id.weekTv);
        monthTv =findViewById(R.id.monthTv);
        inpocketTv =findViewById(R.id.inpocketTv);

        //databse
        mAuth = FirebaseAuth.getInstance();
        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserID);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID);


        budgetcardViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (expensesActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        todaycardViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

        weekcardViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,WeekSpendingActvity.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });

        monthcardViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,WeekSpendingActvity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });

        anlyticscardViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,ChooseExpensesAnlyticsActivity.class);
                startActivity(intent);
            }
        });

        historycardViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(expensesActivity.this, HistortyExActivity.class);
                startActivity(intent);
            }
        });

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetB+=pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetB;
                    personalRef.child("budget").setValue(totalAmountBudgetC);
                }else {
                    personalRef.child("budget").setValue(0);
                    Toast.makeText(expensesActivity.this, "Please Set a BUDGET ", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        getBudgetAmount();
        getTodaySpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        //inpocket
        getSavings();

    }

    private void getBudgetAmount() {

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudget+=pTotal;
                        budgetTv.setText("Rs "+String.valueOf(totalAmountBudget));
                    }
                }else {
                    totalAmountBudget=0;
                    budgetTv.setText("Rs. "+String.valueOf(0));


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getTodaySpentAmount() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    todayTv.setText("Rs. "+ totalAmount);
                }
                personalRef.child("today").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(expensesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getMonthSpentAmount() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    monthTv.setText("Rs. "+ totalAmount);

                }
                personalRef.child("month").setValue(totalAmount);
                totalAmountMonth = totalAmount;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getWeekSpentAmount() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    weekTv.setText("Rs. "+ totalAmount);
                }
                personalRef.child("week").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getSavings() {

        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int budget;
                    if (snapshot.hasChild("budget")) {
                        budget = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    } else {
                        budget = 0;
                    }
                    int monthSpending;
                    if (snapshot.hasChild("month")) {
                        monthSpending = Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    } else {
                        monthSpending = 0;
                    }

                    int savings = budget - monthSpending;
                    inpocketTv.setText("Rs. " + savings);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(expensesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



}