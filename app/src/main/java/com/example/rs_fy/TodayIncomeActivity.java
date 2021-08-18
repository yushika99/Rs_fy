package com.example.rs_fy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayIncomeActivity extends AppCompatActivity {


    private Toolbar incometodaytoolbar;
    private TextView todayTotalAmoutIncome;
    private ProgressBar TodayIncomeProgressBar;
    private RecyclerView TodayIncomeRecyclerView;
    private FloatingActionButton ifab;//check
    private ProgressDialog loader;

    //database refrence
    private FirebaseAuth mAuth;
    private String onlineUserID="";
    private DatabaseReference incomeRef;

    private TodayItemsAdapter todayItemsAdapter;
    private List<GoalData> myDataList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_income);

        incometodaytoolbar=findViewById(R.id.incometodaytoolbar);
        //set support action bar
        setSupportActionBar(incometodaytoolbar);
        getActionBar().setTitle("My Today Income ");

        todayTotalAmoutIncome=findViewById(R.id.todayTotalAmoutIncome);
        TodayIncomeProgressBar=findViewById(R.id.TodayIncomeProgressBar);
        TodayIncomeRecyclerView=findViewById(R.id.TodayIncomeRecyclerView);


        ifab=findViewById(R.id.ifab);
        loader=new ProgressDialog(this);

        ifab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncome();
            }
            private void addIncome() {
                AlertDialog.Builder myDiolag = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                View myView = inflater.inflate(R.layout.input_goal_layout, null);
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
                            Weeks weeks= Weeks.weeksBetween(epoch,now);
                            Months months = Months.monthsBetween(epoch,now);


                            String itemNday = Item+date;
                            String itemNweek = Item+weeks.getWeeks();
                            String itemNmonth = Item+months.getMonths();

                            //pass parameters according to the paramiterlized constucter
//                    Data data = new Data(Item,date,id,notes,Integer.parseInt(Amount), months.getMonths(),weeks.getWeeks());
                            Data data = new Data(Item,date,id,itemNday,itemNweek,itemNmonth,Integer.parseInt(Amount), months.getMonths(),weeks.getWeeks(),null);
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