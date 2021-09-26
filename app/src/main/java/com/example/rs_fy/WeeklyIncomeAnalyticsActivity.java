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
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

public class WeeklyIncomeAnalyticsActivity extends AppCompatActivity {
    private Toolbar settingsToolbar;


    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference incomeRef, personalRef;

    private TextView TotalGoalAmountTextview, analyticsSalaryAmount, analyticsGrantsAmount, analyticsRentalAmount, analyticsInvesmentAmount;
    private TextView analyticsWagesAmount, analyticsSidebusinessAmount, analyticsDividendAmount, analyticsPensionAmount, analyticsOtherIncomeAmount, monthIncomeAmount;

    private RelativeLayout relativeLayoutSalary, relativeLayoutGrants, relativeLayoutRental, relativeLayoutInvest, relativeLayoutWages;
    private RelativeLayout relativeLayoutSidebusiness, relativeLayoutDividend, relativeLayoutPension, relativeLayoutOtherIncome, linearLayoutAnalysis;

    private AnyChartView anyChartView;
    private TextView progress_ratio_salary, progress_ratio_Grants, progress_ratio_Rental, progress_ratio_Invesment, progress_ratio_Wages, progress_ratio_Sidebusiness, progress_ratio_Dividend, progress_ratio_Pension, progress_ratio_other, monthRatioIncome;
    private ImageView status_Image_Salary, status_Image_Grants, status_Image_Rental, status_Image_Invesment, status_Image_Wages, status_Image_sidebusiness, status_Image_Dividend, status_Image_Pension, status_Image_Other, monthRatioIncome_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_income_analytics);
        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        settingsToolbar.setTitle("Weekly Analytics");


        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        incomeRef = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);


        TotalGoalAmountTextview = findViewById(R.id.weektotalAmoutincome);

        //general analytic
        monthIncomeAmount = findViewById(R.id.monthIncomeAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        monthRatioIncome = findViewById(R.id.monthRatioIncome);
        monthRatioIncome_Image = findViewById(R.id.monthRatioIncome_Image);

        analyticsSalaryAmount = findViewById(R.id.analyticsSalaryAmount);
        analyticsGrantsAmount = findViewById(R.id.analyticsGrantsAmount);
        analyticsRentalAmount = findViewById(R.id.analyticsRentalAmount);
        analyticsInvesmentAmount = findViewById(R.id.analyticsInvesmentAmount);
        analyticsWagesAmount = findViewById(R.id.analyticsWagesAmount);
        analyticsSidebusinessAmount = findViewById(R.id.analyticsSidebusinessAmount);
        analyticsDividendAmount = findViewById(R.id.analyticsDividendAmount);
        analyticsPensionAmount = findViewById(R.id.analyticsPensionAmount);
        analyticsOtherIncomeAmount = findViewById(R.id.analyticsOtherIncomeAmount);


        //Relative layouts views
        relativeLayoutSalary = findViewById(R.id.relativeLayoutSalary);
        relativeLayoutGrants = findViewById(R.id.relativeLayoutGrants);
        relativeLayoutRental = findViewById(R.id.relativeLayoutRental);
        relativeLayoutInvest = findViewById(R.id.relativeLayoutInvest);
        relativeLayoutWages = findViewById(R.id.relativeLayoutWages);
        relativeLayoutSidebusiness = findViewById(R.id.relativeLayoutSidebusiness);
        relativeLayoutDividend = findViewById(R.id.relativeLayoutDividend);
        relativeLayoutPension = findViewById(R.id.relativeLayoutPension);
        relativeLayoutOtherIncome = findViewById(R.id.relativeLayoutOtherIncome);

        //textviews
        progress_ratio_salary = findViewById(R.id.progress_ratio_salary);
        progress_ratio_Grants = findViewById(R.id.progress_ratio_Grants);
        progress_ratio_Rental = findViewById(R.id.progress_ratio_Rental);
        progress_ratio_Invesment = findViewById(R.id.progress_ratio_Invesment);
        progress_ratio_Wages = findViewById(R.id.progress_ratio_Wages);
        progress_ratio_Sidebusiness = findViewById(R.id.progress_ratio_Sidebusiness);
        progress_ratio_Dividend = findViewById(R.id.progress_ratio_Dividend);
        progress_ratio_Pension = findViewById(R.id.progress_ratio_Pension);
        progress_ratio_other = findViewById(R.id.progress_ratio_other);

        //imageviews
        status_Image_Salary = findViewById(R.id.status_Image_Salary);
        status_Image_Grants = findViewById(R.id.status_Image_Grants);
        status_Image_Rental = findViewById(R.id.status_Image_Rental);
        status_Image_Invesment = findViewById(R.id.status_Image_Invesment);
        status_Image_Wages = findViewById(R.id.status_Image_Wages);
        status_Image_sidebusiness = findViewById(R.id.status_Image_sidebusiness);
        status_Image_Dividend = findViewById(R.id.status_Image_Dividend);
        status_Image_Pension = findViewById(R.id.status_Image_Pension);
        status_Image_Other = findViewById(R.id.status_Image_Other);


        //anyChartView
        anyChartView = findViewById(R.id.anyChartView);

        getTotalWeekSalaryIncome();
        getTotalWeekGrantsIncome();
        getTotalWeekRentalIncome();
        getTotalWeekInvesmentIncome();
        getTotalWeekWagesIncome();
        getTotalWeeksidebusinessIncome();
        getTotalWeekDividendIncome();
        getTotalWeekPensionIncome();
        getTotalWeekOtherIncome();
        getTotalWeekIncome();

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

    private void getTotalWeekSalaryIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Salary"+weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsSalaryAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekSal").setValue(totalAmount);

                } else {
                    relativeLayoutSalary.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getTotalWeekGrantsIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Grants"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsGrantsAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekGrant").setValue(totalAmount);
                } else {
                    relativeLayoutGrants.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekRentalIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Rent"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsRentalAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekRent").setValue(totalAmount);
                } else {
                    relativeLayoutRental.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekInvesmentIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Invesment"+weeks.getWeeks();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsInvesmentAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekInvest").setValue(totalAmount);
                } else {
                    relativeLayoutInvest.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekWagesIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Wages"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsWagesAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekWage").setValue(totalAmount);
                } else {
                    relativeLayoutWages.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeeksidebusinessIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "SB"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsSidebusinessAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekSB").setValue(totalAmount);
                } else {
                    relativeLayoutSidebusiness.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekDividendIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Dividend"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsDividendAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekDevi").setValue(totalAmount);
                } else {
                    relativeLayoutDividend.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekPensionIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Pension"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPensionAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekPension").setValue(totalAmount);
                } else {
                    relativeLayoutPension.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekOtherIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Other"+weeks.getWeeks();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsOtherIncomeAmount.setText("income: " + totalAmount);
                    }
                    personalRef.child("weekOther").setValue(totalAmount);
                } else {
                    relativeLayoutOtherIncome.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekIncome() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("income").child(onlineUserId);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;

                    }
                    TotalGoalAmountTextview.setText("Total week's Income: Rs. " + totalAmount);
                    monthIncomeAmount.setText("Total income: $ " + totalAmount);
                } else {
                    TotalGoalAmountTextview.setText("No income week");
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int salTotal;
                    if (snapshot.hasChild("weekSal")) {
                        salTotal = Integer.parseInt(snapshot.child("weekSal").getValue().toString());
                    } else {
                        salTotal = 0;
                    }

                    int grantTotal;
                    if (snapshot.hasChild("weekGrant")) {
                        grantTotal = Integer.parseInt(snapshot.child("weekGrant").getValue().toString());
                    } else {
                        grantTotal = 0;
                    }

                    int rentTotal;
                    if (snapshot.hasChild("weekRent")) {
                        rentTotal = Integer.parseInt(snapshot.child("weekRent").getValue().toString());
                    } else {
                        rentTotal = 0;
                    }

                    int investTotal;
                    if (snapshot.hasChild("weekInvest")) {
                        investTotal = Integer.parseInt(snapshot.child("weekInvest").getValue().toString());
                    } else {
                        investTotal = 0;
                    }

                    int wageTotal;
                    if (snapshot.hasChild("weekWage")) {
                        wageTotal = Integer.parseInt(snapshot.child("weekWage").getValue().toString());
                    } else {
                        wageTotal = 0;
                    }

                    int sbTotal;
                    if (snapshot.hasChild("weekSB")) {
                        sbTotal = Integer.parseInt(snapshot.child("weekSB").getValue().toString());
                    } else {
                        sbTotal = 0;
                    }

                    int deviTotal;
                    if (snapshot.hasChild("weekDevi")) {
                        deviTotal = Integer.parseInt(snapshot.child("weekDevi").getValue().toString());
                    } else {
                        deviTotal = 0;
                    }


                    int pensionTotal;
                    if (snapshot.hasChild("weekPension")) {
                        pensionTotal = Integer.parseInt(snapshot.child("weekPension").getValue().toString());
                    } else {
                        pensionTotal = 0;
                    }
                    int otherTotal;
                    if (snapshot.hasChild("weekOther")) {
                        otherTotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
                    } else {
                        otherTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Salary", salTotal));
                    data.add(new ValueDataEntry("Grants", grantTotal));
                    data.add(new ValueDataEntry("Rental", rentTotal));
                    data.add(new ValueDataEntry("Invesment", investTotal));
                    data.add(new ValueDataEntry("Wages", wageTotal));
                    data.add(new ValueDataEntry("side business", sbTotal));
                    data.add(new ValueDataEntry("Dividend", deviTotal));
                    data.add(new ValueDataEntry("Pension", pensionTotal));
                    data.add(new ValueDataEntry("Other", otherTotal));


                    pie.data(data);

                    pie.title("weekly Analytics");

                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items income")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                } else {
                    Toast.makeText(WeeklyIncomeAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyIncomeAnalyticsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatusAndImageResource() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float salTotal;
                    if (snapshot.hasChild("weekSal")) {
                        salTotal = Integer.parseInt(snapshot.child("weekSal").getValue().toString());
                    } else {
                        salTotal = 0;
                    }

                    float grantTotal;
                    if (snapshot.hasChild("weekGrant")) {
                        grantTotal = Integer.parseInt(snapshot.child("weekGrant").getValue().toString());
                    } else {
                        grantTotal = 0;
                    }

                    float rentTotal;
                    if (snapshot.hasChild("weekRent")) {
                        rentTotal = Integer.parseInt(snapshot.child("weekRent").getValue().toString());
                    } else {
                        rentTotal = 0;
                    }

                    float investTotal;
                    if (snapshot.hasChild("weekInvest")) {
                        investTotal = Integer.parseInt(snapshot.child("weekInvest").getValue().toString());
                    } else {
                        investTotal = 0;
                    }

                    float wageTotal;
                    if (snapshot.hasChild("weekWage")) {
                        wageTotal = Integer.parseInt(snapshot.child("weekWage").getValue().toString());
                    } else {
                        wageTotal = 0;
                    }

                    float sbTotal;
                    if (snapshot.hasChild("weekSB")) {
                        sbTotal = Integer.parseInt(snapshot.child("weekSB").getValue().toString());
                    } else {
                        sbTotal = 0;
                    }
                    float deviTotal;
                    if (snapshot.hasChild("weekDevi")) {
                        deviTotal = Integer.parseInt(snapshot.child("weekDevi").getValue().toString());
                    } else {
                        deviTotal = 0;
                    }


                    float pensionTotal;
                    if (snapshot.hasChild("weekPension")) {
                        pensionTotal = Integer.parseInt(snapshot.child("weekPension").getValue().toString());
                    } else {
                        pensionTotal = 0;
                    }


                    float otherTotal;
                    if (snapshot.hasChild("weekOther")) {
                        otherTotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
                    } else {
                        otherTotal = 0;
                    }

                    float monthTotalIncomeAmount;
                    if (snapshot.hasChild("week")) {
                        monthTotalIncomeAmount = Integer.parseInt(snapshot.child("week").getValue().toString());
                    } else {
                        monthTotalIncomeAmount = 0;
                    }


                    //GETTING RATIOS
                    float salRatio;
                    if (snapshot.hasChild("weekSalRatio")) {
                        salRatio = Integer.parseInt(snapshot.child("weekSalRatio").getValue().toString());
                    } else {
                        salRatio = 0;
                    }

                    float grantRatio;
                    if (snapshot.hasChild("weekGrantsRatio")) {
                        grantRatio = Integer.parseInt(snapshot.child("weekGrantsRatio").getValue().toString());
                    } else {
                        grantRatio = 0;
                    }

                    float rentRatio;
                    if (snapshot.hasChild("weekRentRatio")) {
                        rentRatio = Integer.parseInt(snapshot.child("weekRentRatio").getValue().toString());
                    } else {
                        rentRatio = 0;
                    }

                    float InvestRatio;
                    if (snapshot.hasChild("weekInvestRatio")) {
                        InvestRatio = Integer.parseInt(snapshot.child("weekInvestRatio").getValue().toString());
                    } else {
                        InvestRatio = 0;
                    }

                    float wageRatio;
                    if (snapshot.hasChild("weekWageRatio")) {
                        wageRatio = Integer.parseInt(snapshot.child("weekWageRatio").getValue().toString());
                    } else {
                        wageRatio = 0;
                    }

                    float sbRatio;
                    if (snapshot.hasChild("weekSbRatio")) {
                        sbRatio = Integer.parseInt(snapshot.child("weekSbRatio").getValue().toString());
                    } else {
                        sbRatio = 0;
                    }

                    float deviRatio;
                    if (snapshot.hasChild("weekDividendRatio")) {
                        deviRatio = Integer.parseInt(snapshot.child("weekDividendRatio").getValue().toString());
                    } else {
                        deviRatio = 0;
                    }

                    float pensionRatio;
                    if (snapshot.hasChild("weekPensionRatio")) {
                        pensionRatio = Integer.parseInt(snapshot.child("weekPensionRatio").getValue().toString());
                    } else {
                        pensionRatio = 0;
                    }


                    float otherRatio;
                    if (snapshot.hasChild("weekOtherRatio")) {
                        otherRatio = Integer.parseInt(snapshot.child("weekOtherRatio").getValue().toString());
                    } else {
                        otherRatio = 0;
                    }

                    float monthTotalIncomeAmountRatio;
                    if (snapshot.hasChild("weekGoal")) {
                        monthTotalIncomeAmountRatio = Integer.parseInt(snapshot.child("weekGoal").getValue().toString());
                    } else {
                        monthTotalIncomeAmountRatio = 0;
                    }


                    float monthPercent = (monthTotalIncomeAmount / monthTotalIncomeAmountRatio) * 100;
                    if (monthPercent < 50) {
                        monthRatioIncome.setText(monthPercent + " %" + " used of " + monthTotalIncomeAmountRatio + ". Status:");
                        monthRatioIncome_Image.setImageResource(R.drawable.green);
                    } else if (monthPercent >= 50 && monthPercent < 100) {
                        monthRatioIncome.setText(monthPercent + " %" + " used of " + monthTotalIncomeAmountRatio + ". Status:");
                        monthRatioIncome_Image.setImageResource(R.drawable.brown);
                    } else {
                        monthRatioIncome.setText(monthPercent + " %" + " used of " + monthTotalIncomeAmountRatio + ". Status:");
                        monthRatioIncome_Image.setImageResource(R.drawable.red);

                    }

                    float salaryPercent = (salTotal / salRatio) * 100;
                    if (salaryPercent < 50) {
                        progress_ratio_salary.setText(salaryPercent + " %" + " used of " + salRatio + ". Status:");
                        status_Image_Salary.setImageResource(R.drawable.green);
                    } else if (salaryPercent >= 50 && salaryPercent < 100) {
                        progress_ratio_salary.setText(salaryPercent + " %" + " used of " + salRatio + ". Status:");
                        status_Image_Salary.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_salary.setText(salaryPercent + " %" + " used of " + salRatio + ". Status:");
                        status_Image_Salary.setImageResource(R.drawable.red);

                    }

                    float grantPercent = (grantTotal / grantRatio) * 100;
                    if (grantPercent < 50) {
                        progress_ratio_Grants.setText(grantPercent + " %" + " used of " + grantRatio + ". Status:");
                        status_Image_Grants.setImageResource(R.drawable.green);
                    } else if (grantPercent >= 50 && grantPercent < 100) {
                        progress_ratio_Grants.setText(grantPercent + " %" + " used of " + grantRatio + ". Status:");
                        status_Image_Grants.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Grants.setText(grantPercent + " %" + " used of " + grantRatio + ". Status:");
                        status_Image_Grants.setImageResource(R.drawable.red);

                    }

                    float rentPercent = (rentTotal / rentRatio) * 100;
                    if (rentPercent < 50) {
                        progress_ratio_Rental.setText(rentPercent + " %" + " used of " + rentRatio + ". Status:");
                        status_Image_Rental.setImageResource(R.drawable.green);
                    } else if (rentPercent >= 50 && rentPercent < 100) {
                        progress_ratio_Rental.setText(rentPercent + " %" + " used of " + rentRatio + ". Status:");
                        status_Image_Rental.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Rental.setText(rentPercent + " %" + " used of " + rentRatio + ". Status:");
                        status_Image_Rental.setImageResource(R.drawable.red);

                    }

                    float investPercent = (investTotal / InvestRatio) * 100;
                    if (investPercent < 50) {
                        progress_ratio_Invesment.setText(investPercent + " %" + " used of " + InvestRatio + ". Status:");
                        status_Image_Invesment.setImageResource(R.drawable.green);
                    } else if (investPercent >= 50 && investPercent < 100) {
                        progress_ratio_Invesment.setText(investPercent + " %" + " used of " + InvestRatio + ". Status:");
                        status_Image_Invesment.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Invesment.setText(investPercent + " %" + " used of " + InvestRatio + ". Status:");
                        status_Image_Invesment.setImageResource(R.drawable.red);

                    }

                    float wagePercent = (wageTotal / wageRatio) * 100;
                    if (wagePercent < 50) {
                        progress_ratio_Wages.setText(wagePercent + " %" + " used of " + wageRatio + ". Status:");
                        status_Image_Wages.setImageResource(R.drawable.green);
                    } else if (wagePercent >= 50 && wagePercent < 100) {
                        progress_ratio_Wages.setText(wagePercent + " %" + " used of " + wageRatio + ". Status:");
                        status_Image_Wages.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Wages.setText(wagePercent + " %" + " used of " + wageRatio + ". Status:");
                        status_Image_Wages.setImageResource(R.drawable.red);

                    }

                    float sbPercent = (sbTotal / sbRatio) * 100;
                    if (sbPercent < 50) {
                        progress_ratio_Sidebusiness.setText(sbPercent + " %" + " used of " + sbRatio + ". Status:");
                        status_Image_sidebusiness.setImageResource(R.drawable.green);
                    } else if (sbPercent >= 50 && sbPercent < 100) {
                        progress_ratio_Sidebusiness.setText(sbPercent + " %" + " used of " + sbRatio + ". Status:");
                        status_Image_sidebusiness.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Sidebusiness.setText(sbPercent + " %" + " used of " + sbRatio + ". Status:");
                        status_Image_sidebusiness.setImageResource(R.drawable.red);

                    }

                    float deviPercent = (deviTotal / deviRatio) * 100;
                    if (deviPercent < 50) {
                        progress_ratio_Dividend.setText(deviPercent + " %" + " used of " + deviRatio + ". Status:");
                        status_Image_Dividend.setImageResource(R.drawable.green);
                    } else if (deviPercent >= 50 && deviPercent < 100) {
                        progress_ratio_Dividend.setText(deviPercent + " %" + " used of " + deviRatio + ". Status:");
                        status_Image_Dividend.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Dividend.setText(deviPercent + " %" + " used of " + deviRatio + ". Status:");
                        status_Image_Dividend.setImageResource(R.drawable.red);

                    }

                    float PensionPercent = (pensionTotal / pensionRatio) * 100;
                    if (PensionPercent < 50) {
                        progress_ratio_Pension.setText(PensionPercent + " %" + " used of " + pensionRatio + ". Status:");
                        status_Image_Pension.setImageResource(R.drawable.green);
                    } else if (PensionPercent >= 50 && PensionPercent < 100) {
                        progress_ratio_Pension.setText(PensionPercent + " %" + " used of " + pensionRatio + ". Status:");
                        status_Image_Pension.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_Pension.setText(PensionPercent + " %" + " used of " + pensionRatio + ". Status:");
                        status_Image_Pension.setImageResource(R.drawable.red);

                    }


                    float otherPercent = (otherTotal / otherRatio) * 100;
                    if (otherPercent < 50) {
                        progress_ratio_other.setText(otherPercent + " %" + " used of " + otherRatio + ". Status:");
                        status_Image_Other.setImageResource(R.drawable.green);
                    } else if (otherPercent >= 50 && otherPercent < 100) {
                        progress_ratio_other.setText(otherPercent + " %" + " used of " + otherRatio + ". Status:");
                        status_Image_Other.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_other.setText(otherPercent + " %" + " used of " + otherRatio + ". Status:");
                        status_Image_Other.setImageResource(R.drawable.red);

                    }

                } else {
                    Toast.makeText(WeeklyIncomeAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

