package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;



public class ChooseIncomeAnalyticsActivity extends AppCompatActivity {
    private CardView todayIncomeAnalyticscardView, weekIncomeAnalyticscardView, monthIncomeAnalyticscardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_income_analytics);

        todayIncomeAnalyticscardView = findViewById(R.id.todayIncomeAnalyticscardView);
        weekIncomeAnalyticscardView = findViewById(R.id.weekIncomeAnalyticscardView);
        monthIncomeAnalyticscardView = findViewById(R.id.monthIncomeAnalyticscardView);

        todayIncomeAnalyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseIncomeAnalyticsActivity.this, DailyIncomeAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        weekIncomeAnalyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseIncomeAnalyticsActivity.this, WeeklyIncomeAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        monthIncomeAnalyticscardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseIncomeAnalyticsActivity.this, MonthlyIncomeAnalyticsActivity.class);
                startActivity(intent);
            }
        });
    }
}