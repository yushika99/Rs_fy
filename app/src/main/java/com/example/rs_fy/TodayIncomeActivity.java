package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodayIncomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalAmountIncome;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    //database refrence
    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference incomeRef;

    private TodayIncomeItemAdapter todayIncomeItemAdapter;
    private List<GoalData> myDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_income);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today income");

        totalAmountIncome = findViewById(R.id.totalAmountIncome);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        incomeRef = FirebaseDatabase.getInstance().getReference("income").child(onlineUserID);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        todayIncomeItemAdapter = new TodayIncomeItemAdapter(TodayIncomeActivity.this,myDataList);
        recyclerView.setAdapter(todayIncomeItemAdapter);

        readItems();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemIncome();
            }
        });

    }

    private void readItems() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserID);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GoalData data = dataSnapshot.getValue(GoalData.class);
                    myDataList.add(data);
                }

                todayIncomeItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalAmountIncome.setText("Total Day's income: Rs." + totalAmount);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void addItemIncome() {

        AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_goal_layout, null);
        myDiolag.setView(myView);

        final AlertDialog dialog = myDiolag.create();
        dialog.setCancelable(false);


        final Spinner itemSpinner = myView.findViewById(R.id.itemspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText note = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        note.setVisibility(View.GONE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // validation
                String Amount = amount.getText().toString();
                String Item = itemSpinner.getSelectedItem().toString();
                String notes = note.getText().toString();


                if (TextUtils.isEmpty(Amount)) {
                    amount.setError("Amount is required!");
                    return;
                }
                if (Item.equals("Select Goal")) {
                    Toast.makeText(TodayIncomeActivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("Adding a Goal");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = incomeRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    String itemNday = Item + date;
                    String itemNweek = Item + weeks.getWeeks();
                    String itemNmonth = Item + months.getMonths();


                    //pass parameters according to the paramiterlized constucter
                    GoalData data = new GoalData(Item, date, id, itemNday, itemNweek, itemNmonth, Integer.parseInt(Amount), weeks.getWeeks(), months.getMonths(), notes);
                    incomeRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TodayIncomeActivity.this, "Goal Item Added Sccessfuly", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TodayIncomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
