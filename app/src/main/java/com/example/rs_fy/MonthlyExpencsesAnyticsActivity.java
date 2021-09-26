package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonthlyExpencsesAnyticsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef,personalRef;

    private TextView totalAmoutSpentOn,analyticsTransportAmount,analyticsFoodAmount,analyticsEntertainmentAmount,analyticsHomeAmount,
            analyticsEducationAmount,analyticsCharityAmount,analyticsHealthAmount,analyticsPersonalAmount,analyticsOtherAmount,monthSpentAmount;

    private RelativeLayout relativeLayoutTransport,relativeLayoutFood,relativeLayoutEntertainment,relativeLayoutHome,relativeLayoutEducation,
            relativeLayoutCharity,relativeLayoutHealth,relativeLayoutPersonal,relativeLayoutOther,linerLayoutExAnliysis;

    private AnyChartView anyChartViewEx;
    private TextView monthRatioSpending,progress_ratio_transport,progress_ratio_food,progress_ratio_house,progress_ratio_ent,progress_ratio_edu,progress_ratio_cha,progress_ratio_hea,progress_ratio_per,progress_ratio_oth;
    private ImageView transport_status,food_status,Home_status,education_status,charity_status,entertainment_status,personal_status,health_status,other_status,monthRatioSpending_Image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_expencses_anytics);

        settingsToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(settingsToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        settingsToolbar.setTitle("Monthly Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        totalAmoutSpentOn = findViewById(R.id.totalAmoutSpentOn);

        //general analytic
        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        linerLayoutExAnliysis = findViewById(R.id.linerLayoutExAnliysis);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);


        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsHomeAmount = findViewById(R.id.analyticsHomeAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalAmount = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //Relative layouts views
        relativeLayoutTransport = findViewById(R.id.relativeLayoutTransport);
        relativeLayoutFood = findViewById(R.id.relativeLayoutFood);
        relativeLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        relativeLayoutHome = findViewById(R.id.relativeLayoutHome);
        relativeLayoutEducation = findViewById(R.id.relativeLayoutEducation);
        relativeLayoutCharity = findViewById(R.id.relativeLayoutCharity);
        relativeLayoutHealth = findViewById(R.id.relativeLayoutHealth);
        relativeLayoutPersonal = findViewById(R.id.relativeLayoutPersonal);
        relativeLayoutOther = findViewById(R.id.relativeLayoutOther);

        //status textviews
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_ent);
        progress_ratio_edu = findViewById(R.id.progress_ratio_edu);
        progress_ratio_cha = findViewById(R.id.progress_ratio_cha);
        progress_ratio_hea = findViewById(R.id.progress_ratio_hea);
        progress_ratio_per = findViewById(R.id.progress_ratio_per);
        progress_ratio_oth = findViewById(R.id.progress_ratio_oth);

        //imageviews


        transport_status = findViewById(R.id.transport_status);
        food_status = findViewById(R.id.food_status);
        Home_status = findViewById(R.id.Home_status);
        education_status = findViewById(R.id.education_status);
        charity_status = findViewById(R.id.charity_status);
        health_status = findViewById(R.id.health_status);
        entertainment_status = findViewById(R.id.entertainment_status);
        personal_status = findViewById(R.id.personal_status);
        other_status = findViewById(R.id.other_status);

        //anyChartView
        anyChartViewEx = findViewById(R.id.anyChartViewEx);


        getTotalWeekTransportExpense();
        getTotalMonthsFoodExpense();
        getTotalMonthsHouseExpense();
        getTotalMonthEntertainmentExpenses();
        getTotalWeekEducationExpenses();
        getTotalWeekCharityExpenses();
        getTotalWeekHealthExpenses();
        getTotalWeekPersonalExpenses();
        getTotalWeekOtherExpenses();
        getTotalWeekSpending();


        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        loadGraph();
                        setStatusAndImageResource();
                    }
                },
                2000
        );
    }

    private void getTotalWeekTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Transport"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthTrans").setValue(totalAmount);

                }
                else {
                    relativeLayoutTransport.setVisibility(View.GONE);
                    personalRef.child("monthTrans").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getTotalMonthsFoodExpense(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Food"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthFood").setValue(totalAmount);
                }else {
                    relativeLayoutFood.setVisibility(View.GONE);
                    personalRef.child("monthFood").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsHouseExpense(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "House Expenses"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthHouse").setValue(totalAmount);
                }else {
                    relativeLayoutFood.setVisibility(View.GONE);
                    personalRef.child("monthHouse").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthEntertainmentExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Entertainment"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEntertainmentAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthEnt").setValue(totalAmount);
                }else {
                    relativeLayoutEntertainment.setVisibility(View.GONE);
                    personalRef.child("monthEnt").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekEducationExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Education"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEducationAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthEdu").setValue(totalAmount);
                }else {
                    relativeLayoutEducation.setVisibility(View.GONE);
                    personalRef.child("monthEdu").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekCharityExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Charity"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsCharityAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthChar").setValue(totalAmount);
                }else {
                    relativeLayoutCharity.setVisibility(View.GONE);
                    personalRef.child("monthChar").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekHealthExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Health"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHealthAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthHea").setValue(totalAmount);
                }else {
                    relativeLayoutHealth.setVisibility(View.GONE);
                    personalRef.child("monthHea").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekPersonalExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Personal Expenses"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPersonalAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthPer").setValue(totalAmount);
                }else {
                    relativeLayoutPersonal.setVisibility(View.GONE);
                    personalRef.child("monthPer").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekOtherExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Other"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsOtherAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("monthOther").setValue(totalAmount);
                }else {
                    personalRef.child("monthOther").setValue(0);
                    relativeLayoutOther.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyExpencsesAnyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalWeekSpending(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  dataSnapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;

                    }
                    totalAmoutSpentOn.setText("Total Months's spending: $ "+ totalAmount);
                    monthSpentAmount.setText("Total Spent: $ "+totalAmount);
                }else {
                    anyChartViewEx.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int traTotal;
                    if (snapshot.hasChild("monthTrans")){
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    }else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("monthFood")){
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    }else {
                        foodTotal = 0;
                    }

                    int houseTotal;
                    if (snapshot.hasChild("monthHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    }else {
                        houseTotal = 0;
                    }

                    int entTotal;
                    if (snapshot.hasChild("monthEnt")){
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    }else {
                        entTotal=0;
                    }

                    int eduTotal;
                    if (snapshot.hasChild("monthEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString());
                    }else {
                        eduTotal = 0;
                    }

                    int chaTotal;
                    if (snapshot.hasChild("monthChar")){
                        chaTotal = Integer.parseInt(snapshot.child("monthChar").getValue().toString());
                    }else {
                        chaTotal = 0;
                    }


                    int heaTotal;
                    if (snapshot.hasChild("monthHea")){
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString());
                    }else {
                        heaTotal =0;
                    }

                    int perTotal;
                    if (snapshot.hasChild("monthPer")){
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    }else {
                        perTotal=0;
                    }
                    int othTotal;
                    if (snapshot.hasChild("monthOther")){
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    }else {
                        othTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("House exp", houseTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", chaTotal));
                    data.add(new ValueDataEntry("Health", heaTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("other", othTotal));


                    pie.data(data);

                    pie.title("Month Analytics");

                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items Spent On")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartViewEx.setChart(pie);
                }
                else {
                    Toast.makeText(MonthlyExpencsesAnyticsActivity.this,"Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setStatusAndImageResource(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    float traTotal;
                    if (snapshot.hasChild("monthTrans")){
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    }else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("monthFood")){
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    }else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("monthHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    }else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("monthEnt")){
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    }else {
                        entTotal=0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("monthEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString());
                    }else {
                        eduTotal = 0;
                    }

                    float chaTotal;
                    if (snapshot.hasChild("monthChar")){
                        chaTotal = Integer.parseInt(snapshot.child("monthChar").getValue().toString());
                    }else {
                        chaTotal = 0;
                    }


                    float heaTotal;
                    if (snapshot.hasChild("monthHea")){
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString());
                    }else {
                        heaTotal =0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("monthPer")){
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    }else {
                        perTotal=0;
                    }
                    float othTotal;
                    if (snapshot.hasChild("monthOther")){
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    }else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("month")){
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("month").getValue().toString());
                    }else {
                        monthTotalSpentAmount = 0;
                    }


                    //GETTING RATIOS

                    float traRatio;
                    if (snapshot.hasChild("monthTransRatio")){
                        traRatio = Integer.parseInt(snapshot.child("monthTransRatio").getValue().toString());
                    }else {
                        traRatio=0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("monthFoodRatio")){
                        foodRatio = Integer.parseInt(snapshot.child("monthFoodRatio").getValue().toString());
                    }else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("monthHouseRatio")){
                        houseRatio = Integer.parseInt(snapshot.child("monthHouseRatio").getValue().toString());
                    }else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("monthEntRatio")){
                        entRatio= Integer.parseInt(snapshot.child("monthEntRatio").getValue().toString());
                    }else {
                        entRatio = 0;
                    }

                    float eduRatio;
                    if (snapshot.hasChild("monthEduRatio")){
                        eduRatio= Integer.parseInt(snapshot.child("monthEduRatio").getValue().toString());
                    }else {
                        eduRatio=0;
                    }

                    float chaRatio;
                    if (snapshot.hasChild("monthCharRatio")){
                        chaRatio = Integer.parseInt(snapshot.child("monthCharRatio").getValue().toString());
                    }else {
                        chaRatio = 0;
                    }

                    float heaRatio;
                    if (snapshot.hasChild("monthHealthRatio")){
                        heaRatio = Integer.parseInt(snapshot.child("monthHealthRatio").getValue().toString());
                    }else {
                        heaRatio=0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("monthPerRatio")){
                        perRatio = Integer.parseInt(snapshot.child("monthPerRatio").getValue().toString());
                    }else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("monthOtherRatio")){
                        othRatio = Integer.parseInt(snapshot.child("monthOtherRatio").getValue().toString());
                    }else {
                        othRatio=0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("budget")){
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    }else {
                        monthTotalSpentAmountRatio =0;
                    }


                    float monthPercent = (monthTotalSpentAmount/monthTotalSpentAmountRatio)*100;
                    if (monthPercent<50){
                        monthRatioSpending.setText(monthPercent+" %" +" used of "+monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    }else if (monthPercent >= 50 && monthPercent <100){
                        monthRatioSpending.setText(monthPercent+" %" +" used of "+monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    }else {
                        monthRatioSpending.setText(monthPercent+" %" +" used of "+monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);

                    }


                    float transportPercent = (traTotal/traRatio)*100;
                    if (transportPercent<50){
                        progress_ratio_transport.setText(transportPercent+" %" +" used of "+traRatio + ". Status:");
                        transport_status.setImageResource(R.drawable.green);
                    }else if (transportPercent >= 50 && transportPercent <100){
                        progress_ratio_transport.setText(transportPercent+" %" +" used of "+traRatio + ". Status:");
                        transport_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_transport.setText(transportPercent+" %" +" used of "+traRatio + ". Status:");
                        transport_status.setImageResource(R.drawable.red);

                    }

                    float foodPercent = (foodTotal/foodRatio)*100;
                    if (foodPercent<50){
                        progress_ratio_food.setText(foodPercent+" %" +" used of "+foodRatio + ". Status:");
                        food_status.setImageResource(R.drawable.green);
                    }else if (foodPercent >= 50 && foodPercent <100){
                        progress_ratio_food.setText(foodPercent+" %" +" used of "+foodRatio + ". Status:");
                        food_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_food.setText(foodPercent+" %" +" used of "+foodRatio + ". Status:");
                        food_status.setImageResource(R.drawable.red);

                    }

                    float housePercent = (houseTotal/houseRatio)*100;
                    if (housePercent<50){
                        progress_ratio_house.setText(housePercent+" %" +" used of "+houseRatio + ". Status:");
                        Home_status.setImageResource(R.drawable.green);
                    }else if (housePercent >= 50 && housePercent <100){
                        progress_ratio_house.setText(housePercent+" %" +" used of "+houseRatio + ". Status:");
                        Home_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_house.setText(housePercent+" %" +" used of "+houseRatio + ". Status:");
                        Home_status.setImageResource(R.drawable.red);

                    }

                    float entPercent = (entTotal/entRatio)*100;
                    if (entPercent<50){
                        progress_ratio_ent.setText(entPercent+" %" +" used of "+entRatio + ". Status:");
                        entertainment_status.setImageResource(R.drawable.green);
                    }else if (entPercent >= 50 && entPercent <100){
                        progress_ratio_ent.setText(entPercent+" %" +" used of "+entRatio + ". Status:");
                        entertainment_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_ent.setText(entPercent+" %" +" used of "+entRatio + ". Status:");
                        entertainment_status.setImageResource(R.drawable.red);

                    }

                    float eduPercent = (eduTotal/eduRatio)*100;
                    if (eduPercent<50){
                        progress_ratio_edu.setText(eduPercent+" %" +" used of "+eduRatio + ". Status:");
                        education_status.setImageResource(R.drawable.green);
                    }
                    else if (eduPercent >= 50 && eduPercent <100){
                        progress_ratio_edu.setText(eduPercent+" %" +" used of "+eduRatio + ". Status:");
                        education_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_edu.setText(eduPercent+" %" +" used of "+eduRatio + ". Status:");
                        education_status.setImageResource(R.drawable.red);

                    }

                    float chaPercent = (chaTotal/chaRatio)*100;
                    if (chaPercent<50){
                        progress_ratio_cha.setText(chaPercent+" %" +" used of "+chaRatio + ". Status:");
                        charity_status.setImageResource(R.drawable.green);
                    }else if (chaPercent >= 50 && chaPercent <100){
                        progress_ratio_cha.setText(chaPercent+" %" +" used of "+chaRatio + ". Status:");
                        charity_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_cha.setText(chaPercent+" %" +" used of "+chaRatio + ". Status:");
                        charity_status.setImageResource(R.drawable.red);

                    }


                    float heaPercent = (heaTotal/heaRatio)*100;
                    if (heaPercent<50){
                        progress_ratio_hea.setText(heaPercent+" %" +" used of "+heaRatio + ". Status:");
                        health_status.setImageResource(R.drawable.green);
                    }
                    else if (heaPercent >= 50 && heaPercent <100){
                        progress_ratio_hea.setText(heaPercent+" %" +" used of "+heaRatio + ". Status:");
                        health_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_hea.setText(heaPercent+" %" +" used of "+heaRatio + ". Status:");
                        health_status.setImageResource(R.drawable.red);

                    }


                    float perPercent = (perTotal/perRatio)*100;
                    if (perPercent<50){
                        progress_ratio_per.setText(perPercent+" %" +" used of "+perRatio + " . Status:");
                        personal_status.setImageResource(R.drawable.green);
                    }else if (perPercent >= 50 && perPercent <100){
                        progress_ratio_per.setText(perPercent+" %" +" used of "+perRatio + " . Status:");
                        personal_status.setImageResource(R.drawable.brown);
                    }
                    else {
                        progress_ratio_per.setText(perPercent+" %" +" used of "+perRatio + " . Status:");
                        personal_status.setImageResource(R.drawable.red);
                    }


                    float otherPercent = (othTotal/othRatio)*100;
                    if (otherPercent<50){
                        progress_ratio_oth.setText(otherPercent+" %" +" used of "+othRatio + ". Status:");
                       other_status.setImageResource(R.drawable.green);
                    }
                    else if (otherPercent >= 50 && otherPercent <100){
                        progress_ratio_oth.setText(otherPercent+" %" +" used of "+othRatio + ". Status:");
                        other_status.setImageResource(R.drawable.brown);
                    }else {
                        progress_ratio_oth.setText(otherPercent+" %" +" used of "+othRatio + ". Status:");
                        other_status.setImageResource(R.drawable.red);

                    }

                }
                else {
                    Toast.makeText(MonthlyExpencsesAnyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}