package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistartionActivity extends AppCompatActivity {

    private EditText email,password;
    private Button registerbtn;
    private TextView registerQn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registartion);

        email =findViewById(R.id.email);
        password =findViewById(R.id.password);
        registerbtn =findViewById(R.id.registerbtn);
        registerQn =findViewById(R.id.registerQn);

        registerQn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegistartionActivity.this,LoginActivity.class);
                startActivity(intent);
            }

        });
    }
}