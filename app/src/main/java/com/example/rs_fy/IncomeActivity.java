package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class IncomeActivity extends AppCompatActivity {
    private CardView ihomegoalcardView;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        ihomegoalcardView =findViewById(R.id.ihomegoalcardView);

        ihomegoalcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (IncomeActivity.this, IncomeGoalActivity.class);
                startActivity(intent);
            }
        });
    }
}