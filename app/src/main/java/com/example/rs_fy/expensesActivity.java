package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class expensesActivity extends AppCompatActivity {
    private CardView budgetcardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        budgetcardView =findViewById(R.id.budgetcardView);

        budgetcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (expensesActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

    }

}