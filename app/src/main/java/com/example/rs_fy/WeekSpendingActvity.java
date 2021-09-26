package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekSpendingActvity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView totalWeekAmountTextView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeekASpendingAdapter weekASpendingAdapter;
    private List<Data> myDataList;

    //database refrence

    private FirebaseAuth mAuth;
    private String onlineUserID="";
    private DatabaseReference expensesRef;

    //month
    private String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending_actvity);

        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle("My Week Spending ");

        totalWeekAmountTextView=findViewById(R.id.totalWeekAmountTextView);
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.TodayIncomeRecyclerView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //database val initialing
        mAuth=FirebaseAuth.getInstance();
        onlineUserID=mAuth.getCurrentUser().getUid();

        myDataList= new ArrayList<>();
       weekASpendingAdapter=new WeekASpendingAdapter(WeekSpendingActvity.this,myDataList);
        recyclerView.setAdapter(weekASpendingAdapter);
            if (getIntent().getExtras()!=null){
                type=getIntent().getStringExtra("type");
                if(type.equals("week")){
                    readWeekSpendingItems();
                }else if (type.equals("month")){
                    readMonthSpending();
                }
            }

    }

    private void readMonthSpending() {

            MutableDateTime epoch =new MutableDateTime();
            epoch.setDate(0);
            DateTime now =new DateTime();
            Months months= Months.monthsBetween(epoch, now);

            expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
            Query query = expensesRef.orderByChild("month").equalTo(months.getMonths());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    myDataList.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Data data =dataSnapshot.getValue(Data.class);
                        myDataList.add(data);
                    }
                    weekASpendingAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    int totalAmount =0;
                    for(DataSnapshot ds:snapshot.getChildren()){

                        Map<String,Object> map =(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        totalAmount +=pTotal;

                        totalWeekAmountTextView.setText("Total Month's Spending: Rs."+totalAmount);
                    }
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });
        }



    private void readWeekSpendingItems() {

        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now =new DateTime();
        Weeks weeks= Weeks.weeksBetween(epoch,now);

        expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = expensesRef.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data =dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                weekASpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount =0;
                for(DataSnapshot ds:snapshot.getChildren()){

                    Map<String,Object> map =(Map<String, Object>)ds.getValue();
                    Object total=map.get("amount");
                    int pTotal=Integer.parseInt(String.valueOf(total));
                    totalAmount +=pTotal;

                    totalWeekAmountTextView.setText("Total Week's Spending: Rs."+totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}