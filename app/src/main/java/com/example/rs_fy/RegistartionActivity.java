package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistartionActivity extends AppCompatActivity {

    private EditText email,password;
    private Button registerbtn;
    private TextView registerQn;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registartion);

        email =findViewById(R.id.email);
        password =findViewById(R.id.password);
        registerbtn =findViewById(R.id.registerbtn);
        registerQn =findViewById(R.id.registerQn);

        mAuth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        registerQn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(RegistartionActivity.this,LoginActivity.class);
                startActivity(intent);
            }

        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  emailString = email.getText().toString();
                String  passwordString = password.getText().toString();

                if (TextUtils.isEmpty(emailString)){
                    email.setError("Email is required");
                }

                if (TextUtils.isEmpty(passwordString)){
                    password.setError("Password is required");
                }
                else{
                    progressDialog.setMessage("registration  in process");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(RegistartionActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(RegistartionActivity.this,task.getException().toString() ,Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }


            }
        });
    }
}