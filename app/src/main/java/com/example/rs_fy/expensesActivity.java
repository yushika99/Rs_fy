package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class expensesActivity extends AppCompatActivity {


private ImageView budgetEBtnImageView,todayEBtnImageView,weekEBtnImageView,monthEBtnImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        budgetEBtnImageView =findViewById(R.id.budgetEBtnImageView);
        todayEBtnImageView =findViewById(R.id.todayEBtnImageView);
        weekEBtnImageView =findViewById(R.id.weekEBtnImageView);
        monthEBtnImageView =findViewById(R.id.monthEBtnImageView);

        budgetEBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (expensesActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        todayEBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

        weekEBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,WeekSpendingActvity.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });

        monthEBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,WeekSpendingActvity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });

    }

}