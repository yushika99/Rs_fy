package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class IncomeHistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private RecyclerView recyclerView;

    private TodayIncomeItemAdapter todayIncomeItemAdapter;
    private List<GoalData> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference incomeRef, personalRef;

    private Toolbar settingsToolbar;

    private Button search;
    private TextView historyTotalAmountIncome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_history);
        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        settingsToolbar.setTitle("History");


        search = findViewById(R.id.search);
        historyTotalAmountIncome = findViewById(R.id.historyTotalAmountIncome);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recycler_View_Id_Feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        myDataList = new ArrayList<>();
        todayIncomeItemAdapter = new TodayIncomeItemAdapter(IncomeHistoryActivity.this, myDataList);
        recyclerView.setAdapter(todayIncomeItemAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePickerDialog();
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        int months = month+1;
        String date = dayOfMonth+"-"+"0"+months +"-"+year;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDataList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    GoalData data = snapshot.getValue(GoalData.class);
                    myDataList.add(data);
                }
                todayIncomeItemAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    if (totalAmount >0){
                        historyTotalAmountIncome.setVisibility(View.VISIBLE);
                        historyTotalAmountIncome.setText("This day your income Rs.: "+ totalAmount);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IncomeHistoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}