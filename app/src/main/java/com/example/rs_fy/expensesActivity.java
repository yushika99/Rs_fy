package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class expensesActivity extends AppCompatActivity {
    private CardView budgetcardView,todaycardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        budgetcardView =findViewById(R.id.budgetcardView);
        todaycardView =findViewById(R.id.todaycardView);

        budgetcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (expensesActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        todaycardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(expensesActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

    }

}