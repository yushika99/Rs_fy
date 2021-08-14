package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseExpensesAnlyticsActivity extends AppCompatActivity {

        private CardView todayAnalyticscardView,weekAnalyticscardView,monthAnalyticscardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_expenses_anlytics);

        todayAnalyticscardView=findViewById(R.id.todayAnalyticscardView);
        weekAnalyticscardView=findViewById(R.id.weekAnalyticscardView);
        monthAnalyticscardView=findViewById(R.id.monthAnalyticscardView);

        todayAnalyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChooseExpensesAnlyticsActivity.this,DailyExpensesAnlyticsActivity.class);
                startActivity(intent);
            }
        });


        weekAnalyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChooseExpensesAnlyticsActivity.this,WeeklyExpencesAnalyticsActivity.class);
                startActivity(intent);
            }
        });


        monthAnalyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChooseExpensesAnlyticsActivity.this,MonthlyExpencsesAnyticsActivity.class);
                startActivity(intent);
            }
        });



    }
}