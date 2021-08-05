package com.example.rs_fy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button loginbtn;
    private TextView loginQn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email =findViewById(R.id.email);
        password =findViewById(R.id.password);
        loginbtn =findViewById(R.id.loginbtn);
        loginQn =findViewById(R.id.loginQn);

        loginQn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this,RegistartionActivity.class);
                startActivity(intent);
            }

        });
    }
}