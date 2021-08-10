package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class incomeActivity extends AppCompatActivity {
    private CardView IncomeGoalcardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        IncomeGoalcardView =findViewById(R.id.IncomeGoalcardView);

        IncomeGoalcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (incomeActivity.this, IncomeGoalActivity.class);
                startActivity(intent);
            }
        });
    }
}