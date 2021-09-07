package com.example.rs_fy;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class IncomeActivity extends AppCompatActivity {
    private Toolbar ihometoolbar;
    private TextView ihomegoalTv,ihometodayTv,ihomeweekTv,ihomemonthTv,ihomeextraTv;
    private CardView ihomegoalcardView,ihometodaycardView,ihomeweekcardView,ihomemonthcardView,ihomeanlyticscardView,ihomehistorycardView;

    private CardView analyticsCardView;

    private FirebaseAuth mAuth;
    private DatabaseReference goalRef, incomeRef, personalIRef;
    private String onlineUserID = "";

    private int totalIAmountMonth = 0;
    private int totalAImountGoal = 0;
    private  int totalAImountGoalB = 0;
    private  int totalAImountGoalC = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        ihometoolbar = findViewById(R.id.ihometoolbar);
        setSupportActionBar(ihometoolbar);
        getSupportActionBar().setTitle("My income");


        ihomegoalTv =findViewById(R.id.ihomegoalTv);
        ihometodayTv =findViewById(R.id.ihometodayTv);
        ihomeweekTv =findViewById(R.id.ihomeweekTv);
        ihomemonthTv =findViewById(R.id.ihomemonthTv);
        ihomeextraTv =findViewById(R.id.ihomeextraTv);


        mAuth = FirebaseAuth.getInstance();
        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        goalRef = FirebaseDatabase.getInstance().getReference("goal").child(onlineUserID);
        incomeRef = FirebaseDatabase.getInstance().getReference("income").child(onlineUserID);
        personalIRef = FirebaseDatabase.getInstance().getReference("ipersonal").child(onlineUserID);


        //goal card
        ihomegoalcardView = findViewById(R.id.ihomegoalcardView);
        ihomegoalcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, IncomeGoalActivity.class);
                startActivity(intent);

            }
        });

        //today card
        ihometodaycardView = findViewById(R.id.ihometodaycardView);
        ihometodaycardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, TodayIncomeActivity.class);
                startActivity(intent);

            }
        });

        //week card
        ihomeweekcardView = findViewById(R.id.ihomeweekcardView);
        ihomeweekcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, WeekIncomeActivity.class);
                startActivity(intent);

            }
        });

        //month card
        ihomemonthcardView = findViewById(R.id.ihomemonthcardView);
        ihomemonthcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, MonthIncomeActivity.class);
                startActivity(intent);

            }
        });

        //analytics card
        ihomeanlyticscardView = findViewById(R.id.ihomeanlyticscardView);
        ihomeanlyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, ChooseIncomeAnalyticsActivity.class);
                startActivity(intent);

            }
        });

        //history card
        ihomehistorycardView = findViewById(R.id.ihomehistorycardView);
        ihomehistorycardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, IncomeHistoryActivity.class);
                startActivity(intent);

            }
        });
//        private int totalIAmountMonth = 0;
//        private int totalAImountGoal = 0;
//        private  int totalAImountGoalB = 0;
//        private  int totalAImountGoalC = 0;

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAImountGoalB+=pTotal;
                    }
                    totalAImountGoalC = totalAImountGoalB;
                    personalIRef.child("goal").setValue(totalAImountGoalC);
                }else {
                    personalIRef.child("goal").setValue(0);
                    Toast.makeText(IncomeActivity.this, "Please Set an Income Goal ", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getGoalAmount();
        getTodayIncomeAmount();
        getWeekIncomeAmount();
        getMonthIncomeAmount();
        getExtra();
    }

    private void getGoalAmount() {
        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAImountGoal+=pTotal;
                        ihomegoalTv.setText("$ "+String.valueOf(totalAImountGoal));
                    }
                }else {
                    totalAImountGoal=0;
                    ihomegoalTv.setText("$ "+String.valueOf(0));


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getTodayIncomeAmount(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalIAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalIAmount+=pTotal;
                    ihomemonthTv.setText("$ "+ totalIAmount);
                }
                personalIRef.child("today").setValue(totalIAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IncomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMonthIncomeAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalIAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalIAmount+=pTotal;
                    ihomemonthTv.setText("$ "+ totalIAmount);

                }
                personalIRef.child("month").setValue(totalIAmount);
                totalIAmount = totalIAmount;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getWeekIncomeAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalIAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalIAmount+=pTotal;
                    ihomeweekTv.setText("$ "+ totalIAmount);
                }
                personalIRef.child("week").setValue(totalIAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getExtra(){
        personalIRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int goal;
                    if (snapshot.hasChild("goal")) {
                        goal = Integer.parseInt(snapshot.child("goal").getValue().toString());
                    } else {
                        goal = 0;
                    }
                    int monthIncome;
                    if (snapshot.hasChild("month")) {
                        monthIncome = Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    } else {
                        monthIncome = 0;
                    }

                    int savings = goal - monthIncome;
                    ihomeextraTv.setText("$ " + savings);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IncomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.account){
            Intent intent  = new Intent(IncomeActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

}

}