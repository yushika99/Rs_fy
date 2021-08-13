package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TodaySpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalAmoutSpentOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    //database refrence
    private FirebaseAuth mAuth;
    private String onlineUserID="";
    private DatabaseReference expensesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        // initializing

        toolbar=findViewById(R.id.toolbar);
        //set support action bar
        setSupportActionBar(toolbar);
        getActionBar().setTitle("My Today Spending ");

        totalAmoutSpentOn=findViewById(R.id.totalAmoutSpentOn);
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerView);
        fab=findViewById(R.id.fab);
        loader=new ProgressDialog(this);

        //database val initialing
        mAuth=FirebaseAuth.getInstance();
        onlineUserID=mAuth.getCurrentUser().getUid();
        expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIteamSpentOn();
            }
        });
    }

    private void addIteamSpentOn() {
        AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDiolag.setView(myView);

        final AlertDialog dialog=myDiolag.create();
        dialog.setCancelable(false);

        final Spinner itemsSpinner = myView.findViewById(R.id.itemsSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText note=myView.findViewById(R.id.note);
        final Button cancel =myView.findViewById(R.id.cancel);
        final Button save =myView.findViewById(R.id.save);

        note.setVisibility(View.VISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validation
                String Amount = amount.getText().toString();
                String Item = itemsSpinner.getSelectedItem().toString();
                String notes = note.getText().toString();
                if(TextUtils.isEmpty(Amount)){
                    amount.setError("Amount is Required!");
                    return;
                }
                if (Item.equals("Select Item")){
                    Toast.makeText(TodaySpendingActivity.this,"Select a valid Item",Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(notes)){
                    note.setError("Note is Reqired ");
                    return;
                }
                else{
                    loader.setMessage("Adding a Budget Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id=expensesRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal =Calendar.getInstance();
                    String date= dateFormat.format(cal.getTime());

                    MutableDateTime epoch =new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now =new DateTime();
                    Months months = Months.monthsBetween(epoch,now);


                    //pass parameters according to the paramiterlized constucter
                    Data data = new Data(Item,date,id,notes,Integer.parseInt(Amount), months.getMonths());
                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TodaySpendingActivity.this,"Budget Item Added Sccessfuly",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(TodaySpendingActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}